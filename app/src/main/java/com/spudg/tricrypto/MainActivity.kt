package com.spudg.tricrypto

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.spudg.tricrypto.databinding.ActivityMainBinding
import drewcarlson.coingecko.CoinGeckoClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
        }
    }

    private fun getPrice(coin: String) = runBlocking {
        launch {
            bindingMain.noHoldings.text = coinGecko.getPrice(coin, "usd").getValue(coin).toString()
        }
    }


}