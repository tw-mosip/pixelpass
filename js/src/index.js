const {
    DEFAULT_QR_QUALITY,
    DEFAULT_QR_BORDER,
    DEFAULT_QR_SCALE,
    COLOR_BLACK,
    COLOR_WHITE,
    DEFAULT_ZLIB_COMPRESSION_LEVEL,
    DEFAULT_ECC_LEVEL
} = require('./shared/Constants');
const QRCode = require('qrcode');
const b45 = require("base45-web");
const pako = require('pako');
const cbor = require('cbor-web');

function generateQRData(data, header = "") {
    let parsedData = null;
    let compressedData, b45EncodedData;
    try {
        parsedData = JSON.parse(data);
        const cborEncodedData = cbor.encode(parsedData);
        compressedData = pako.deflate(cborEncodedData, {level: DEFAULT_ZLIB_COMPRESSION_LEVEL});
    } catch (e) {
        console.log("Data is not JSON");
        compressedData = pako.deflate(data, {level: DEFAULT_ZLIB_COMPRESSION_LEVEL});
    } finally {
        b45EncodedData = b45.encode(compressedData).toString();
    }
    return header + b45EncodedData;
}

async function generateQRCode(data, ecc = DEFAULT_ECC_LEVEL, header = "") {
    const base45Data = generateQRData(data, header);
    const opts = {
        errorCorrectionLevel: ecc,
        quality: DEFAULT_QR_QUALITY,
        margin: DEFAULT_QR_BORDER,
        scale: DEFAULT_QR_SCALE,
        color: {
            dark: COLOR_BLACK,
            light: COLOR_WHITE
        }
    };
    return QRCode.toDataURL(base45Data, opts);
}

function decode(data) {
    const decodedBase45Data = b45.decode(data);
    const decompressedData = pako.inflate(decodedBase45Data);
    const textData = new TextDecoder().decode(decompressedData);
    try {
        const decodedCBORData = cbor.decode(decompressedData);
        if (decodedCBORData) return JSON.stringify(decodedCBORData);
        return textData;
    } catch (e) {
        return textData;
    }
}

module.exports = {
    generateQRData,
    generateQRCode,
    decode
};
