package com.example.proyecto

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.databinding.ItemTransactionBinding
import data.entities.Transaction

class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.VH>() {

    private val items = mutableListOf<Transaction>()

    fun submitList(list: List<Transaction>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tx: Transaction) {
            binding.tvCategory.text = tx.category ?: "Kategoriarik gabe"
            binding.tvAmount.text = "${if (tx.type=="Errenta") "+" else "-"}€${tx.amount}"
            binding.tvTimestamp.text = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(java.util.Date(tx.timestamp))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size
}
