const express = require('express')
const bodyParser = require('body-parser')
const path = require('path');
const {generateQRCode} = require("../src/PixelPass");
const app = express()
const port = 3000

app.use(bodyParser.text({type: "*/*"}));
app.get('/', (req, res) => {
    const options = {
        root: path.join(__dirname)
    };
    res.sendFile("index.html", options)
})

app.post('/qr', (req, res) => {
    let json = req.body
    // res.send(json)
    res.send(generateQRCode(json))
})

app.listen(port, () => {
    console.log(`Example app listening on port ${port}`)
})