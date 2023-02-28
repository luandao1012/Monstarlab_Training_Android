package com.example.activity_fragment_recyclerview.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.activity_fragment_recyclerview.R
import com.example.activity_fragment_recyclerview.databinding.ActivityLoginBinding
import com.example.activity_fragment_recyclerview.fragments.login.LoginFragment
import com.example.activity_fragment_recyclerview.fragments.login.SignUpFragment

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportFragmentManager.commit {
            add(R.id.layout_fragment_login, LoginFragment())
            addToBackStack("login")
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}