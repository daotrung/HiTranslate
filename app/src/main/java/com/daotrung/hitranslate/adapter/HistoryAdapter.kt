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
import com.daotrung.hitranslate.base.Params
import com.daotrung.hitranslate.fragment.TextTranslateFragment
import com.daotrung.hitranslate.model.TranslateHistory
import com.daotrung.hitranslate.utils.AppUtils.shareFile
import com.daotrung.hitranslate.utils.AppUtils.showText

class HistoryAdapter(private val context: Context) :
    PagedListAdapter<TranslateHistory, BaseViewHolder<*>>(DataDifferntiator) {

    @SuppressLint("UseCompatLoadingForDrawables")
    inner class ViewHolder(itemView: View) : BaseViewHolder<TranslateHistory>(itemView) {
        val imgFlagIn: ImageView by lazy {
            itemView.findViewById(R.id.imgFlagInMyHistoryIn) as ImageView
        }
        val txtIn: TextView by lazy {
            itemView.findViewById<TextView>(R.id.txtHistoryIn) as TextView
        }

        val imgFlagOut: ImageView by lazy {
            itemView.findViewById<ImageView>(R.id.imgFlagInMyHistoryOut) as ImageView
        }

        val txtOut: TextView by lazy {
            itemView.findViewById<TextView>(R.id.txtHistoryOut) as TextView
        }

        val imgShare: ImageView by lazy {
            itemView.findViewById<ImageView>(R.id.imgReturnRec) as ImageView
        }

        override fun bind(item: TranslateHistory) {
            imgFlagIn.setImageResource(item.imgFlagIn)
            txtIn.text = item.txtIn
            txtIn.textSize = App.fontSize.toFloat()
            imgFlagOut.setImageResource(item.imgFlagOut)
            txtOut.text = item.txtOut
            txtOut.textSize = App.fontSize.toFloat()
            imgShare.setOnClickListener {
                context.shareFile(item.txtOut)
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
        var studentView: View? = null

        when (viewType) {
            Params.TYPE1 -> studentView = inflater.inflate(
                R.layout.item_my_translate_history_type_one,
                parent, false
            )
            Params.TYPE2 -> studentView = inflater.inflate(
                R.layout.item_my_translate_history_type_two,
                parent, false
            )
        }

        return ViewHolder(studentView!!)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        (holder as ViewHolder).bind(getItem(position) as TranslateHistory)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0)
            Params.TYPE2
        else
            Params.TYPE1
    }
}