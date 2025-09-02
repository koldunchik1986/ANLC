
package com.abclient.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager

class ChatFragment : Fragment() {
    private lateinit var chatList: RecyclerView
    private lateinit var chatInput: TextView
    private lateinit var chatSendButton: android.widget.Button
    private var messages: MutableList<String> = mutableListOf()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = requireContext()
        val layout = android.widget.LinearLayout(context)
        layout.orientation = android.widget.LinearLayout.VERTICAL

        chatList = RecyclerView(context)
        chatList.layoutManager = LinearLayoutManager(context)
        chatAdapter = ChatAdapter(messages)
        chatList.adapter = chatAdapter

        chatInput = TextView(context)
        chatInput.hint = "Введите сообщение..."
        chatInput.setPadding(16, 16, 16, 16)

        chatSendButton = android.widget.Button(context)
        chatSendButton.text = "Отправить"
        chatSendButton.setOnClickListener {
            SendChatMsg(chatInput.text.toString())
            chatInput.text = ""
        }

        val params = android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            0, 1f
        )
        layout.addView(chatList, params)
        layout.addView(chatInput)
        layout.addView(chatSendButton)
        return layout
    }

    fun WriteChatMsg(msg: String) {
        messages.add(msg)
        chatAdapter.notifyItemInserted(messages.size - 1)
        chatList.scrollToPosition(messages.size - 1)
    }

    fun UpdateChat(newMessages: List<String>) {
        messages.clear()
        messages.addAll(newMessages)
        chatAdapter.notifyDataSetChanged()
    }

    fun SendChatMsg(msg: String) {
        if (msg.isNotBlank()) {
            WriteChatMsg("Вы: $msg")
            // Здесь добавить отправку на сервер
        }
    }

    class ChatAdapter(private val items: List<String>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
        class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val textView = TextView(parent.context)
            textView.textSize = 16f
            textView.setPadding(16, 8, 16, 8)
            return ViewHolder(textView)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView.text = items[position]
        }
        override fun getItemCount() = items.size
    }
}
