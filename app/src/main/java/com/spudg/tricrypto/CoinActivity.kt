package com.spudg.tricrypto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.spudg.tricrypto.databinding.ActivityCoinBinding
import com.spudg.tricrypto.databinding.ActivityMarketBinding
import drewcarlson.coingecko.CoinGeckoClient
import drewcarlson.coingecko.models.coins.CoinFullData
import drewcarlson.coingecko.models.coins.CoinMarkets
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat
import java.text.NumberFormat

class CoinActivity : AppCompatActivity() {

    private lateinit var bindingCoin: ActivityCoinBinding

    private val coinGecko = CoinGeckoClient.create()

    private var entriesBar: ArrayList<BarEntry> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingCoin = ActivityCoinBinding.inflate(layoutInflater)
        val view = bindingCoin.root
        setContentView(view)

        setUpCoinInfo()

    }

    private fun setUpCoinInfo() = runBlocking {
        launch {

            val gbpFormatter: NumberFormat = DecimalFormat("£#,##0.00")
            val gbpNoPFormatter: NumberFormat = DecimalFormat("£#,##0")

            val coin = coinGecko.getCoinMarkets("gbp",Globals.SELECTED_COIN,null,null,null,true).markets[0]

            bindingCoin.name.text = coin.name
            bindingCoin.mktCap.text = gbpNoPFormatter.format(coin.marketCap)
            bindingCoin.symbol.text = coin.symbol
            bindingCoin.price.text = gbpFormatter.format(coin.currentPrice)


            // Make data for chart

            entriesBar = arrayListOf()

            val yValues: ArrayList<Float> = arrayListOf()

            repeat (coin.sparklineIn7d!!.price!!.size) {
                yValues.add(coin.sparklineIn7d!!.price!![it].toFloat())
            }

            // Make and configure chart

            repeat (coin.sparklineIn7d!!.price!!.size) {
                entriesBar.add(BarEntry(it.toFloat(), yValues[it]))
            }
            val dataSetBar = BarDataSet(entriesBar, "")
            val dataBar = BarData(dataSetBar)

            val chartBar: BarChart = bindingCoin.chart7Days
            chartBar.data = dataBar

            chartBar.animateY(800)
            chartBar.setNoDataText("No data returned from API")
            chartBar.setNoDataTextColor(0xff000000.toInt())
            //chartBar.setNoDataTextTypeface(ResourcesCompat.getFont(this@ActivityMain, R.font.open_sans_light))
            chartBar.dragDecelerationFrictionCoef = .95f
            chartBar.setDrawValueAboveBar(false)

            chartBar.description.isEnabled = false

            chartBar.invalidate()



        }
    }

}