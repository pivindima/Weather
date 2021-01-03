package ru.weather.city

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.weather.model.data.DailyForecasts
import ru.weather.model.data.Note
import ru.weather.model.api.ApiRepo
import ru.weather.model.data.RealmDaily
import ru.weather.model.data.RealmNote
import timber.log.Timber


@InjectViewState
class CityPresenter : MvpPresenter<CityView>() {
    private val apiRepo = ApiRepo.instance

    fun requestForecasts(note: Note) {
        apiRepo.requestForecasts(note.key)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ daily ->
                note.dailyForecasts.clear()

                val realm = Realm.getDefaultInstance()
                realm.executeTransactionAsync { itRealm ->

                    val rl: RealmList<RealmDaily> = RealmList()

                    for (i in daily.list.indices) {
                        note.dailyForecasts.add(
                            DailyForecasts(
                                note.key,
                                daily.list[i].epochDate,
                                daily.list[i].temperature.minimum.value,
                                daily.list[i].temperature.maximum.value,
                                daily.list[i].day.iconPhrase,
                                daily.list[i].night.iconPhrase
                            )
                        )
                        rl.add(
                            RealmDaily(
                                i,
                                note.key,
                                daily.list[i].epochDate,
                                daily.list[i].temperature.minimum.value,
                                daily.list[i].temperature.maximum.value,
                                daily.list[i].day.iconPhrase,
                                daily.list[i].night.iconPhrase
                            )
                        )
                    }

                    val realmNote = itRealm.where(RealmNote::class.java).equalTo("key", note.key).findFirst()
                    realmNote?.dailyForecasts?.clear()
                    realmNote?.dailyForecasts?.addAll(rl)
                }

                viewState.showResult(note)

                Timber.d("Days ${daily.list.size}")
            }, { error ->
                viewState.showResult(null)
                error.printStackTrace()
            })
    }
}