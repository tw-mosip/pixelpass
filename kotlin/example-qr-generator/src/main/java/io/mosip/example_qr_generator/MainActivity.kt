package io.mosip.example_qr_generator

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.ComponentActivity
import io.mosip.pixelpass.PixelPass
import io.mosip.pixelpass.types.QRConfig


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        val textF = findViewById<View>(R.id.textF) as EditText

        textF.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val bmp = PixelPass().generateQRBitmap(s.toString(), QRConfig())
                findViewById<ImageView>(R.id.qrImage).setImageBitmap(bmp)
                findViewById<ImageView>(R.id.qrImage).invalidate()
            }
        })
    }
}