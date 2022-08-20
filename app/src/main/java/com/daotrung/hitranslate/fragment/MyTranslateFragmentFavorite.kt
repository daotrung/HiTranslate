package com.daotrung.hitranslate.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.daotrung.hitranslate.App
import com.daotrung.hitranslate.BuildConfig
import com.daotrung.hitranslate.R
import com.daotrung.hitranslate.activity.MainActivity
import com.daotrung.hitranslate.adapter.FavoriteAdapter
import com.daotrung.hitranslate.base.BaseFragment
import com.daotrung.hitranslate.databinding.FragmentMyTranslateFavoriteBinding
import com.daotrung.hitranslate.utils.NetworkUtils
import com.daotrung.hitranslate.utils.admob.InterstitialManager
import com.daotrung.hitranslate.utils.billing.InAppManager
import com.google.android.gms.ads.*


class MyTranslateFragmentFavorite : BaseFragment() {
    private lateinit var binding: FragmentMyTranslateFavoriteBinding
    private var adapter: FavoriteAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyTranslateFavoriteBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // initId
        binding.btnFavoriteCheck.isEnabled = false
        binding.btnHistoryUnCheck.setOnClickListener {
            val myFragment: Fragment = MyTranslateFragment()
            (mActivity as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, myFragment).commit()
        }

        adapter = FavoriteAdapter(mActivity)
        binding.rvFavorite.layoutManager = LinearLayoutManager(mContext)
        binding.rvFavorite.adapter = adapter
        App.historyViewModel.getFavoritePage().observe(requireActivity(), Observer { responseBody ->
            responseBody?.let {
                adapter?.submitList(it)
            }
            if (responseBody.isEmpty()) {
                binding.imgEmpty.visibility = View.VISIBLE
            }
        })

        if (NetworkUtils.instance.isOnline() && !InAppManager.instance!!.isRemovedAds(mContext)) {
            val adView = AdView(mContext)
            val adRequest = AdRequest.Builder().build()
            adView.adUnitId = BuildConfig.adsBannerId
            adView.adSize = AdSize.BANNER
            adView.adListener = object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    binding.toolbarMyFavorite.visibility = View.GONE
                }

                override fun onAdLoaded() {
                    binding.toolbarMyFavorite.addView(adView)
                }
            }
            adView.loadAd(adRequest)
        } else {
            binding.toolbarMyFavorite.visibility = View.GONE
        }
    }


}