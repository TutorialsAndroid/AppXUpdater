package com.github.tutorialsandroid.appxupdater

import android.content.Context
import android.content.DialogInterface
import com.github.tutorialsandroid.appxupdater.enums.UpdateFrom
import java.net.URL


/**
 * Click listener for the "Update" button of the update dialog. <br></br>
 * Extend this class to add custom actions to the button on top of the default functionality.
 */
class UpdateClickListener(private val context: Context, private val updateFrom: UpdateFrom, private val apk: URL?) : DialogInterface.OnClickListener {
  override fun onClick(dialog: DialogInterface, which: Int) {
    UtilsLibrary.goToUpdate(context, updateFrom, apk)
  }
}
