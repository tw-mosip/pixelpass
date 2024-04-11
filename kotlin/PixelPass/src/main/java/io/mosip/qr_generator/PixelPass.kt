package io.mosip.qr_generator

import android.graphics.Bitmap
import io.mosip.qr_generator.types.ECC
import io.mosip.qr_generator.types.QRConfig
import io.mosip.qr_generator.zlib.ZLib
import nl.minvws.encoding.Base45


class PixelPass {
    fun generateQRBase64(data: String, config: QRConfig): String {
        val compressedData = ZLib().encode(data.toByteArray())
        val base45Data = Base45.getEncoder().encode(compressedData)
        return PixelPassQR(base45Data,config.ecc).toBase64ImageData(config.imageType)
    }

    fun generateQRBitmap(data: String, ecc: ECC): Bitmap {
        val compressedData = ZLib().encode(data.toByteArray())
        val base45Data = Base45.getEncoder().encode(compressedData)
        return PixelPassQR(base45Data,ecc).toBitmapImage()
    }

    fun decodeData(base45Data: String): String {
        val decodedBase45Data = Base45.getDecoder().decode(base45Data)
        val decompressedData = ZLib().decode(decodedBase45Data)
        return String(decompressedData)
    }
}