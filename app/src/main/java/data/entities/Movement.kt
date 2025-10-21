package data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movements")
data class Movement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionId: Long,
    val destination: String,
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis()
)
