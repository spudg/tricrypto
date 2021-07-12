package com.spudg.tricrypto

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.spudg.tricrypto.databinding.CoinRowBinding
import drewcarlson.coingecko.models.coins.CoinMarkets
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.collections.ArrayList

class CoinListAdapter(private val context: Context, private val coins: ArrayList<CoinMarkets>) :
    RecyclerView.Adapter<CoinListAdapter.CoinViewHolder>() {

    inner class CoinViewHolder(val binding: CoinRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        val binding = CoinRowBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return CoinViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {

        val gbpFormatter: NumberFormat = DecimalFormat("£#,##0.00")

        with(holder) {

            val coin = coins[position]

            binding.coinRow.setOnClickListener {
                if (context is MarketActivity) {
                    context.selectCoin(coin.id.toString())
                }
            }

            binding.name.text = "${coin.name} (${(coin.symbol)!!.uppercase()})"
            binding.dailyChange.text = gbpFormatter.format(coin.priceChangePercentage24h)
            if (coin.priceChangePercentage24h < 0) {
                binding.dailyChange.setTextColor(Color.RED)
            }
            binding.price.text = gbpFormatter.format(coin.currentPrice)

                }

            }

    override fun getItemCount(): Int {
        return coins.size
    }

}