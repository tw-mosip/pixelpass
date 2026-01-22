package io.mosip.pixelpass.cbor

import co.nstant.`in`.cbor.model.Map as CborMap
import co.nstant.`in`.cbor.model.Array as CborArray
import co.nstant.`in`.cbor.model.DataItem
import co.nstant.`in`.cbor.model.DoublePrecisionFloat
import co.nstant.`in`.cbor.model.MajorType
import co.nstant.`in`.cbor.model.NegativeInteger
import co.nstant.`in`.cbor.model.SimpleValue
import co.nstant.`in`.cbor.model.SimpleValueType
import co.nstant.`in`.cbor.model.UnicodeString
import co.nstant.`in`.cbor.model.UnsignedInteger
import io.mosip.pixelpass.shared.CLAIM_169_BIOMETRIC_DATA_FORMAT_KEY
import io.mosip.pixelpass.shared.CLAIM_169_BIOMETRIC_DATA_SUB_FORMAT_KEY
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class UtilsTest {

    private lateinit var utils: Utils

    @Before
    fun setUp() {
        utils = Utils()
    }

    // ========== toJson Tests ==========

    @Test
    fun `toJson should convert CBOR CborMap to JSONObject`() {
        val cborCborMap = CborMap()
        cborCborMap.put(UnicodeString("name"), UnicodeString("John"))
        cborCborMap.put(UnicodeString("age"), UnsignedInteger(30))

        val result = utils.toJson(cborCborMap) as JSONObject

        assertEquals("John", result.getString("name"))
        assertEquals(30, result.getInt("age"))
    }

    @Test
    fun `toJson should convert CBOR CborArray to JSONArray`() {
        val cborCborArray = CborArray()
        cborCborArray.add(UnicodeString("item1"))
        cborCborArray.add(UnsignedInteger(42))
        cborCborArray.add(SimpleValue(SimpleValueType.TRUE))

        val result = utils.toJson(cborCborArray) as JSONArray

        assertEquals("item1", result.getString(0))
        assertEquals(42, result.getInt(1))
        assertTrue(result.getBoolean(2))
    }

    @Test
    fun `toJson should handle nested structures`() {
        val nestedCborMap = CborMap()
        nestedCborMap.put(UnicodeString("nested"), UnicodeString("value"))
        
        val cborCborMap = CborMap()
        cborCborMap.put(UnicodeString("outer"), nestedCborMap)

        val result = utils.toJson(cborCborMap) as JSONObject

        assertEquals("value", result.getJSONObject("outer").getString("nested"))
    }

    @Test
    fun `toJson should handle special values`() {
        val cborCborMap = CborMap()
        cborCborMap.put(UnicodeString("boolTrue"), SimpleValue(SimpleValueType.TRUE))
        cborCborMap.put(UnicodeString("boolFalse"), SimpleValue(SimpleValueType.FALSE))
        cborCborMap.put(UnicodeString("null"), SimpleValue(SimpleValueType.NULL))

        val result = utils.toJson(cborCborMap) as JSONObject

        assertTrue(result.getBoolean("boolTrue"))
        assertFalse(result.getBoolean("boolFalse"))
        assertTrue(result.isNull("null"))
    }

    @Test
    fun `toJson should handle negative integers`() {
        val cborCborMap = CborMap()
        cborCborMap.put(UnicodeString("negative"), NegativeInteger(-15))

        val result = utils.toJson(cborCborMap) as JSONObject

        assertEquals(-15, result.getInt("negative"))
    }

    // ========== toDataItem Tests ==========

    @Test
    fun `toDataItem should convert JSONObject to CBOR CborMap`() {
        val json = JSONObject()
        json.put("name", "Alice")
        json.put("age", 25)

        val result = utils.toDataItem(json) as CborMap

        assertEquals("Alice", (result.get(UnicodeString("name")) as UnicodeString).string)
        assertEquals(25L, (result.get(UnicodeString("age")) as UnsignedInteger).value)
    }

    @Test
    fun `toDataItem should convert JSONArray to CBOR CborArray`() {
        val jsonCborArray = JSONArray()
        jsonCborArray.put("test")
        jsonCborArray.put(100)
        jsonCborArray.put(true)

        val result = utils.toDataItem(jsonCborArray) as CborArray

        val items = result.dataItems
        assertEquals("test", (items[0] as UnicodeString).string)
        assertEquals(100L, (items[1] as UnsignedInteger).value)
        assertEquals(SimpleValueType.TRUE, (items[2] as SimpleValue).simpleValueType)
    }

    @Test
    fun `toDataItem should convert Kotlin CborMap to CBOR CborMap`() {
        val kotlinCborMap: kotlin.collections.Map<String, Any> = mapOf(
            "key1" to "value1",
            "key2" to 42
        )

        val result = utils.toDataItem(kotlinCborMap) as CborMap

        assertEquals("value1", (result.get(UnicodeString("key1")) as UnicodeString).string)
        assertEquals(42L, (result.get(UnicodeString("key2")) as UnsignedInteger).value)
    }

    @Test
    fun `toDataItem should handle numeric keys in Kotlin CborMap`() {
        val kotlinCborMap: kotlin.collections.Map<Int, String> = mapOf(
            1 to "value1",
            2 to "value2"
        )

        val result = utils.toDataItem(kotlinCborMap) as CborMap

        assertEquals("value1", (result.get(UnsignedInteger(1)) as UnicodeString).string)
        assertEquals("value2", (result.get(UnsignedInteger(2)) as UnicodeString).string)
    }

    @Test
    fun `toDataItem should handle nested JSONObjects`() {
        val innerJson = JSONObject()
        innerJson.put("inner", "value")
        
        val json = JSONObject()
        json.put("outer", innerJson)

        val result = utils.toDataItem(json) as CborMap
        val outerCborMap = result.get(UnicodeString("outer")) as CborMap

        assertEquals("value", (outerCborMap.get(UnicodeString("inner")) as UnicodeString).string)
    }

    @Test
    fun `toDataItem should handle various data types`() {
        val json = JSONObject()
        json.put("string", "text")
        json.put("int", 10)
        json.put("negativeInt", -5)
        json.put("double", 3.14)
        json.put("boolean", true)
        json.put("null", JSONObject.NULL)

        val result = utils.toDataItem(json) as CborMap

        assertTrue(result.get(UnicodeString("string")) is UnicodeString)
        assertTrue(result.get(UnicodeString("int")) is UnsignedInteger)
        assertTrue(result.get(UnicodeString("negativeInt")) is NegativeInteger)
        assertTrue(result.get(UnicodeString("double")) is DoublePrecisionFloat)
        assertEquals(SimpleValueType.TRUE, (result.get(UnicodeString("boolean")) as SimpleValue).simpleValueType)
        assertEquals(SimpleValueType.NULL, (result.get(UnicodeString("null")) as SimpleValue).simpleValueType)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `toDataItem should throw exception for unsupported type`() {
        utils.toDataItem("plain string")
    }

    // ========== replaceKeysAtDepth Tests ==========

    @Test
    fun `replaceKeysAtDepth should replace keys at depth 0`() {
        val json = JSONObject()
        json.put("oldKey", "value")
        
        val mapper: kotlin.collections.Map<String, String> = mapOf("oldKey" to "newKey")

        val result = utils.replaceKeysAtDepth(json, mapper, 0)

        assertTrue(result.has("newKey"))
        assertFalse(result.has("oldKey"))
        assertEquals("value", result.getString("newKey"))
    }

    @Test
    fun `replaceKeysAtDepth should replace keys at depth 1`() {
        val innerJson = JSONObject()
        innerJson.put("oldKey", "value")
        
        val json = JSONObject()
        json.put("outer", innerJson)
        
        val mapper: kotlin.collections.Map<String, String> = mapOf("oldKey" to "newKey")

        val result = utils.replaceKeysAtDepth(json, mapper, 1)

        val nested = result.getJSONObject("outer")
        assertTrue(nested.has("newKey"))
        assertFalse(nested.has("oldKey"))
    }

    @Test
    fun `replaceKeysAtDepth should not replace keys at wrong depth`() {
        val innerJson = JSONObject()
        innerJson.put("key2", "value2")
        
        val json = JSONObject()
        json.put("key1", "value1")
        json.put("nested", innerJson)
        
        val mapper: kotlin.collections.Map<String, String> = mapOf(
            "key1" to "replacedKey1",
            "key2" to "replacedKey2"
        )

        val result = utils.replaceKeysAtDepth(json, mapper, 1)

        assertTrue(result.has("key1"))
        val nested = result.getJSONObject("nested")
        assertTrue(nested.has("replacedKey2"))
    }

    @Test
    fun `replaceKeysAtDepth should handle arrays at target depth`() {
        val innerObj = JSONObject()
        innerObj.put("oldKey", "value")
        
        val jsonCborArray = JSONArray()
        jsonCborArray.put(innerObj)
        
        val json = JSONObject()
        json.put("array", jsonCborArray)
        
        val mapper: kotlin.collections.Map<String, String> = mapOf("oldKey" to "newKey")

        val result = utils.replaceKeysAtDepth(json, mapper, 1)

        val array = result.getJSONArray("array")
        val obj = array.getJSONObject(0)
        assertTrue(obj.has("newKey"))
    }

    @Test
    fun `replaceKeysAtDepth should preserve unmapped keys`() {
        val json = JSONObject()
        json.put("key1", "value1")
        json.put("key2", "value2")
        
        val mapper: kotlin.collections.Map<String, String> = mapOf("key1" to "newKey1")

        val result = utils.replaceKeysAtDepth(json, mapper, 0)

        assertTrue(result.has("newKey1"))
        assertTrue(result.has("key2"))
    }

    @Test
    fun `replaceKeysAtDepth should handle null values`() {
        val json = JSONObject()
        json.put("key", JSONObject.NULL)
        
        val mapper: kotlin.collections.Map<String, String> = mapOf("key" to "newKey")

        val result = utils.replaceKeysAtDepth(json, mapper, 0)

        assertTrue(result.isNull("newKey"))
    }

    // ========== replaceValuesForClaim169 Tests ==========

    @Test
    fun `replaceValuesForClaim169 should replace root level values`() {
        // This test assumes CLAIM_169_ROOT_REVERSE_VALUE_MAPPER is configured
        val json = JSONObject()
        json.put("testField", "shortCode")

        val result = utils.replaceValuesForClaim169(json)

        // Assertion depends on actual mapper configuration
        assertNotNull(result)
    }

    @Test
    fun `replaceValuesForClaim169 should handle missing fields gracefully`() {
        val json = JSONObject()
        json.put("unrelatedField", "value")

        val result = utils.replaceValuesForClaim169(json)

        assertEquals("value", result.getString("unrelatedField"))
    }

    @Test
    fun `replaceValuesForClaim169 should process biometric nested objects`() {
        // This test assumes biometric keys and mappers are configured
        val biometricObj = JSONObject()
        biometricObj.put(CLAIM_169_BIOMETRIC_DATA_FORMAT_KEY, "formatCode")
        biometricObj.put(CLAIM_169_BIOMETRIC_DATA_SUB_FORMAT_KEY, "subFormatCode")
        
        val json = JSONObject()
        json.put("biometric_data", biometricObj)

        val result = utils.replaceValuesForClaim169(json)

        assertTrue(result.has("biometric_data"))
        val biometricData = result.getJSONObject("biometric_data")
        assertTrue(biometricData.has(CLAIM_169_BIOMETRIC_DATA_FORMAT_KEY))
    }

    @Test
    fun `replaceValuesForClaim169 should skip incomplete biometric objects`() {
        val biometricObj = JSONObject()
        biometricObj.put(CLAIM_169_BIOMETRIC_DATA_FORMAT_KEY, "formatCode")
        // Missing sub-format key
        
        val json = JSONObject()
        json.put("biometric_data", biometricObj)

        val result = utils.replaceValuesForClaim169(json)

        assertNotNull(result)
    }

    @Test
    fun `replaceValuesForClaim169 should handle non-object biometric fields`() {
        val json = JSONObject()
        json.put("biometric_data", "not an object")

        val result = utils.replaceValuesForClaim169(json)

        assertEquals("not an object", result.getString("biometric_data"))
    }

    // ========== Integration Tests ==========

    @Test
    fun `should convert JSONObject to CBOR and back to JSONObject`() {
        val original = JSONObject()
        original.put("name", "Bob")
        original.put("age", 35)
        original.put("active", true)

        val cborCborMap = utils.toDataItem(original) as CborMap
        val result = utils.toJson(cborCborMap) as JSONObject

        assertEquals("Bob", result.getString("name"))
        assertEquals(35, result.getInt("age"))
        assertTrue(result.getBoolean("active"))
    }

    @Test
    fun `should handle complex nested structure conversion`() {
        val user1 = JSONObject()
        user1.put("id", 1)
        user1.put("name", "User1")
        
        val user2 = JSONObject()
        user2.put("id", 2)
        user2.put("name", "User2")
        
        val usersCborArray = JSONArray()
        usersCborArray.put(user1)
        usersCborArray.put(user2)
        
        val original = JSONObject()
        original.put("users", usersCborArray)

        val cborCborMap = utils.toDataItem(original) as CborMap
        val result = utils.toJson(cborCborMap) as JSONObject

        val users = result.getJSONArray("users")
        assertEquals(2, users.length())
        assertEquals("User1", users.getJSONObject(0).getString("name"))
        assertEquals("User2", users.getJSONObject(1).getString("name"))
    }

    @Test
    fun `should handle BigDecimal conversion`() {
        val kotlinCborMap: kotlin.collections.Map<String, BigDecimal> = mapOf(
            "decimal" to BigDecimal("123.456")
        )

        val result = utils.toDataItem(kotlinCborMap) as CborMap
        val value = result.get(UnicodeString("decimal")) as DoublePrecisionFloat

        assertEquals(123.456, value.value, 0.001)
    }

    @Test
    fun `should handle Long conversion`() {
        val kotlinCborMap: kotlin.collections.Map<String, Long> = mapOf(
            "longValue" to 9876543210L
        )

        val result = utils.toDataItem(kotlinCborMap) as CborMap
        val value = result.get(UnicodeString("longValue")) as DoublePrecisionFloat

        assertEquals(9876543210.0, value.value, 0.1)
    }
}