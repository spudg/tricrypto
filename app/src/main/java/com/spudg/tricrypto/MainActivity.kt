package com.spudg.tricrypto

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.spudg.tricrypto.databinding.ActivityMainBinding
import drewcarlson.coingecko.CoinGeckoClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {

    private lateinit var bindingMain: ActivityMainBinding

    private val coinGecko = CoinGeckoClient.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMain = ActivityMainBinding.inflate(layoutInflater)
        val view = bindingMain.root
        setContentView(view)

        bindingMain.marketBtn.setOnClickListener {
            val intent = Intent(this, MarketActivity::class.java)
            startActivity(intent)
            finish()
        }

        val dbCash = CashHandler(this, null)
        if (dbCash.getCashBal() == "-1") {
            dbCash.setInitial()
        }

        cleanHoldings()
        setTotalPortfolioInfo()
        setUpHoldingList()

    }

    private fun cleanHoldings() {
        val db = HoldingHandler(this, null)
        val holdings = db.getAllHoldings()
        for (item in holdings) {
            if (item.amount.toFloat() <= 0) {
                db.removeHolding(item.symbol)
            }
        }
    }

    private fun setUpHoldingList() {
        val db = HoldingHandler(this, null)
        val holdings = db.getAllHoldings()

        if (holdings.size > 0) {
            bindingMain.rvHoldings.visibility = View.VISIBLE
            bindingMain.noHoldings.visibility = View.GONE
            val manager = LinearLayoutManager(this)
            bindingMain.rvHoldings.layoutManager = manager
            val holdingAdapter = HoldingAdapter(this, holdings)
            bindingMain.rvHoldings.adapter = holdingAdapter
        } else {
            bindingMain.rvHoldings.visibility = View.GONE
            bindingMain.noHoldings.visibility = View.VISIBLE
        }

    }

    fun getAmount(symbol: String): String {
        val db = HoldingHandler(this, null)
        return db.getAmount(symbol)
    }

    fun getCost(symbol: String): String {
        val db = HoldingHandler(this, null)
        return db.getCost(symbol)
    }

    private fun setTotalPortfolioInfo() = runBlocking {
        launch {

            val dbCrypto = HoldingHandler(this@MainActivity, null)
            val dbCash = CashHandler(this@MainActivity, null)
            val holdings = dbCrypto.getAllHoldings()
            var runningTotal = 0.00
            val allCoins = coinGecko.getCoinMarkets("usd").markets
            for (holding in holdings) {
                for (coin in allCoins) {
                    if (coin.id == holding.id) {
                        runningTotal += holding.amount.toFloat() * coin.currentPrice.toFloat()

                    }
                }
            }
            val totalReturn = (((runningTotal+(dbCash.getCashBal()).toFloat()))/100000)-1

            val usdFormatter: NumberFormat = DecimalFormat("$#,##0.00")
            val percentFormatter: NumberFormat = DecimalFormat("#,##0.00%")
            bindingMain.portfolioHeading.text =
                "Portfolio - " + usdFormatter.format(runningTotal + dbCash.getCashBal().toFloat())
            if (totalReturn >= 0) {
                bindingMain.totalReturn.setTextColor(Color.GREEN)
            } else {
                bindingMain.totalReturn.setTextColor(Color.RED)
            }
            bindingMain.totalReturn.text = percentFormatter.format(totalReturn)
            bindingMain.cryptoValue.text = "Crypto - " + usdFormatter.format(runningTotal)
            bindingMain.cashValue.text =
                "Cash - " + usdFormatter.format(dbCash.getCashBal().toFloat())

        }
    }

    fun selectCoin(id: String, symbol: String) {
        Globals.SELECTED_COIN_ID = id
        Globals.SELECTED_COIN_SYM = symbol
        val intent = Intent(this, CoinActivity::class.java)
        startActivity(intent)
    }


}