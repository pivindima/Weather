package ru.weather.model.api

import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("Key") val key : String,
    @SerializedName("LocalizedName") val localizedName : String,
    @SerializedName("Country") val country : Country,
    @SerializedName("AdministrativeArea") val administrativeArea : AdministrativeArea
)

data class Country(
    @SerializedName("ID") val id : String,
    @SerializedName("LocalizedName") val localizedName : String
)

data class AdministrativeArea(
    @SerializedName("ID") val id : String,
    @SerializedName("LocalizedName") val localizedName : String
)




data class CurrentConditions(
    @SerializedName("WeatherText") val weatherText : String,
    @SerializedName("Temperature") val temperature : Temperature
)

data class Temperature(
    @SerializedName("Metric") val metric : Metric
)

data class Metric(
    @SerializedName("Value") val value : String
)




data class Daily(
    @SerializedName("DailyForecasts") val list : MutableList<DailyForecasts>
)

data class DailyForecasts(
    @SerializedName("EpochDate") val epochDate : Long,
    @SerializedName("Temperature") val temperature : Temp,
    @SerializedName("Day") val day : Day,
    @SerializedName("Night") val night : Night
)

data class Temp(
    @SerializedName("Minimum") val minimum : Minimum,
    @SerializedName("Maximum") val maximum : Maximum
)

data class Minimum(
    @SerializedName("Value") val value : String
)

data class Maximum(
    @SerializedName("Value") val value : String
)

data class Day(
    @SerializedName("IconPhrase") val iconPhrase : String
)

data class Night(
    @SerializedName("IconPhrase") val iconPhrase : String
)