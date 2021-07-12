package com.spudg.tricrypto

import android.R
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.target.Target
import com.spudg.tricrypto.databinding.CoinRowBinding
import drewcarlson.coingecko.models.coins.CoinMarkets
import java.text.DecimalFormat
import java.text.NumberFormat


class CoinListAdapter(private val context: Context, private val coins: ArrayList<CoinMarkets>) :
    RecyclerView.Adapter<CoinListAdapter.CoinViewHolder>() {

    inner class CoinViewHolder(val binding: CoinRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        val binding = CoinRowBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return CoinViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {

        val usdFormatter: NumberFormat = DecimalFormat("$#,##0.00")
        val percentFormatter: NumberFormat = DecimalFormat("#,##0.00%")

        with(holder) {

            val coin = coins[position]

            binding.coinRow.setOnClickListener {
                if (context is MarketActivity) {
                    context.selectCoin(coin.id.toString())
                }
            }

            if (context is MarketActivity) {
                Glide.with(context)
                    .load(coin.image)
                    .into(binding.logo)
            }

            binding.name.text = "${coin.name} (${(coin.symbol)!!.uppercase()})"

            binding.dailyChange.text = percentFormatter.format(coin.priceChangePercentage24h/100)

            if (coin.priceChangePercentage24h < 0) {
                binding.dailyChange.setTextColor(Color.RED)
            }

            binding.price.text = usdFormatter.format(coin.currentPrice)

        }

    }

    override fun getItemCount(): Int {
        return coins.size
    }

}