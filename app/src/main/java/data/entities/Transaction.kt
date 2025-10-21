package data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val currency: String = "EUR",
    val description: String? = null,
    val category: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "Errenta" // o "Gasto"
)
