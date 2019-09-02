package kuick.api.core.parameters.include

import com.google.gson.JsonElement
import kuick.api.core.Node
import kuick.api.core.applyToEachObject
import kuick.api.core.clazz
import kuick.api.core.emptyNode
import kuick.api.core.splitBy
import kuick.json.Json.gson
import kotlin.reflect.jvm.reflect

typealias IncludeConfiguration = Map<Class<out Any?>, Map<String, suspend (id: String) -> Any>>

/* Representation of include parameter. Include parameter should be defined according to following rules:
 * 1) Should contain only field names that exist in requested model
 * 2) If contains a nested field of related resource, related resource should also be included
 * 3) Should only contain fields for which include operation is supported (configured)
 */
data class IncludeParam private constructor(
    val root: Node<String>
) {

    companion object {
        fun <T : Any?> create(
            root: Node<String>,
            responseClass: Class<T>,
            configuration: IncludeConfiguration
        ): IncludeParam {
            val result = validateIncludeParamNode(root, responseClass, configuration)
            if (result.isNotEmpty()) throw InvalidIncludeParamException.Composite(result)
            return IncludeParam(root)
        }
    }
}

suspend fun JsonElement.includeRelatedResources(
    includeParam: IncludeParam,
    configuration: IncludeConfiguration,
    clazz: Class<out Any>
) =
    includeRelatedResources(includeParam.root, configuration, clazz)

private suspend fun JsonElement.includeRelatedResources(
    includeParam: Node<String>,
    configuration: IncludeConfiguration,
    clazz: Class<out Any>
) {
    configuration[clazz]?.let { config ->
        val fieldsToInclude = includeParam.children
            .mapNotNull { it.value }
            .toSet()

        applyToEachObject { jsonObject ->
            jsonObject.entrySet().toList().forEach { field ->
                if (field.key in fieldsToInclude) {
                    config[field.key]?.let { method ->
                        jsonObject.remove(field.key)

                        val relatedResource = method(field.value.asString)
                        val relatedResourceJson = gson.toJsonTree(relatedResource)

                        includeParam.children.find { it.value == field.key }?.let {
                            relatedResourceJson.includeRelatedResources(it, configuration, relatedResource.javaClass)
                        }

                        jsonObject.add(
                            field.key,
                            relatedResourceJson
                        )
                    }
                }
            }
        }
    }
}

private fun <T : Any?> validateIncludeParamNode(
    node: Node<String>,
    relatedClass: Class<T>,
    configuration: IncludeConfiguration
): List<InvalidIncludeParamException> {
    val config = configuration[relatedClass] ?: emptyMap()

    val result = mutableListOf<InvalidIncludeParamException>()

    val nodeFieldNames = node.children
        .mapNotNull { it.value }
        .filter { it != "" }
    val relatedClassFields = relatedClass.declaredFields.toSet()
    val relatedClassFieldNames = relatedClassFields
        .map { it.name }
        .toSet()

    val notMatchingFields = nodeFieldNames
        .filter { !relatedClassFieldNames.contains(it) }
    if (notMatchingFields.isNotEmpty()) {
        result.add(InvalidIncludeParamException.NotExistingFields(notMatchingFields))
    }

    val notConfiguredFields = nodeFieldNames.filter { !config.keys.contains(it) }
    if (notConfiguredFields.isNotEmpty()) {
        result.add(InvalidIncludeParamException.NotSupportedByApi(notConfiguredFields))
    }

    val (nodesToInclude, nodesWithoutProperDefinition) = node.children
        .splitBy { it.children.contains(Node.emptyNode()) }
        .let {
            Pair(it.first, it.second.filter { it != Node.emptyNode() })
        }

    if (nodesWithoutProperDefinition.isNotEmpty()) { // TODO provide option to ignore this case
        result.add(InvalidIncludeParamException.OrphanFields(nodesWithoutProperDefinition.mapNotNull { it.value }))
    }

    node.children.forEach { child ->
        if (child.value != "" && child.value != null) {
            config[child.value]?.reflect()?.returnType?.clazz?.java?.let { childClass ->
                result.addAll(validateIncludeParamNode(child, childClass, configuration))
            }
        }
    }

    return result
}
