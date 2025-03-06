const open = require('open');
const express = require('express')
const path = require('path');
const { generateQRData, getMappedData} = require('../src')
const QRCode = require("qrcode");
const {
    DEFAULT_QR_QUALITY,
    DEFAULT_QR_BORDER,
    DEFAULT_QR_SCALE,
    COLOR_BLACK,
    COLOR_WHITE, DEFAULT_ECC_LEVEL
} = require("../src/shared/Constants");

const app = express()
const port = 3000

app.use(express.json())
app.get('/', (req, res) => {
    const options = {
        root: path.join(__dirname)
    };
    res.sendFile("index.html", options)
})

app.get('/styles.css', (req, res) => {
    const options = {
        root: path.join(__dirname)
    };
    res.sendFile("stylesheet.css", options)
})

app.listen(port, () => {
    console.log(`Example app listening on port ${port}`)
    open('http://localhost:3000');
})

app.post('/qr', (req, res) => {
    console.log("DATA RECEIVED QR: ", req.body)
    const opts = {
        errorCorrectionLevel: DEFAULT_ECC_LEVEL,
        quality: DEFAULT_QR_QUALITY,
        margin: DEFAULT_QR_BORDER,
        scale: DEFAULT_QR_SCALE,
        color: {
            dark: COLOR_BLACK,
            light: COLOR_WHITE
        }
    }
    let qrData = generateQRData(hexStringToArrayBuffer(req.body.cwt));
    let version = QRCode.create(qrData, {errorCorrectionLevel : DEFAULT_ECC_LEVEL}).version
    QRCode.toDataURL(qrData,opts).then(qr => res.send([version,qr]))
})
function hexStringToArrayBuffer(hexString) {
    hexString = hexString.replace(/^0x/, '');
    if (hexString.length % 2 != 0) {
        console.log('WARNING: expecting an even number of characters in the hexString');
    }
    var bad = hexString.match(/[G-Z\s]/i);
    if (bad) {
        console.log('WARNING: found non-hex characters', bad);
    }
    var pairs = hexString.match(/[\dA-F]{2}/gi);
    var integers = pairs.map(function(s) {
        return parseInt(s, 16);
    });
    var array = new Uint8Array(integers);
    console.log(array);
    return array.buffer;
}
app.post('/convert', (req, res) => {
    let json = req.body;
    const faceMap  = new Map();
    faceMap.set(0,json.face.data);
    faceMap.set(1,0);
    faceMap.set(2,4);
    json["face"] = faceMap;
    console.log("DATA RECEIVED CONVERT: ", json)
    const claim169MappedData = getMappedData(json)
    res.send(claim169MappedData.toString('hex'))
})
