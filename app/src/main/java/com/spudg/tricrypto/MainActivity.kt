package com.spudg.tricrypto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.spudg.tricrypto.databinding.ActivityMainBinding
import drewcarlson.coingecko.CoinGeckoClient
import drewcarlson.coingecko.models.coins.CoinMarkets
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

        setTotalPortfolioValue()
        setUpHoldingList()

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

    private fun setTotalPortfolioValue() = runBlocking {
        launch {

            val dbCrypto = HoldingHandler(this@MainActivity, null)
            val dbCash = CashHandler(this@MainActivity, null)
            val holdings = dbCrypto.getAllHoldings()
            var runningTotal = 0.00
            for (holding in holdings) {
                runningTotal += holding.amount.toFloat() * coinGecko.getCoinMarkets("usd", holding.id).markets[0].currentPrice.toString().toFloat()
            }

            val usdFormatter: NumberFormat = DecimalFormat("$#,##0.00")
            bindingMain.portfolioHeading.text = "Portfolio - " + usdFormatter.format(runningTotal + dbCash.getCashBal().toFloat())
            bindingMain.cryptoValue.text = "Crypto - " + usdFormatter.format(runningTotal)
            bindingMain.cashValue.text = "Cash - " + usdFormatter.format(dbCash.getCashBal().toFloat())

        }
    }

    fun selectCoin(id: String, symbol: String) {
        Globals.SELECTED_COIN_ID = id
        Globals.SELECTED_COIN_SYM = symbol
        val intent = Intent(this, CoinActivity::class.java)
        startActivity(intent)
    }




}