package com.pawcare.pawcare.services

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

class Backend(
    val context: Context,
    val preferences: SharedPreferences
) {

    val backOffice: BackOffice = BackOffice(this)

    fun getVersionCode(): Int {

        val pInfo: PackageInfo
        try {
            pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return pInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 1
    }

}