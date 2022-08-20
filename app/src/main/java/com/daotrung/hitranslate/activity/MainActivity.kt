package com.daotrung.hitranslate.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.daotrung.hitranslate.App
import com.daotrung.hitranslate.R
import com.daotrung.hitranslate.databinding.ActivityMainBinding
import com.daotrung.hitranslate.fragment.*
import com.daotrung.hitranslate.model.TranslateHistory
import com.daotrung.hitranslate.utils.LogInstance
import com.daotrung.hitranslate.utils.admob.InterstitialManager
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val homeFragment = HomeFragment()
    private val voiceFragment = VoiceTranslateFragment()
    private val cameraTranslateFragment = CameraTranslateFragment()
    private val myTranslateFragment = MyTranslateFragment()
    private val textTranslateFragment = TextTranslateFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        App.historyViewModel.loadData()
        App.historyViewModel.getHistory30Days()
            .observe(this@MainActivity, Observer { responseBody ->
                responseBody?.let {
                    it.forEach { item ->
                        App.getDb().appDao().deleteHistory(item)
                    }
                }
            })

        setCurrentFragment(homeFragment)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    setCurrentFragment(homeFragment)
                }
                R.id.voice -> {
                    setCurrentFragment(voiceFragment)
                }
                R.id.camera -> {
                    setCurrentFragment(cameraTranslateFragment)
                }
                R.id.text -> {
                    setCurrentFragment(textTranslateFragment)
                }
                R.id.document -> {
                    setCurrentFragment(myTranslateFragment)
                }
            }
            true
        }

        initReview()
    }

    private fun setCurrentFragment(fragment: Fragment) =
        this@MainActivity.supportFragmentManager.beginTransaction().apply {
            InterstitialManager.showAds(this@MainActivity)
            replace(R.id.fragment_container, fragment).commit()
        }

    private fun setFragment(fragment: Fragment) =
        this@MainActivity.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment).commit()

    fun setIconHomeNavigation() {
        setFragment(homeFragment)
        binding.bottomNavigationView.selectedItemId = R.id.home
    }

    fun setIconVoiceNavigation() {
        setFragment(voiceFragment)
        binding.bottomNavigationView.selectedItemId = R.id.voice
    }

    fun setIconCameraTranslate() {
        setFragment(cameraTranslateFragment)
        binding.bottomNavigationView.selectedItemId = R.id.camera
    }

    fun setIconTextTranslate() {
        setFragment(textTranslateFragment)
        binding.bottomNavigationView.selectedItemId = R.id.text
    }

    fun setIconMyTranslate() {
        setFragment(myTranslateFragment)
        binding.bottomNavigationView.selectedItemId = R.id.document
    }

    fun setTextFragment(item: TranslateHistory) {
        val bundle = Bundle()
        bundle.putSerializable("item", item)
        textTranslateFragment.arguments = bundle
        setFragment(textTranslateFragment)
        binding.bottomNavigationView.selectedItemId = R.id.text
    }


    private var reviewManager: ReviewManager? = null
    private fun initReview() {
        reviewManager = ReviewManagerFactory.create(this)
        val request = reviewManager?.requestReviewFlow()
        request?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = reviewManager?.launchReviewFlow(this, reviewInfo)
                flow?.addOnCompleteListener { _ ->
                    LogInstance.e("_______  Review success")
                }
            } else {
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (App.interstitialAd == null) {
            InterstitialManager.loadInterstitial(this)
        }
    }
}
