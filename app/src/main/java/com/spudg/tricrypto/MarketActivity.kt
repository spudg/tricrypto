package com.spudg.tricrypto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

        setUpCoinList()
    }

    private fun setUpCoinList() = runBlocking {
        launch {

            val coinList = coinGecko.getCoinMarkets("usd",null,null,null,null,true)
            val allCoins = arrayListOf<CoinMarkets>()

            repeat (coinList.markets.size) {
                allCoins.add(coinList.markets[it])
            }

            val manager = LinearLayoutManager(this@MarketActivity)
            bindingMarket.rvCoins.layoutManager = manager
            val coinAdapter = CoinListAdapter(this@MarketActivity, allCoins)
            bindingMarket.rvCoins.adapter = coinAdapter

        }
    }

    fun selectCoin(coin: String) {
        Globals.SELECTED_COIN = coin
        val intent = Intent(this, CoinActivity::class.java)
        startActivity(intent)
    }

}