package ru.weather.model.data

import androidx.lifecycle.MutableLiveData
import io.realm.Realm

object NotesRepository {
    val notes: MutableList<Note> = mutableListOf()
    private val liveData = MutableLiveData<MutableList<Note>>()

    fun getLiveData(): MutableLiveData<MutableList<Note>> = liveData

    init {
        val realm = Realm.getDefaultInstance()
        val realmNotes = realm.where(RealmNote::class.java).sort("id").findAll()
        val realmList = realm.copyFromRealm(realmNotes)

        realmList.forEach {
            val dailyForecasts = mutableListOf<DailyForecasts>()
            it.dailyForecasts.forEach{df ->
                dailyForecasts.add(DailyForecasts(df.key, df.epochDate, df.minimum, df.maximum, df.dayIconPhrase, df.nightIconPhrase))
            }
            notes.add(Note(it.id, it.key, it.title, it.temperature, it.weatherText, dailyForecasts))
        }

    }

}