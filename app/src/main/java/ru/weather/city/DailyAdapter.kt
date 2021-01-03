package ru.weather.city

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_day.view.*
import ru.weather.R
import ru.weather.model.data.DailyForecasts
import java.text.SimpleDateFormat
import java.util.*

class DailyAdapter() : RecyclerView.Adapter<DailyAdapter.ViewHolder>() {

    var dailyForecasts: List<DailyForecasts> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false))

    override fun getItemCount(): Int = dailyForecasts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(dailyForecasts[position])

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SimpleDateFormat")
        fun bind(dailyForecasts: DailyForecasts) = with(itemView) {
            val sdf = SimpleDateFormat("dd MMMM yyyy")
            sdf.timeZone = TimeZone.getTimeZone("GMT")
            val date = Date(dailyForecasts.epochDate * 1000L)
            tv_day.text = sdf.format(date)

            tv_day_temperature.text = String.format("${dailyForecasts.maximum} ℃")
            tv_night_temperature.text = String.format("${dailyForecasts.minimum} ℃")
            tv_day_weather_text.text = dailyForecasts.dayIconPhrase
            tv_night_weather_text.text = dailyForecasts.nightIconPhrase
        }
    }
}