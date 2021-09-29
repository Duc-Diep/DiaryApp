package com.example.dictionary.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dictionary.R
import com.example.dictionary.objects.DaysOfWeek

class DayOfWeekAdapter(var context: Context?, var listDaysOfWeek: ArrayList<DaysOfWeek>) :
    RecyclerView.Adapter<DayOfWeekAdapter.DaysViewHolder>() {
    var onClick: ((Int) -> Unit)? = null

    fun setOnItemClick(callBack: (Int) -> Unit) {
        onClick = callBack
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DaysViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.day_of_week_item, parent, false)
        return DaysViewHolder(view)

    }

    override fun onBindViewHolder(holder: DaysViewHolder, position: Int) {
        var daysOfWeek = listDaysOfWeek[position]
        holder.tvDay.text = daysOfWeek.name
        holder.tvDay.setOnClickListener {
            onClick?.invoke(daysOfWeek.value)
        }
    }

    override fun getItemCount(): Int {
        return listDaysOfWeek.size
    }

    class DaysViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvDay: TextView = itemView.findViewById(R.id.tv_day_item)
    }
}