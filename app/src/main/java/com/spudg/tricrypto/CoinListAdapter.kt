package com.spudg.tricrypto

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.spudg.tricrypto.databinding.CoinRowBinding
import drewcarlson.coingecko.models.coins.CoinFullData

class CoinListAdapter(private val coins: ArrayList<CoinFullData>) :
    RecyclerView.Adapter<CoinListAdapter.CoinViewHolder>() {

    inner class CoinViewHolder(val binding: CoinRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        val binding = CoinRowBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return CoinViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {

        with(holder) {
            val coin = coins[position]

            binding.name.text = coin.name



        }

    }

    override fun getItemCount(): Int {
        return coins.size
    }

}