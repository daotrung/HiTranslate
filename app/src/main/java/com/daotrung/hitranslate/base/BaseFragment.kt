package com.daotrung.hitranslate.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBindings
import com.daotrung.hitranslate.activity.MainActivity
import com.daotrung.hitranslate.utils.LogInstance

open class BaseFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogInstance.e(this@BaseFragment::class.java)
    }

    protected lateinit var mActivity: Activity
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mActivity = activity
    }

    protected lateinit var mContext: Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private var mPermission: String? = null

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isPermissionGranted(it, mPermission)
        }

    fun requestPermission(permission: String): Boolean {
        val isGranted =
            ContextCompat.checkSelfPermission(
                mContext,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        if (!isGranted) {
            mPermission = permission
            permissionLauncher.launch(permission)
        }
        return isGranted
    }

    open fun isPermissionGranted(isGranted: Boolean, permission: String?) {}

    fun onBack() {
        (mActivity as MainActivity).setIconHomeNavigation()
    }
}