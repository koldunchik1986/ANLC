package com.abclient.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView

class InventoryFragment : Fragment() {
    private lateinit var invList: RecyclerView
    private lateinit var inventoryAdapter: InventoryAdapter
    private var items: MutableList<InvEntry> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = requireContext()
        invList = RecyclerView(context)
        invList.layoutManager = LinearLayoutManager(context)
        inventoryAdapter = InventoryAdapter(items) { position ->
            ShowItemInfo(position)
        }
        invList.adapter = inventoryAdapter
        // Пример заполнения
        UpdateInventory(listOf(
            InvEntry("Меч новичка"),
            InvEntry("Зелье лечения"),
            InvEntry("Кольцо силы"),
            InvEntry("Щит"),
            InvEntry("Рыба"),
            InvEntry("Ключ от сундука")
        ))
        return invList
    }

    fun UpdateInventory(newItems: List<InvEntry>) {
        items.clear()
        items.addAll(newItems)
        inventoryAdapter.notifyDataSetChanged()
    }

    fun ShowItemInfo(position: Int) {
        // Аналог ShowItemInfo в ПК-версии
        val item = items[position]
        // Здесь можно показать диалог с инфо и кнопками "Использовать", "Выбросить"
    }

    fun UseItem(position: Int) {
        // Аналог UseItem в ПК-версии
        val item = items[position]
        // Логика использования предмета
    }

    data class InvEntry(val name: String)

    class InventoryAdapter(
        private val items: List<InvEntry>,
        private val onItemClick: (Int) -> Unit
    ) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {
        class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val textView = TextView(parent.context)
            textView.textSize = 18f
            textView.setPadding(32, 16, 32, 16)
            return ViewHolder(textView)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView.text = items[position].name
            holder.textView.setOnClickListener { onItemClick(position) }
        }
        override fun getItemCount() = items.size
    }
}
