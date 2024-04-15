package io.mosip.pixelpass

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Base64
import android.util.Base64.DEFAULT
import io.mosip.pixelpass.shared.QR_BORDER
import io.mosip.pixelpass.shared.QR_QUALITY
import io.mosip.pixelpass.shared.QR_SCALE
import io.mosip.pixelpass.types.ECC
import io.mosip.pixelpass.types.ImageType
import io.nayuki.qrcodegen.*
import java.io.ByteArrayOutputStream
import java.util.Objects

class PixelPassQR(private val base45Data: ByteArray, private val ecc: ECC) {

    fun toBase64ImageData(compressFormat: ImageType): String {
        val qrCode = QrCode.encodeText(String(base45Data), ecc.mEcc)

        val qrBitmap = toBitmap(qrCode)
        val stream = ByteArrayOutputStream()
        qrBitmap.compress(compressFormat.type, QR_QUALITY, stream)
        val byteArray = stream.toByteArray()

        val base64ImageBytes = String(Base64.encode(byteArray,DEFAULT))
        val base64ImageData = compressFormat.prefix + base64ImageBytes
        qrBitmap.recycle()
        return  base64ImageData
    }

    fun toBitmapImage(): Bitmap {
        val qrCode = QrCode.encodeText(String(base45Data), ecc.mEcc)
        return toBitmap(qrCode)
    }


    private fun toBitmap(qrCode: QrCode): Bitmap {
        Objects.requireNonNull(qrCode)
        require(!(QR_SCALE <= 0 || QR_BORDER < 0)) { "Value out of range" }
        require(!(QR_BORDER > Int.MAX_VALUE / 2 || qrCode.size + QR_BORDER * 2L > Int.MAX_VALUE / QR_SCALE)) { "Scale or border too large" }
        val result = Bitmap.createBitmap(
            (qrCode.size + QR_BORDER * 2) * QR_SCALE,
            (qrCode.size + QR_BORDER * 2) * QR_SCALE,
            Bitmap.Config.ARGB_8888
        )
        for (y in 0 until result.getHeight()) {
            for (x in 0 until result.getWidth()) {
                val color = qrCode.getModule(x / QR_SCALE - QR_BORDER, y / QR_SCALE - QR_BORDER)
                result.setPixel(x, y, if (color) Color.BLACK else Color.WHITE)
            }
        }
        return result
    }
}
