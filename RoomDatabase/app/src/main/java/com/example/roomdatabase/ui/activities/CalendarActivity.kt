package com.example.roomdatabase.ui.activities

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.roomdatabase.AlarmReceiver
import com.example.roomdatabase.R
import com.example.roomdatabase.collectFlow
import com.example.roomdatabase.data.NoteRoomDatabase
import com.example.roomdatabase.data.Type
import com.example.roomdatabase.databinding.ActivityCalendarBinding
import com.example.roomdatabase.setTimeCalendar
import com.example.roomdatabase.ui.adapters.ViewPagerAdapter
import com.example.roomdatabase.ui.fragments.CalendarFragment
import com.example.roomdatabase.ui.viewmodels.NoteViewModel
import com.example.roomdatabase.ui.viewmodels.NoteViewModelFactory
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class CalendarActivity : AppCompatActivity() {
    companion object {
        private const val WRITE_STORAGE_PERMISSION_CODE = 111
        private const val READ_STORAGE_PERMISSION_CODE = 222
    }

    var dateSelected = 0L
    private val binding by lazy { ActivityCalendarBinding.inflate(layoutInflater) }
    private var viewPagerAdapter: ViewPagerAdapter? = null
    private lateinit var activityCreateFile: ActivityResultLauncher<Intent>
    private lateinit var activitySelectFile: ActivityResultLauncher<Intent>
    private val noteViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory(
            NoteRoomDatabase.getDatabase(applicationContext).diaryDao()
        )
    }

    override fun onNewIntent(intent: Intent) {
        Log.d("test123", "onNewIntent: ")
        super.onNewIntent(intent)
        val bundle = intent.extras
        val openNoteActivity = bundle?.getBoolean(CalendarFragment.IS_VIEW_OR_EDIT)
        if (openNoteActivity == true) {
            val intent = Intent(this, AddNoteActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        initListeners()
    }

    private fun initViews() {
        val bundle = intent.extras
        Log.d("test123", bundle.toString())
        val openNoteActivity = bundle?.getBoolean(CalendarFragment.IS_VIEW_OR_EDIT)
        if (openNoteActivity == true) {
            val intent = Intent(this, AddNoteActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.apply {
            adapter = viewPagerAdapter
            setCurrentItem(50, false)
            offscreenPageLimit = 1
        }
        setSupportActionBar(binding.toolbar)
        setDateSelectedIsToday()
    }

    private fun initListeners() {
//        activityCreateFile =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//                if (result.resultCode == RESULT_OK) {
//                    result.data?.data?.let { uri ->
//                        diaryViewModel.backupToCSV(this, uri)
//                        Toast.makeText(this, "Backup thành công", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        activitySelectFile =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//                if (result.resultCode == RESULT_OK) {
//                    result.data?.data?.let { uri ->
//                        diaryViewModel.restoreFromCSV(this, uri)
//                    }
//                }
//            }
    }

    private fun setDateSelectedIsToday() {
        val calendar = Calendar.getInstance()
        calendar.setTimeCalendar()
        dateSelected = calendar.timeInMillis
    }

    fun resetALlFragment() {
        supportFragmentManager.fragments.forEach {
            (it as? CalendarFragment)?.resetFragment()
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.current_date_menu -> {
                binding.viewPager.setCurrentItem(50, true)
                setDateSelectedIsToday()
                resetALlFragment()
            }

            R.id.set_password_menu -> {
                val intent = Intent(this, MainActivity::class.java)
                val bundle = Bundle().apply {
                    putString("setPassword", "setPassword")
                }
                intent.putExtras(bundle)
                startActivity(intent)
            }

            R.id.diary_list_menu -> {
                startActivity(Intent(this, NoteListActivity::class.java))
            }

            R.id.backup_menu -> {
                if (checkPermission(WRITE_EXTERNAL_STORAGE)) {
                    createFileCSVBackup()
                } else {
                    requestPermission(WRITE_EXTERNAL_STORAGE, WRITE_STORAGE_PERMISSION_CODE)
                }
            }

            R.id.restore_menu -> {
                if (checkPermission(READ_EXTERNAL_STORAGE)) {
                    selectFileCSVRestore()
                } else {
                    requestPermission(READ_EXTERNAL_STORAGE, READ_STORAGE_PERMISSION_CODE)
                }
            }

            R.id.present -> {
                startActivity(Intent(this, WebViewActivity::class.java))
            }
        }
        return true
    }

    private fun createFileCSVBackup() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/comma-separated-values"
            putExtra(Intent.EXTRA_TITLE, "backup.csv")
        }
        activityCreateFile.launch(intent)
    }

    private fun selectFileCSVRestore() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/comma-separated-values"
        }
        activitySelectFile.launch(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            WRITE_STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createFileCSVBackup()
                } else {
                    Toast.makeText(this, "Không có quyền truy cập", Toast.LENGTH_SHORT).show()
                }
            }

            READ_STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectFileCSVRestore()
                } else {
                    Toast.makeText(this, "Không có quyền truy cập", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}