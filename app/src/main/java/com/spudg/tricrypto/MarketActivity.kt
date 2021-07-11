package com.spudg.tricrypto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

            val coinList = coinGecko.getCoinMarkets("gbp",null,null,null,null,true)
            val topTenCoins: ArrayList<CoinMarkets> = arrayListOf(
                coinList.markets[0],
                coinList.markets[1],
                coinList.markets[2],
                coinList.markets[3],
                coinList.markets[4],
                coinList.markets[5],
                coinList.markets[6],
                coinList.markets[7],
                coinList.markets[8],
                coinList.markets[9],
                coinList.markets[10],
                coinList.markets[11],
                coinList.markets[12],
                coinList.markets[13],
                coinList.markets[14],
                coinList.markets[15],
                coinList.markets[16],
                coinList.markets[17],
                coinList.markets[18],
                coinList.markets[19],
                coinList.markets[20],
                coinList.markets[21],
                coinList.markets[22],
                coinList.markets[23],
                coinList.markets[24],
                coinList.markets[25],
                coinList.markets[26],
                coinList.markets[27],
                coinList.markets[28],
                coinList.markets[29])

            val manager = LinearLayoutManager(this@MarketActivity)
            bindingMarket.rvCoins.layoutManager = manager
            val coinAdapter = CoinListAdapter(topTenCoins)
            bindingMarket.rvCoins.adapter = coinAdapter

        }
    }

}