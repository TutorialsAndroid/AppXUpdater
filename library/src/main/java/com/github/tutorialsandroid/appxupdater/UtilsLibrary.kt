package com.github.tutorialsandroid.appxupdater

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.github.tutorialsandroid.appxupdater.enums.Duration
import com.github.tutorialsandroid.appxupdater.enums.UpdateFrom
import com.github.tutorialsandroid.appxupdater.objects.GitHub
import com.github.tutorialsandroid.appxupdater.objects.Update
import com.github.tutorialsandroid.appxupdater.objects.Version
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.util.*


internal object UtilsLibrary {
  fun getAppName(context: Context): String {
    val applicationInfo = context.applicationInfo
    val stringId = applicationInfo.labelRes
    return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(stringId)
  }

  fun getAppPackageName(context: Context): String {
    return context.packageName
  }

  fun getAppInstalledVersion(context: Context): String {
    var version = "0.0.0.0"
    try {
      version = context.packageManager.getPackageInfo(context.packageName, 0).versionName
    } catch (e: PackageManager.NameNotFoundException) {
      e.printStackTrace()
    }
    return version
  }

  fun getAppInstalledVersionCode(context: Context): Int {
    var versionCode = 0
    try {
      versionCode = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
    } catch (e: PackageManager.NameNotFoundException) {
      e.printStackTrace()
    }
    return versionCode
  }

  fun isUpdateAvailable(installedVersion: Update, latestVersion: Update?): Boolean {
    return if (latestVersion!!.latestVersionCode != null && latestVersion.latestVersionCode!! > 0) {
      latestVersion.latestVersionCode!! > installedVersion.latestVersionCode!!
    } else {
      if (!TextUtils.equals(installedVersion.latestVersion, "0.0.0.0") && !TextUtils.equals(latestVersion.latestVersion, "0.0.0.0")) {
        try {
          val installed = Version(installedVersion.latestVersion!!)
          val latest = Version(latestVersion.latestVersion!!)
          installed.compareTo(latest) < 0
        } catch (e: Exception) {
          e.printStackTrace()
          false
        }
      } else false
    }
  }

  fun isStringAVersion(version: String?): Boolean {
    return version!!.matches(".*\\d+.*".toRegex())
  }

  fun isStringAnUrl(s: String?): Boolean {
    var res = false
    try {
      URL(s)
      res = true
    } catch (ignored: MalformedURLException) {
    }
    return res
  }

  fun getDurationEnumToBoolean(duration: Duration?): Boolean {
    var res = false
    when (duration) {
      Duration.INDEFINITE -> res = true
    }
    return res
  }

  private fun getUpdateURL(context: Context, updateFrom: UpdateFrom, gitHub: GitHub?): URL {
    val res: String
    res = when (updateFrom) {
      UpdateFrom.GITHUB -> Config.GITHUB_URL + gitHub!!.gitHubUser + "/" + gitHub.gitHubRepo+ "/releases/latest"
      UpdateFrom.AMAZON -> Config.AMAZON_URL + getAppPackageName(context)
      UpdateFrom.FDROID -> Config.FDROID_URL + getAppPackageName(context)
      else -> String.format(Config.PLAY_STORE_URL, getAppPackageName(context), Locale.getDefault().language)
    }
    return try {
      URL(res)
    } catch (e: MalformedURLException) {
      throw RuntimeException(e)
    }
  }

  fun getLatestAppVersionStore(context: Context, updateFrom: UpdateFrom, gitHub: GitHub?): Update {
    return when (updateFrom) {
      UpdateFrom.GOOGLE_PLAY -> getLatestAppVersionGooglePlay(context)
      else -> getLatestAppVersionHttp(context, updateFrom, gitHub)
    }
  }

  private fun getLatestAppVersionGooglePlay(context: Context): Update {
    var version = "0.0.0.0"
    val recentChanges = ""
    val updateURL = getUpdateURL(context, UpdateFrom.GOOGLE_PLAY, null)
    try {
      version = getJsoupString(updateURL.toString(), ".hAyfc .htlgb", 7)

      //TODO: Release Notes for Google Play is not working
      //recentChanges = getJsoupString(updateURL.toString(), ".W4P4ne .DWPxHb", 1);
      if (TextUtils.isEmpty(version)) {
        Log.e("AppUpdater", "Cannot retrieve latest version. Is it configured properly?")
      }
    } catch (e: Exception) {
      Log.e("AppUpdater", "App wasn't found in the provided source. Is it published?")
    }
    Log.e("Update", version)
    return Update(version, recentChanges, updateURL)
  }

  @Throws(Exception::class)
  private fun getJsoupString(url: String, css: String, position: Int): String {
    return Jsoup.connect(url)
      .timeout(30000)
      .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
      .get()
      .select(css)[position]
      .ownText()
  }

  private fun getLatestAppVersionHttp(context: Context, updateFrom: UpdateFrom, gitHub: GitHub?): Update {
    var isAvailable = false
    var source = ""
    val client = OkHttpClient()
    val url = getUpdateURL(context, updateFrom, gitHub)
    val request = Request.Builder()
      .url(url)
      .build()
    var body: ResponseBody? = null
    try {
      val response = client.newCall(request).execute()
      body = response.body
      val reader = BufferedReader(InputStreamReader(body!!.byteStream(), "UTF-8"))
      val str = StringBuilder()
      var line: String
      while (reader.readLine().also { line = it } != null) {
        when (updateFrom) {
          UpdateFrom.GITHUB -> if (line.contains(Config.GITHUB_TAG_RELEASE)) {
            str.append(line)
            isAvailable = true
          }
          UpdateFrom.AMAZON -> if (line.contains(Config.AMAZON_TAG_RELEASE)) {
            str.append(line)
            isAvailable = true
          }
          UpdateFrom.FDROID -> if (line.contains(Config.FDROID_TAG_RELEASE)) {
            str.append(line)
            isAvailable = true
          }
        }
      }
      if (str.length == 0) {
        Log.e("AppUpdater", "Cannot retrieve latest version. Is it configured properly?")
      }
      response.body!!.close()
      source = str.toString()
    } catch (e: FileNotFoundException) {
      Log.e("AppUpdater", "App wasn't found in the provided source. Is it published?")
    } catch (ignore: IOException) {
    } finally {
      body?.close()
    }
    val version = getVersion(updateFrom, isAvailable, source)
    val updateUrl = getUpdateURL(context, updateFrom, gitHub)
    return Update(version, updateUrl)
  }

  private fun getVersion(updateFrom: UpdateFrom, isAvailable: Boolean, source: String): String {
    var version = "0.0.0.0"
    if (isAvailable) {
      when (updateFrom) {
        UpdateFrom.GITHUB -> {
          var splitGitHub = source.split(Config.GITHUB_TAG_RELEASE.toRegex()).toTypedArray()
          if (splitGitHub.size > 1) {
            splitGitHub = splitGitHub[1].split("(\")".toRegex()).toTypedArray()
            version = splitGitHub[0].trim { it <= ' ' }
            if (version.startsWith("v")) { // Some repo uses vX.X.X
              splitGitHub = version.split("(v)".toRegex(), 2).toTypedArray()
              version = splitGitHub[1].trim { it <= ' ' }
            }
          }
        }
        UpdateFrom.AMAZON -> {
          var splitAmazon = source.split(Config.AMAZON_TAG_RELEASE.toRegex()).toTypedArray()
          splitAmazon = splitAmazon[1].split("(<)".toRegex()).toTypedArray()
          version = splitAmazon[0].trim { it <= ' ' }
        }
        UpdateFrom.FDROID -> {
          var splitFDroid = source.split(Config.FDROID_TAG_RELEASE.toRegex()).toTypedArray()
          splitFDroid = splitFDroid[1].split("(<)".toRegex()).toTypedArray()
          version = splitFDroid[0].trim { it <= ' ' }
        }
      }
    }
    return version
  }

  fun getLatestAppVersion(updateFrom: UpdateFrom, url: String?): Update? {
    return if (updateFrom == UpdateFrom.XML) {
      val parser = ParserXML(url)
      parser.parse()
    } else {
      ParserJSON(url).parse()
    }
  }

  fun intentToUpdate(context: Context, updateFrom: UpdateFrom, url: URL?): Intent {
    val intent: Intent
    intent = if (updateFrom == UpdateFrom.GOOGLE_PLAY) {
      Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getAppPackageName(context)))
    } else {
      Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()))
    }
    return intent
  }

  fun goToUpdate(context: Context, updateFrom: UpdateFrom, url: URL?) {
    var intent = intentToUpdate(context, updateFrom, url)
    if (updateFrom == UpdateFrom.GOOGLE_PLAY) {
      try {
        context.startActivity(intent)
      } catch (e: ActivityNotFoundException) {
        intent = Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()))
        context.startActivity(intent)
      }
    } else {
      context.startActivity(intent)
    }
  }

  fun isAbleToShow(successfulChecks: Int?, showEvery: Int): Boolean {
    return successfulChecks!! % showEvery == 0
  }

  fun isNetworkAvailable(context: Context): Boolean {
    var res = false
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (cm != null) {
      val networkInfo = cm.activeNetworkInfo
      if (networkInfo != null) {
        res = networkInfo.isConnected
      }
    }
    return res
  }
}
