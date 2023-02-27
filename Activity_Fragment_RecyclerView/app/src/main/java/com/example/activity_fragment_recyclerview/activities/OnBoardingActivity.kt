package com.example.activity_fragment_recyclerview.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.children
import androidx.viewpager2.widget.ViewPager2
import com.example.activity_fragment_recyclerview.R
import com.example.activity_fragment_recyclerview.adapters.ViewPagerAdapter
import com.example.activity_fragment_recyclerview.databinding.ActivityOnboardingBinding

class OnBoardingActivity : AppCompatActivity(), OnClickListener {
    private val binding by lazy { ActivityOnboardingBinding.inflate(layoutInflater) }
    private val sharedPreferences by lazy { getSharedPreferences("Preferences", MODE_PRIVATE) }
    private var viewPagerAdapter: ViewPagerAdapter? = null
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (sharedPreferences.getBoolean("onboarding", false)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        setContentView(binding.root)
        initView()
        initListener()
    }

    private fun initView() {
        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewpagerOnboarding.adapter = viewPagerAdapter
    }

    private fun initListener() {
        binding.tvSkipOnboarding.setOnClickListener(this)
        binding.imgBackFragmentOnboarding.setOnClickListener(this)
        binding.btnNextFragmentOnboarding.setOnClickListener(this)
        binding.viewpagerOnboarding.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setBackground(position)
            }
        })
    }

    private fun setBackground(pos: Int) {
        position = pos
        binding.imgBackFragmentOnboarding.visibility = if (pos > 0) View.VISIBLE else View.GONE
        binding.btnNextFragmentOnboarding.text = if (pos < 2) "Next" else "Start"
        binding.llSelectedFragmentOnboarding.children.forEachIndexed { index, view ->
            var idResBg = R.drawable.bg_not_selected_fragment_onboarding
            if (index == pos) idResBg = R.drawable.bg_selected_fragment_onboarding
            view.background = AppCompatResources.getDrawable(this, idResBg)
        }
    }

    private fun saveCompletedOnBoarding() {
        sharedPreferences?.edit()?.putBoolean("onboarding", true)?.apply()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.img_back_fragment_onboarding -> {
                binding.viewpagerOnboarding.currentItem = position - 1
            }
            R.id.btn_next_fragment_onboarding -> {
                if (position < 2) {
                    binding.viewpagerOnboarding.currentItem = position + 1
                } else {
                    saveCompletedOnBoarding()
                }
            }
            R.id.tv_skip_onboarding -> {
                saveCompletedOnBoarding()
            }
        }
    }
}