package com.spudg.tricrypto

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import com.spudg.tricrypto.databinding.ActivityCoinBinding
import com.spudg.tricrypto.databinding.DialogBuyBinding
import com.spudg.tricrypto.databinding.DialogSellBinding
import drewcarlson.coingecko.CoinGeckoClient
import drewcarlson.coingecko.models.coins.MarketChart
import io.ktor.client.engine.android.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat
import java.text.NumberFormat

class CoinActivity : AppCompatActivity() {

    private lateinit var bindingCoin: ActivityCoinBinding
    private lateinit var bindingBuyDialog: DialogBuyBinding
    private lateinit var bindingSellDialog: DialogSellBinding

    private val coinGecko = CoinGeckoClient.create()

    private var entriesLine: ArrayList<Entry> = ArrayList()

    private var coinCurrentPrice = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingCoin = ActivityCoinBinding.inflate(layoutInflater)
        val view = bindingCoin.root
        setContentView(view)

        setUpCoinInfo(7)
        setUpUI()

        bindingCoin.portfolioBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        bindingCoin.marketBtn.setOnClickListener {
            val intent = Intent(this, MarketActivity::class.java)
            startActivity(intent)
            finish()
        }

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

    private fun setUpUI() {

        val dbHolding = HoldingHandler(this, null)
        val usdFormatter: NumberFormat = DecimalFormat("$#,##0.00")
        val amountFormatter: NumberFormat = DecimalFormat("#,###.####")
        val percentFormatter: NumberFormat = DecimalFormat("#,##0.00%")

        if (dbHolding.getAmount(Globals.SELECTED_COIN_SYM).toFloat() > 0F) {
            val value = dbHolding.getAmount(Globals.SELECTED_COIN_SYM)
                .toFloat() * coinCurrentPrice.toFloat()
            val cost = dbHolding.getCost(Globals.SELECTED_COIN_SYM).toFloat()
            val amount = dbHolding.getAmount(Globals.SELECTED_COIN_SYM).toFloat()
            val pReturn = (value - cost) / cost

            bindingCoin.llHoldingInfo.visibility = View.VISIBLE
            bindingCoin.infoHeader.text =
                getString(R.string.holding_title, Globals.SELECTED_COIN_SYM.uppercase())
            bindingCoin.value.text = getString(R.string.value_figure, usdFormatter.format(value))
            bindingCoin.pReturn.text = percentFormatter.format(pReturn)
            if (pReturn < 0) {
                bindingCoin.pReturn.setTextColor(Color.RED)
            } else {
                bindingCoin.pReturn.setTextColor(Color.GREEN)
            }
            bindingCoin.amount.text = getString(R.string.amount_figure, amountFormatter.format(amount))
            bindingCoin.cost.text = getString(R.string.cost_figure, usdFormatter.format(cost))



            bindingCoin.btnSell.visibility = View.VISIBLE

            bindingCoin.btnSell.setOnClickListener {
                val sellDialog = Dialog(this, R.style.Theme_Dialog)
                sellDialog.setCancelable(false)
                bindingSellDialog = DialogSellBinding.inflate(layoutInflater)
                val view = bindingSellDialog.root
                sellDialog.setContentView(view)
                sellDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                val dbCrypto = HoldingHandler(this, null)
                val currentAmount = dbCrypto.getAmount(Globals.SELECTED_COIN_SYM).toFloat()
                val currentPrice = coinCurrentPrice.toFloat()

                bindingSellDialog.ofSymbol.text = getString(R.string.of_symbol, Globals.SELECTED_COIN_SYM.uppercase())
                bindingSellDialog.currentHoldingValue.text =
                    getString(R.string.current_holding_value_figure, usdFormatter.format(currentAmount * currentPrice))


                bindingSellDialog.tvSell.setOnClickListener {
                    if (bindingSellDialog.etAmount.text.isEmpty()) {
                        Toast.makeText(this, "Enter a value to sell.", Toast.LENGTH_SHORT).show()
                    } else {
                        val dbCash = CashHandler(this, null)
                        val existingCost = dbCrypto.getCost(Globals.SELECTED_COIN_SYM).toFloat()
                        val soldCost = bindingSellDialog.etAmount.text.toString().toFloat()
                        if (existingCost >= soldCost) {
                            dbCrypto.sell(
                                HoldingModel(
                                    Globals.SELECTED_COIN_SYM,
                                    Globals.SELECTED_COIN_ID,
                                    ((1 - (soldCost / (currentAmount * currentPrice))) * existingCost).toString(),
                                    ((1 - (soldCost / (currentAmount * currentPrice))) * currentAmount).toString()
                                )
                            )
                            dbCash.addCash(soldCost.toString())
                            Toast.makeText(this, "Crypto sold.", Toast.LENGTH_SHORT).show()
                            sellDialog.dismiss()
                            setUpUI()
                        } else {
                            Toast.makeText(
                                this,
                                "You don't have enough to sell this amount.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                bindingSellDialog.tvSellAll.setOnClickListener {
                    val dbCash = CashHandler(this, null)
                    dbCrypto.removeHolding(Globals.SELECTED_COIN_SYM)
                    dbCash.addCash((currentAmount * currentPrice).toString())
                    Toast.makeText(this, "Crypto sold.", Toast.LENGTH_SHORT).show()
                    sellDialog.dismiss()
                    setUpUI()
                }

                bindingSellDialog.tvCancel.setOnClickListener {
                    sellDialog.dismiss()
                }

                sellDialog.show()
            }
        } else {

            bindingCoin.llHoldingInfo.visibility = View.GONE

            bindingCoin.btnSell.visibility = View.GONE
        }

        bindingCoin.btnBuy.setOnClickListener {
            val buyDialog = Dialog(this, R.style.Theme_Dialog)
            buyDialog.setCancelable(false)
            bindingBuyDialog = DialogBuyBinding.inflate(layoutInflater)
            val view = bindingBuyDialog.root
            buyDialog.setContentView(view)
            buyDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val dbCrypto = HoldingHandler(this, null)
            val dbCash = CashHandler(this, null)
            val currentCash = dbCash.getCashBal()

            bindingBuyDialog.ofSymbol.text = getString(R.string.of_symbol, Globals.SELECTED_COIN_SYM.uppercase())
            bindingBuyDialog.availableCash.text =
                getString(R.string.available_cash_figure, usdFormatter.format(currentCash.toFloat()))

            bindingBuyDialog.tvBuy.setOnClickListener {
                val existingCost = dbCrypto.getCost(Globals.SELECTED_COIN_SYM).toFloat()
                val newCost = bindingBuyDialog.etAmount.text.toString().toFloat()
                if (dbCash.getCashBal().toFloat() >= newCost) {
                    dbCrypto.buy(
                        HoldingModel(
                            Globals.SELECTED_COIN_SYM,
                            Globals.SELECTED_COIN_ID,
                            (existingCost + newCost).toString(),
                            (dbCrypto.getAmount(Globals.SELECTED_COIN_SYM)
                                .toFloat() + (newCost / coinCurrentPrice.toFloat())).toString()
                        )
                    )
                    dbCash.takeCash(newCost.toString())
                    Toast.makeText(this, "Crypto bought.", Toast.LENGTH_SHORT).show()
                    buyDialog.dismiss()
                    setUpUI()
                } else {
                    Toast.makeText(this, "You don't have enough cash.", Toast.LENGTH_SHORT).show()
                }
            }

            bindingBuyDialog.tvCancel.setOnClickListener {
                buyDialog.dismiss()
            }

            buyDialog.show()
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
                Globals.SELECTED_COIN_ID,
                null,
                null,
                null,
                true
            ).markets[0]
            val chartData: MarketChart =
                coinGecko.getCoinMarketChartById(Globals.SELECTED_COIN_ID, "usd", days)
            Log.e("test", chartData.prices.last().toString())

            coinCurrentPrice = coin.currentPrice.toString()

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
                bindingCoin.maxSupply.text = getString(R.string.unlimited)
            }
            if (coin.totalSupply != null) {
                bindingCoin.totalSupply.text = number0dpFormatter.format(coin.totalSupply)
            } else {
                bindingCoin.totalSupply.text = getString(R.string.unlimited)
            }
            bindingCoin.ath.text = usdFormatter.format(coin.ath)
            bindingCoin.athDate.text = (coin.athDate?.split("T"))?.first()
            bindingCoin.atl.text = usdFormatter.format(coin.atl)
            bindingCoin.atlDate.text = (coin.atlDate?.split("T"))?.first()

            // Make data for chart

            entriesLine = arrayListOf()

            val yValues: ArrayList<Float> = arrayListOf()

            repeat(chartData.prices.size) {
                yValues.add(chartData.prices[it].last().toString().toFloat())
            }
            Log.e("test1", yValues.last().toString())

            // Make and configure chart

            repeat(chartData.prices.size) {
                entriesLine.add(Entry(it.toFloat(), yValues[it]))
            }
            Log.e("test2", entriesLine.size.toString())
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