package com.daotrung.hitranslate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.daotrung.hitranslate.R
import com.daotrung.hitranslate.model.OnBoardingItem

class OnBoardingAdapter(private val onBoardingItem: List<OnBoardingItem>):
        RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder>(){
    inner class OnBoardingViewHolder(view:View):RecyclerView.ViewHolder(view) {
           private val imgOnBoarding = view.findViewById<ImageView>(R.id.imgOnBoard)
           private val titleOnBoarding = view.findViewById<TextView>(R.id.titleOnBoard)
           private val desOnBoarding = view.findViewById<TextView>(R.id.desOnBoard)

        fun bind(onBoardingItem: OnBoardingItem){
            imgOnBoarding.setImageResource(onBoardingItem.imageOnBoarding)
            titleOnBoarding.text = onBoardingItem.titleOnBoarding
            desOnBoarding.text = onBoardingItem.descriptionOnBoarding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder {
        return OnBoardingViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_onboard,parent,
                false)
        )
    }

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        holder.bind(onBoardingItem[position])
    }

    override fun getItemCount(): Int {
        return onBoardingItem.size
    }
}