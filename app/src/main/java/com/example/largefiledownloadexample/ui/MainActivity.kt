package com.example.largefiledownloadexample.ui

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.largefiledownloadexample.R
import com.example.largefiledownloadexample.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*


private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(
    Manifest.permission.INTERNET,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE
)

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var workerViewModel: DownloadViewModel
    private lateinit var downloadManagerViewModel: DownloadManagerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        workerViewModel = ViewModelProvider(this).get(DownloadViewModel::class.java)
        downloadManagerViewModel = ViewModelProvider(this).get(DownloadManagerViewModel::class.java)

        getSystemService(DOWNLOAD_SERVICE);
        registerReceiver(
            onComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
        drawerLayout = binding.drawerLayout
        val navController = findNavController(R.id.myNavHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(navView, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context?, intent: Intent?) {
            finished()
        }
    }

    private fun finished() {
        Toast.makeText(this@MainActivity, getString(R.string.downloadSuccess), Toast.LENGTH_LONG)
            .show()
        startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS))
    }

}