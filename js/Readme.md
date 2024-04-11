# PixelPass

## Features

- Create QR Code for given data
- Uses zlib compression and base45 encoding
- Decode QR data encoded by PixelPass

## Installation 
`npm i @mosip/pixelpass`

[npm](https://www.npmjs.com/package/@mosip/pixelpass)

## APIs
`generateQRData( data, header )`

`data` - Data needs to be compressed and encoded

`header` - Data header need to be prepened to identtify the encoded data. defaults to `""`

returns a zlib compressed and base45 encoded string with header prepended if provided.

`generateQRBase64Image( data, ecc , header )`

`data` - Data needs to be compressed and encoded

`ecc` - Erro Correction Level for the QR generated. defaults to `"M"`

`header` - Data header need to be prepened to identtify the encoded data. defaults to `""`

returns a base64 encoded PNG image with header prepended if provided.

`decodeData(base45Data)`

`base45Data` - Data needs to be decoded and decompressed without header

returns a base45 decoded and zlib decompressed string
## License
MIT
