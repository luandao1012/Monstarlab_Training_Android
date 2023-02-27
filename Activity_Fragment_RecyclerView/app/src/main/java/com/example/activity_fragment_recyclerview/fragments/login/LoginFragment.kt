package com.example.activity_fragment_recyclerview.fragments.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.view.ContentInfoCompat.Flags
import com.example.activity_fragment_recyclerview.R
import com.example.activity_fragment_recyclerview.activities.MainActivity
import com.example.activity_fragment_recyclerview.databinding.FragmentLoginBinding

class LoginFragment() : Fragment(), OnClickListener {
    private lateinit var loginBinding: FragmentLoginBinding
    private var email = ""
    private var password = ""
    var callbackLoginFragment: CallbackLoginFragment? = null

    interface CallbackLoginFragment {
        fun backSignup()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbackLoginFragment = context as CallbackLoginFragment
    }

    override fun onDetach() {
        super.onDetach()
        callbackLoginFragment = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loginBinding = FragmentLoginBinding.inflate(inflater, container, false)
        return loginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginBinding.tvBackSignup.setOnClickListener(this)
        loginBinding.btnLogin.setOnClickListener(this)
        if (email.isNotEmpty() && password.isNotEmpty()) {
            loginBinding.edtEmailLogin.setText(email)
            loginBinding.edtPasswordLogin.setText(password)
        }
    }

    fun getDataFromSignup(e: String, p: String) {
        email = e
        password = p
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tv_back_signup -> {
                callbackLoginFragment?.backSignup()
            }
            R.id.btn_login -> {
                val intent = Intent(activity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }
}