package ru.weather.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_note.view.*
import ru.weather.R
import ru.weather.model.data.Note

class NotesAdapter(val onItemClick : ((Note) -> Unit)? = null, val onLongItemClick : ((Note) -> Unit)? = null) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    var notes: List<Note> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false))

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(notes[position])

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(note: Note) = with(itemView) {
            tv_title.text = note.title

            val temperature = note.temperature.split(".")[0]
            tv_temperature.text = String.format("$temperature ℃")

            tv_weather_text.text = note.weatherText

            itemView.setOnClickListener {
                onItemClick?.invoke(note)
            }


            itemView.setOnLongClickListener {
                onLongItemClick?.invoke(note)
                return@setOnLongClickListener true
            }
        }
    }
}