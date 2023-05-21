package com.example.roomdatabase.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.roomdatabase.R
import com.example.roomdatabase.collectFlow
import com.example.roomdatabase.data.NoteRoomDatabase
import com.example.roomdatabase.data.Type
import com.example.roomdatabase.databinding.ActivityDiaryListBinding
import com.example.roomdatabase.ui.adapters.NoteListAdapter
import com.example.roomdatabase.ui.viewmodels.NoteViewModel
import com.example.roomdatabase.ui.viewmodels.NoteViewModelFactory

class NoteListActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDiaryListBinding.inflate(layoutInflater) }
    private val noteViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory(
            NoteRoomDatabase.getDatabase(applicationContext).diaryDao()
        )
    }
    private var noteListAdapter: NoteListAdapter? = null
    private var type = Type.DIARY
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        initListeners()
    }

    private fun initViews() {
        noteListAdapter = NoteListAdapter()
        binding.rvDiary.adapter = noteListAdapter
        setView()
    }

    private fun setView() {
        noteViewModel.getALlNoteByType(type)
        if (type == Type.DIARY) {
            binding.tvSelectDiary.setBackgroundResource(R.drawable.bg_selected)
            binding.tvSelectReminder.setBackgroundResource(R.drawable.bg_search)
        } else {
            binding.tvSelectDiary.setBackgroundResource(R.drawable.bg_search)
            binding.tvSelectReminder.setBackgroundResource(R.drawable.bg_selected)
        }
    }

    private fun initListeners() {
        binding.tvSelectDiary.setOnClickListener {
            type = Type.DIARY
            setView()
        }
        binding.tvSelectReminder.setOnClickListener {
            type = Type.REMINDER
            setView()
        }
        collectFlow(noteViewModel.allNote) { selectedDiary ->
            noteListAdapter?.setData(selectedDiary)
        }
        noteListAdapter?.setOnClickCallback {
            val bundle = Bundle().apply {
                putLong("date", it)
            }
            val intent = Intent(this, AddNoteActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(key: CharSequence?, p1: Int, p2: Int, p3: Int) {
                noteViewModel.search(key.toString(), type)
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }
}