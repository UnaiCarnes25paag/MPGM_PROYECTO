package com.example.proyecto

import android.content.Intent
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import com.example.proyecto.data.BankDb
import data.entities.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class MyBankHceService : HostApduService() {

    companion object {
        var ultimoComercio: String = ""
        var ultimoImporte: Double = 0.0
        var ultimoTipo: String = ""

        private const val AID = "F0010203040506"
        private val STATUS_SUCCESS = byteArrayOf(0x90.toByte(), 0x00)
        private val STATUS_FAILED = byteArrayOf(0x6A.toByte(), 0x82.toByte())

        private fun hexStringToByteArray(s: String): ByteArray {
            val len = s.length
            val data = ByteArray(len / 2)
            var i = 0
            while (i < len) {
                data[i / 2] =
                    ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
                i += 2
            }
            return data
        }

        val SELECT_APDU: ByteArray = run {
            val aidBytes = hexStringToByteArray(AID)
            byteArrayOf(
                0x00, 0xA4.toByte(), 0x04, 0x00, (AID.length / 2).toByte()
            ) + aidBytes + byteArrayOf(0x00)
        }

        fun mapearCategoria(comercio: String): String {
            return when (comercio) {
                in listOf("Eroski", "Mercadona", "Lidl", "Carrefour Market", "McDonald’s", "Burger King") -> "Janaria"
                in listOf("Netflix", "Spotify", "Disney+", "Steam", "Cine Yelmo", "Decathlon") -> "Aisia"
                in listOf("Zara", "H&M", "Amazon", "MediaMarkt", "FNAC") -> "Erosketak"
                in listOf("IKEA", "Leroy Merlin", "Endesa", "Iberdrola") -> "Etxea"
                in listOf("Repsol", "Cepsa", "Uber", "Renfe", "Cabify") -> "Garraioa"
                in listOf("Osakidetza", "Farmacia Central", "Vodafone", "Movistar") -> "Beharrak"
                else -> "Besteak"
            }
        }
    }

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        if (commandApdu == null) return STATUS_FAILED

        Log.d("HCE", "APDU jaso: ${commandApdu.joinToString(" ") { String.format("%02X", it) }}")

        // --- SELECT AID ---
        if (commandApdu.size >= 5 &&
            commandApdu[1] == 0xA4.toByte() &&
            commandApdu[2] == 0x04.toByte()) {
            Log.d("HCE", "SELECT AID detectado correctamente")
            return STATUS_SUCCESS
        }

        // --- Recibir JSON del TPV ---
        if (commandApdu[1] == 0xDA.toByte()) {
            try {
                val data = commandApdu.copyOfRange(5, commandApdu.size)
                val payload = String(data, Charsets.UTF_8).trim()
                Log.d("HCE", "JSON recibido: $payload")

                val json = JSONObject(payload)
                ultimoComercio = json.getString("negocio")
                ultimoImporte = json.getDouble("importe")
                ultimoTipo = json.getString("tipo")

                // Guardar en base de datos
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val dao = BankDb.getInstance(applicationContext).bankDao()
                        val tx = Transaction(
                            amount = ultimoImporte,
                            category = mapearCategoria(ultimoComercio),
                            type = ultimoTipo,
                            timestamp = System.currentTimeMillis()
                        )
                        dao.insertTransaction(tx)
                        Log.d("HCE", "✅ Transakzioa behar bezala gorde da")

                        // 🔹 Enviar broadcast para notificar a PagoAutomaticoActivity
                        val intent = Intent("com.example.proyecto.PAGO_RECIBIDO")
                        intent.putExtra("comercio", ultimoComercio)
                        intent.putExtra("importe", ultimoImporte)
                        sendBroadcast(intent)

                    } catch (e: Exception) {
                        Log.e("HCE", "❌ Errore bat gertatu da datu-basean gordetzean: ${e.message}")
                    }
                }

                return STATUS_SUCCESS

            } catch (e: Exception) {
                Log.e("HCE", "❌ Errore bat gertatu da JSON prozesatzen: ${e.message}")
                return STATUS_FAILED
            }
        }

        // --- GET DATA (opcional) ---
        if (commandApdu[1] == 0xCA.toByte()) {
            val json = """{"negocio":"$ultimoComercio","importe":$ultimoImporte,"tipo":"$ultimoTipo"}"""
            return json.toByteArray(Charsets.UTF_8) + STATUS_SUCCESS
        }

        return STATUS_FAILED
    }

    override fun onDeactivated(reason: Int) {
        Log.d("HCE", "onDeactivated $reason")
    }
}
