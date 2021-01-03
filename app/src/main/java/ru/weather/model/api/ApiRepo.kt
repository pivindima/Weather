package ru.weather.model.api

import io.reactivex.rxjava3.core.Observable

class ApiRepo {

    companion object {
        val instance = ApiRepo()
    }

    private val apiService = ApiService.create()

    fun searchCity(query: String): Observable<List<City>> {
        return apiService.searchCity(query)
    }

    fun currentConditions(key: String): Observable<List<CurrentConditions>> {
        return apiService.currentConditions(key)
    }

    fun requestForecasts(key: String): Observable<Daily> {
        return apiService.requestForecasts(key)
    }
}