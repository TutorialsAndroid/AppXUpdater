package com.github.tutorialsandroid.appxupdater

import android.content.Context
import android.os.AsyncTask
import com.github.tutorialsandroid.appxupdater.enums.AppUpdaterError
import com.github.tutorialsandroid.appxupdater.enums.UpdateFrom
import com.github.tutorialsandroid.appxupdater.interfaces.IAppUpdater.LibraryListener
import com.github.tutorialsandroid.appxupdater.objects.GitHub
import com.github.tutorialsandroid.appxupdater.objects.Update
import java.lang.ref.WeakReference


internal class UtilsAsync {
  internal class LatestAppVersion(context: Context, fromUtils: Boolean, updateFrom: UpdateFrom, gitHub: GitHub?, xmlOrJsonUrl: String?, listener: LibraryListener?) : AsyncTask<Void?, Void?, Update?>() {
    private val contextRef: WeakReference<Context> = WeakReference(context)
    private val libraryPreferences: LibraryPreferences = LibraryPreferences(context)
    private val fromUtils: Boolean = fromUtils
    private val updateFrom: UpdateFrom = updateFrom
    private val gitHub: GitHub? = gitHub
    private val xmlOrJsonUrl: String? = xmlOrJsonUrl
    private val listener: LibraryListener? = listener
    override fun onPreExecute() {
      super.onPreExecute()
      val context = contextRef.get()
      if (context == null || listener == null) {
        cancel(true)
      } else if (UtilsLibrary.isNetworkAvailable(context)) {
        if (!fromUtils && !libraryPreferences.appUpdaterShow!!) {
          cancel(true)
        } else {
          if (updateFrom == UpdateFrom.GITHUB && !GitHub.Companion.isGitHubValid(gitHub)) {
            listener.onFailed(AppUpdaterError.GITHUB_USER_REPO_INVALID)
            cancel(true)
          } else if (updateFrom == UpdateFrom.XML && (xmlOrJsonUrl == null || !UtilsLibrary.isStringAnUrl(xmlOrJsonUrl))) {
            listener.onFailed(AppUpdaterError.XML_URL_MALFORMED)
            cancel(true)
          } else if (updateFrom == UpdateFrom.JSON && (xmlOrJsonUrl == null || !UtilsLibrary.isStringAnUrl(xmlOrJsonUrl))) {
            listener.onFailed(AppUpdaterError.JSON_URL_MALFORMED)
            cancel(true)
          }
        }
      } else {
        listener.onFailed(AppUpdaterError.NETWORK_NOT_AVAILABLE)
        cancel(true)
      }
    }


    override fun onPostExecute(update: Update?) {
      super.onPostExecute(update)
      if (listener != null) {
        if (UtilsLibrary.isStringAVersion(update!!.latestVersion)) {
          listener.onSuccess(update)
        } else {
          listener.onFailed(AppUpdaterError.UPDATE_VARIES_BY_DEVICE)
        }
      }
    }

    override fun doInBackground(vararg params: Void?): Update? {
      return try {
        if (updateFrom == UpdateFrom.XML || updateFrom == UpdateFrom.JSON) {
          val update = UtilsLibrary.getLatestAppVersion(updateFrom, xmlOrJsonUrl)
          if (update != null) {
            update
          } else {
            val error = if (updateFrom == UpdateFrom.XML) AppUpdaterError.XML_ERROR else AppUpdaterError.JSON_ERROR
            listener?.onFailed(error)
            cancel(true)
            null
          }
        } else {
          val context = contextRef.get()
          if (context != null) {
            UtilsLibrary.getLatestAppVersionStore(context, updateFrom, gitHub)
          } else {
            cancel(true)
            null
          }
        }
      } catch (ex: Exception) {
        cancel(true)
        null
      }
    }
  }
}
