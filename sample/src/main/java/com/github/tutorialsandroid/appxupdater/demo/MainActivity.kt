package com.github.tutorialsandroid.appxupdater.demo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.tutorialsandroid.appxupdater.AppUpdater

import com.github.tutorialsandroid.appxupdater.demo.databinding.ActivityMainBinding
import com.github.tutorialsandroid.appxupdater.enums.Display
import com.github.tutorialsandroid.appxupdater.enums.UpdateFrom

class MainActivity : AppCompatActivity() {
  //Context
  private var mContext: Context? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    mContext = this
    setSupportActionBar(binding.toolbar)
    binding.fab.setOnClickListener { view: View? ->
      startActivity(Intent(Intent.ACTION_VIEW,
        Uri.parse("https://github.com/TutorialsAndroid/AppXUpdater")))
    }
    binding.included.dialogUpdateChangelog.setOnClickListener { view: View? ->
      AppUpdater(this) //.setUpdateFrom(UpdateFrom.GITHUB)
        //.setGitHubUserAndRepo("TutorialsAndroid", "AppXUpdater")
        .setUpdateFrom(UpdateFrom.JSON)
        .setUpdateJSON("https://raw.githubusercontent.com/TutorialsAndroid/AppXUpdater/master/files/update-changelog.json")
        .setDisplay(Display.DIALOG)
        .showAppUpdated(true)
        .start()
    }
    binding.included.dialogUpdate.setOnClickListener { view: View? ->
      AppUpdater(this) //.setUpdateFrom(UpdateFrom.GITHUB)
        //.setGitHubUserAndRepo("TutorialsAndroid", "AppXUpdater")
        .setUpdateFrom(UpdateFrom.JSON)
        .setUpdateXML("https://raw.githubusercontent.com/TutorialsAndroid/AppXUpdater/master/files/update.json")
        .setDisplay(Display.DIALOG)
        .showAppUpdated(true)
        .start()
    }
    binding.included.snackbarUpdate.setOnClickListener { view: View? ->
      AppUpdater(this)
        .setUpdateFrom(UpdateFrom.GITHUB)
        .setGitHubUserAndRepo("TutorialsAndroid", "AppXUpdater")
        .setUpdateFrom(UpdateFrom.XML)
        .setUpdateXML("https://raw.githubusercontent.com/TutorialsAndroid/AppXUpdater/master/files/update.xml")
        .setDisplay(Display.SNACKBAR)
        .showAppUpdated(true)
        .start()
    }
    binding.included.notificationUpdate.setOnClickListener { view: View? ->
      AppUpdater(this) //.setUpdateFrom(UpdateFrom.GITHUB)
        //.setGitHubUserAndRepo("TutorialsAndroid", "AppXUpdater")
        .setUpdateFrom(UpdateFrom.XML)
        .setUpdateXML("https://raw.githubusercontent.com/TutorialsAndroid/AppXUpdater/master/files/update.xml")
        .setDisplay(Display.NOTIFICATION)
        .showAppUpdated(true)
        .start()
    }
    binding.included.dialogNoUpdate.setOnClickListener { view: View? ->
      AppUpdater(this)
        .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
        .setDisplay(Display.DIALOG)
        .showAppUpdated(true)
        .start()
    }
    binding.included.snackbarNoUpdate.setOnClickListener { view: View? ->
      AppUpdater(this)
        .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
        .setDisplay(Display.SNACKBAR)
        .showAppUpdated(true)
        .start()
    }
    binding.included.notificationNoUpdate.setOnClickListener { view: View? ->
      AppUpdater(this)
        .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
        .setDisplay(Display.NOTIFICATION)
        .showAppUpdated(true)
        .start()
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val inflater = menuInflater
    inflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == R.id.action_settings) {
      startActivity(Intent(this, SettingsActivity::class.java))
      return true
    }
    return super.onOptionsItemSelected(item)
  }
}
