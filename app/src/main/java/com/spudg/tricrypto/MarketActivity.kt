package com.spudg.tricrypto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.spudg.tricrypto.databinding.ActivityMarketBinding
import drewcarlson.coingecko.CoinGeckoClient
import drewcarlson.coingecko.models.coins.CoinFullData
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

            val coinList = arrayListOf<CoinFullData>(
                coinGecko.getCoinById("bitcoin"),
                coinGecko.getCoinById("ethereum"),
                coinGecko.getCoinById("tether"),
                coinGecko.getCoinById("binancecoin"),
                coinGecko.getCoinById("cardano"),
                coinGecko.getCoinById("ripple"),
                coinGecko.getCoinById("dogecoin"),
                coinGecko.getCoinById("usd-coin"),
                coinGecko.getCoinById("polkadot"),
                coinGecko.getCoinById("binance-usd"),
                coinGecko.getCoinById("uniswap"),
                coinGecko.getCoinById("bitcoin-cash"),
                coinGecko.getCoinById("litecoin"),
                coinGecko.getCoinById("solana"),
                coinGecko.getCoinById("chainlink")
            )

            val manager = LinearLayoutManager(this@MarketActivity)
            bindingMarket.rvCoins.layoutManager = manager
            val coinAdapter = CoinListAdapter(coinList)
            bindingMarket.rvCoins.adapter = coinAdapter

        }
    }

}