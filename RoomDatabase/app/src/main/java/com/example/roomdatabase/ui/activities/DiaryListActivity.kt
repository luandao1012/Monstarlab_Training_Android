package com.example.roomdatabase.ui.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.roomdatabase.collectFlow
import com.example.roomdatabase.data.DiaryRoomDatabase
import com.example.roomdatabase.databinding.ActivityDiaryListBinding
import com.example.roomdatabase.ui.adapters.DiaryListAdapter
import com.example.roomdatabase.ui.viewmodels.DiaryViewModel
import com.example.roomdatabase.ui.viewmodels.DiaryViewModelFactory

class DiaryListActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDiaryListBinding.inflate(layoutInflater) }
    private val diaryViewModel: DiaryViewModel by viewModels {
        DiaryViewModelFactory(
            DiaryRoomDatabase.getDatabase(applicationContext).diaryDao()
        )
    }
    private var diaryListAdapter: DiaryListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        initListeners()
    }

    private fun initViews() {
        diaryListAdapter = DiaryListAdapter()
        binding.rvDiary.adapter = diaryListAdapter
        diaryViewModel.getAllDiary()
    }

    private fun initListeners() {
        collectFlow(diaryViewModel.allDiary) { selectedDiary ->
            diaryListAdapter?.setData(selectedDiary)
        }
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                diaryViewModel.search(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }
}