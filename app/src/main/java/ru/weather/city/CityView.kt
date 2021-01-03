package ru.weather.city

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.weather.model.data.Note

@StateStrategyType(AddToEndSingleStrategy::class)
interface CityView : MvpView {
    fun showResult(cities: Note?)
}