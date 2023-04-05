package com.example.roomdatabase

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun <T> AppCompatActivity.collectFlow(
    flow: Flow<T>,
    callback: (T) -> Unit
) {
    lifecycleScope.launch {
        flow.collect(callback)
    }
}
