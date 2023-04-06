package com.example.roomdatabase.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.roomdatabase.collectFlow
import com.example.roomdatabase.data.Diary
import com.example.roomdatabase.data.DiaryRoomDatabase
import com.example.roomdatabase.databinding.ActivityDiaryBinding
import com.example.roomdatabase.showDatePickerDialog
import com.example.roomdatabase.showDialog
import com.example.roomdatabase.ui.viewmodels.DiaryViewModel
import com.example.roomdatabase.ui.viewmodels.DiaryViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
class AddDiaryActivity : AppCompatActivity(), OnClickListener {
    private val binding by lazy { ActivityDiaryBinding.inflate(layoutInflater) }
    private val diaryViewModel: DiaryViewModel by viewModels {
        DiaryViewModelFactory(
            DiaryRoomDatabase.getDatabase(applicationContext).diaryDao()
        )
    }
    private var date = 0L
    private val calendar = Calendar.getInstance()
    private var diary: Diary? = null
    private var isViewOrUpdateDiary = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        initListeners()
    }


    private fun initViews() {
        val bundle = intent.extras
        date = bundle!!.getLong("date")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        binding.tvDiaryDate.text = SimpleDateFormat("dd/MM/yyyy").format(calendar.time)
        diaryViewModel.getDiary(date)
    }

    private fun initListeners() {
        binding.ivDone.setOnClickListener(this)
        binding.tvDiaryDate.setOnClickListener(this)
        binding.ivDelete.setOnClickListener(this)
        binding.ivBack.setOnClickListener(this)
        collectFlow(diaryViewModel.currentDiary) { selectedDiary ->
            if (selectedDiary != null) {
                binding.ivDelete.visibility = View.VISIBLE
                binding.diaryContent.setText(selectedDiary.content)
                diary = selectedDiary
                isViewOrUpdateDiary = true
            } else {
                isViewOrUpdateDiary = false
                binding.ivDelete.visibility = View.GONE
                binding.diaryContent.setText("")
            }
        }
    }

    private fun showConfirmUpdateDialog() {
        diary?.let {
            showDialog(it, "Xác nhận cập nhật", "Bạn có chắc muốn cập nhật nhật ký này không?") {
                diaryViewModel.updateDiary(diary!!)
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.ivDone -> {
                val content = binding.diaryContent.text.toString().trim()
                if (content.isNotEmpty()) {
                    if (isViewOrUpdateDiary) {
                        if (content != diary?.content) {
                            diary?.content = content
                            showConfirmUpdateDialog()
                        } else {
                            onBackPressedDispatcher.onBackPressed()
                        }
                    } else {
                        val diary = Diary(date, binding.diaryContent.text.toString())
                        diaryViewModel.addDiary(diary)
                        Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show()
                        onBackPressedDispatcher.onBackPressed()
                    }
                } else {
                    Toast.makeText(this, "Không để trống", Toast.LENGTH_SHORT).show()
                }
            }
            binding.tvDiaryDate -> {
                showDatePickerDialog(date) {
                    date = it
                    calendar.timeInMillis = it
                    binding.tvDiaryDate.text = SimpleDateFormat("dd/MM/yyyy").format(calendar.time)
                    diaryViewModel.getDiary(date)
                }
            }
            binding.ivDelete -> {
                diary?.let {
                    showDialog(it, "Xác nhận xóa", "Bạn có chắc muốn xóa nhật ký này không?") {
                        diaryViewModel.deleteDiary(diary!!)
                        Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show()
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
            binding.ivBack -> {
                val content = binding.diaryContent.text.toString().trim()
                if (diary == null) {
                    if (content.isNotEmpty()) {
                        showDialog(null, "Xác nhận thoát", "Bạn có chắc muốn thoát không?") {
                            onBackPressedDispatcher.onBackPressed()
                        }
                    } else {
                        onBackPressedDispatcher.onBackPressed()
                    }
                } else {
                    if (content != diary?.content) {
                        showDialog(diary, "Xác nhận thoát", "Bạn có chắc muốn thoát không?") {
                            onBackPressedDispatcher.onBackPressed()
                        }
                    } else {
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        }
    }
}