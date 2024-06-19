const {
  DEFAULT_QR_QUALITY,
  DEFAULT_QR_BORDER,
  DEFAULT_QR_SCALE,
  COLOR_BLACK,
  COLOR_WHITE,
  DEFAULT_ZLIB_COMPRESSION_LEVEL,
  DEFAULT_ECC_LEVEL,
  CLAIM_169,
} = require("./shared/Constants");
const QRCode = require("qrcode");
const b45 = require("base45-web");
const pako = require("pako");
const cbor = require("cbor-web");
const cose = require("cose-js");

function generateQRData(data, header = "") {
  const compressedData = pako.deflate(data, {
    level: DEFAULT_ZLIB_COMPRESSION_LEVEL,
  });
  return header + b45.encode(compressedData).toString();
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
      light: COLOR_WHITE,
    },
  };
  return QRCode.toDataURL(base45Data, opts);
}

function decode(data) {
  const decodedBase45Data = b45.decode(data);
  const compressedData = pako.inflate(decodedBase45Data);
  const textData = new TextDecoder().decode(compressedData);
  try {
    const decodedCBORData = cbor.decode(textData);
    if (decodedCBORData) return JSON.stringify(decodedCBORData);
    return textData;
  } catch (e) {
    return textData;
  }
}

async function getCWT(jsonData, claimMap, encryptionKey, algorithm) {
  const payload = new Map();
  for (const param in jsonData) {
    const key = claimMap[param] ? claimMap[param] : param;
    const value = jsonData[param];
    payload.set(key, value);
  }

  const claim169 = new Map();
  claim169.set(CLAIM_169, payload);

  const cborEncodedData = cbor.encode(claim169);

  const headers = {
    p: {alg: algorithm},
    u: {kid: encryptionKey.kid},
  };
  const recipent = {
    key: encryptionKey.k,
  };

  return (await cose.mac.create(headers, cborEncodedData, recipent)).toString("hex");
}

module.exports = {
  generateQRData,
  generateQRCode,
  decode,
  getCWT,
};
