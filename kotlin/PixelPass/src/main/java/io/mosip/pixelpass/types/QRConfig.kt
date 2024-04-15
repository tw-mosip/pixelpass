package io.mosip.pixelpass.types

data class QRConfig(val ecc: ECC = ECC.M, val imageType: ImageType = ImageType.PNG, val header: String = "")
