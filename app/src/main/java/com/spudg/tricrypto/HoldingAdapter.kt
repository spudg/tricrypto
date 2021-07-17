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
import drewcarlson.coingecko.models.coins.CoinMarkets
import java.text.DecimalFormat
import java.text.NumberFormat


class HoldingAdapter(private val context: Context, private val holdings: ArrayList<HoldingModel>) :
    RecyclerView.Adapter<HoldingAdapter.HoldingViewHolder>() {

    inner class HoldingViewHolder(val binding: HoldingRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoldingViewHolder {
        val binding = HoldingRowBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return HoldingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HoldingViewHolder, position: Int) {

        val usdFormatter: NumberFormat = DecimalFormat("$#,##0.00")
        val percentFormatter: NumberFormat = DecimalFormat("#,##0.00%")

        with(holder) {

            val holding = holdings[position]

            if (context is MainActivity) {
                binding.amountAndSymbol.text = context.getAmount(holding.symbol) + " " + holding.symbol
                binding.cost.text = "Cost: ${usdFormatter.format(context.getCost(holding.symbol).toFloat())}"
                Log.e("getPrice",context.getPrice(holding.id))
                Log.e("test",holding.id)
                Log.e("getAmount",context.getAmount(holding.symbol))
                //Log.e("TEst", (context.getPrice(holding.id).toFloat()*context.getAmount(holding.symbol).toFloat()).toString())
            }


        }

    }

    override fun getItemCount(): Int {
        return holdings.size
    }

}