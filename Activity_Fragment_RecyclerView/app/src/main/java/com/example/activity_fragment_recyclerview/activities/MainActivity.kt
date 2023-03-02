package com.example.activity_fragment_recyclerview.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.activity_fragment_recyclerview.R
import com.example.activity_fragment_recyclerview.databinding.ActivityMainBinding
import com.example.activity_fragment_recyclerview.fragments.login.LoginFragment
import com.example.activity_fragment_recyclerview.fragments.main.CoinFragment
import com.example.activity_fragment_recyclerview.fragments.main.HomeFragment
import com.example.activity_fragment_recyclerview.fragments.main.MenuFragment
import com.example.activity_fragment_recyclerview.fragments.main.NewsFragment
import com.google.android.material.shape.ShapeAppearanceModel

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        initListeners()
    }

    private fun initViews() {
        replaceFragment(HomeFragment())
    }

    private fun initListeners() {
        binding.myBottomNavNar.getIndexItemSelected {
            when (it) {
                0 -> replaceFragment(HomeFragment())
                1 -> replaceFragment(CoinFragment())
                2 -> replaceFragment(NewsFragment())
                3 -> replaceFragment(MenuFragment())
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.fragment_container_view, fragment)
            addToBackStack(null)
        }
    }
}