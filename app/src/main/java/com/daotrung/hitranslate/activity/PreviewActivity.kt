package com.daotrung.hitranslate.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.daotrung.hitranslate.App
import com.daotrung.hitranslate.R
import com.daotrung.hitranslate.adapter.OnBoardingAdapter
import com.daotrung.hitranslate.base.Params.KEY_OPEN_APP
import com.daotrung.hitranslate.databinding.ActivityPreviewBinding
import com.daotrung.hitranslate.model.OnBoardingItem

class PreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreviewBinding
    private lateinit var onBoardingAdapter: OnBoardingAdapter
    private lateinit var indicator: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewBinding.inflate(layoutInflater)

        setFullScreen()
        setContentView(binding.root)

        setOnBoardingItem()
        setUpIndicator()
        setCurrentIndicator(0)
    }

    private fun setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun setOnBoardingItem() {
        onBoardingAdapter = OnBoardingAdapter(
            listOf(
                OnBoardingItem(
                    imageOnBoarding = R.drawable.voice_translate_preview,
                    titleOnBoarding = "Voice Translate",
                    descriptionOnBoarding = "Lorem locum is simply dummy text of the printing and typesetting industry Lorem locum has been."
                ), OnBoardingItem(
                    imageOnBoarding = R.drawable.camera_translate_preview,
                    titleOnBoarding = "Camera Translate",
                    descriptionOnBoarding = "Lorem locum is simply dummy text of the printing and typesetting industry Lorem locum has been."
                ), OnBoardingItem(
                    imageOnBoarding = R.drawable.text_translate_preview,
                    titleOnBoarding = "Text Translate",
                    descriptionOnBoarding = "Lorem locum is simply dummy text of the printing and typesetting industry Lorem locum has been."
                )
            )
        )
        val onBoardingViewPager = findViewById<ViewPager2>(R.id.onBoardingViewPager)
        onBoardingViewPager.adapter = onBoardingAdapter
        onBoardingViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)

            }
        })

        (onBoardingViewPager.getChildAt(0) as RecyclerView).overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER


        binding.btnOne.setOnClickListener {
            if (onBoardingViewPager.currentItem + 1 < onBoardingAdapter.itemCount) {
                onBoardingViewPager.currentItem += 1
            } else {
                binding.btnOne.visibility = View.INVISIBLE
                binding.btnGetStarted.visibility = View.VISIBLE
                binding.btnGetStarted.setOnClickListener {
                    navigationMainActivity()
                }
            }
        }
        binding.txtSkip.setOnClickListener {
            navigationMainActivity()
        }

    }

    private fun navigationMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        App.dataPer.putBoolean(KEY_OPEN_APP, false)
    }

    private fun setUpIndicator() {

        val indicators = arrayOfNulls<ImageView>(onBoardingAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.indicator_inactive_background)
                )
                it.layoutParams = layoutParams
                binding.dot.addView(it)
            }
        }
    }

    private fun setCurrentIndicator(pos: Int) {
        val childCount = binding.dot.childCount
        for (i in 0 until childCount) {
            val imageView = binding.dot.getChildAt(i) as ImageView
            if (i == pos) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.indicator_active_background
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.indicator_inactive_background
                    )
                )
            }
        }
    }
}