package com.daotrung.hitranslate.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daotrung.hitranslate.R
import com.daotrung.hitranslate.activity.MainActivity
import com.daotrung.hitranslate.activity.MenuActivity
import com.daotrung.hitranslate.adapter.HomeFragmentAdapter
import com.daotrung.hitranslate.base.BaseFragment
import com.daotrung.hitranslate.model.HomeFIcon

class HomeFragment : BaseFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var arrayList: ArrayList<HomeFIcon>
    lateinit var imgId: Array<Int>
    lateinit var heading: Array<String>
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var voiceBanner: ImageView
    private lateinit var menuDraw: ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        //init Id
        toolbar = view.findViewById(R.id.toolbarMenuHome)
        voiceBanner = view.findViewById(R.id.voiceBanner)
        menuDraw = view.findViewById(R.id.imgMenuDraw)
        recyclerView = view.findViewById(R.id.rv_translate_icon)

        // setColorStatusBar
        changeColorStatusBarAndToolbar()

        //setOnClickListener
        voiceBanner.setOnClickListener {
            (requireActivity() as MainActivity).setIconVoiceNavigation()
        }
        menuDraw.setOnClickListener {
            startActivity(Intent(view.context, MenuActivity::class.java))
        }

        // setDataRecyclerView
        imgId = arrayOf(
            R.drawable.camera_translate, R.drawable.text_translate, R.drawable.my_translate
        )
        heading = arrayOf(
            "Camera Translate", "Text Translate", "My Translate"
        )

        // recyclerView
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)

        arrayList = arrayListOf()

        getData()

        return view
    }

    private fun changeColorStatusBarAndToolbar() {
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        requireActivity().window.statusBarColor = Color.rgb(255, 255, 255)
    }

    private fun getData() {
        for (i in imgId.indices) {
            val homfIcon = HomeFIcon(imgId[i], heading[i])
            arrayList.add(homfIcon)
        }
        recyclerView.adapter = HomeFragmentAdapter(arrayList)
    }

}