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

class LoginActivity : AppCompatActivity(), LoginFragment.CallbackLoginFragment,
    SignUpFragment.CallbackSignupFragment {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val loginFragment = LoginFragment()
    private val signupFragment = SignUpFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        supportFragmentManager.commit {
            add(R.id.layout_fragment_login, loginFragment)
            addToBackStack("login")
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        if(supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    private fun initView() {
        loginFragment.callbackLoginFragment = this
        signupFragment.callbackSignupFragment = this
    }

    override fun backSignup() {
        supportFragmentManager.commit {
            replace(R.id.layout_fragment_login, signupFragment)
            addToBackStack("login")
        }
    }

    override fun backLogin() {
        supportFragmentManager.popBackStack()
    }
}