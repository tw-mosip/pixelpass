package io.mosip.pixelpass

import android.graphics.Bitmap
import io.mosip.pixelpass.types.QRConfig
import io.mosip.pixelpass.zlib.ZLib
import nl.minvws.encoding.Base45

class PixelPass {
    fun generateQRBase64Image(data: String, config: QRConfig): String {
        val dataWithHeader = getDataBytes(data, config.header)
        return PixelPassQR(dataWithHeader,config.ecc).toBase64ImageData(config.imageType)
    }

    fun generateQRBitmap(data: String, config: QRConfig): Bitmap {
        val dataWithHeader = getDataBytes(data, config.header)
        return PixelPassQR(dataWithHeader,config.ecc).toBitmapImage()
    }
    fun generateQRData(data: String, config: QRConfig): String {
        return String(getDataBytes(data, config.header))
    }

    fun decodeData(base45Data: String): String {
        val decodedBase45Data = Base45.getDecoder().decode(base45Data)
        val decompressedData = ZLib().decode(decodedBase45Data)
        return String(decompressedData)
    }

    private fun getDataBytes(
        data: String,
        header: String
    ): ByteArray {
        val compressedData = ZLib().encode(data.toByteArray())
        val base45Data = String(Base45.getEncoder().encode(compressedData))
        return (header + base45Data).toByteArray()
    }
}