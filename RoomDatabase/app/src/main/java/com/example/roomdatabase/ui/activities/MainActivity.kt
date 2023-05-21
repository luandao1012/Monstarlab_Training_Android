package com.example.roomdatabase.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.roomdatabase.databinding.ActivityMainBinding
import com.example.roomdatabase.ui.fragments.CalendarFragment

class MainActivity : AppCompatActivity(), OnClickListener {
    companion object {
        private const val PREFERENCES_PASSWORD = "password"
    }

    private val sharedPreferences by lazy {
        getSharedPreferences("passwordPreferences", MODE_PRIVATE)
    }
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var password = -1
    private var inputPassword = ""
    private var bundle: Bundle? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        initListeners()
    }

    private fun initViews() {
        password = sharedPreferences.getInt(PREFERENCES_PASSWORD, -1)
        bundle = intent.extras
        val key = bundle?.getString("setPassword")
        if (key == null) {
            binding.btSavePassword.visibility = View.GONE
            binding.btConfirmPassword.visibility = View.VISIBLE
            if (password == -1) {
                val intent = Intent(this, CalendarActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
                if (bundle != null) {
                    intent.putExtras(bundle!!)
                }
                startActivity(intent)
                finish()
            }
        } else {
            binding.btSavePassword.visibility = View.VISIBLE
            binding.btConfirmPassword.visibility = View.GONE
        }
    }

    private fun initListeners() {
        binding.btConfirmPassword.setOnClickListener(this)
        binding.btSavePassword.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        inputPassword = binding.edtPassword.text.toString().trim()
        when (view) {
            binding.btSavePassword -> {
                if (inputPassword.isNotEmpty()) {
                    sharedPreferences.edit().putInt(PREFERENCES_PASSWORD, inputPassword.toInt())
                        .apply()
                    Toast.makeText(
                        applicationContext,
                        "Đặt mật khẩu thành công",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }

            binding.btConfirmPassword -> {
                if (inputPassword.isNotEmpty()) {
                    if (password == inputPassword.toInt()) {
                        if (isTaskRoot) {
                            val intent = Intent(this, CalendarActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
                            if (bundle != null) {
                                intent.putExtras(bundle!!)
                            }
                            Log.d("test123", bundle.toString())
                            startActivity(intent)
                            finish()
                        } else {
                            finish()
                        }
                    } else {
                        binding.edtPassword.error = "Sai mật khẩu"
                    }
                } else {
                    binding.edtPassword.error = "Không để trống"
                }
            }
        }
    }
}