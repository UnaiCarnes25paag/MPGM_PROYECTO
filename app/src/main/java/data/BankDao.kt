package data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import data.entities.CategoryTotal
import data.entities.Movement
import data.entities.Rule
import data.entities.Transaction

@Dao
interface BankDao {
    @Insert suspend fun insertTransaction(tx: Transaction): Long
    @Insert suspend fun insertRule(rule: Rule): Long
    @Insert suspend fun insertMovements(vararg movements: Movement)

    @Query("SELECT * FROM rules WHERE category = :category ORDER BY priority DESC")
    suspend fun getRulesForCategory(category: String): List<Rule>

    @Query("SELECT * FROM movements WHERE transactionId = :txId")
    suspend fun getMovementsForTransaction(txId: Long): List<Movement>

    @Query("SELECT COUNT(*) FROM rules")
    suspend fun countRules(): Int

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    suspend fun getAllTransactions(): List<Transaction>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type")
    suspend fun getTotalByType(type: String): Double?

    @Query("SELECT category, SUM(amount) as total FROM transactions WHERE type = :type GROUP BY category")
    suspend fun getTotalByCategory(type: String): List<CategoryTotal>
}
