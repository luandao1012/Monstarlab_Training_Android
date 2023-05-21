package com.example.roomdatabase

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun <T> Fragment.collectFlow(flow: Flow<T>, callback: (T) -> Unit) {
    lifecycleScope.launch {
        flow.collect {
            callback.invoke(it)
        }
    }
}