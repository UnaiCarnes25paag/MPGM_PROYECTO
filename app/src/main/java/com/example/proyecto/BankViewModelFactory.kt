package com.example.proyecto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import repository.BankRepository

class BankViewModelFactory(private val repo: BankRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BankViewModel::class.java)) {
            return BankViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
