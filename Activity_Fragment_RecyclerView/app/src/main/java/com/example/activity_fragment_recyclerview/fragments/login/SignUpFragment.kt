package com.example.activity_fragment_recyclerview.fragments.login

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.example.activity_fragment_recyclerview.R
import com.example.activity_fragment_recyclerview.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment(), OnClickListener {
    private lateinit var binding: FragmentSignUpBinding
    var callbackSignupFragment: CallbackSignupFragment? = null

    interface CallbackSignupFragment {
        fun backLogin()
        fun sendDataForLogin(email: String, password: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbackSignupFragment = context as CallbackSignupFragment
    }

    override fun onDetach() {
        super.onDetach()
        callbackSignupFragment = null
    }

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
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_sign_up -> {
                callbackSignupFragment?.let {
                    it.backLogin()
                    it.sendDataForLogin(
                        binding.edtEmailSignUp.text.toString().trim(),
                        binding.edtPasswordSignUp.text.toString().trim()
                    )
                }
            }
            R.id.tv_back_login -> {
                callbackSignupFragment?.backLogin()
            }
        }
    }
}