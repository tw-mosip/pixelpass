const ECC = require("../types/ECC").ECC

exports.DEFAULT_ZLIB_COMPRESSION_LEVEL = 9
exports.DEFAULT_ECC_LEVEL = ECC.L
exports.COLOR_BLACK = "#000000"
exports.COLOR_WHITE = "#FFFFFF"
exports.DEFAULT_QR_SCALE = 10
exports.DEFAULT_QR_BORDER = 3
exports.DEFAULT_QR_QUALITY = 1
exports.ZIP_HEADER = "PK"
exports.DEFAULT_ZIP_FILE_NAME = "certificate.json"
exports.CLAIM_169_MAP =
    new Map([
        ["id", 1],
        ["version", 2],
        ["language", 3],
        ["fullName", 4],
        ["firstName", 5],
        ["middleName", 6],
        ["lastName", 7],
        ["dob", 8],
        ["gender", 9],
        ["address", 10],
        ["email", 11],
        ["phone", 12],
        ["nationality", 13],
        ["maritalStatus", 14],
        ["guardian", 15],
        ["binaryImage", 16],
        ["binaryImageFormat", 17],
        ["bestQualityFingers", 18],
        ["rightThumb", 50],
        ["rightPointerFinger", 51],
        ["rightMiddleFinger", 52],
        ["rightRingFinger", 53],
        ["rightLittleFinger", 54],
        ["leftThumb", 55],
        ["leftPointerFinger", 56],
        ["leftMiddleFinger", 57],
        ["leftRingFinger", 58],
        ["leftLittleFinger", 59],
        ["rightIris", 60],
        ["leftIris", 61],
        ["face", 62],
        ["rightPalmPrint", 63],
        ["leftPalmPrint", 64],
        ["voice", 65]
    ]);
