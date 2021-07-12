package com.spudg.tricrypto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import com.spudg.tricrypto.databinding.ActivityCoinBinding
import com.spudg.tricrypto.databinding.ActivityMarketBinding
import drewcarlson.coingecko.CoinGeckoClient
import drewcarlson.coingecko.models.coins.CoinFullData
import drewcarlson.coingecko.models.coins.CoinMarkets
import drewcarlson.coingecko.models.coins.MarketChart
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat
import java.text.NumberFormat

class CoinActivity : AppCompatActivity() {

    private lateinit var bindingCoin: ActivityCoinBinding

    private val coinGecko = CoinGeckoClient.create()

    private var entriesLine: ArrayList<Entry> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingCoin = ActivityCoinBinding.inflate(layoutInflater)
        val view = bindingCoin.root
        setContentView(view)

        setUpCoinInfo(7)

        bindingCoin.btn7d.setOnClickListener {
            setUpCoinInfo(7)
        }

        bindingCoin.btn1m.setOnClickListener {
            setUpCoinInfo(30)
        }

        bindingCoin.btn1y.setOnClickListener {
            setUpCoinInfo(365)
        }

        bindingCoin.btn5y.setOnClickListener {
            setUpCoinInfo(1825)
        }

    }

    private fun setUpCoinInfo(days: Int) = runBlocking {
        launch {

            val usdFormatter: NumberFormat = DecimalFormat("$#,##0.00")
            val usdNoCFormatter: NumberFormat = DecimalFormat("£#,##0")

            val coin = coinGecko.getCoinMarkets("usd",Globals.SELECTED_COIN,null,null,null,true).markets[0]
            val chartData: MarketChart = coinGecko.getCoinMarketChartById(Globals.SELECTED_COIN, "usd", days)

            Glide.with(applicationContext)
                .load(coin.image)
                .into(bindingCoin.logo)

            bindingCoin.name.text = coin.name
            bindingCoin.mktCap.text = usdNoCFormatter.format(coin.marketCap)
            bindingCoin.symbol.text = coin.symbol!!.uppercase()
            bindingCoin.price.text = usdFormatter.format(coin.currentPrice)

            // Make data for chart

            entriesLine = arrayListOf()

            val yValues: ArrayList<Float> = arrayListOf()

            repeat (chartData.prices.size) {
                yValues.add(chartData.prices[it].last().toString().toFloat())
            }

            // Make and configure chart

            repeat (coin.sparklineIn7d!!.price!!.size) {
                entriesLine.add(Entry(it.toFloat(), yValues[it]))
            }
            val dataSetLine = LineDataSet(entriesLine, "")
            val dataLine = LineData(dataSetLine)

            val chartLine: LineChart = bindingCoin.chart7Days
            chartLine.data = dataLine

            chartLine.animateY(800)
            chartLine.setNoDataText("No data returned from API")
            chartLine.setNoDataTextColor(0xff000000.toInt())
            chartLine.setNoDataTextTypeface(ResourcesCompat.getFont(this@CoinActivity, R.font.open_sans_light))
            chartLine.dragDecelerationFrictionCoef = .95f
            chartLine.setDrawGridBackground(false)
            chartLine.xAxis.setDrawGridLines(false)
            chartLine.xAxis.setDrawLabels(false)
            chartLine.axisLeft.setDrawGridLines(false)
            chartLine.xAxis.setDrawAxisLine(false)
            chartLine.axisLeft.setDrawAxisLine(false)
            chartLine.setTouchEnabled(false)
            chartLine.axisRight.isEnabled = false
            chartLine.legend.isEnabled = false
            chartLine.description.isEnabled = false

            dataSetLine.setDrawFilled(true)
            dataSetLine.setDrawCircles(false)
            dataSetLine.setDrawCircleHole(false)
            dataSetLine.setDrawValues(false)

            chartLine.description.isEnabled = false

            chartLine.invalidate()



        }
    }

}