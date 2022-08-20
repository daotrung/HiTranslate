package com.daotrung.hitranslate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.daotrung.hitranslate.R
import com.daotrung.hitranslate.model.ListLanguage

class SpinnerChooseLanguageAdapter(private var data:ArrayList<ListLanguage>) :
   RecyclerView.Adapter<SpinnerChooseLanguageAdapter.MyChooseViewHolder>(){

       private lateinit var mListener : onItemClickListener

       interface onItemClickListener{
           fun onItemClick(position: Int, item : ListLanguage)
       }

       fun setOnItemClickListener(listener: onItemClickListener){
           mListener = listener
       }

       inner class MyChooseViewHolder(val view : View):RecyclerView.ViewHolder(view){
           fun bind(listLanguage:ListLanguage){
               val imgFlag = view.findViewById<ImageView>(R.id.imgFlagChoose)
               imgFlag.setImageResource(listLanguage.imgFlag)
               val txtNameLanguage = view.findViewById<TextView>(R.id.txtNameChooseLanguage)
               txtNameLanguage.text = listLanguage.txtNameLanguage

               itemView.setOnClickListener {
                   mListener.onItemClick(layoutPosition,listLanguage)
               }
           }
       }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyChooseViewHolder {
        val context: Context = parent.context
        val inflater = LayoutInflater.from(context)
        var view : View? = null
        view = inflater.inflate(R.layout.item_choose_language,parent,false)
        return MyChooseViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: MyChooseViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun filterList(filterList: ArrayList<ListLanguage>) {
          data = filterList
          notifyDataSetChanged()

    }
}