const open = require('open');
const express = require('express')
const path = require('path');
const { generateQRData, getMappedData,getSignedCwt} = require('../src')
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

app.post('/qr', async (req, res) => {
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

    let qrData = await generateQRData(hexStringToArrayBuffer(req.body.cwt));
    let version = QRCode.create(qrData[1], {errorCorrectionLevel : DEFAULT_ECC_LEVEL}).version
    QRCode.toDataURL(qrData[1],opts).then(qr => res.send([version,qrData[1].length,qr,qrData[0].toString().replaceAll(`h'`,`\nh'`)]))
})
function hexStringToArrayBuffer(hexString) {
    hexString = hexString.replace(/^0x/, '');
    if (hexString.length % 2 != 0) {
        console.log('WARNING: expecting an even number of characters in the hexString');
    }
    const bad = hexString.match(/[G-Z\s]/i);
    if (bad) {
        console.log('WARNING: found non-hex characters', bad);
    }
    const pairs = hexString.match(/[\dA-F]{2}/gi);
    const integers = pairs.map(function (s) {
        return parseInt(s, 16);
    });
    const array = new Uint8Array(integers);
    console.log(array);
    return array.buffer;
}
app.post('/convert', async (req, res) => {
    let json = req.body.claims;
    console.log(json)
    if(json.face) {
        const faceMap = new Map();
        faceMap.set(0, json.face);
        json["face"] = faceMap;
    }
    console.log("DATA RECEIVED CONVERT: ", json)
    const claim169MappedData = getMappedData(json)
    console.log(claim169MappedData)
    const cwt = await getSignedCwt(claim169MappedData);
    res.send(cwt)
})
