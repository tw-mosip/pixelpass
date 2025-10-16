package io.mosip.pixelpass.utils

import org.json.JSONArray
import org.json.JSONObject

fun JSONObject.toMapWithKeyAndValueMapper(
    keyMapper: Map<String, Any> = emptyMap(),
    valueMapper: Map<String, Map<Any, Any>> = emptyMap()
): Map<Any, Any?> {
    val map = mutableMapOf<Any, Any?>()
    val keys = this.keys()

    while (keys.hasNext()) {
        val originalKey: String = keys.next().toString()

        val newKey: Any = keyMapper[originalKey] ?: originalKey

        val originalValue = this.get(originalKey)

        val fieldValueMapper = valueMapper[originalKey]

        var processedValue: Any? = originalValue

        if (originalValue != JSONObject.NULL && fieldValueMapper != null) {
            processedValue = fieldValueMapper[originalValue] ?: originalValue
        }

        processedValue = when (processedValue) {
            is JSONObject -> processedValue.toMapWithKeyAndValueMapper(keyMapper, valueMapper)
            is JSONArray -> processedValue.toListWithKeyAndValueMapper(keyMapper, valueMapper)
            JSONObject.NULL -> null
            else -> processedValue
        }

        map[newKey] = processedValue
    }
    return map
}
fun JSONArray.toListWithKeyAndValueMapper(
    keyMapper: Map<String, Any> = emptyMap(),
    valueMapper: Map<String, Map<Any, Any>> = emptyMap()
): List<Any?> {
    val list = mutableListOf<Any?>()
    for (i in 0 until this.length()) {
        val processedValue: Any? = when (val value = this.get(i)) {
            is JSONObject -> value.toMapWithKeyAndValueMapper(keyMapper, valueMapper)
            is JSONArray -> value.toListWithKeyAndValueMapper(keyMapper, valueMapper)
            JSONObject.NULL -> null
            else -> value
        }
        list.add(processedValue)
    }
    return list
}