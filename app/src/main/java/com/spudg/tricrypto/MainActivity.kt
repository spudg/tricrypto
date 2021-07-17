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
            val holdingAdapter = HoldingAdapter(this, db.getAllHoldings())
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

            val db = HoldingHandler(this@MainActivity, null)
            val holdings = db.getAllHoldings()
            var runningTotal = 0.00
            for (holding in holdings) {
                runningTotal += holding.amount.toFloat() * coinGecko.getCoinMarkets("usd", holding.id).markets[0].currentPrice.toString().toFloat()
            }

            val usdFormatter: NumberFormat = DecimalFormat("$#,##0.00")
            bindingMain.portfolioValue.text = "Total value: " + usdFormatter.format(runningTotal)

        }
    }

    fun selectCoin(id: String, symbol: String) {
        Globals.SELECTED_COIN_ID = id
        Globals.SELECTED_COIN_SYM = symbol
        val intent = Intent(this, CoinActivity::class.java)
        startActivity(intent)
    }




}