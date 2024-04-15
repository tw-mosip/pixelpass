# PixelPass

## Features

- Create QR Code for given data
- Uses zlib compression and base45 encoding
- Decode QR data encoded by PixelPass

## Installation

todo :: add maven link

## APIs
`generateQRData( data, config )`

`data` - Data needs to be compressed and encoded

`config` - configuration to be used to generate QR Code. see [QRConfig](#qrconfig)

returns a zlib compressed and base45 encoded string with header prepended if provided.

`generateQRBase64Image( data, config )`

`data` - Data needs to be compressed and encoded

`config` - configuration to be used to generate QR Code. see [QRConfig](/#QRConfig)

returns a base64 encoded PNG image with header prepended if provided.

`generateQRBitmap( data, config )`

`data` - Data needs to be compressed and encoded

`config` - configuration to be used to generate QR Code. see [QRConfig](/#QRConfig)

returns a Bitmap image with header prepended if provided.

`decodeData(base45Data)`

`base45Data` - Data needs to be decoded and decompressed without header

returns a base45 decoded and zlib decompressed string

## QRConfig

`ecc` - Error Correction Level for the QR generated. defaults to `"M"`

`imageType` - Output image type Level for the QR generated. defaults to `"PNG"`

`header` - Data header need to be prepened to identtify the encoded data. defaults to `""`

## License
MIT