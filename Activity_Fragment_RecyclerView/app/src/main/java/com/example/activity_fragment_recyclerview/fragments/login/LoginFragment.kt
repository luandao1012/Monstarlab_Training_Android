package com.example.activity_fragment_recyclerview.fragments.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import com.example.activity_fragment_recyclerview.R
import com.example.activity_fragment_recyclerview.activities.MainActivity
import com.example.activity_fragment_recyclerview.databinding.FragmentLoginBinding

class LoginFragment() : Fragment(), OnClickListener {
    private lateinit var binding: FragmentLoginBinding
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
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        binding.edtEmailLogin.setText("")
        binding.edtPasswordLogin.setText("")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvBackSignup.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)
        setFragmentResultListener("signup") { _, bundle ->
            val email = bundle.getString("email").toString()
            val password = bundle.getString("password").toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                binding.edtEmailLogin.setText(email)
                binding.edtPasswordLogin.setText(password)
            }
        }
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