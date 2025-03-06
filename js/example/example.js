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
    let qrData = generateQRData(req.body.cwt);
    let version = QRCode.create(qrData, {errorCorrectionLevel : DEFAULT_ECC_LEVEL}).version
    QRCode.toDataURL(qrData,opts).then(qr => res.send([version,qr]))
})

app.post('/convert', (req, res) => {
    let json = req.body;
    console.log("DATA RECEIVED : ", json)
    const faceMap  = new Map();
    faceMap.set(0,json.face.data);
    faceMap.set(1,0);
    faceMap.set(2,4);
    json["face"] = faceMap;
    console.log("DATA AFTER : ", json)
    const claim169MappedData = getMappedData(json)
    res.send([claim169MappedData.toString('hex'),claim169MappedData.length])
})
