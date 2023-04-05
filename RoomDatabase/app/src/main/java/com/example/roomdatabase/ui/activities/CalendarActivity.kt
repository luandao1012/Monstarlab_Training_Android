package com.example.roomdatabase.ui.activities

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.roomdatabase.MainActivity
import com.example.roomdatabase.R
import com.example.roomdatabase.data.DiaryRoomDatabase
import com.example.roomdatabase.databinding.ActivityCalendarBinding
import com.example.roomdatabase.ui.adapters.ViewPagerAdapter
import com.example.roomdatabase.ui.fragments.CalendarFragment
import com.example.roomdatabase.ui.viewmodels.DiaryViewModel
import com.example.roomdatabase.ui.viewmodels.DiaryViewModelFactory
import java.util.*

class CalendarActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_PASSWORD = "extra password"
        const val PREFERENCES_PASSWORD = "password"
        private const val WRITE_STORAGE_PERMISSION_CODE = 111
        private const val READ_STORAGE_PERMISSION_CODE = 222
    }

    var dateSelected = 0L
    private val binding by lazy { ActivityCalendarBinding.inflate(layoutInflater) }
    private var viewPagerAdapter: ViewPagerAdapter? = null
    private lateinit var activityResultPassword: ActivityResultLauncher<Intent>
    private lateinit var activityCreateFile: ActivityResultLauncher<Intent>
    private lateinit var activitySelectFile: ActivityResultLauncher<Intent>
    private val diaryViewModel: DiaryViewModel by viewModels {
        DiaryViewModelFactory(
            DiaryRoomDatabase.getDatabase(applicationContext).diaryDao()
        )
    }
    private val sharedPreferences by lazy {
        getSharedPreferences("passwordPreferences", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        initListeners()
    }

    private fun initViews() {
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
        activityResultPassword =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val password = result.data?.getStringExtra(EXTRA_PASSWORD)?.toInt()
                    if (password != null) {
                        sharedPreferences.edit().putInt(PREFERENCES_PASSWORD, password).apply()
                    }
                    Toast.makeText(this, "Đặt mật khẩu thành công", Toast.LENGTH_SHORT).show()
                }
            }
        activityCreateFile =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.data?.let { uri ->
                        diaryViewModel.backupToCSV(this, uri)
                        Toast.makeText(this, "Backup thành công", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        activitySelectFile =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.data?.let { uri ->
                        diaryViewModel.restoreFromCSV(this, uri)
                        Toast.makeText(this, "Restore thành công", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun setDateSelectedIsToday() {
        val calendar = Calendar.getInstance()
        calendar.apply {
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        dateSelected = calendar.timeInMillis
    }

    fun resetALlFragment() {
        supportFragmentManager.fragments.forEach {
            (it as? CalendarFragment)?.resetFragment()
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
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
                activityResultPassword.launch(intent)
            }
            R.id.diary_list_menu -> {
                startActivity(Intent(this, DiaryListActivity::class.java))
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
        }
        return true
    }

    private fun createFileCSVBackup() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
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