package ru.weather.city

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_city.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import ru.weather.R
import ru.weather.model.data.Note


class CityActivity : MvpAppCompatActivity(), CityView {

    private var city: Note? = null
    private lateinit var liveData: MutableLiveData<Note>
    private lateinit var adapter: DailyAdapter

    @InjectPresenter
    lateinit var cityPresenter: CityPresenter

    companion object {

        const val BROADCAST_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
        private val EXTRA_CITY = CityActivity::class.java.name + "extra.CITY"

        fun start(context: Context, note: Note? = null) {
            val intent = Intent(context, CityActivity::class.java)
            intent.putExtra(EXTRA_CITY, note)
            context.startActivity(intent)
        }
    }

    private var intentFilter = IntentFilter(BROADCAST_ACTION)
    private var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BROADCAST_ACTION -> city?.let {
                    cityPresenter.requestForecasts(it)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city)

        city = intent.getParcelableExtra(EXTRA_CITY)
        city?.let {
            tv_city.text = it.title
            val temperature = it.temperature.split(".")[0]
            tv_temperature.text = String.format("$temperature ℃")
            tv_weather_text.text = it.weatherText

            rv_days.layoutManager = LinearLayoutManager(this)
            adapter = DailyAdapter()
            rv_days.adapter = adapter
            adapter.dailyForecasts = it.dailyForecasts

            liveData = MutableLiveData()
            liveData.observe(this, Observer { d ->
                adapter.dailyForecasts = d.dailyForecasts
            })

            cityPresenter.requestForecasts(it)
        }
    }

    override fun showResult(note: Note?) {
        if (note != null) {
            adapter.dailyForecasts = note.dailyForecasts
        } else {
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_LONG).show()
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