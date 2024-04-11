package io.mosip.qr_generator.types

import android.graphics.Bitmap

enum class ImageType(val type: Bitmap.CompressFormat, val prefix: String) {
    PNG(Bitmap.CompressFormat.PNG,"data:image/png;base64,")
}