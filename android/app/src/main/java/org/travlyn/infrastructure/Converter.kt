package org.travlyn.infrastructure

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken


var gson = GsonBuilder().create()

//convert a data class to a map
fun <T> T.serializeToMap(): Map<String, Any> {
    return convert()
}

//convert a map to a data class
inline fun <reified T> Map<String, Any>.toDataClass(): T {
    return convert()
}

//convert an object of type I to type O
inline fun <I, reified O> I.convert(): O {
    val json = gson.toJson(this)
    return gson.fromJson(json, object : TypeToken<O>() {}.type)
}

/**
 * Creates from a tree structure based on a map, a single map whose values are separated by '.'.
 * The respective test represents a good example.
 *
 * @return map
 */
fun <K, V> Map<K, V>.toQueryParameters(): Map<String, List<String>> {
    val map: MutableMap<String, List<String>> = mutableMapOf()
    this.forEach { entry ->
        when (entry.value) {
            is Map<*, *> -> {
                (entry.value as Map<*, *>).toQueryParameters().forEach { innerEntry ->
                    map[entry.key.toString() + "." + innerEntry.key] =
                        listOf(innerEntry.value.toString())
                }
            }
            is ArrayList<*> -> {
                var index = 0
                (entry.value as ArrayList<*>).forEach { listEntry ->
                    (listEntry as Map<*, *>).toQueryParameters().forEach { innerEntry ->
                        map[entry.key.toString() + "[" + index + "]." + innerEntry.key] =
                            listOf(innerEntry.value.toString())
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
