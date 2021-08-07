package com.developermindset.letmeknow.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.Keep

@Keep
class StoreData(context: Context) {
    private val mode = 0
    private val prefName = "LetMeKnow"

    var pref: SharedPreferences = context.getSharedPreferences(prefName, mode)
    var editor: SharedPreferences.Editor = pref.edit()

    fun setIsServiceEnable(flag: Boolean) {
        editor.putBoolean(AppConstant.isServiceEnable, flag)
        editor.commit()
    }

    fun getIsServiceEnable(): Boolean {
        return pref.getBoolean(AppConstant.isServiceEnable, false)
    }

    fun setIsCameraServiceEnable(flag: Boolean) {
        editor.putBoolean(AppConstant.isCameraServiceEnable, flag)
        editor.commit()
    }

    fun getIsCameraServiceEnable(): Boolean {
        return pref.getBoolean(AppConstant.isCameraServiceEnable, false)
    }

    fun setIsMicrophoneServiceEnable(flag: Boolean) {
        editor.putBoolean(AppConstant.isMicrophoneServiceEnable, flag)
        editor.commit()
    }

    fun getIsMicroPhoneServiceEnable(): Boolean {
        return pref.getBoolean(AppConstant.isMicrophoneServiceEnable, false)
    }

}