package com.example.proyecto

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.proyecto.data.BankDb
import data.entities.Transaction
import kotlinx.coroutines.launch

class SummaryActivity : AppCompatActivity() {

    private lateinit var layoutCategorias: LinearLayout
    private lateinit var tvTotalIngresos: TextView
    private lateinit var tvTotalGastos: TextView
    private lateinit var btnVolver: Button

    private lateinit var tvTotalNeto: TextView

    private val categorias = listOf(
        "Janaria", "Aisia", "Erosketak", "Etxea", "Garraioa", "Beharrak", "Besteak"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        layoutCategorias = findViewById(R.id.layoutCategorias)
        tvTotalIngresos = findViewById(R.id.tvTotalIngresos)
        tvTotalGastos = findViewById(R.id.tvTotalGastos)
        btnVolver = findViewById(R.id.btnVolver)
        tvTotalNeto = findViewById(R.id.tvTotalNeto)

        btnVolver.setOnClickListener { finish() }

        val db = BankDb.getInstance(this)

        lifecycleScope.launch {
            val transacciones = db.bankDao().getAllTransactions()

            mostrarResumen(transacciones)
        }
    }

    private fun mostrarResumen(transacciones: List<Transaction>) {
        val ingresosPorCategoria = mutableMapOf<String, Double>()
        val gastosPorCategoria = mutableMapOf<String, Double>()

        categorias.forEach {
            ingresosPorCategoria[it] = 0.0
            gastosPorCategoria[it] = 0.0
        }

        for (tx in transacciones) {
            val categoria = if (categorias.contains(tx.category)) tx.category!! else "Besteak"
            if (tx.type == "Errenta") {
                ingresosPorCategoria[categoria] = (ingresosPorCategoria[categoria] ?: 0.0) + tx.amount
            } else {
                gastosPorCategoria[categoria] = (gastosPorCategoria[categoria] ?: 0.0) + kotlin.math.abs(tx.amount)
            }
        }

        val totalIngresos = ingresosPorCategoria.values.sum()
        val totalGastos = gastosPorCategoria.values.sum()

        tvTotalIngresos.text = String.format("+%.2f €", totalIngresos)
        tvTotalGastos.text = String.format("-%.2f €", totalGastos)

        val totalNeto = totalIngresos - totalGastos
        tvTotalNeto.text = String.format("Totala: %+.2f €", totalNeto)
        tvTotalNeto.setTextColor(
            if (totalNeto >= 0) android.graphics.Color.parseColor("#1B5E20")
            else android.graphics.Color.parseColor("#B71C1C")
        )

        layoutCategorias.removeAllViews()
        categorias.forEach { categoria ->
            val fila = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 24, 0, 24)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                gravity = android.view.Gravity.CENTER
            }

            val tvNombre = TextView(this).apply {
                text = categoria
                textSize = 18f
                setTextColor(android.graphics.Color.WHITE)
                gravity = android.view.Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val tvIngreso = TextView(this).apply {
                text = String.format("+%.2f €", ingresosPorCategoria[categoria])
                setTextColor(android.graphics.Color.parseColor("#1B5E20"))
                gravity = android.view.Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val tvGasto = TextView(this).apply {
                text = String.format("-%.2f €", gastosPorCategoria[categoria])
                setTextColor(android.graphics.Color.parseColor("#B71C1C"))
                gravity = android.view.Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            fila.addView(tvNombre)
            fila.addView(tvIngreso)
            fila.addView(tvGasto)
            layoutCategorias.addView(fila)
        }
    }
}
