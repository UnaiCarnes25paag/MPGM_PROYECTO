package repository

import data.entities.Rule
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import data.BankDao
import data.entities.Movement
import data.entities.Transaction


class BankRepository(private val dao: BankDao) {

    private val gson = Gson()

    suspend fun applyRulesAndStore(amount: Double, category: String?, description: String?): Long {
        val txId = dao.insertTransaction(
            Transaction(
                amount = amount,
                description = description,
                category = category
            )
        )
        val rules = category?.let { dao.getRulesForCategory(it) } ?: emptyList()
        val movements = mutableListOf<Movement>()

        if (rules.isEmpty()) {
            movements.add(Movement(transactionId = txId, destination = "CuentaPrincipal", amount = amount))
            dao.insertMovements(*movements.toTypedArray())
            return txId
        }

        val rule = rules.first()
        val defs: List<Map<String, Any>> = gson.fromJson(rule.definitionJson, object: TypeToken<List<Map<String, Any>>>(){}.type)
        var remaining = amount
        for (d in defs) {
            val type = d["type"] as? String
            if (type == "fixed") {
                val a = (d["amount"] as Number).toDouble()
                val dest = d["destination"] as String
                val take = minOf(a, remaining)
                if (take > 0) {
                    movements.add(Movement(transactionId = txId, destination = dest, amount = take))
                    remaining -= take
                }
            }
        }
        for (d in defs) {
            val type = d["type"] as? String
            if (type == "percent") {
                val p = (d["percent"] as Number).toDouble()
                val dest = d["destination"] as String
                val take = amount * (p / 100.0)
                if (take > 0) {
                    val actual = minOf(take, remaining)
                    movements.add(Movement(transactionId = txId, destination = dest, amount = actual))
                    remaining -= actual
                }
            }
        }
        if (remaining > 0.0001) {
            movements.add(Movement(transactionId = txId, destination = "CuentaPrincipal", amount = remaining))
        }

        dao.insertMovements(*movements.toTypedArray())
        return txId
    }

    suspend fun seedDefaultRulesIfNeeded() {
        val sample = Rule(
            name = "Utilities split",
            category = "utilities",
            priority = 10,
            definitionJson = """
            [
              {"destination":"PagoServicios","type":"fixed","amount":30.0},
              {"destination":"Ahorro","type":"percent","percent":20}
            ]
            """.trimIndent()
        )
        dao.insertRule(sample)
    }
}