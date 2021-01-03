package ru.weather.model.data

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmNote(
    @PrimaryKey var id: Int = 0,
    var key: String = "",
    var title: String = "",
    var temperature: String = "",
    var weatherText: String = "",
    var dailyForecasts: RealmList<RealmDaily> = RealmList()
) : RealmObject()

open class RealmDaily(
    @PrimaryKey var id: Int = 0,
    var key: String = "",
    var epochDate: Long = 0,
    var minimum: String = "",
    var maximum: String = "",
    var dayIconPhrase: String = "",
    var nightIconPhrase: String = ""
) : RealmObject()