package io.mosip.pixelpass.utils

import io.mosip.pixelpass.utils.toListWithKeyAndValueMapper
import io.mosip.pixelpass.utils.toMapWithKeyAndValueMapper
import org.json.JSONArray
import org.json.JSONObject
import kotlin.test.*

class MapperUtilTest {

    // ========== toMapWithKeyAndValueMapper - Basic Tests ==========

    @Test
    fun testToMapWithKeyAndValueMapperBasicMapping() {
        val json = JSONObject()
        json.put("firstName", "John")
        json.put("lastName", "Doe")

        val keyMapper = mapOf(
            "firstname" to "first_name",
            "lastname" to "last_name"
        )

        val result = json.toMapWithKeyAndValueMapper(keyMapper = keyMapper)

        assertEquals("John", result["first_name"])
        assertEquals("Doe", result["last_name"])
    }

    @Test
    fun testToMapWithKeyAndValueMapperCaseInsensitiveKeys() {
        val json = JSONObject()
        json.put("NAME", "Alice")
        json.put("Age", "25")

        val keyMapper = mapOf(
            "name" to "user_name",
            "age" to "user_age"
        )

        val result = json.toMapWithKeyAndValueMapper(keyMapper = keyMapper)

        assertEquals("Alice", result["user_name"])
        assertEquals("25", result["user_age"])
    }

    @Test
    fun testToMapWithKeyAndValueMapperUnmappedKeys() {
        val json = JSONObject()
        json.put("firstName", "John")
        json.put("age", 30)

        val keyMapper = mapOf("firstname" to "first_name")

        val result = json.toMapWithKeyAndValueMapper(keyMapper = keyMapper)

        assertEquals("John", result["first_name"])
        assertEquals(30, result["age"]) // Unmapped key remains as is
    }

    @Test
    fun testToMapWithKeyAndValueMapperEmptyKeyMapper() {
        val json = JSONObject()
        json.put("name", "Test")
        json.put("value", 123)

        val result = json.toMapWithKeyAndValueMapper(keyMapper = emptyMap())

        assertEquals("Test", result["name"])
        assertEquals(123, result["value"])
    }

    // ========== toMapWithKeyAndValueMapper - Value Mapping Tests ==========

    @Test
    fun testToMapWithKeyAndValueMapperValueMapping() {
        val json = JSONObject()
        json.put("status", "active")
        json.put("role", "admin")

        val valueMapper: Map<String, Map<Any, Any>> = mapOf(
            "status" to mapOf("active" to 1, "inactive" to 0),
            "role" to mapOf("admin" to "A", "user" to "U")
        )

        val result = json.toMapWithKeyAndValueMapper(valueMapper = valueMapper)

        assertEquals(1, result["status"])
        assertEquals("A", result["role"])
    }

    @Test
    fun testToMapWithKeyAndValueMapperCaseInsensitiveValues() {
        val json = JSONObject()
        json.put("status", "ACTIVE")

        val valueMapper: Map<String, Map<Any, Any>> = mapOf(
            "status" to mapOf("active" to 1, "inactive" to 0)
        )

        val result = json.toMapWithKeyAndValueMapper(valueMapper = valueMapper)

        assertEquals(1, result["status"])
    }

    @Test
    fun testToMapWithKeyAndValueMapperUnmappedValues() {
        val json = JSONObject()
        json.put("status", "pending")

        val valueMapper: Map<String, Map<Any, Any>> = mapOf(
            "status" to mapOf("active" to 1, "inactive" to 0)
        )

        val result = json.toMapWithKeyAndValueMapper(valueMapper = valueMapper)

        assertEquals("pending", result["status"]) // Unmapped value remains as is
    }

    @Test
    fun testToMapWithKeyAndValueMapperNumericValues() {
        val json = JSONObject()
        json.put("status", 1)
        json.put("type", 2)

        val valueMapper: Map<String, Map<Any, Any>> = mapOf(
            "status" to mapOf(1 to "active", 0 to "inactive"),
            "type" to mapOf(1 to "A", 2 to "B")
        )

        val result = json.toMapWithKeyAndValueMapper(valueMapper = valueMapper)

        assertEquals("active", result["status"])
        assertEquals("B", result["type"])
    }

    @Test
    fun testToMapWithKeyAndValueMapperEmptyValueMapper() {
        val json = JSONObject()
        json.put("status", "active")
        json.put("count", 5)

        val result = json.toMapWithKeyAndValueMapper(valueMapper = emptyMap())

        assertEquals("active", result["status"])
        assertEquals(5, result["count"])
    }

    // ========== toMapWithKeyAndValueMapper - Complex Structures ==========

    @Test
    fun testToMapWithKeyAndValueMapperNestedObjects() {
        val nestedJson = JSONObject()
        nestedJson.put("city", "Bangalore")
        nestedJson.put("country", "India")

        val json = JSONObject()
        json.put("name", "John")
        json.put("address", nestedJson)

        val keyMapper = mapOf(
            "city" to "location_city",
            "country" to "location_country"
        )

        val result = json.toMapWithKeyAndValueMapper(keyMapper = keyMapper)

        val address = result["address"] as Map<*, *>
        assertEquals("Bangalore", address["location_city"])
        assertEquals("India", address["location_country"])
    }

    @Test
    fun testToMapWithKeyAndValueMapperNestedArrays() {
        val item1 = JSONObject()
        item1.put("id", 1)
        item1.put("name", "Item1")

        val item2 = JSONObject()
        item2.put("id", 2)
        item2.put("name", "Item2")

        val itemsArray = JSONArray()
        itemsArray.put(item1)
        itemsArray.put(item2)

        val json = JSONObject()
        json.put("items", itemsArray)

        val keyMapper = mapOf("name" to "item_name")

        val result = json.toMapWithKeyAndValueMapper(keyMapper = keyMapper)

        val items = result["items"] as List<*>
        assertEquals(2, items.size)
        val firstItem = items[0] as Map<*, *>
        assertEquals("Item1", firstItem["item_name"])
    }

    @Test
    fun testToMapWithKeyAndValueMapperDeepNesting() {
        val level3 = JSONObject()
        level3.put("value", "deepValue")

        val level2 = JSONObject()
        level2.put("nested", level3)

        val level1 = JSONObject()
        level1.put("data", level2)

        val keyMapper = mapOf("value" to "mapped_value")

        val result = level1.toMapWithKeyAndValueMapper(keyMapper = keyMapper)

        val data = result["data"] as Map<*, *>
        val nested = data["nested"] as Map<*, *>
        assertEquals("deepValue", nested["mapped_value"])
    }

    // ========== toMapWithKeyAndValueMapper - Special Values ==========

    @Test
    fun testToMapWithKeyAndValueMapperHandlesNull() {
        val json = JSONObject()
        json.put("field1", "value1")
        json.put("field2", JSONObject.NULL)

        val result = json.toMapWithKeyAndValueMapper()

        assertEquals("value1", result["field1"])
        assertNull(result["field2"])
    }

    @Test
    fun testToMapWithKeyAndValueMapperHandlesBoolean() {
        val json = JSONObject()
        json.put("active", true)
        json.put("deleted", false)

        val result = json.toMapWithKeyAndValueMapper()

        assertEquals(true, result["active"])
        assertEquals(false, result["deleted"])
    }

    @Test
    fun testToMapWithKeyAndValueMapperHandlesNumbers() {
        val json = JSONObject()
        json.put("int", 42)
        json.put("double", 3.14)
        json.put("long", 9876543210L)

        val result = json.toMapWithKeyAndValueMapper()

        assertEquals(42, result["int"])
        assertEquals(3.14, result["double"])
        assertEquals(9876543210L, result["long"])
    }

    // ========== toMapWithKeyAndValueMapper - Combined Mapping ==========

    @Test
    fun testToMapWithKeyAndValueMapperCombinedKeyAndValueMapping() {
        val json = JSONObject()
        json.put("userStatus", "active")
        json.put("userRole", "admin")

        val keyMapper = mapOf(
            "userstatus" to "status",
            "userrole" to "role"
        )

        val valueMapper: Map<String, Map<Any, Any>> = mapOf(
            "userstatus" to mapOf("active" to 1, "inactive" to 0),
            "userrole" to mapOf("admin" to "A", "user" to "U")
        )

        val result = json.toMapWithKeyAndValueMapper(keyMapper, valueMapper)

        assertEquals(1, result["status"])
        assertEquals("A", result["role"])
    }

    @Test
    fun testToMapWithKeyAndValueMapperComplexNestedMapping() {
        val addressJson = JSONObject()
        addressJson.put("cityName", "Bangalore")
        addressJson.put("countryCode", "IN")

        val json = JSONObject()
        json.put("userName", "Alice")
        json.put("userAddress", addressJson)

        val keyMapper = mapOf(
            "username" to "name",
            "useraddress" to "address",
            "cityname" to "city",
            "countrycode" to "country"
        )

        val valueMapper: Map<String, Map<Any, Any>> = mapOf(
            "countrycode" to mapOf("IN" to "India", "US" to "United States")
        )

        val result = json.toMapWithKeyAndValueMapper(keyMapper, valueMapper)

        assertEquals("Alice", result["name"])
        val address = result["address"] as Map<*, *>
        assertEquals("Bangalore", address["city"])
        assertEquals("India", address["country"])
    }

    // ========== JSONArray.toListWithKeyAndValueMapper Tests ==========

    @Test
    fun testToListWithKeyAndValueMapperBasic() {
        val obj1 = JSONObject()
        obj1.put("name", "Alice")

        val obj2 = JSONObject()
        obj2.put("name", "Bob")

        val jsonArray = JSONArray()
        jsonArray.put(obj1)
        jsonArray.put(obj2)

        val keyMapper = mapOf("name" to "user_name")

        val result = jsonArray.toListWithKeyAndValueMapper(keyMapper = keyMapper)

        assertEquals(2, result.size)
        val first = result[0] as Map<*, *>
        assertEquals("Alice", first["user_name"])
        val second = result[1] as Map<*, *>
        assertEquals("Bob", second["user_name"])
    }

    @Test
    fun testToListWithKeyAndValueMapperMixedTypes() {
        val jsonArray = JSONArray()
        jsonArray.put("string")
        jsonArray.put(123)
        jsonArray.put(true)
        jsonArray.put(JSONObject.NULL)

        val result = jsonArray.toListWithKeyAndValueMapper()

        assertEquals(4, result.size)
        assertEquals("string", result[0])
        assertEquals(123, result[1])
        assertEquals(true, result[2])
        assertNull(result[3])
    }

    @Test
    fun testToListWithKeyAndValueMapperNestedArrays() {
        val inner = JSONArray()
        inner.put(1)
        inner.put(2)
        inner.put(3)

        val outer = JSONArray()
        outer.put(inner)
        outer.put(4)

        val result = outer.toListWithKeyAndValueMapper()

        assertEquals(2, result.size)
        val innerResult = result[0] as List<*>
        assertEquals(3, innerResult.size)
        assertEquals(1, innerResult[0])
        assertEquals(2, innerResult[1])
        assertEquals(3, innerResult[2])
        assertEquals(4, result[1])
    }

    @Test
    fun testToListWithKeyAndValueMapperWithValueMapping() {
        val obj1 = JSONObject()
        obj1.put("status", "active")

        val obj2 = JSONObject()
        obj2.put("status", "inactive")

        val jsonArray = JSONArray()
        jsonArray.put(obj1)
        jsonArray.put(obj2)

        val valueMapper: Map<String, Map<Any, Any>> = mapOf(
            "status" to mapOf("active" to 1, "inactive" to 0)
        )

        val result = jsonArray.toListWithKeyAndValueMapper(valueMapper = valueMapper)

        assertEquals(2, result.size)
        val first = result[0] as Map<*, *>
        assertEquals(1, first["status"])
        val second = result[1] as Map<*, *>
        assertEquals(0, second["status"])
    }

    @Test
    fun testToListWithKeyAndValueMapperEmptyArray() {
        val jsonArray = JSONArray()

        val result = jsonArray.toListWithKeyAndValueMapper()

        assertEquals(0, result.size)
        assertTrue(result.isEmpty())
    }

    @Test
    fun testToListWithKeyAndValueMapperNestedObjects() {
        val inner = JSONObject()
        inner.put("id", 1)

        val outer = JSONObject()
        outer.put("data", inner)

        val jsonArray = JSONArray()
        jsonArray.put(outer)

        val keyMapper = mapOf("id" to "identifier")

        val result = jsonArray.toListWithKeyAndValueMapper(keyMapper = keyMapper)

        assertEquals(1, result.size)
        val outerMap = result[0] as Map<*, *>
        val innerMap = outerMap["data"] as Map<*, *>
        assertEquals(1, innerMap["identifier"])
    }

    // ========== Edge Cases ==========

    @Test
    fun testToMapWithKeyAndValueMapperEmptyJson() {
        val json = JSONObject()

        val result = json.toMapWithKeyAndValueMapper()

        assertTrue(result.isEmpty())
    }

    @Test
    fun testToMapWithKeyAndValueMapperEmptyNestedObject() {
        val emptyNested = JSONObject()
        
        val json = JSONObject()
        json.put("nested", emptyNested)

        val result = json.toMapWithKeyAndValueMapper()

        val nested = result["nested"] as Map<*, *>
        assertTrue(nested.isEmpty())
    }

    @Test
    fun testToMapWithKeyAndValueMapperEmptyNestedArray() {
        val emptyArray = JSONArray()
        
        val json = JSONObject()
        json.put("items", emptyArray)

        val result = json.toMapWithKeyAndValueMapper()

        val items = result["items"] as List<*>
        assertTrue(items.isEmpty())
    }
}