package com.example.proyecto

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.entities.Transaction
import kotlinx.coroutines.launch
import repository.BankRepository

class BankViewModel(private val repo: BankRepository) : ViewModel() {

    private val _lastTransaction = MutableLiveData<Transaction?>()
    val lastTransaction: LiveData<Transaction?> = _lastTransaction

    fun processIncomingTransaction(amount: Double, category: String?, description: String?) {
        viewModelScope.launch {
            repo.seedDefaultRulesIfNeeded()
            val txId = repo.applyRulesAndStore(amount, category, description)
            // opcional: leer de DB el transaction guardado para exponer a UI
            // (aquí simplificamos y exponemos null-safe)
            _lastTransaction.postValue(Transaction(id = txId, amount = amount, description = description, category = category))
        }
    }
}
