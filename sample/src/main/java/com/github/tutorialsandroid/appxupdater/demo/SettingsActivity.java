package com.github.tutorialsandroid.appxupdater.demo;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.github.tutorialsandroid.appxupdater.AppUpdater;
import com.github.tutorialsandroid.appxupdater.enums.Display;
import com.github.tutorialsandroid.appxupdater.enums.UpdateFrom;

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        Preference prefCheckForUpdates = findPreference("prefCheckForUpdates");

        prefCheckForUpdates.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AppUpdater(SettingsActivity.this)
                        //.setUpdateFrom(UpdateFrom.GITHUB)
                        //.setGitHubUserAndRepo("javiersantos", "AppUpdater")
                        .setUpdateFrom(UpdateFrom.XML)
                        .setUpdateXML("https://raw.githubusercontent.com/javiersantos/AppUpdater/master/app/update-changelog.xml")
                        .setDisplay(Display.DIALOG)
                        .showAppUpdated(true)
                        .start();
                return true;
            }
        });

    }

    @Override
    public void setContentView(int layoutResID) {
        ViewGroup contentView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.activity_settings, new LinearLayout(this), false);
        Toolbar toolbar = (Toolbar) contentView.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.action_settings);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ViewGroup contentWrapper = (ViewGroup) contentView.findViewById(R.id.content_wrapper);
        LayoutInflater.from(this).inflate(layoutResID, contentWrapper, true);
        getWindow().setContentView(contentView);

    }

}
