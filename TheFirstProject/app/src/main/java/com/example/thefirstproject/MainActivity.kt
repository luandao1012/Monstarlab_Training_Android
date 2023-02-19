package com.example.thefirstproject

import android.annotation.SuppressLint
import android.icu.lang.UCharacter.VerticalOrientation
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thefirstproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var enviarAdapter: EnviarAdapter
    private lateinit var activityAdapter: ActivityAdapter
    private lateinit var listEnviars: ArrayList<Enviar>
    private lateinit var listActivities: ArrayList<Activity>
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun init() {
        enviarAdapter = EnviarAdapter()
        activityAdapter = ActivityAdapter()
        listEnviars = arrayListOf()
        listActivities = arrayListOf()
        addDataEnviar()
        addDataActivity()
        enviarAdapter.setData(listEnviars)
        activityAdapter.setData(listActivities)
        binding.rvEnviarDeNuevo.adapter = enviarAdapter
        binding.rvEnviarDeNuevo.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvActivity.apply {
            adapter = activityAdapter
            layoutManager = LinearLayoutManager(this.context)
        }
    }

    private fun addDataEnviar() {
        listEnviars += Enviar("Carlos Roca", R.drawable.bitmap_copy)
        listEnviars += Enviar("Ruby Sanz", R.drawable.bitmap_copy_3)
        listEnviars += Enviar("Mary Rich", R.drawable.bitmap_copy_4)
        listEnviars += Enviar("José Porto", R.drawable.bitmap_copy_5)
    }

    private fun addDataActivity() {
        listActivities += Activity("El corte inglés", "Pago aceptado", "-50€")
        listActivities += Activity("Maria Lujan", "Pago aceptado", "650€")
        listActivities += Activity("Maria Lujan", "Pago aceptado", "250€")
        listActivities += Activity("Maria Lujan", "Pago aceptado", "250€")
        listActivities += Activity("Maria Lujan", "Pago aceptado", "250€")
    }
}