package ru.weather

import android.content.Context

object Preferences {

    private const val NAME = "preferences"

    fun putString(key: String, value: String) = App.instance.getSharedPreferences(NAME, 0).edit().putString(key, value).apply()

    fun getString(key: String): String? = App.instance.getSharedPreferences(NAME, 0).getString(key, "")

    fun getString(key: String, def: String): String? = App.instance.getSharedPreferences(NAME, 0).getString(key, def)

    fun putInt(key: String, value: Int) = App.instance.getSharedPreferences(NAME, 0).edit().putInt(key, value).apply()

    fun getInt(key: String): Int = App.instance.getSharedPreferences(NAME, 0).getInt(key, 0)

    fun getInt(key: String, def: Int): Int = App.instance.getSharedPreferences(NAME, 0).getInt(key, def)
}