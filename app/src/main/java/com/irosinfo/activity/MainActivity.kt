package com.irosinfo.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.bases.bases_module.base_activity.BaseActivity
import com.irosinfo.R
import com.irosinfo.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding, Int>() {

    override val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override val navHostResourceId = R.id.navHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

    }
}