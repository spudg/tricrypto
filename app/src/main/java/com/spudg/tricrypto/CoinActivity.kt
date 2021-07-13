package com.spudg.tricrypto

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import com.spudg.tricrypto.databinding.ActivityCoinBinding
import drewcarlson.coingecko.CoinGeckoClient
import drewcarlson.coingecko.models.coins.MarketChart
import io.ktor.client.engine.android.*
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
            val usdNoCFormatter: NumberFormat = DecimalFormat("$#,##0")
            val percentFormatter: NumberFormat = DecimalFormat("#,##0.00%")
            val number0dpFormatter: NumberFormat = DecimalFormat("#,##0")

            val coin = coinGecko.getCoinMarkets(
                "usd",
                Globals.SELECTED_COIN,
                null,
                null,
                null,
                true
            ).markets[0]
            val chartData: MarketChart =
                coinGecko.getCoinMarketChartById(Globals.SELECTED_COIN, "usd", days)

            Glide.with(applicationContext)
                .load(coin.image)
                .into(bindingCoin.logo)

            bindingCoin.name.text = coin.name
            bindingCoin.symbol.text = coin.symbol!!.uppercase()
            bindingCoin.price.text = usdFormatter.format(coin.currentPrice)
            if (coin.priceChange24h < 0) {
                bindingCoin.change24h.setTextColor(Color.RED)
                bindingCoin.change24hPerc.setTextColor(Color.RED)
            } else {
                bindingCoin.change24h.setTextColor(Color.GREEN)
                bindingCoin.change24hPerc.setTextColor(Color.GREEN)
            }
            bindingCoin.change24h.text = usdFormatter.format(coin.priceChange24h)
            bindingCoin.change24hPerc.text =
                percentFormatter.format(coin.priceChangePercentage24h / 100)

            bindingCoin.high24h.text = usdFormatter.format(coin.high24h)
            bindingCoin.low24h.text = usdFormatter.format(coin.low24h)
            bindingCoin.volume.text = usdFormatter.format(coin.totalVolume)
            bindingCoin.mktCap.text = usdNoCFormatter.format(coin.marketCap)
            if (coin.fullyDilutedValuation != null) {
                bindingCoin.dilMktCap.text = usdNoCFormatter.format(coin.fullyDilutedValuation)
            } else {
                bindingCoin.dilMktCap.text = "n/a"
            }
            bindingCoin.mktCapRank.text = coin.marketCapRank.toString()
            bindingCoin.circSupply.text = number0dpFormatter.format(coin.circulatingSupply)
            if (coin.maxSupply != 0.0) {
                bindingCoin.maxSupply.text = number0dpFormatter.format(coin.maxSupply)
            } else {
                bindingCoin.maxSupply.text = "Unlimited"
            }
            if (coin.totalSupply != null) {
                bindingCoin.totalSupply.text = number0dpFormatter.format(coin.totalSupply)
            } else {
                bindingCoin.totalSupply.text = "Unlimited"
            }
            bindingCoin.ath.text = usdFormatter.format(coin.ath)
            bindingCoin.athDate.text = coin.athDate
            bindingCoin.atl.text = usdFormatter.format(coin.atl)
            bindingCoin.atlDate.text = coin.atlDate


            // Make data for chart

            entriesLine = arrayListOf()

            val yValues: ArrayList<Float> = arrayListOf()

            repeat(chartData.prices.size) {
                yValues.add(chartData.prices[it].last().toString().toFloat())
            }

            // Make and configure chart

            repeat(coin.sparklineIn7d!!.price!!.size) {
                entriesLine.add(Entry(it.toFloat(), yValues[it]))
            }
            val dataSetLine = LineDataSet(entriesLine, "")
            val dataLine = LineData(dataSetLine)

            val chartLine: LineChart = bindingCoin.chart7Days
            chartLine.data = dataLine

            chartLine.animateY(800)
            chartLine.setNoDataText("No data returned from API")
            chartLine.setNoDataTextColor(0xff000000.toInt())
            chartLine.setNoDataTextTypeface(
                ResourcesCompat.getFont(
                    this@CoinActivity,
                    R.font.open_sans_light
                )
            )
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