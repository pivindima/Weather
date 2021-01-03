package ru.weather.main

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_delete.*
import kotlinx.android.synthetic.main.dialog_search_city.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.weather.R
import ru.weather.model.api.City
import ru.weather.city.CityActivity
import ru.weather.model.data.NotesRepository
import ru.weather.model.data.Note
import java.util.concurrent.TimeUnit

class MainActivity : MvpAppCompatActivity(), MainView {

    private lateinit var adapter: NotesAdapter
    private lateinit var searchCityAdapter: SearchCityAdapter
    private lateinit var dialogSearch: Dialog
    private lateinit var liveData: MutableLiveData<MutableList<Note>>
    private lateinit var searchLiveData: MutableLiveData<List<City>>

    @InjectPresenter
    lateinit var mainPresenter: MainPresenter

    companion object {
        const val BROADCAST_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
    }

    private var intentFilter = IntentFilter(CityActivity.BROADCAST_ACTION)
    private var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BROADCAST_ACTION -> mainPresenter.updateCurrentConditions()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv_cities.layoutManager = LinearLayoutManager(this)
        adapter = NotesAdapter(
            { note ->
                CityActivity.start(this, note)
            },
            { note ->
                when {
                    // Если лонгклик не Москва и не Питер
                    note.key != "294021" && note.key != "295212" -> {

                        val dialog = Dialog(this)
                        dialog.setContentView(R.layout.dialog_delete)
                        dialog.setCancelable(false)
                        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

                        dialog.btn_delete_cancel.setOnClickListener {
                            dialog.dismiss()
                        }

                        dialog.btn_delete.setOnClickListener {
                            dialog.dismiss()
                            mainPresenter.deleteCity(note)
                        }

                        dialog.show()
                    }
                }
            }
        )
        rv_cities.adapter = adapter
        adapter.notes = NotesRepository.notes

        liveData = NotesRepository.getLiveData()
        liveData.observe(this, Observer{
            adapter.notes = it
        })

        searchLiveData = MutableLiveData()
        searchLiveData.observe(this, Observer{
            searchCityAdapter.cities = it
        })

        btn_add.setOnClickListener { view ->
            onClickBtnAdd(view)
        }

        mainPresenter.updateCurrentConditions()

    }

    @ProvidePresenter
    fun provideMainPresenter() = MainPresenter()

    private fun onClickBtnAdd(view: View) {
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_click))

        dialogSearch = Dialog(this)
        dialogSearch.setContentView(R.layout.dialog_search_city)
        dialogSearch.setCancelable(false)
        dialogSearch.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialogSearch.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

        dialogSearch.rv_search.layoutManager = LinearLayoutManager(this)
        searchCityAdapter = SearchCityAdapter {
            dialogSearch.dismiss()
            mainPresenter.onClickSearchedCity(it)

        }
        dialogSearch.rv_search.adapter = searchCityAdapter

        dialogSearch.et_search_city.requestFocus()
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

        val disposable = Observable.create(ObservableOnSubscribe<String> { emitter -> dialogSearch.et_search_city.doAfterTextChanged { emitter.onNext(it.toString().trim()) } })
            .debounce(500, TimeUnit.MILLISECONDS)
            .filter { query -> query.length > 1 }
            .distinctUntilChanged()
            .subscribe { query ->
                mainPresenter.citySearch(query)
            }

        dialogSearch.setOnDismissListener {
            dialogSearch.et_search_city.clearFocus()
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            disposable.dispose()
        }

        dialogSearch.btn_search_cancel.setOnClickListener {
            dialogSearch.dismiss()
        }

        dialogSearch.show()

    }

    override fun showCitySearchResult(cities: List<City>?) {
        if (cities != null) {
            searchLiveData.value = cities
        } else {
            Toast.makeText(this@MainActivity, getString(R.string.error_connection), Toast.LENGTH_LONG).show()
        }
    }

    override fun thisCityAlreadyExists() {
        Toast.makeText(this@MainActivity, getString(R.string.this_city_already_exists), Toast.LENGTH_LONG).show()
    }

    override fun updateNotes() {
        runOnUiThread {
            liveData.value = NotesRepository.notes
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, intentFilter);
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver);
    }

}