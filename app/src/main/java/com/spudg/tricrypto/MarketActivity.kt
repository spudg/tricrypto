package com.spudg.tricrypto

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.spudg.tricrypto.databinding.ActivityMarketBinding
import drewcarlson.coingecko.CoinGeckoClient
import drewcarlson.coingecko.models.coins.CoinMarkets
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MarketActivity : AppCompatActivity() {

    private lateinit var bindingMarket: ActivityMarketBinding

    private val coinGecko = CoinGeckoClient.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMarket = ActivityMarketBinding.inflate(layoutInflater)
        val view = bindingMarket.root
        setContentView(view)

        bindingMarket.portfolioBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        bindingMarket.moreBtn.setOnClickListener {
            val popupMenu = PopupMenu(this, bindingMarket.moreBtn)
            popupMenu.menuInflater.inflate(R.menu.menu_popup, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.actionAbout -> {
                        val intent = Intent(this, AboutActivity::class.java)
                        startActivity(intent)
                    }
                }
                true
            }

            popupMenu.show()
        }

        var timerSec = 60

        val mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            override fun run() {
                if (timerSec > 0) {
                    timerSec -= 1
                }
                mainHandler.postDelayed(this, 1000)
            }
        })

        bindingMarket.btnRefresh.setOnClickListener {
            if (timerSec > 0) {
                Toast.makeText(this, "Please wait before refreshing again", Toast.LENGTH_SHORT)
                    .show()
            } else {
                setUpCoinList()
                Toast.makeText(this, "Prices refreshed", Toast.LENGTH_SHORT).show()
                timerSec = 60
            }
        }

        setUpCoinList()

    }

    private fun setUpCoinList() = runBlocking {
        launch {

            val coinList = coinGecko.getCoinMarkets("usd", null, null, 100, 1)
            val allCoins = arrayListOf<CoinMarkets>()

            repeat(coinList.markets.size) {
                allCoins.add(coinList.markets[it])
            }

            val manager = LinearLayoutManager(this@MarketActivity)
            bindingMarket.rvCoins.layoutManager = manager
            val coinAdapter = CoinListAdapter(this@MarketActivity, allCoins)
            bindingMarket.rvCoins.adapter = coinAdapter

        }
    }

    fun selectCoin(id: String, symbol: String) {
        Globals.SELECTED_COIN_ID = id
        Globals.SELECTED_COIN_SYM = symbol
        val intent = Intent(this, CoinActivity::class.java)
        startActivity(intent)
    }

}