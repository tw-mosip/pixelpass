package io.mosip.qr_generator

import android.graphics.Bitmap
import android.util.Base64
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mosip.qr_generator.types.ECC
import io.mosip.qr_generator.types.ImageType
import org.junit.After
import org.junit.Assert
import org.junit.Test

class PixelPassQRTest {

    @After
    fun after(){
        clearAllMocks()
    }
    @Test
    fun `should encode data to a QR and return base64 encoded image data`() {
        mockkStatic(Bitmap::class)
        mockkStatic(Base64::class)
        val mockedBitmap: Bitmap = mockk()
        val testData = "test"
        val expected = "data:image/png;base64,test";

        every { Bitmap.createBitmap(any(), any(), Bitmap.Config.ARGB_8888) } returns mockedBitmap
        every { mockedBitmap.height } returns 10
        every { mockedBitmap.width } returns 10
        every { mockedBitmap.setPixel(any(), any(), any()) } just runs
        every { mockedBitmap.compress(any(), any(), any()) } returns true
        every { mockedBitmap.recycle() } just runs
        every { Base64.encode(any(), any()) } returns testData.toByteArray()
        val pixelPassQR = PixelPassQR(testData.toByteArray(), ECC.M)
        val actual = pixelPassQR.toBase64ImageData(ImageType.PNG)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `should encode data to a QR and return bitmap image`() {
        mockkStatic(Bitmap::class)
        val expected: Bitmap = mockk()

        every { Bitmap.createBitmap(any(), any(), Bitmap.Config.ARGB_8888) } returns expected
        every { expected.height } returns 10
        every { expected.width } returns 10
        every { expected.setPixel(any(), any(), any()) } just runs
        every { expected.compress(any(), any(), any()) } returns true
        val pixelPassQR = PixelPassQR("".toByteArray(), ECC.M)
        val actual = pixelPassQR.toBitmapImage()
        Assert.assertEquals(expected, actual)
    }
}