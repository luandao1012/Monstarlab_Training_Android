package com.example.roomdatabase.ui.activities

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.roomdatabase.AlarmReceiver
import com.example.roomdatabase.R
import com.example.roomdatabase.data.Note
import com.example.roomdatabase.data.NoteRoomDatabase
import com.example.roomdatabase.data.Type
import com.example.roomdatabase.databinding.ActivityDiaryBinding
import com.example.roomdatabase.showDatePickerDialog
import com.example.roomdatabase.showDialog
import com.example.roomdatabase.showTimePickerDialog
import com.example.roomdatabase.ui.fragments.CalendarFragment
import com.example.roomdatabase.ui.viewmodels.NoteViewModel
import com.example.roomdatabase.ui.viewmodels.NoteViewModelFactory
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("SimpleDateFormat")
class AddNoteActivity : AppCompatActivity(), OnClickListener {
    private val binding by lazy { ActivityDiaryBinding.inflate(layoutInflater) }
    private val noteViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory(
            NoteRoomDatabase.getDatabase(applicationContext).diaryDao()
        )
    }
    private var alarmManager: AlarmManager? = null
    private var date = 0L
    private val calendar = Calendar.getInstance()
    private val alarmCalendar = Calendar.getInstance()
    private var note: Note? = null
    private var isViewOrEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        initListeners()
    }


    private fun initViews() {
        val bundle = intent.extras
        if (bundle != null) {
            val noteSerializable = bundle.getString(CalendarFragment.DATE)
            note = noteSerializable?.let { Json.decodeFromString(it) }
            if (note != null) {
                date = note!!.date
            }
            isViewOrEdit = bundle.getBoolean(CalendarFragment.IS_VIEW_OR_EDIT)
        }
        calendar.timeInMillis = date
        binding.tvDiaryDate.text = SimpleDateFormat("dd/MM/yyyy").format(calendar.time)
        noteViewModel.getDiary(date)
        binding.ivDelete.visibility = if (isViewOrEdit) View.VISIBLE else View.GONE
        if (isViewOrEdit) {
            binding.tvDiaryDate.isEnabled = false
            note?.type?.ordinal?.let { binding.spinnerType.setSelection(it) }
            binding.diaryContent.setText(note?.content)
            binding.cbComplete.isChecked = note?.isCompleted == true
            val alarmTime = note?.alarmTime
            if (alarmTime != null) {
                binding.switchReminder.isChecked = true
                binding.layoutSelectAlarm.visibility = View.VISIBLE
                alarmCalendar.timeInMillis = alarmTime
                binding.tvSelectAlarmDate.text =
                    SimpleDateFormat("dd/MM/yyyy").format(alarmCalendar.time)
                binding.tvSelectAlarmTime.text =
                    SimpleDateFormat("HH:mm").format(alarmCalendar.time)
            } else {
                binding.switchReminder.isChecked = false
            }
        }
    }

    private fun initListeners() {
        binding.ivDone.setOnClickListener(this)
        binding.tvDiaryDate.setOnClickListener(this)
        binding.ivDelete.setOnClickListener(this)
        binding.ivBack.setOnClickListener(this)
        binding.tvSelectAlarmDate.setOnClickListener(this)
        binding.tvSelectAlarmTime.setOnClickListener(this)
        binding.spinnerType.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (position == Type.DIARY.ordinal) {
                    binding.layoutRemeider.visibility = View.GONE
                } else {
                    binding.layoutRemeider.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) = Unit
        }
        binding.switchReminder.setOnCheckedChangeListener { _, isOn ->
            if (isOn) binding.layoutSelectAlarm.visibility = View.VISIBLE
            else binding.layoutSelectAlarm.visibility = View.GONE
        }
    }

    private fun showConfirmUpdateDialog(isChangeAlarmTime: Boolean) {
        note?.let {
            showDialog(it, "Xác nhận cập nhật", "Bạn có chắc muốn cập nhật nhật ký này không?") {
                noteViewModel.updateDiary(note!!)
                if (isChangeAlarmTime) {
                    setAlarmTime(note!!)
                }
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun checkAlarmTime(): Long? {
        if (binding.switchReminder.isChecked) {
            if (binding.tvSelectAlarmDate.text.isEmpty() and binding.tvSelectAlarmTime.text.isEmpty()) {
                return 0L
            } else {
                return alarmCalendar.timeInMillis
            }
        }
        return null
    }

    private fun setAlarmTime(note: Note) {
        if (note.alarmTime != null && note.isCompleted == false) {
            val intent = Intent(this, AlarmReceiver::class.java)
            intent.action = AlarmReceiver.ALARM_REMINDER
            val bundle = Bundle().apply {
                putString(CalendarFragment.DATE, Json.encodeToString(note))
                putBoolean(CalendarFragment.IS_VIEW_OR_EDIT, true)
            }
            intent.putExtras(bundle)
            intent.putExtra(AlarmReceiver.CONTENT_REMINDER, note.content)
            alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager?
            val pendingIntent = PendingIntent.getBroadcast(
                this, note.alarmTime?.toInt() ?: 0, intent, PendingIntent.FLAG_IMMUTABLE
            )
            note.alarmTime?.let {
                alarmManager?.setExact(AlarmManager.RTC_WAKEUP, it, pendingIntent)
            }
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.ivDone -> {
                val content = binding.diaryContent.text.toString().trim()
                val type = Type.values()[binding.spinnerType.selectedItemPosition]
                val alarmTime = checkAlarmTime()
                val isCompleted = binding.cbComplete.isChecked
                if (content.isNotEmpty()) {
                    val newNote = Note(
                        id = note?.id,
                        date = date,
                        content = content,
                        type = type,
                        alarmTime = null,
                        isCompleted = if (type == Type.DIARY) null else isCompleted
                    )
                    if (alarmTime == 0L) {
                        Toast.makeText(this, "Chọn thời gian nhắc nhở", Toast.LENGTH_SHORT).show()
                    } else {
                        if (type == Type.REMINDER) {
                            newNote.alarmTime = alarmTime
                        }
                        if (isViewOrEdit) {
                            if (newNote != note) {
                                val isChangeAlarmTime: Boolean =
                                    note?.alarmTime != newNote.alarmTime
                                note = newNote
                                showConfirmUpdateDialog(isChangeAlarmTime)
                            } else {
                                onBackPressedDispatcher.onBackPressed()
                            }
                        } else {
                            noteViewModel.addNote(newNote)
                            Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show()
                            onBackPressedDispatcher.onBackPressed()
                            setAlarmTime(newNote)
                        }
                    }
                } else {
                    Toast.makeText(this, "Không để trống", Toast.LENGTH_SHORT).show()
                }
            }

            binding.tvDiaryDate -> {
                showDatePickerDialog(date) {
                    date = it
                    calendar.timeInMillis = it
                    binding.tvDiaryDate.text = SimpleDateFormat("dd/MM/yyyy").format(date)
                }
            }

            binding.tvSelectAlarmDate -> {
                showDatePickerDialog(alarmCalendar.timeInMillis) {
                    alarmCalendar.timeInMillis = it
                    binding.tvSelectAlarmDate.text =
                        SimpleDateFormat("dd/MM/yyyy").format(alarmCalendar.time)
                }
            }

            binding.tvSelectAlarmTime -> {
                showTimePickerDialog(alarmCalendar.timeInMillis) {
                    alarmCalendar.timeInMillis = it
                    binding.tvSelectAlarmTime.text =
                        SimpleDateFormat("HH:mm").format(alarmCalendar.time)
                }
            }

            binding.ivDelete -> {
                note?.let {
                    showDialog(it, "Xác nhận xóa", "Bạn có chắc muốn xóa nhật ký này không?") {
                        noteViewModel.deleteDiary(note!!)
                        Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show()
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }

            binding.ivBack -> {
                val content = binding.diaryContent.text.toString().trim()
                if (note == null) {
                    if (content.isNotEmpty()) {
                        showDialog(null, "Xác nhận thoát", "Bạn có chắc muốn thoát không?") {
                            onBackPressedDispatcher.onBackPressed()
                        }
                    } else {
                        onBackPressedDispatcher.onBackPressed()
                    }
                } else {
                    if (content != note?.content) {
                        showDialog(note, "Xác nhận thoát", "Bạn có chắc muốn thoát không?") {
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