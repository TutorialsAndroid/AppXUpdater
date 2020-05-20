package com.github.tutorialsandroid.appxupdater.demo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.github.tutorialsandroid.appxupdater.AppUpdater;
import com.github.tutorialsandroid.appxupdater.demo.databinding.ActivityMainBinding;
import com.github.tutorialsandroid.appxupdater.enums.Display;
import com.github.tutorialsandroid.appxupdater.enums.UpdateFrom;

public class MainActivity extends AppCompatActivity {

    //Context
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        this.mContext = this;
        setSupportActionBar(binding.toolbar);
		
        binding.fab.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://github.com/TutorialsAndroid/AppXUpdater"))));

        binding.included.dialogUpdateChangelog.setOnClickListener(view -> new AppUpdater(mContext)
                //.setUpdateFrom(UpdateFrom.GITHUB)
                //.setGitHubUserAndRepo("TutorialsAndroid", "AppXUpdater")
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON("https://raw.githubusercontent.com/TutorialsAndroid/AppXUpdater/master/files/update-changelog.json")
                .setDisplay(Display.DIALOG)
                .showAppUpdated(true)
                .start());

        binding.included.dialogUpdate.setOnClickListener(view -> new AppUpdater(mContext)
                //.setUpdateFrom(UpdateFrom.GITHUB)
                //.setGitHubUserAndRepo("TutorialsAndroid", "AppXUpdater")
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateXML("https://raw.githubusercontent.com/TutorialsAndroid/AppXUpdater/master/files/update.json")
                .setDisplay(Display.DIALOG)
                .showAppUpdated(true)
                .start());

        binding.included.snackbarUpdate.setOnClickListener(view -> new AppUpdater(mContext)
                .setUpdateFrom(UpdateFrom.GITHUB)
                .setGitHubUserAndRepo("TutorialsAndroid", "AppXUpdater")
                .setUpdateFrom(UpdateFrom.XML)
                .setUpdateXML("https://raw.githubusercontent.com/TutorialsAndroid/AppXUpdater/master/files/update.xml")
                .setDisplay(Display.SNACKBAR)
                .showAppUpdated(true)
                .start());

        binding.included.notificationUpdate.setOnClickListener(view -> new AppUpdater(mContext)
                //.setUpdateFrom(UpdateFrom.GITHUB)
                //.setGitHubUserAndRepo("TutorialsAndroid", "AppXUpdater")
                .setUpdateFrom(UpdateFrom.XML)
                .setUpdateXML("https://raw.githubusercontent.com/TutorialsAndroid/AppXUpdater/master/files/update.xml")
                .setDisplay(Display.NOTIFICATION)
                .showAppUpdated(true)
                .start());

        binding.included.dialogNoUpdate.setOnClickListener(view -> new AppUpdater(mContext)
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .setDisplay(Display.DIALOG)
                .showAppUpdated(true)
                .start());

        binding.included.snackbarNoUpdate.setOnClickListener(view -> new AppUpdater(mContext)
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .setDisplay(Display.SNACKBAR)
                .showAppUpdated(true)
                .start());

        binding.included.notificationNoUpdate.setOnClickListener(view -> new AppUpdater(mContext)
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .setDisplay(Display.NOTIFICATION)
                .showAppUpdated(true)
                .start());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}