package com.spudg.tricrypto

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.spudg.tricrypto.databinding.ActivityMainBinding
import drewcarlson.coingecko.CoinGeckoClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private lateinit var bindingMain: ActivityMainBinding

    private val coinGecko = CoinGeckoClient.create()

    private var price = "0"

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

    fun getPrice(id: String) = runBlocking {
        launch {
            price = coinGecko.getPrice(id, "usd").values.first().toString()
        }
        return@runBlocking price
    }

    fun getAmount(symbol: String): String {
        val db = HoldingHandler(this, null)
        return db.getAmount(symbol)
    }

    fun getCost(symbol: String): String {
        val db = HoldingHandler(this, null)
        return db.getCost(symbol)
    }




}