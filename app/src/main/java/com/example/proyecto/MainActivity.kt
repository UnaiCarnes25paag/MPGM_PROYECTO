package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDeposit.setOnClickListener {
            goToCategory("Errenta")
        }

        binding.btnWithdraw.setOnClickListener {
            goToCategory("Gastua")
        }

        binding.btnGoSummary.setOnClickListener {
            startActivity(Intent(this, SummaryActivity::class.java))
        }
    }

    private fun goToCategory(type: String) {
        val intent = Intent(this, CategoryActivity::class.java)
        intent.putExtra("type", type)
        startActivity(intent)
    }
}
