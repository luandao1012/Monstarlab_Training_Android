package com.example.activity_fragment_recyclerview.activities

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.activity_fragment_recyclerview.R
import com.example.activity_fragment_recyclerview.databinding.ActivityMainBinding
import com.example.activity_fragment_recyclerview.fragments.main.CoinFragment
import com.example.activity_fragment_recyclerview.fragments.main.HomeFragment
import com.example.activity_fragment_recyclerview.fragments.main.MenuFragment
import com.example.activity_fragment_recyclerview.fragments.main.NewsFragment

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val listFragment =
        listOf(HomeFragment(), CoinFragment(), NewsFragment(), MenuFragment())
    private val backStack = arrayListOf<Int>()
    private lateinit var builderDialog: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        initListeners()
    }

    private fun initViews() {
        showFragment(0)
        builderDialog = AlertDialog.Builder(this)
        builderDialog.apply {
            setTitle("Xác nhận thoát ứng dụng")
            setMessage("Bạn có muốn thoát ứng dụng không?")
            setPositiveButton("Có") { _, _ -> finish() }
            setNegativeButton("Không") { _, _ -> }
        }
    }

    private fun initListeners() {
        binding.myBottomNavNar.setFragmentSelected {
            showFragment(it)
        }
    }

    fun showFragment(index: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        val frag = listFragment[index]
        if (!frag.isAdded) {
            transaction.add(R.id.fragment_container_view, frag)
        }
        listFragment.forEach { if (it != frag) transaction.hide(it) }
        transaction.show(frag)
        transaction.commit()
        if (backStack.any { it == index } && index != 0) {
            backStack.remove(index)
        } else if (index == 0 && backStack.lastIndexOf(0) > 0) {
            backStack.removeAt(backStack.lastIndexOf(0))
        }
        if (backStack.lastOrNull() != index) backStack.add(index)
        binding.myBottomNavNar.setIndexSelected(index)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (backStack.last() == 2 && listFragment[2].childFragmentManager.backStackEntryCount > 0) {
            listFragment[2].let {
                it.childFragmentManager.popBackStack()
                (it as NewsFragment).setTitleNews()
            }
        } else {
            if (backStack.size == 1) {
                builderDialog.show()
            } else {
                backStack.removeLast()
                backStack.last().let {
                    showFragment(it)
                    binding.myBottomNavNar.setIndexSelected(it)
                }
            }
        }
    }
}