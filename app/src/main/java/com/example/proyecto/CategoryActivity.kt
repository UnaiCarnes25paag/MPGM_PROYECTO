package com.example.proyecto

import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.proyecto.data.BankDb
import com.example.proyecto.databinding.ActivityCategoryBinding
import data.entities.Transaction
import kotlinx.coroutines.launch

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private lateinit var type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        type = intent.getStringExtra("type") ?: "Gastua"
        binding.textViewTitle.text = "Erregistratu $type"

        binding.btnSave.setOnClickListener { saveTransaction() }

        binding.btnBack.setOnClickListener { finish() }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Erregistratu $type"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun saveTransaction() {
        val amountStr = binding.editAmount.text.toString()
        if (amountStr.isBlank()) {
            Toast.makeText(this, "Sartu zenbateko bat", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountStr.toDoubleOrNull()
        if (amount == null) {
            Toast.makeText(this, "Zenbateko baliogabea", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedId = binding.categoryGroup.checkedRadioButtonId
        if (selectedId == -1) {
            Toast.makeText(this, "Hautatu kategoria bat", Toast.LENGTH_SHORT).show()
            return
        }

        val rb = findViewById<RadioButton>(selectedId)

        val category = rb.tag?.toString()?.trim()
            ?: rb.text.toString().replace(Regex("[^\\p{L}\\s]"), "").trim()

        val signedAmount = if (type == "Gastua") -amount else amount

        val transaction = Transaction(
            amount = signedAmount,
            category = category,
            type = type
        )

        lifecycleScope.launch {
            BankDb.getInstance(this@CategoryActivity).bankDao().insertTransaction(transaction)
            Toast.makeText(this@CategoryActivity, "$type erregistratua", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}