package ru.weather.main


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.realm.Realm
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.weather.model.api.City
import ru.weather.model.api.CurrentConditions
import ru.weather.model.data.NotesRepository
import ru.weather.model.data.Note
import ru.weather.model.api.ApiRepo
import ru.weather.model.data.RealmNote
import timber.log.Timber

@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {
    private val apiRepo = ApiRepo.instance

    fun citySearch(query: String) {
        apiRepo.searchCity(query)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ cities ->
                viewState.showCitySearchResult(cities)
                Timber.d("Result size ${cities.size}")
            }, { error ->
                viewState.showCitySearchResult(null)
                error.printStackTrace()
            })
    }

    fun onClickSearchedCity(city: City) {
        val realm = Realm.getDefaultInstance()
        val realmNote = realm.where(RealmNote::class.java).equalTo("key", city.key).findFirst()

        if (realmNote != null) {
            viewState.thisCityAlreadyExists()
        } else {
            realm.executeTransactionAsync {

                val number = it.where(RealmNote::class.java).max("id")
                var id = 0
                if (number != null) id = number.toInt() + 1

                val newRealmNote = it.createObject(RealmNote::class.java, id)
                newRealmNote.key = city.key
                newRealmNote.title = city.localizedName
                newRealmNote.temperature = "-"
                newRealmNote.weatherText = "-"

                val note = Note(newRealmNote.id, newRealmNote.key, newRealmNote.title, newRealmNote.temperature, newRealmNote.weatherText, mutableListOf())
                NotesRepository.notes.add(note)
                viewState.updateNotes()

                apiRepo.currentConditions(newRealmNote.key)
                    .subscribe({ result ->
                        val realm = Realm.getDefaultInstance()
                        realm.executeTransactionAsync { itRealm ->
                            val realmNote = itRealm.where(RealmNote::class.java).equalTo("key", city.key).findFirst()
                            realmNote?.temperature = result[0].temperature.metric.value
                            realmNote?.weatherText = result[0].weatherText

                            note.temperature = result[0].temperature.metric.value
                            note.weatherText = result[0].weatherText

                            Timber.d("${realmNote?.title} ${result[0].temperature.metric.value}")
                            viewState.updateNotes()
                        }

                    }, { error ->
                        error.printStackTrace()
                    })
            }
        }

    }

    fun deleteCity(note: Note) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransactionAsync {
            it.where(RealmNote::class.java).equalTo("id", note.id).findAll().deleteFirstFromRealm()
            NotesRepository.notes.remove(note)
            viewState.updateNotes()
        }
    }

    fun updateCurrentConditions() {
        val listObservables = mutableListOf<Observable<List<CurrentConditions>>>()
        NotesRepository.notes.forEach {
            listObservables.add(apiRepo.currentConditions(it.key))
        }

        Observable.zip(listObservables) {
            val realm = Realm.getDefaultInstance()
            for (i in it.indices) {
                val currentConditions = it[i] as List<CurrentConditions>
                realm.executeTransactionAsync { itRealm ->
                    val realmNote = itRealm.where(RealmNote::class.java).equalTo("key", NotesRepository.notes[i].key).findFirst()
                    realmNote?.temperature = currentConditions[0].temperature.metric.value
                    realmNote?.weatherText = currentConditions[0].weatherText
                    NotesRepository.notes[i].temperature = currentConditions[0].temperature.metric.value
                    NotesRepository.notes[i].weatherText = currentConditions[0].weatherText
                    Timber.d("${realmNote?.title} ${currentConditions[0].temperature.metric.value}")
                    viewState.updateNotes()
                }
            }

        }.onErrorResumeNext {
            it.printStackTrace()
            Observable.empty()
        }.subscribe()
    }
}