package com.spudg.tricrypto

import android.content.ClipData
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.spudg.tricrypto.databinding.ActivityMainBinding
import drewcarlson.coingecko.CoinGeckoClient
import kotlinx.coroutines.delay
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

        bindingMain.portfolioBtn.setOnClickListener {
            val intent = Intent(this, MarketActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getPrice(coin: String) = runBlocking {
        launch {
            bindingMain.noHoldings.text = coinGecko.getPrice(coin, "usd").getValue(coin).toString()
        }
    }


}