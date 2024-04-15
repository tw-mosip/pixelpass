package io.mosip.qr_generator

import android.graphics.Bitmap
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mosip.pixelpass.PixelPass
import io.mosip.pixelpass.PixelPassQR
import io.mosip.pixelpass.types.ECC
import io.mosip.pixelpass.types.ImageType
import io.mosip.pixelpass.types.QRConfig
import io.mosip.pixelpass.zlib.ZLib
import org.junit.After
import org.junit.Assert
import org.junit.Test

class PixelPassTest{

    @After
    fun after(){
        clearAllMocks()
    }
    @Test
    fun `should return base64 encoded QR for given data`(){
        mockkConstructor(ZLib::class)
        mockkConstructor(PixelPassQR::class)

        val mockedEncoded = byteArrayOf(1,2,3,4)
        every { anyConstructed<ZLib>().encode(any(),any()) } returns mockedEncoded
        every { anyConstructed<PixelPassQR>().toBase64ImageData(any()) } returns "data:image/png;base64,abc"

        val data = "test"
        val expected = "data:image/png;base64,abc";
        val config = QRConfig(ECC.M,ImageType.PNG)
        val actual = PixelPass().generateQRBase64Image(data,config)
        Assert.assertNotNull(actual)
        Assert.assertEquals(expected,actual)
    }

    @Test
    fun `should return bitmap QR for given data`(){
        mockkConstructor(ZLib::class)
        mockkConstructor(PixelPassQR::class)

        val mockedEncoded = byteArrayOf(1,2,3,4)
        val expected = mockk<Bitmap>()
        every { anyConstructed<ZLib>().encode(any(),any()) } returns mockedEncoded
        every { anyConstructed<PixelPassQR>().toBitmapImage() } returns expected

        val data = "test"
        val actual = PixelPass().generateQRBitmap(data, QRConfig(ECC.M))
        Assert.assertNotNull(actual)
        Assert.assertEquals(expected,actual)
    }
    @Test
    fun `should return encoded data for given data without header`(){
        mockkConstructor(ZLib::class)
        mockkConstructor(PixelPassQR::class)

        val expected = "NCFKVPV0QSIP600GP5L0"

        val data = "hello"
        val actual = PixelPass().generateQRData(data, QRConfig())
        Assert.assertNotNull(actual)
        Assert.assertEquals(expected,actual)
    }

    @Test
    fun `should return encoded data for given data with given header`(){
        mockkConstructor(ZLib::class)
        mockkConstructor(PixelPassQR::class)

        val expected = "HEADER://NCFKVPV0QSIP600GP5L0"

        val data = "hello"
        val header = "HEADER://"
        val actual = PixelPass().generateQRData(data, QRConfig(header = header))
        Assert.assertNotNull(actual)
        Assert.assertEquals(expected,actual)
    }

    @Test
    fun `should return decoded data for given QR data`(){
        val data = "NCFKVPV0QSIP600GP5L0"
        val expected = "hello"

        val actual = PixelPass().decodeData(data)
        Assert.assertEquals(expected,actual)
    }
}