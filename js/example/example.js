const open = require('open');
const express = require('express')
const path = require('path');
const {generateQRCode} = require('../src')

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

app.post('/qr', (req, res) => {
    let json = req.body
    console.log("JSON RECEIVED : ", json)
    generateQRCode(JSON.stringify(json)).then(qr=> res.send(qr))
})

app.listen(port, () => {
    console.log(`Example app listening on port ${port}`)
    open('http://localhost:3000');
})