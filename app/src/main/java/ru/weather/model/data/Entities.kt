package ru.weather.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Note(val id : Int, val key : String, val title : String, var temperature : String, var weatherText : String, var dailyForecasts: MutableList<DailyForecasts>) : Parcelable

@Parcelize
class DailyForecasts(val key : String, var epochDate: Long, var minimum: String, var maximum: String, var dayIconPhrase: String, var nightIconPhrase: String) : Parcelable