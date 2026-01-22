package io.mosip.pixelpass

import io.mosip.pixelpass.exception.UnknownBinaryFileTypeException
import io.mosip.pixelpass.types.ECC
import org.json.JSONArray
import org.json.JSONObject
import kotlin.test.*

@IgnoreOnAndroid
class PixelPassTest {

    private lateinit var pixelPass: PixelPass

    @BeforeTest
    fun setUp() {
        pixelPass = PixelPass()
    }

    // ========== generateQRCode Tests ==========

    @Test
    fun testGenerateQRCodeWithDefaultParams() {
        val data = """{"name":"Test","value":123}"""
        
        val result = pixelPass.generateQRCode(data)
        
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testGenerateQRCodeWithLowECC() {
        val data = """{"name":"Test"}"""
        
        val result = pixelPass.generateQRCode(data, ECC.L)
        
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testGenerateQRCodeWithMediumECC() {
        val data = """{"name":"Test"}"""
        
        val result = pixelPass.generateQRCode(data, ECC.M)
        
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testGenerateQRCodeWithQuartileECC() {
        val data = """{"name":"Test"}"""
        
        val result = pixelPass.generateQRCode(data, ECC.Q)
        
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testGenerateQRCodeWithHighECC() {
        val data = """{"name":"Test"}"""
        
        val result = pixelPass.generateQRCode(data, ECC.H)
        
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testGenerateQRCodeWithHeader() {
        val data = """{"name":"Test"}"""
        val header = "HC1:"
        
        val result = pixelPass.generateQRCode(data, header = header)
        
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testGenerateQRCodeWithCustomHeader() {
        val data = """{"id":12345}"""
        val header = "CUSTOM_PREFIX:"
        
        val result = pixelPass.generateQRCode(data, ECC.M, header)
        
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testGenerateQRCodeWithEmptyHeader() {
        val data = """{"test":"value"}"""
        
        val result = pixelPass.generateQRCode(data, ECC.L, "")
        
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    // ========== decode Tests ==========

    @Test
    fun testDecodeValidJsonObject() {
        val originalData = """{"name":"John","age":30}"""
        val qrData = pixelPass.generateQRData(originalData)
        
        val decoded = pixelPass.decode(qrData)
        
        assertNotNull(decoded)
        assertTrue(decoded.contains("name"))
        assertTrue(decoded.contains("John"))
        assertTrue(decoded.contains("age"))
    }

    @Test
    fun testDecodeValidJsonArray() {
        val originalData = """[{"id":1,"name":"Alice"},{"id":2,"name":"Bob"}]"""
        val qrData = pixelPass.generateQRData(originalData)
        
        val decoded = pixelPass.decode(qrData)
        
        assertNotNull(decoded)
        assertTrue(decoded.startsWith("["))
        assertTrue(decoded.endsWith("]"))
        assertTrue(decoded.contains("Alice"))
        assertTrue(decoded.contains("Bob"))
    }

    @Test
    fun testDecodeComplexObject() {
        val originalData = """{"user":{"name":"Alice","details":{"age":25,"city":"Bangalore"}}}"""
        val qrData = pixelPass.generateQRData(originalData)
        
        val decoded = pixelPass.decode(qrData)
        
        assertNotNull(decoded)
        assertTrue(decoded.contains("user"))
        assertTrue(decoded.contains("Alice"))
        assertTrue(decoded.contains("Bangalore"))
    }

    @Test
    fun testDecodePlainText() {
        val originalData = "Simple plain text message"
        val qrData = pixelPass.generateQRData(originalData)
        
        val decoded = pixelPass.decode(qrData)
        
        assertNotNull(decoded)
        assertEquals(originalData, decoded)
    }

    @Test
    fun testDecodeWithSpecialCharacters() {
        val originalData = """{"message":"Hello @#$%^&*()"}"""
        val qrData = pixelPass.generateQRData(originalData)
        
        val decoded = pixelPass.decode(qrData)
        
        assertNotNull(decoded)
        assertTrue(decoded.contains("Hello"))
    }

    // ========== generateQRData Tests ==========

    @Test
    fun testGenerateQRDataWithJsonObject() {
        val data = """{"name":"Test","value":123,"active":true}"""
        
        val result = pixelPass.generateQRData(data)
        
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testGenerateQRDataWithJsonArray() {
        val data = """[{"id":1},{"id":2},{"id":3}]"""
        
        val result = pixelPass.generateQRData(data)
        
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testGenerateQRDataWithHeader() {
        val data = """{"name":"Test"}"""
        val header = "PREFIX:"
        
        val result = pixelPass.generateQRData(data, header)
        
        assertTrue(result.startsWith(header))
    }

    @Test
    fun testGenerateQRDataWithEmptyHeader() {
        val data = """{"test":"value"}"""
        
        val result = pixelPass.generateQRData(data, "")
        
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testGenerateQRDataWithPlainText() {
        val data = "Plain text data without JSON structure"
        
        val result = pixelPass.generateQRData(data)
        
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testGenerateQRDataWithEmptyString() {
        val data = ""
        
        val result = pixelPass.generateQRData(data)
        
        assertNotNull(result)
    }

    @Test
    fun testGenerateQRDataWithLargeJson() {
        val largeJson = buildString {
            append("{")
            for (i in 1..100) {
                append("\"field$i\":\"value$i\"")
                if (i < 100) append(",")
            }
            append("}")
        }
        
        val result = pixelPass.generateQRData(largeJson)
        
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    // ========== getMappedData Tests (String mapper) ==========

    @Test
    fun testGetMappedDataWithStringMapperBasic() {
        val json = JSONObject()
        json.put("firstName", "John")
        json.put("lastName", "Doe")

        val mapper = mapOf(
            "firstName" to "first_name",
            "lastName" to "last_name"
        )

        val result = pixelPass.getMappedData(json, mapper, cborEnable = false)

        assertNotNull(result)
        assertTrue(result.contains("first_name"))
        assertTrue(result.contains("last_name"))
        assertFalse(result.contains("firstName"))
        assertFalse(result.contains("lastName"))
    }

    @Test
    fun testGetMappedDataWithStringMapperCborEnabled() {
        val json = JSONObject()
        json.put("name", "Test")
        json.put("value", 42)

        val mapper = mapOf("name" to "user_name")

        val result = pixelPass.getMappedData(json, mapper, cborEnable = true)

        assertNotNull(result)
        assertTrue(result is String)
        assertTrue((result as String).isNotEmpty())
    }

    @Test
    fun testGetMappedDataWithStringMapperNoMapping() {
        val json = JSONObject()
        json.put("field1", "value1")
        json.put("field2", "value2")

        val mapper = mapOf("otherField" to "mapped_field")

        val result = pixelPass.getMappedData(json, mapper, cborEnable = false)

        assertNotNull(result)
        assertTrue(result.contains("field1"))
        assertTrue(result.contains("field2"))
    }

    @Test
    fun testGetMappedDataWithStringMapperEmptyMapper() {
        val json = JSONObject()
        json.put("key", "value")

        val mapper = emptyMap<String, String>()

        val result = pixelPass.getMappedData(json, mapper, cborEnable = false)

        assertNotNull(result)
        assertTrue(result.contains("key"))
    }

    // ========== getMappedData Tests (Int mapper - JSONObject) ==========

    @Test
    fun testGetMappedDataWithIntMapperBasic() {
        val json = JSONObject()
        json.put("name", "John")
        json.put("age", 30)

        val keyMapper = mapOf("name" to 1, "age" to 2)
        val valueMapper = emptyMap<String, Map<Any, Int>>()

        val result = pixelPass.getMappedData(json, keyMapper, valueMapper, false)

        assertNotNull(result)
        assertTrue(result is Map<*, *>)
        val resultMap = result as Map<*, *>
        assertTrue(resultMap.containsKey(1))
        assertTrue(resultMap.containsKey(2))
        assertEquals("John", resultMap[1])
        assertEquals(30, resultMap[2])
    }

    @Test
    fun testGetMappedDataWithIntMapperCborEnabled() {
        val json = JSONObject()
        json.put("name", "Test")
        json.put("id", 123)

        val keyMapper = mapOf("name" to 1, "id" to 2)
        val valueMapper = emptyMap<String, Map<Any, Int>>()

        val result = pixelPass.getMappedData(json, keyMapper, valueMapper, true)

        assertNotNull(result)
        assertTrue(result is String)
        assertTrue((result as String).isNotEmpty())
    }

    @Test
    fun testGetMappedDataWithIntMapperAndValueMapper() {
        val json = JSONObject()
        json.put("status", "active")
        json.put("role", "admin")

        val keyMapper = mapOf("status" to 1, "role" to 2)
        val valueMapper: Map<String, Map<Any, Int>> = mapOf(
            "status" to mapOf("active" to 1, "inactive" to 0),
            "role" to mapOf("admin" to 10, "user" to 20)
        )

        val result = pixelPass.getMappedData(json, keyMapper, valueMapper, false)

        assertNotNull(result)
        val resultMap = result as Map<*, *>
        assertEquals(1, resultMap[1])
        assertEquals(10, resultMap[2])
    }

    @Test
    fun testGetMappedDataWithIntMapperComplexValues() {
        val json = JSONObject()
        json.put("count", 100)
        json.put("price", 99.99)
        json.put("active", true)

        val keyMapper = mapOf("count" to 1, "price" to 2, "active" to 3)
        val valueMapper = emptyMap<String, Map<Any, Int>>()

        val result = pixelPass.getMappedData(json, keyMapper, valueMapper, false)

        val resultMap = result as Map<*, *>
        assertEquals(100, resultMap[1])
        assertEquals(99.99, resultMap[2])
        assertEquals(true, resultMap[3])
    }

    // ========== getMappedData Tests (JSONArray) ==========

    @Test
    fun testGetMappedDataWithJsonArrayBasic() {
        val obj1 = JSONObject()
        obj1.put("name", "Alice")
        obj1.put("age", 25)

        val obj2 = JSONObject()
        obj2.put("name", "Bob")
        obj2.put("age", 30)

        val jsonArray = JSONArray()
        jsonArray.put(obj1)
        jsonArray.put(obj2)

        val keyMapper = mapOf("name" to 1, "age" to 2)
        val valueMapper = emptyMap<String, Map<Any, Int>>()

        val result = pixelPass.getMappedData(jsonArray, keyMapper, valueMapper, false)

        assertEquals(2, result.length())
    }

    @Test
    fun testGetMappedDataWithJsonArrayCborEnabled() {
        val obj1 = JSONObject()
        obj1.put("id", 1)

        val jsonArray = JSONArray()
        jsonArray.put(obj1)

        val keyMapper = mapOf("id" to 1)
        val valueMapper = emptyMap<String, Map<Any, Int>>()

        val result = pixelPass.getMappedData(jsonArray, keyMapper, valueMapper, true)

        assertEquals(1, result.length())
    }

    @Test
    fun testGetMappedDataWithJsonArrayEmptyArray() {
        val jsonArray = JSONArray()

        val keyMapper = emptyMap<String, Int>()
        val valueMapper = emptyMap<String, Map<Any, Int>>()

        val result = pixelPass.getMappedData(jsonArray, keyMapper, valueMapper, false)

        assertEquals(0, result.length())
    }

    @Test
    fun testGetMappedDataWithJsonArrayInvalidItem() {
        val jsonArray = JSONArray()
        jsonArray.put("string value")

        val keyMapper = emptyMap<String, Int>()
        val valueMapper = emptyMap<String, Map<Any, Int>>()

        assertFailsWith<IllegalArgumentException> {
            pixelPass.getMappedData(jsonArray, keyMapper, valueMapper, false)
        }
    }

    @Test
    fun testGetMappedDataWithJsonArrayMixedInvalidItems() {
        val obj1 = JSONObject()
        obj1.put("id", 1)

        val jsonArray = JSONArray()
        jsonArray.put(obj1)
        jsonArray.put(123) // Invalid: not a JSONObject

        val keyMapper = mapOf("id" to 1)
        val valueMapper = emptyMap<String, Map<Any, Int>>()

        assertFailsWith<IllegalArgumentException> {
            pixelPass.getMappedData(jsonArray, keyMapper, valueMapper, false)
        }
    }

    // ========== decodeMappedData Tests (String mapper) ==========

    @Test
    fun testDecodeMappedDataWithStringMapperBasic() {
        val json = JSONObject()
        json.put("first_name", "John")
        json.put("last_name", "Doe")

        val mapper = mapOf(
            "first_name" to "firstName",
            "last_name" to "lastName"
        )

        val encodedData = json.toString()

        val result = pixelPass.decodeMappedData(encodedData, mapper)

        val decoded = JSONObject(result)
        assertEquals("John", decoded.getString("firstName"))
        assertEquals("Doe", decoded.getString("lastName"))
    }

    @Test
    fun testDecodeMappedDataWithStringMapperNoMapping() {
        val json = JSONObject()
        json.put("field1", "value1")
        json.put("field2", "value2")

        val mapper = mapOf("otherField" to "mappedField")

        val encodedData = json.toString()

        val result = pixelPass.decodeMappedData(encodedData, mapper)

        val decoded = JSONObject(result)
        assertTrue(decoded.has("field1"))
        assertTrue(decoded.has("field2"))
    }

    @Test
    fun testDecodeMappedDataWithStringMapperEmptyMapper() {
        val json = JSONObject()
        json.put("key", "value")

        val mapper = emptyMap<String, String>()

        val encodedData = json.toString()

        val result = pixelPass.decodeMappedData(encodedData, mapper)

        val decoded = JSONObject(result)
        assertEquals("value", decoded.getString("key"))
    }

    // ========== decodeMappedData Tests (Array mapper) ==========

    @Test
    fun testDecodeMappedDataWithArrayMapperSingleDepth() {
        val json = JSONObject()
        json.put("1", "value1")
        json.put("2", "value2")

        val keyMapper = arrayOf(
            mapOf("1" to "key1", "2" to "key2")
        )

        val encodedData = json.toString()

        val result = pixelPass.decodeMappedData(encodedData, keyMapper)

        val decoded = JSONObject(result)
        assertTrue(decoded.has("key1"))
        assertTrue(decoded.has("key2"))
        assertEquals("value1", decoded.getString("key1"))
        assertEquals("value2", decoded.getString("key2"))
    }

    @Test
    fun testDecodeMappedDataWithArrayMapperMultipleDepths() {
        val nested = JSONObject()
        nested.put("b", "nestedValue")

        val json = JSONObject()
        json.put("a", nested)

        val keyMapper = arrayOf(
            mapOf("a" to "A"),
            mapOf("b" to "B")
        )

        val encodedData = json.toString()

        val result = pixelPass.decodeMappedData(encodedData, keyMapper)

        val decoded = JSONObject(result)
        assertTrue(decoded.has("A"))
        val nestedDecoded = decoded.getJSONObject("A")
        assertTrue(nestedDecoded.has("B"))
        assertEquals("nestedValue", nestedDecoded.getString("B"))
    }

    @Test
    fun testDecodeMappedDataWithArrayMapperDeepNesting() {
        val level3 = JSONObject()
        level3.put("c", "deep")

        val level2 = JSONObject()
        level2.put("b", level3)

        val level1 = JSONObject()
        level1.put("a", level2)

        val keyMapper = arrayOf(
            mapOf("a" to "A"),
            mapOf("b" to "B"),
            mapOf("c" to "C")
        )

        val encodedData = level1.toString()

        val result = pixelPass.decodeMappedData(encodedData, keyMapper)

        val decoded = JSONObject(result)
        val l2 = decoded.getJSONObject("A")
        val l3 = l2.getJSONObject("B")
        assertEquals("deep", l3.getString("C"))
    }

    @Test
    fun testDecodeMappedDataWithArrayMapperEmptyMapper() {
        val json = JSONObject()
        json.put("key", "value")

        val keyMapper = arrayOf<Map<String, String>>()

        val encodedData = json.toString()

        val result = pixelPass.decodeMappedData(encodedData, keyMapper)

        val decoded = JSONObject(result)
        assertEquals("value", decoded.getString("key"))
    }

    // ========== decodeMappedData Tests (Array of strings) ==========

    @Test
    fun testDecodeMappedDataArrayOfStringsBasic() {
        val json1 = JSONObject()
        json1.put("a", "value1")

        val json2 = JSONObject()
        json2.put("b", "value2")

        val dataArray = arrayOf(json1.toString(), json2.toString())

        val keyMapper = arrayOf(
            mapOf("a" to "A", "b" to "B")
        )

        val result = pixelPass.decodeMappedData(dataArray, keyMapper)

        assertEquals(2, result.size)
        val decoded1 = JSONObject(result[0])
        val decoded2 = JSONObject(result[1])
        assertTrue(decoded1.has("A"))
        assertTrue(decoded2.has("B"))
    }

    @Test
    fun testDecodeMappedDataArrayOfStringsEmptyArray() {
        val dataArray = arrayOf<String>()

        val keyMapper = arrayOf(mapOf("a" to "A"))

        val result = pixelPass.decodeMappedData(dataArray, keyMapper)

        assertEquals(0, result.size)
    }

    @Test
    fun testDecodeMappedDataArrayOfStringsSingleItem() {
        val json = JSONObject()
        json.put("key", "value")

        val dataArray = arrayOf(json.toString())

        val keyMapper = arrayOf(mapOf("key" to "mappedKey"))

        val result = pixelPass.decodeMappedData(dataArray, keyMapper)

        assertEquals(1, result.size)
        val decoded = JSONObject(result[0])
        assertEquals("value", decoded.getString("mappedKey"))
    }

    // ========== decodeBinary Tests ==========

    @Test
    fun testDecodeBinaryInvalidData() {
        val invalidData = "Invalid binary data".toByteArray()

        assertFailsWith<UnknownBinaryFileTypeException> {
            pixelPass.decodeBinary(invalidData)
        }
    }

    // ========== Integration Tests ==========

    @Test
    fun testEncodeDecodeRoundTripSimple() {
        val originalData = """{"name":"John","age":30,"active":true}"""
        
        val qrData = pixelPass.generateQRData(originalData)
        val decoded = pixelPass.decode(qrData)
        
        val decodedJson = JSONObject(decoded)
        assertEquals("John", decodedJson.getString("name"))
        assertEquals(30, decodedJson.getInt("age"))
        assertTrue(decodedJson.getBoolean("active"))
    }

    @Test
    fun testEncodeDecodeRoundTripArray() {
        val originalData = """[{"id":1,"name":"Alice"},{"id":2,"name":"Bob"}]"""
        
        val qrData = pixelPass.generateQRData(originalData)
        val decoded = pixelPass.decode(qrData)
        
        val decodedArray = JSONArray(decoded)
        assertEquals(2, decodedArray.length())
        assertEquals("Alice", decodedArray.getJSONObject(0).getString("name"))
        assertEquals("Bob", decodedArray.getJSONObject(1).getString("name"))
    }

    @Test
    fun testMapEncodeDecodeRoundTrip() {
        val json = JSONObject()
        json.put("firstName", "Alice")
        json.put("age", 25)

        val encodeMapper = mapOf(
            "firstName" to "first_name",
            "age" to "user_age"
        )

        val decodeMapper = mapOf(
            "first_name" to "firstName",
            "user_age" to "age"
        )

        val encoded = pixelPass.getMappedData(json, encodeMapper, false)
        val decoded = pixelPass.decodeMappedData(encoded, decodeMapper)

        val decodedJson = JSONObject(decoded)
        assertEquals("Alice", decodedJson.getString("firstName"))
        assertEquals(25, decodedJson.getInt("age"))
    }

    @Test
    fun testComplexMappingRoundTrip() {
        val json = JSONObject()
        json.put("userStatus", "active")
        json.put("userName", "TestUser")

        val encodeKeyMapper = mapOf("userStatus" to 1, "userName" to 2)
        val encodeValueMapper: Map<String, Map<Any, Int>> = mapOf(
            "userStatus" to mapOf("active" to 100, "inactive" to 200)
        )

        val decodeKeyMapper = arrayOf(
            mapOf("1" to "userStatus", "2" to "userName")
        )

        val encoded = pixelPass.getMappedData(json, encodeKeyMapper, encodeValueMapper, false)
        
        assertNotNull(encoded)
        assertTrue(encoded is Map<*, *>)
    }

    @Test
    fun testGenerateQRCodeAndDecodeIntegration() {
        val originalData = """{"user":"test","timestamp":1234567890}"""
        
        val qrImage = pixelPass.generateQRCode(originalData, ECC.M, "APP:")
        
        assertNotNull(qrImage)
        assertTrue(qrImage.isNotEmpty())
    }
}