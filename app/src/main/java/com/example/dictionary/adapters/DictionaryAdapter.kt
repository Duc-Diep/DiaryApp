package com.example.dictionary.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dictionary.R
import com.example.dictionary.objects.Day

class DictionaryAdapter(var context: Context, var listEvent: List<Day>) :
    RecyclerView.Adapter<DictionaryAdapter.DictionaryViewHolder>() {
    lateinit var onClick: ((Day) -> Unit)

    fun setOnClickItem(callBack: (Day) -> Unit) {
        onClick = callBack
    }

    class DictionaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate = itemView.findViewById<TextView>(R.id.tv_date_item)
        val tvContent = itemView.findViewById<TextView>(R.id.tv_content_item)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DictionaryViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.dictionary_item, parent, false)
        return DictionaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DictionaryViewHolder, position: Int) {
        var day = listEvent[position]
        holder.tvDate.text = day.date.toString()
        holder.tvContent.text = day.eventContent
        holder.itemView.setOnClickListener {
            onClick.invoke(day)
        }
    }

    override fun getItemCount(): Int {
        return listEvent.size
    }
}