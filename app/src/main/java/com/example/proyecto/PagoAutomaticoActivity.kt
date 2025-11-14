package com.example.proyecto

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PagoAutomaticoActivity : AppCompatActivity() {

    private lateinit var tvInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pago_automatico)
        tvInfo = findViewById(R.id.tvInfo)

        // Solo mostrar mensaje de espera, no conectamos a IsoDep
        if (!packageManager.hasSystemFeature("android.hardware.nfc")) {
            Toast.makeText(this, "⚠️ Gailu honek ez du NFCrik.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        tvInfo.text = "💳 NFC ordainketaren zain...\nHurbildu POS terminala ordainketa jasotzeko."
    }
    fun mostrarPagoRecibido(comercio: String, importe: Double) {
        tvInfo.text = """ 
            ✅ Jasotako ordainketa 
            Merkataritza: $comercio 
            Zenbatekoa: %.2f € 
        """.trimIndent().format(importe)
    }
}