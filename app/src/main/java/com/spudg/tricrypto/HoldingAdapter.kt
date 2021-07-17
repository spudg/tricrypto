package com.spudg.tricrypto

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.spudg.tricrypto.databinding.CoinRowBinding
import com.spudg.tricrypto.databinding.HoldingRowBinding
import drewcarlson.coingecko.CoinGeckoClient
import drewcarlson.coingecko.models.coins.CoinMarkets
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat
import java.text.NumberFormat


class HoldingAdapter(private val context: Context, private val holdings: ArrayList<HoldingModel>) :
    RecyclerView.Adapter<HoldingAdapter.HoldingViewHolder>() {

    private val coinGecko = CoinGeckoClient.create()

    inner class HoldingViewHolder(val binding: HoldingRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoldingViewHolder {
        val binding = HoldingRowBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return HoldingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HoldingViewHolder, position: Int) {
        setUpRow(holder)
    }

    override fun getItemCount(): Int {
        return holdings.size
    }

    private fun setUpRow(holder: HoldingViewHolder) = runBlocking {
        launch {

            with(holder) {

                val holding = holdings[position]

                val usdFormatter: NumberFormat = DecimalFormat("$#,###.00")
                val percentFormatter: NumberFormat = DecimalFormat("#,##0.00%")
                val amountFormatter: NumberFormat = DecimalFormat("#,###.####")

                if (context is MainActivity) {
                    val price = coinGecko.getCoinMarkets("usd", holding.id).markets[0].currentPrice.toString()
                    val amount = context.getAmount(holding.symbol)
                    val cost = context.getCost(holding.symbol)
                    val value = (price.toFloat() * amount.toFloat()).toString()
                    val symbol = holding.symbol.uppercase()
                    val percentageChange = (value.toFloat() - cost.toFloat())/cost.toFloat()
                    Glide.with(context)
                        .load(coinGecko.getCoinMarkets("usd", holding.id).markets[0].image)
                        .into(binding.logo)
                    binding.amountAndSymbol.text = "${amountFormatter.format(amount.toFloat())} $symbol"
                    binding.cost.text = "Cost = ${usdFormatter.format(cost.toFloat())}"
                    binding.price.text = "1 " + symbol + " = " + usdFormatter.format(price.toFloat())
                    binding.value.text = usdFormatter.format(value.toFloat())
                    binding.percentageChange.text = percentFormatter.format(percentageChange)
                    if (percentageChange < 0) {
                        binding.percentageChange.setTextColor(Color.RED)
                    } else {
                        binding.percentageChange.setTextColor(Color.GREEN)
                    }
                }

            }

        }
    }

}