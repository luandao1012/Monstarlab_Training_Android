package com.example.activity_fragment_recyclerview

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.example.activity_fragment_recyclerview.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {
    private val binding by lazy { ActivityOnboardingBinding.inflate(layoutInflater) }
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var listFragmentOnboarding: List<ImageView>
    private var sharedPreferences: SharedPreferences? = null
    private var checkOnboarding: Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        binding.viewpagerOnboarding.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setBackground(position)
            }
        })
        if (checkOnboarding == true) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun init() {
        listFragmentOnboarding = listOf(
            binding.imgSelectedFragment1,
            binding.imgSelectedFragment2,
            binding.imgSelectedFragment3
        )
        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewpagerOnboarding.adapter = viewPagerAdapter
        sharedPreferences = getSharedPreferences("Preferences", MODE_PRIVATE)
        checkOnboarding = sharedPreferences?.getBoolean("onboarding", false)
    }

    private fun setBackground(position: Int) {
        binding.imgReturnFragmentOnboarding.let {
            it.visibility = if (position > 0) View.VISIBLE else View.GONE
        }
        binding.btnNextOnboarding.let { btn ->
            if (position < 2) {
                btn.setOnClickListener {
                    binding.viewpagerOnboarding.currentItem = position + 1
                }
                btn.text = "Next"
            } else {
                btn.setOnClickListener {
                    sharedPreferences?.edit()?.putBoolean("onboarding", true)?.apply()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                btn.text = "Start"
            }
            binding.imgReturnFragmentOnboarding.setOnClickListener{
                binding.viewpagerOnboarding.currentItem = position - 1
            }
        }
        binding.tvSkipOnboarding.setOnClickListener {
            binding.viewpagerOnboarding.currentItem = 2
        }
        for (i in listFragmentOnboarding.indices) {
            if (i == position) {
                listFragmentOnboarding[i].setImageResource(R.drawable.bg_selected_fragment_onboarding)
            } else {
                listFragmentOnboarding[i].setImageResource(R.drawable.bg_not_selected_fragment_onboarding)
            }
        }
    }
}