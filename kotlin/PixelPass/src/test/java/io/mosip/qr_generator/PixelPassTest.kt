package io.mosip.qr_generator

import android.graphics.Bitmap
import android.util.Log
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.runs
import io.mosip.pixelpass.PixelPass
import io.mosip.pixelpass.types.ECC
import io.mosip.pixelpass.zlib.ZLib
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class   PixelPassTest {

    @Before
    fun before(){
        mockkStatic(Log::class)
        every { Log.e(any(),any()) } returns 0
    }
    @After
    fun after() {
        clearAllMocks()
    }

    @Test
    fun `should return bitmap QR for given data`() {
        mockkConstructor(ZLib::class)
        mockkStatic(Bitmap::class)

        val mockedEncoded = byteArrayOf(1, 2, 3, 4)
        val expected = mockk<Bitmap>()
        every { anyConstructed<ZLib>().encode(any(), any()) } returns mockedEncoded
        every { Bitmap.createBitmap(any(), any(), any()) } returns expected
        every { expected.height } returns 10
        every { expected.width } returns 10
        every { expected.setPixel(any(), any(), any()) } just runs


        val data = "test"
        val actual = PixelPass().generateQRCode(data, ECC.M, "")
        Assert.assertNotNull(actual)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `should return decoded data for given QR data`() {
        val data = "NCFKVPV0QSIP600GP5L0"
        val expected = "hello"

        val actual = PixelPass().decode(data)
        Assert.assertEquals(expected, actual)
    }
    @Test
    fun `should return decoded data for given QR data in cbor`() {
        val data = "NCF3QBXJA5NJRCOC004 QN4"
        val expected = "{\"temp\":15}"

        val actual = PixelPass().decode(data)
        Assert.assertEquals(expected, actual)
    }
    @Test
    fun `should return encoded QR data for given data with CBOR`() {
        val data = "{\"str\":\"stringtype\",\"intP\":10,\"intN\":-10,\"intL\":111111110,\"intLN\":111111110,\"float\":10.01,\"nulltype\":null,\"bool\":true,\"bool2\":false,\"arryE\":[],\"arryF\":[1,2,3,-4,\"hello\",{\"temp\":123}],\"objE\":{},\"objS\":{\"str\":\"stringtype\"}}"
        val expected  = "NCF6QB2NJXTAGPTV30I-R.431DJENA2JA-NEO:2RZI.3TL69%5L+2T+BTR\$9M PHQUKSIEUJ4\$F W0XQ08LA-NEYJ25/FTELJTPC31L.R-PI+YQXDPV0Q0C5-Q5S2W5OIJWIQZNOLN*XKRK1OP65QQ-NKQVB%/JX1M%9IF+8U48+SB000Z2WWS7"

        val actual = PixelPass().generateQRData(data)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `should return decoded JSON data for given QR data with CBOR`() {
        val expected = "{\"arryE\":[],\"arryF\":[1,2,3,-4,\"hello\",{\"temp\":123}],\"bool\":true,\"intLN\":111111110,\"intL\":111111110,\"float\":10.01,\"intN\":-10,\"nulltype\":null,\"objS\":{\"str\":\"stringtype\"},\"str\":\"stringtype\",\"intP\":10,\"bool2\":false,\"objE\":{}}"
        val data  = "NCF6QB2NJXTAGPTV30I-R.431DJENA2JA-NEO:2RZI.3TL69%5L+2T+BTR\$9M PHQUKSIEUJ4\$F W0XQ08LA-NEYJ25/FTELJTPC31L.R-PI+YQXDPV0Q0C5-Q5S2W5OIJWIQZNOLN*XKRK1OP65QQ-NKQVB%/JX1M%9IF+8U48+SB000Z2WWS7"

        val actual = PixelPass().decode(data)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `encode in js decode in kotlin`() {
        val expected = "{\"arryE\":[],\"arryF\":[1,2,3,-4,\"hello\",{\"temp\":123}],\"bool\":true,\"intLN\":111111110,\"intL\":111111110,\"float\":10.01,\"intN\":-10,\"nulltype\":null,\"objS\":{\"str\":\"stringtype\"},\"str\":\"stringtype\",\"intP\":10,\"bool2\":false,\"objE\":{}}"
        val data  = "NCF6QBJUBZJA W04IJFLTY\$IFHL4IJNU44TBJQQRJ2\$SVMLM:8QP/I2NC7D8RDDQOVXY4%V3WABH-EF3OU0Q8O5MIP.HDQ1JMZI.9K:V6JR8X\$F1Y9WH5FWE%109/D6XH1+P:GLVHL E7JJ1 H9LOEQS4PRAAUI+SBSCGCHSU7D00089AWS7"

        val actual = PixelPass().decode(data)
        Assert.assertEquals(expected, actual)
    }
}


