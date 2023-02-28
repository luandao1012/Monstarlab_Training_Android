package com.example.activity_fragment_recyclerview.fragments.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import com.example.activity_fragment_recyclerview.R
import com.example.activity_fragment_recyclerview.activities.EmailActivity
import com.example.activity_fragment_recyclerview.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment(), OnClickListener {
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvBackLogin.setOnClickListener(this)
        binding.btnSignUp.setOnClickListener(this)
        binding.tvForgotPassword.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_sign_up -> {
                val bundle = Bundle().apply {
                    putString("email", binding.edtEmailSignUp.text.toString().trim())
                    putString("password", binding.edtPasswordSignUp.text.toString().trim())
                }
                setFragmentResult("signup", bundle)
                fragmentManager?.popBackStack()
            }
            R.id.tv_back_login -> {
                fragmentManager?.popBackStack()
            }
            R.id.tv_forgot_password -> {
                startActivity(Intent(activity, EmailActivity::class.java))
            }
        }
    }
}