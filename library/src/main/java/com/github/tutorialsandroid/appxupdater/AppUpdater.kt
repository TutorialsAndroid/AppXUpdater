package com.github.tutorialsandroid.appxupdater

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.github.tutorialsandroid.appxupdater.UtilsAsync.LatestAppVersion
import com.github.tutorialsandroid.appxupdater.enums.AppUpdaterError
import com.github.tutorialsandroid.appxupdater.enums.Display
import com.github.tutorialsandroid.appxupdater.enums.Duration
import com.github.tutorialsandroid.appxupdater.enums.UpdateFrom
import com.github.tutorialsandroid.appxupdater.interfaces.IAppUpdater
import com.github.tutorialsandroid.appxupdater.interfaces.IAppUpdater.LibraryListener
import com.github.tutorialsandroid.appxupdater.objects.GitHub
import com.github.tutorialsandroid.appxupdater.objects.Update
import com.google.android.material.snackbar.Snackbar


/**
 * @author tkdco , TutorialsAndroid
 */
class AppUpdater(private val context: Context) : IAppUpdater {
  private val libraryPreferences: LibraryPreferences
  private var display: Display
  private var updateFrom: UpdateFrom
  private var duration: Duration
  private var gitHub: GitHub? = null
  private var xmlOrJsonUrl: String? = null
  private var showEvery: Int
  private var showAppUpdated: Boolean
  private var titleUpdate: String
  private var descriptionUpdate: String? = null
  private var btnDismiss: String
  private var btnUpdate: String
  private var btnDisable // Update available
    : String
  private var titleNoUpdate: String
  private var descriptionNoUpdate // Update not available
    : String? = null
  private var iconResId: Int
  private var latestAppVersion: LatestAppVersion? = null
  private var btnUpdateClickListener: DialogInterface.OnClickListener? = null
  private var btnDismissClickListener: DialogInterface.OnClickListener? = null
  private var btnDisableClickListener: DialogInterface.OnClickListener? = null
  private var alertDialog: AlertDialog? = null
  private var snackbar: Snackbar? = null
  private var isDialogCancelable: Boolean
  override fun setDisplay(display: Display): AppUpdater {
    this.display = display
    return this
  }

  override fun setUpdateFrom(updateFrom: UpdateFrom): AppUpdater {
    this.updateFrom = updateFrom
    return this
  }

  override fun setDuration(duration: Duration): AppUpdater {
    this.duration = duration
    return this
  }

  override fun setGitHubUserAndRepo(user: String, repo: String): AppUpdater {
    gitHub = GitHub(user, repo)
    return this
  }

  override fun setUpdateXML(xmlUrl: String): AppUpdater {
    xmlOrJsonUrl = xmlUrl
    return this
  }

  override fun setUpdateJSON(jsonUrl: String): AppUpdater {
    xmlOrJsonUrl = jsonUrl
    return this
  }

  override fun showEvery(times: Int): AppUpdater {
    showEvery = times
    return this
  }

  override fun showAppUpdated(res: Boolean): AppUpdater {
    showAppUpdated = res
    return this
  }

  @Deprecated("")
  override fun setDialogTitleWhenUpdateAvailable(title: String): AppUpdater {
    setTitleOnUpdateAvailable(title)
    return this
  }

  @Deprecated("")
  override fun setDialogTitleWhenUpdateAvailable(@StringRes textResource: Int): AppUpdater {
    setTitleOnUpdateAvailable(textResource)
    return this
  }

  override fun setTitleOnUpdateAvailable(title: String): AppUpdater {
    titleUpdate = title
    return this
  }

  override fun setTitleOnUpdateAvailable(@StringRes textResource: Int): AppUpdater {
    titleUpdate = context.getString(textResource)
    return this
  }

  @Deprecated("")
  override fun setDialogDescriptionWhenUpdateAvailable(description: String): AppUpdater {
    setContentOnUpdateAvailable(description)
    return this
  }

  @Deprecated("")
  override fun setDialogDescriptionWhenUpdateAvailable(@StringRes textResource: Int): AppUpdater {
    setContentOnUpdateAvailable(textResource)
    return this
  }

  override fun setContentOnUpdateAvailable(description: String): AppUpdater {
    descriptionUpdate = description
    return this
  }

  override fun setContentOnUpdateAvailable(@StringRes textResource: Int): AppUpdater {
    descriptionUpdate = context.getString(textResource)
    return this
  }

  @Deprecated("")
  override fun setDialogTitleWhenUpdateNotAvailable(title: String): AppUpdater {
    setTitleOnUpdateNotAvailable(title)
    return this
  }

  @Deprecated("")
  override fun setDialogTitleWhenUpdateNotAvailable(@StringRes textResource: Int): AppUpdater {
    setTitleOnUpdateNotAvailable(textResource)
    return this
  }

  override fun setTitleOnUpdateNotAvailable(title: String): AppUpdater {
    titleNoUpdate = title
    return this
  }

  override fun setTitleOnUpdateNotAvailable(@StringRes textResource: Int): AppUpdater {
    titleNoUpdate = context.getString(textResource)
    return this
  }

  @Deprecated("")
  override fun setDialogDescriptionWhenUpdateNotAvailable(description: String): AppUpdater {
    setContentOnUpdateNotAvailable(description)
    return this
  }

  @Deprecated("")
  override fun setDialogDescriptionWhenUpdateNotAvailable(@StringRes textResource: Int): AppUpdater {
    setContentOnUpdateNotAvailable(textResource)
    return this
  }

  override fun setContentOnUpdateNotAvailable(description: String): AppUpdater {
    descriptionNoUpdate = description
    return this
  }

  override fun setContentOnUpdateNotAvailable(@StringRes textResource: Int): AppUpdater {
    descriptionNoUpdate = context.getString(textResource)
    return this
  }

  @Deprecated("")
  override fun setDialogButtonUpdate(text: String): AppUpdater {
    setButtonUpdate(text)
    return this
  }

  @Deprecated("")
  override fun setDialogButtonUpdate(@StringRes textResource: Int): AppUpdater {
    setButtonUpdate(textResource)
    return this
  }

  override fun setButtonUpdate(text: String): AppUpdater {
    btnUpdate = text
    return this
  }

  override fun setButtonUpdate(@StringRes textResource: Int): AppUpdater {
    btnUpdate = context.getString(textResource)
    return this
  }

  @Deprecated("")
  override fun setDialogButtonDismiss(text: String): AppUpdater {
    setButtonDismiss(text)
    return this
  }

  @Deprecated("")
  override fun setDialogButtonDismiss(@StringRes textResource: Int): AppUpdater {
    setButtonDismiss(textResource)
    return this
  }

  override fun setButtonDismiss(text: String): AppUpdater {
    btnDismiss = text
    return this
  }

  override fun setButtonDismiss(@StringRes textResource: Int): AppUpdater {
    btnDismiss = context.getString(textResource)
    return this
  }

  @Deprecated("")
  override fun setDialogButtonDoNotShowAgain(text: String): AppUpdater {
    setButtonDoNotShowAgain(text)
    return this
  }

  @Deprecated("")
  override fun setDialogButtonDoNotShowAgain(@StringRes textResource: Int): AppUpdater {
    setButtonDoNotShowAgain(textResource)
    return this
  }

  override fun setButtonDoNotShowAgain(text: String): AppUpdater {
    btnDisable = text
    return this
  }

  override fun setButtonDoNotShowAgain(@StringRes textResource: Int): AppUpdater {
    btnDisable = context.getString(textResource)
    return this
  }

  override fun setButtonUpdateClickListener(clickListener: DialogInterface.OnClickListener?): AppUpdater {
    btnUpdateClickListener = clickListener
    return this
  }

  override fun setButtonDismissClickListener(clickListener: DialogInterface.OnClickListener?): AppUpdater {
    btnDismissClickListener = clickListener
    return this
  }

  override fun setButtonDoNotShowAgainClickListener(clickListener: DialogInterface.OnClickListener?): AppUpdater {
    btnDisableClickListener = clickListener
    return this
  }

  override fun setIcon(@DrawableRes iconRes: Int): AppUpdater {
    iconResId = iconRes
    return this
  }

  override fun setCancelable(isDialogCancelable: Boolean): AppUpdater {
    this.isDialogCancelable = isDialogCancelable
    return this
  }

  override fun init(): AppUpdater {
    start()
    return this
  }

  override fun start() {
    latestAppVersion = LatestAppVersion(context, false, updateFrom, gitHub, xmlOrJsonUrl, object : LibraryListener {
      override fun onSuccess(update: Update?) {
        if (context is Activity && context.isFinishing) {
          return
        }
        val installedUpdate = Update(UtilsLibrary.getAppInstalledVersion(context), UtilsLibrary.getAppInstalledVersionCode(context))
        if (UtilsLibrary.isUpdateAvailable(installedUpdate, update)) {
          val successfulChecks = libraryPreferences.successfulChecks
          if (UtilsLibrary.isAbleToShow(successfulChecks, showEvery)) {
            when (display) {
              Display.DIALOG -> {
                val updateClickListener = if (btnUpdateClickListener == null) UpdateClickListener(context, updateFrom, update!!.urlToDownload) else btnUpdateClickListener!!
                val disableClickListener = if (btnDisableClickListener == null) DisableClickListener(context) else btnDisableClickListener!!
                alertDialog = UtilsDisplay.showUpdateAvailableDialog(context, titleUpdate, getDescriptionUpdate(context, update, Display.DIALOG), btnDismiss, btnUpdate, btnDisable, updateClickListener, btnDismissClickListener, disableClickListener)
                alertDialog!!.setCancelable(isDialogCancelable)
                alertDialog!!.show()
              }
              Display.SNACKBAR -> {
                snackbar = UtilsDisplay.showUpdateAvailableSnackbar(context, getDescriptionUpdate(context, update, Display.SNACKBAR), UtilsLibrary.getDurationEnumToBoolean(duration), updateFrom, update!!.urlToDownload)
                snackbar!!.show()
              }
              Display.NOTIFICATION -> UtilsDisplay.showUpdateAvailableNotification(context, titleUpdate, getDescriptionUpdate(context, update, Display.NOTIFICATION), updateFrom, update!!.urlToDownload, iconResId)
            }
          }
          libraryPreferences.successfulChecks = successfulChecks!! + 1
        } else if (showAppUpdated) {
          when (display) {
            Display.DIALOG -> {
              alertDialog = UtilsDisplay.showUpdateNotAvailableDialog(context, titleNoUpdate, getDescriptionNoUpdate(context))
              alertDialog!!.setCancelable(isDialogCancelable)
              alertDialog!!.show()
            }
            Display.SNACKBAR -> {
              snackbar = UtilsDisplay.showUpdateNotAvailableSnackbar(context, getDescriptionNoUpdate(context), UtilsLibrary.getDurationEnumToBoolean(duration))
              snackbar!!.show()
            }
            Display.NOTIFICATION -> UtilsDisplay.showUpdateNotAvailableNotification(context, titleNoUpdate, getDescriptionNoUpdate(context), iconResId)
          }
        }
      }

      override fun onFailed(error: AppUpdaterError) {
        if (error == AppUpdaterError.UPDATE_VARIES_BY_DEVICE) {
          Log.e("AppUpdater", "UpdateFrom.GOOGLE_PLAY isn't valid: update varies by device.")
        } else require(error != AppUpdaterError.GITHUB_USER_REPO_INVALID) { "GitHub user or repo is empty!" }
        require(error != AppUpdaterError.XML_URL_MALFORMED) { "XML file is not valid!" }
      }
    })
    latestAppVersion!!.execute()
  }

  override fun stop() {
    if (latestAppVersion != null && !latestAppVersion!!.isCancelled) {
      latestAppVersion!!.cancel(true)
    }
  }

  override fun dismiss() {
    if (alertDialog != null && alertDialog!!.isShowing) {
      alertDialog!!.dismiss()
    }
    if (snackbar != null && snackbar!!.isShown) {
      snackbar!!.dismiss()
    }
  }

  private fun getDescriptionUpdate(context: Context, update: Update?, display: Display): String? {
    return if (descriptionUpdate == null || TextUtils.isEmpty(descriptionUpdate)) {
      when (display) {
        Display.DIALOG -> if (update!!.releaseNotes != null && !TextUtils.isEmpty(update.releaseNotes)) {
          if (TextUtils.isEmpty(descriptionUpdate)) update.releaseNotes else String.format(context.resources.getString(R.string.appupdater_update_available_description_dialog_before_release_notes), update.latestVersion, update.latestVersion)
        } else {
          String.format(context.resources.getString(R.string.appupdater_update_available_description_dialog), update.latestVersion, UtilsLibrary.getAppName(context))
        }
        Display.SNACKBAR -> String.format(context.resources.getString(R.string.appupdater_update_available_description_snackbar), update!!.latestVersion)
        Display.NOTIFICATION -> String.format(context.resources.getString(R.string.appupdater_update_available_description_notification), update!!.latestVersion, UtilsLibrary.getAppName(context))
      }
    } else descriptionUpdate
  }

  private fun getDescriptionNoUpdate(context: Context): String {
    return if (descriptionNoUpdate == null) {
      String.format(context.resources.getString(R.string.appupdater_update_not_available_description), UtilsLibrary.getAppName(context))
    } else {
      descriptionNoUpdate!!
    }
  }

  init {
    libraryPreferences = LibraryPreferences(context)
    display = Display.DIALOG
    updateFrom = UpdateFrom.GOOGLE_PLAY
    duration = Duration.NORMAL
    showEvery = 1
    showAppUpdated = false
    iconResId = R.drawable.ic_stat_name

    // Dialog
    titleUpdate = context.resources.getString(R.string.appupdater_update_available)
    titleNoUpdate = context.resources.getString(R.string.appupdater_update_not_available)
    btnUpdate = context.resources.getString(R.string.appupdater_btn_update)
    btnDismiss = context.resources.getString(R.string.appupdater_btn_dismiss)
    btnDisable = context.resources.getString(R.string.appupdater_btn_disable)
    isDialogCancelable = true
  }
}
