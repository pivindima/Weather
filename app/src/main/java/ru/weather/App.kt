package ru.weather

import androidx.multidex.MultiDexApplication
import io.realm.Realm
import io.realm.RealmConfiguration
import ru.weather.model.data.RealmNote
import timber.log.Timber

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        Timber.plant(Timber.DebugTree())
        Realm.init(this)
        hardCodeCities()
    }

    companion object {
        lateinit var instance: App
    }

    private fun hardCodeCities() {
        val conf = RealmConfiguration.Builder().allowWritesOnUiThread(true).build()
        val realm = Realm.getInstance(conf)
        val realmNote = realm.where(RealmNote::class.java).equalTo("key", "294021").findFirst()
        if (realmNote == null) {
            realm.executeTransaction {
                val newRealmNote = it.createObject(RealmNote::class.java, 0)
                newRealmNote.key = "294021"
                newRealmNote.title = "Москва"
                newRealmNote.temperature = "-"
                newRealmNote.weatherText = "-"
            }

            realm.executeTransaction {
                val newRealmNote = it.createObject(RealmNote::class.java, 1)
                newRealmNote.key = "295212"
                newRealmNote.title = "Санкт-Петербург"
                newRealmNote.temperature = "-"
                newRealmNote.weatherText = "-"
            }
        }
        realm.close()
    }

}