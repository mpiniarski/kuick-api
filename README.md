# kuick-api

**kuick-api** is a library for quickly creating RPC style HTTP-based APIs in declarative way. 

Extension of `ktor` framework (https://ktor.io)
Inspired by `kuick` library (https://github.com/NoSpoonLab/kuick)

Supports generic mechanisms, designed to increase performance of communication between applications.

### Server-side
```kotlin
import io.ktor.*
import com.google.inject.Guice
import kuick.api.*

...

application.routing {
    route("/rpc") {
        rpcRoute<ResourceApi>(injector) {                                           // 1)
            withFieldsParameter()                                                   // 2)
            withIncludeParameter(                                                   // 3)
                Resource::otherResource to { id -> otherResourseApi.getOne(id) }
            )
        }       

        withParameter(                                                              // 4)
            "session",
            ifNull = { call.respond(HttpStatusCode.Forbidden) }) {
            call.sessions.get<Session>()
        }


        aggregationRoute(injector)                                                  // 5)
        
    }
}

...

```

1) 
Creates RPC style routing for `ResourceApi` class:
```kotlin
class ResourceApi {
    fun getAll(): List<Resource> = /*implementation*/
    fun addResource(resource: Resource) : Boolean = /*implementation*/
}
```
- *POST /rpc/ResourceApi/getAll*, accepting empty body and returning list of Resources 
- *POST /rpc/ResourceApi/addResource*, accepting Resource to create and returning boolean value

2) 
Adds support for *"$fields"* query parameter, which contains a list of resource fields that should be returned. 

In other words, it performs projection of returned resource, which prevents overfetching.

e.g. *POST /rpc/ResourceApi/getAll?$fields=["id","name"]* 

will return list of resources containing only ids and names for each resource

3)
Adds support for *"$include"* query parameter, which contains a list of resource fields that contain id of another resource, which should be included in response. 

Using this parameter can fix N+1 query problem, which is a common performance issue in communication between applications.

e.g. *POST /rpc/ResourceApi/getAll?$include=["otherResource"]&$fields=["id","name","otherResource.id","otherResource.name"]* 

will return list of resources containing ids, names and otherResources, where in "otherResource" fields instead of id, there will be na object containing id and name

4)
Adds additional argument to method call if it was not passed in request. In this example parameter called session will be added to list of arguments of invoked method.
Can be used in cases when e.g. `ResouceApi` requires this parameter:
```kotlin
class ResourceApi {
    ...
    fun addResource(resource: Resource, session: Session) : Boolean = /*implementation*/
    ...
}
```

5)
Adds special route for aggregating requests. Using this mechanism can cause big improvements of communication performance.

*POST /rpc/aggregation*, accepting list of requests e.g.
```json
[
   {
      "path":"rpc/ResourceApi/addResource",
      "queryParameters":{},
      "body":{
         "name":"someName",
         "field1":10,
         "field2":15,
         "otherResource":"other-resource-2-id-1"
      }
   },
   {
      "path":"rpc/ResourceApi/addResource",
      "queryParameters":{},
      "body":{
         "name":"someOtherName",
         "field1":65,
         "field2":12
      }
   }
]
```
which will handle each request and send back aggregated response.

Can be used to solve N+1 query problem and other underfetching problems. Can increase benefits of response compression. Can also be used to perform bulk operations in efficient way without adding separate route to handle it.

### Client-side
Provides classes to perform RPC requests and aggregated requests in type-save way with serialization and deserialization support.
e.g.
```kotlin
val resources : Response<List<Resoruce>>> = device.send(
    resourceApi.getAll()
)
```
`device` is object of class implementing `Device` interface provided in the library.

`resourceApi` is instance of class containing methods, that return proper `Request` objects, e.g.:
```kotlin
class ResourceApiClient {
    fun getAll(): Request<List<Resource>> = Request(
        path = "/rpc/ResourceApi/getAll",
        queryParameters = emptyMap(),
        body = emptyJson()
    )

    fun addResource(resource: Resource): Request<Boolean> = Request(
        path = "/rpc/ResourceApi/getAll",
        queryParameters = emptyMap(),
        body = jsonArrayOf(resource.toJson())
    )
}
```

Supports type-save use of "$fields" and "$include" parameters, e.g.:
```kotlin
data class ModifiedResource(
    val field1: String
)

fun ResourseApi.getAllModified() = resourceApi.getAll()
        .modify<List<ModifiedResource>>("\$fields" to listOf("field1"))

...

val resources : Response<List<ModifiedResource>> = device.send(
    resourceApi.getAllModified()
)
```

Supports type-save use of aggregated requests, e.g.:
```kotlin
val (resource1, resource2, otherResource1, otherResource2) = device.send(
    resourceApi.getOne("resource-id-1"),
    resourceApi.getOne("resource-id-2"),
    otherResourceApi.getOne("other-resource-id-1"),
    otherResourceApi.getOne("other-resource-id-2")
)
```

### Further work
- Deploy to maven repository
- Generate stubs of `*ApiClient` classes instead of making programmer writing them by hand.
- Support asynchronous handling of aggregated requests
- Support optimisation of handling bulk operations in aggregated requests
- Support "$filter" parameter
- Support for pagination and sorting
- Support for caching, especially in aggregated requests
- Support REST-style routes
