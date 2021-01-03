package ru.weather.main

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.weather.model.api.City

@StateStrategyType(AddToEndSingleStrategy::class)
interface MainView : MvpView {

    fun showCitySearchResult(cities: List<City>?)

    fun thisCityAlreadyExists()

    fun updateNotes()

}