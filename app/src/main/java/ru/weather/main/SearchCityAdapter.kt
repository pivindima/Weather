package ru.weather.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_search_city.view.*
import ru.weather.R
import ru.weather.model.api.City

class SearchCityAdapter(val onItemClick : ((City) -> Unit)? = null) : RecyclerView.Adapter<SearchCityAdapter.ViewHolder>() {

    var cities: List<City> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_search_city, parent, false))

    override fun getItemCount(): Int = cities.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(cities[position])

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(city: City) = with(itemView) {
            tv_city.text = city.localizedName
            tv_country.text = city.country.localizedName
            tv_area.text = city.administrativeArea.localizedName

            itemView.setOnClickListener {
                onItemClick?.invoke(city)
            }
        }
    }
}