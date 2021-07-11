package com.spudg.tricrypto

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.spudg.tricrypto.databinding.CoinRowBinding
import drewcarlson.coingecko.models.coins.CoinMarkets
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.coroutineContext
import kotlin.math.roundToLong

class CoinListAdapter(private val coins: ArrayList<CoinMarkets>) :
    RecyclerView.Adapter<CoinListAdapter.CoinViewHolder>() {

    inner class CoinViewHolder(val binding: CoinRowBinding) : RecyclerView.ViewHolder(binding.root)

    private var entriesBar: ArrayList<BarEntry> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        val binding = CoinRowBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return CoinViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {

        val gbpFormatter: NumberFormat = DecimalFormat("£#,##0.00")

        with(holder) {

            val coin = coins[position]

            binding.name.text = "${coin.name} (${(coin.symbol)!!.uppercase()})"
            binding.dailyChange.text = gbpFormatter.format(coin.priceChangePercentage24h)
            if (coin.priceChangePercentage24h < 0) {
                binding.dailyChange.setTextColor(Color.RED)
            }
            binding.price.text = gbpFormatter.format(coin.currentPrice)

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

                    val chartBar: BarChart = binding.chart180Days
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

    override fun getItemCount(): Int {
        return coins.size
    }

}