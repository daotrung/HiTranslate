package com.daotrung.hitranslate.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.daotrung.hitranslate.App
import com.daotrung.hitranslate.R
import com.daotrung.hitranslate.activity.MainActivity
import com.daotrung.hitranslate.base.BaseViewHolder
import com.daotrung.hitranslate.fragment.TextTranslateFragment
import com.daotrung.hitranslate.model.TranslateHistory
import com.daotrung.hitranslate.utils.AppUtils.showText
import com.daotrung.hitranslate.utils.widget.CustomToast

class FavoriteAdapter(private val context: Context) :
    PagedListAdapter<TranslateHistory, BaseViewHolder<*>>(DataDifferntiator) {

    inner class ViewHolder(itemView: View) : BaseViewHolder<TranslateHistory>(itemView) {
        private val imgFlagIn: ImageView by lazy {
            itemView.findViewById(R.id.imgFlagInMyFavoriteIn) as ImageView
        }
        private val txtIn: TextView by lazy {
            itemView.findViewById<TextView>(R.id.txtFavoriteIn) as TextView
        }

        private val imgFlagOut: ImageView by lazy {
            itemView.findViewById<ImageView>(R.id.imgFlagInMyFavoriteOut) as ImageView
        }

        private val txtOut: TextView by lazy {
            itemView.findViewById<TextView>(R.id.txtFavoriteOut) as TextView
        }

        private val imgUnFavorite: ImageView by lazy {
            itemView.findViewById<ImageView>(R.id.imgHeartFavorite) as ImageView
        }

        override fun bind(item: TranslateHistory) {
            imgFlagIn.setImageResource(item.imgFlagIn)
            txtIn.text = item.txtIn
            imgFlagOut.setImageResource(item.imgFlagOut)
            txtOut.text = item.txtOut
            imgUnFavorite.setOnClickListener {
                App.getDb().appDao().updateFavorite(false, item.txtOut,item.txtIn)
                CustomToast.makeText(
                    context,
                    "Delete successful",
                    CustomToast.LENGTH_SHORT,
                    CustomToast.SUCCESS,
                    true
                ).show()
            }


            itemView.setOnClickListener {
                (context as MainActivity).setTextFragment(item)
            }
        }
    }

    object DataDifferntiator : DiffUtil.ItemCallback<TranslateHistory>() {

        override fun areItemsTheSame(
            oldItem: TranslateHistory,
            newItem: TranslateHistory
        ): Boolean {
            return oldItem.idFavorite == (newItem.idFavorite)
        }

        override fun areContentsTheSame(
            oldItem: TranslateHistory,
            newItem: TranslateHistory
        ): Boolean {
            return (oldItem as TranslateHistory) == (newItem as TranslateHistory)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val context: Context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_my_translate_favorite,parent,false)
        return ViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        (holder as ViewHolder).bind(getItem(position) as TranslateHistory)
    }


}