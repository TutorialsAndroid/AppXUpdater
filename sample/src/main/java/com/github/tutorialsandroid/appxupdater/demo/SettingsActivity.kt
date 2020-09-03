package com.github.tutorialsandroid.appxupdater.demo

import android.os.Bundle
import android.preference.Preference.OnPreferenceClickListener
import android.preference.PreferenceActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.github.tutorialsandroid.appxupdater.AppUpdater
import com.github.tutorialsandroid.appxupdater.demo.SettingsActivity
import com.github.tutorialsandroid.appxupdater.enums.Display
import com.github.tutorialsandroid.appxupdater.enums.UpdateFrom

class SettingsActivity : PreferenceActivity() {
  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addPreferencesFromResource(R.xml.settings)
    val prefCheckForUpdates = findPreference("prefCheckForUpdates")
    prefCheckForUpdates.onPreferenceClickListener = OnPreferenceClickListener {
      AppUpdater(this@SettingsActivity) //.setUpdateFrom(UpdateFrom.GITHUB)
        //.setGitHubUserAndRepo("javiersantos", "AppUpdater")
        .setUpdateFrom(UpdateFrom.XML)
        .setUpdateXML("https://raw.githubusercontent.com/javiersantos/AppUpdater/master/app/update-changelog.xml")
        .setDisplay(Display.DIALOG)
        .showAppUpdated(true)
        .start()
      true
    }
  }

  override fun setContentView(layoutResID: Int) {
    val contentView = LayoutInflater.from(this).inflate(R.layout.activity_settings, LinearLayout(this), false) as ViewGroup
    val toolbar = contentView.findViewById<View>(R.id.toolbar) as Toolbar
    toolbar.setTitle(R.string.action_settings)
    toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white))
    toolbar.setNavigationOnClickListener { onBackPressed() }
    val contentWrapper = contentView.findViewById<View>(R.id.content_wrapper) as ViewGroup
    LayoutInflater.from(this).inflate(layoutResID, contentWrapper, true)
    window.setContentView(contentView)
  }
}
