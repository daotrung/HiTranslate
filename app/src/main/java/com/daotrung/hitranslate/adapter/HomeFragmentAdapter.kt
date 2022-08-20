package com.daotrung.hitranslate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.daotrung.hitranslate.R
import com.daotrung.hitranslate.model.HomeFIcon

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.daotrung.hitranslate.activity.MainActivity
import com.daotrung.hitranslate.fragment.CameraTranslateFragment
import com.daotrung.hitranslate.fragment.HomeFragment
import com.daotrung.hitranslate.fragment.MyTranslateFragment
import com.daotrung.hitranslate.fragment.TextTranslateFragment
import com.daotrung.hitranslate.utils.AppUtils


class HomeFragmentAdapter(private val data: List<HomeFIcon>) :
    RecyclerView.Adapter<HomeFragmentAdapter.MyViewHolder>() {
    inner class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(homeFIcon: HomeFIcon) {
            val height = AppUtils.screenWidth() / 2 - AppUtils.dpToPx(15)
            val with = AppUtils.screenWidth() / 3
            val imgView: ImageView = view.findViewById(R.id.imgViewHF)
            val txtView: TextView = view.findViewById(R.id.txtHF)

            imgView.layoutParams.height = height
            imgView.layoutParams.width = with

            txtView.text = homeFIcon.nameTxt
            imgView.setImageResource(homeFIcon.img)
            imgView.setOnClickListener {
                if (homeFIcon.nameTxt == "Camera Translate") {
                    val activityMain = view.context as MainActivity
                    activityMain.setIconCameraTranslate()
                }
                if (homeFIcon.nameTxt == "Text Translate") {
                    val activityMain = view.context as MainActivity
                    activityMain.setIconTextTranslate()
                }
                if (homeFIcon.nameTxt == "My Translate") {
                    val activityMain = view.context as MainActivity
                    activityMain.setIconMyTranslate()
                }

            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_home_fragment, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}