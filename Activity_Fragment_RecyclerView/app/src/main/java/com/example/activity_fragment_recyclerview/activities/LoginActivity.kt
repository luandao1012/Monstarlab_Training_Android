package com.example.activity_fragment_recyclerview.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<LoginFragment>(R.id.layout_fragment_login)
            }
        }
    }

    private fun initView() {
        loginFragment.callbackLoginFragment = this
        signupFragment.callbackSignupFragment = this
    }

    override fun backSignup() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.layout_fragment_login, signupFragment)
        }
    }

    override fun backLogin() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.layout_fragment_login, loginFragment)
        }
    }

    override fun sendDataForLogin(email: String, password: String) {
        loginFragment.getDataFromSignup(email, password)
    }
}