package com.daotrung.hitranslate.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.daotrung.hitranslate.App
import com.daotrung.hitranslate.R
import com.daotrung.hitranslate.adapter.HistoryAdapter
import com.daotrung.hitranslate.base.BaseFragment
import androidx.lifecycle.Observer
import com.daotrung.hitranslate.BuildConfig
import com.daotrung.hitranslate.databinding.FragmentMyTranslateBinding
import com.daotrung.hitranslate.utils.NetworkUtils
import com.daotrung.hitranslate.utils.billing.InAppManager
import com.google.android.gms.ads.*

class MyTranslateFragment : BaseFragment() {
    private var adapter: HistoryAdapter? = null

    private lateinit var binding: FragmentMyTranslateBinding

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().window.statusBarColor = Color.WHITE
        // Inflate the layout for this fragment
        binding = FragmentMyTranslateBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = HistoryAdapter(requireContext())
        binding.rvHistory.layoutManager = LinearLayoutManager(view.context)
        binding.rvHistory.adapter = adapter
        App.historyViewModel.getHistory().observe(requireActivity(), Observer { responseBody ->
            responseBody?.let { it ->
                adapter?.submitList(it)
            }
            if (responseBody.isEmpty()) {
                binding.imgEmpty.visibility = View.VISIBLE
            }
        })
        // setOnClicklistener
        binding.btnFavoriteUnCheck.setOnClickListener {
            val myFragment: Fragment = MyTranslateFragmentFavorite()
            val activity = view.context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, myFragment).commit()
        }

        if (NetworkUtils.instance.isOnline() && !InAppManager.instance!!.isRemovedAds(mContext)) {
            val adView = AdView(mContext)
            val adRequest = AdRequest.Builder().build()
            adView.adUnitId = BuildConfig.adsBannerId
            adView.adSize = AdSize.BANNER
            adView.adListener = object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    binding.toolbarMyHistory.visibility = View.GONE
                }

                override fun onAdLoaded() {
                    binding.toolbarMyHistory.addView(adView)
                }
            }
            adView.loadAd(adRequest)
        }else{
            binding.toolbarMyHistory.visibility = View.GONE
        }
    }


}