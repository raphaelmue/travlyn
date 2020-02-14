package org.travlyn.infrastructure

import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference

/**
 * Creates a map of the given data object. This map contains all properties that this data class has
 * defined and transfers their values.
 *
 * @return map of data object
 */
fun <T> T.toMap(): Map<String, Any> {
    return ObjectMapper().convertValue(this, object :
        TypeReference<Map<String, Any>>() {})
}

/**
 * Creates from a tree structure based on a map, a single map whose values are separated by '.'.
 * The respective test represents a good example.
 *
 * @return query parameters
 */
fun <K, V> Map<K, V>.toQueryParameters(): Map<String, List<String>> {
    val map: MutableMap<String, List<String>> = mutableMapOf()
    this.forEach { entry ->
        when (entry.value) {
            is Map<*, *> -> {
                (entry.value as Map<*, *>).toQueryParameters().forEach { innerEntry ->
                    map[entry.key.toString() + "." + innerEntry.key] =
                        listOf(innerEntry.value[0])
                }
            }
            is ArrayList<*> -> {
                var index = 0
                (entry.value as ArrayList<*>).forEach { listEntry ->
                    (listEntry as Map<*, *>).toQueryParameters().forEach { innerEntry ->
                        map[entry.key.toString() + "[" + index + "]." + innerEntry.key] =
                            listOf(innerEntry.value[0])
                    }
                    index++
                }
            }
            else -> {
                map[entry.key.toString()] = listOf(entry.value.toString())
            }
        }
    }
    return map
}
