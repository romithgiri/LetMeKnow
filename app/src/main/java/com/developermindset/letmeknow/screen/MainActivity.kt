package com.developermindset.letmeknow.screen

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.developermindset.letmeknow.R
import com.developermindset.letmeknow.service.MonitorService
import com.developermindset.letmeknow.utils.StoreData
import com.developermindset.letmeknow.utils.Util
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val appUsagePermissionCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_power_on_off.setOnClickListener {
            try {
                if (usagePermissionCheck()) {
                    if (StoreData(this).getIsServiceEnable()) {
                        StoreData(this).setIsServiceEnable(false)
                        Intent(this, MonitorService::class.java).also {
                            stopService(it)
                        }
                        forPowerOnOff()
                    } else {
                        StoreData(this).setIsServiceEnable(true)
                        startService()
                        forPowerOnOff()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error: $e")
            }
        }

        btn_enable_camera.setOnClickListener {
            StoreData(this).setIsCameraServiceEnable(true)
            forCameraOnOff()
        }

        btn_disable_camera.setOnClickListener {
            StoreData(this).setIsCameraServiceEnable(false)
            forCameraOnOff()
        }

        btn_enable_microphone.setOnClickListener {
            StoreData(this).setIsMicrophoneServiceEnable(true)
            forMicrophoneOnOff()
        }

        btn_disable_microphone.setOnClickListener {
            StoreData(this).setIsMicrophoneServiceEnable(false)
            forMicrophoneOnOff()
        }
    }

    override fun onResume() {
        super.onResume()
        enableButtons(false)
        if (usagePermissionCheck()) {
            if (StoreData(this).getIsServiceEnable()) {
                startService()
                forPowerOnOff()
                forCameraOnOff()
                forMicrophoneOnOff()
                enableButtons(true)
            }
        } else {
            forPowerOnOff()
            forCameraOnOff()
            forMicrophoneOnOff()
        }
    }

    private fun forPowerOnOff() {
        val isServiceEnable = StoreData(this).getIsServiceEnable()
        if (isServiceEnable) {
            if (Util.isBackgroundServiceRunning(this, MonitorService::class.java)) {
                btn_power_on_off.setIconTintResource(R.color.green)
                btn_power_on_off.text = "ON"
                btn_power_on_off.setTextColor(ContextCompat.getColor(this, R.color.green))
                enableButtons(true)
            } else {
                btn_power_on_off.setIconTintResource(R.color.red)
                btn_power_on_off.text = "OFF"
                btn_power_on_off.setTextColor(ContextCompat.getColor(this, R.color.red))
                enableButtons(false)
            }
        } else {
            btn_power_on_off.setIconTintResource(R.color.red)
            btn_power_on_off.text = "OFF"
            btn_power_on_off.setTextColor(ContextCompat.getColor(this, R.color.red))
            enableButtons(false)
        }
    }

    private fun forCameraOnOff() {
        if (StoreData(this).getIsCameraServiceEnable()) {
            tv_camera_status.text = "Enable"
            tv_camera_status.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.green
                )
            )
            btn_enable_camera.visibility = View.GONE
            btn_disable_camera.visibility = View.VISIBLE
        } else {
            tv_camera_status.text = "Disable"
            tv_camera_status.setTextColor(ContextCompat.getColor(this, R.color.red))
            btn_enable_camera.visibility = View.VISIBLE
            btn_disable_camera.visibility = View.GONE
        }
    }

    private fun forMicrophoneOnOff() {
        if (StoreData(this).getIsMicroPhoneServiceEnable()) {
            tv_microphone_status.text = "Enable"
            tv_microphone_status.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.green
                )
            )
            btn_enable_microphone.visibility = View.GONE
            btn_disable_microphone.visibility = View.VISIBLE
        } else {
            tv_microphone_status.text = "Disable"
            tv_microphone_status.setTextColor(ContextCompat.getColor(this, R.color.red))
            btn_disable_microphone.visibility = View.GONE
            btn_enable_microphone.visibility = View.VISIBLE
        }
    }

    private fun usagePermissionCheck(): Boolean {
        return if (Util.hasUsagePermission(this)) {
            true
        } else {
            requestAppUsagePermission()
            false
        }
    }

    private fun startService() {
        Intent(this, MonitorService::class.java).also {
            startService(it)
        }
    }

    private fun requestAppUsagePermission() {
        val mDialogView: View = LayoutInflater.from(this).inflate(
            R.layout.custom_alert_layout,
            null
        )

        val mBuilder = AlertDialog.Builder(this).create()
        mBuilder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mBuilder.window?.setBackgroundDrawableResource(R.drawable.custom_background_dialog)
        mBuilder.setView(mDialogView)
        mBuilder.setCancelable(false)

        val btnCancel = mDialogView.findViewById<Button>(R.id.btn_cancel)
        val btnGrant = mDialogView.findViewById<Button>(R.id.btn_grant)

        btnGrant.setOnClickListener {
            mBuilder.dismiss()
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)
        }

        btnCancel.setOnClickListener {
            mBuilder.dismiss()
            //finish()
        }

        mBuilder.show()
    }

    private fun initialization() {
        Log.i(TAG, "====================>>>>>: ${Util.hasUsagePermission(this)}")
        if (Util.hasUsagePermission(this)) {
            val isServiceEnable = StoreData(this).getIsServiceEnable()
            if (isServiceEnable) {
                if (Util.isBackgroundServiceRunning(this, MonitorService::class.java)) {
                    if (isServiceEnable) {
                        btn_power_on_off.setIconTintResource(R.color.green)
                        btn_power_on_off.setTextColor(ContextCompat.getColor(this, R.color.green))

                        if (StoreData(this).getIsCameraServiceEnable()) {
                            tv_camera_status.text = "Enable"
                            tv_camera_status.setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.green
                                )
                            )
                            btn_enable_camera.alpha = 0.4F
                            btn_enable_camera.isEnabled = false
                        }

                        if (StoreData(this).getIsMicroPhoneServiceEnable()) {
                            tv_microphone_status.text = "Enable"
                            tv_microphone_status.setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.green
                                )
                            )
                            btn_enable_microphone.alpha = 0.4F
                            btn_enable_microphone.isEnabled = false
                        }
                    } else {
                        btn_power_on_off.setIconTintResource(R.color.red)
                        tv_camera_status.setTextColor(ContextCompat.getColor(this, R.color.red))

                        tv_camera_status.text = "Disable"
                        tv_camera_status.setTextColor(ContextCompat.getColor(this, R.color.red))
                        btn_disable_camera.alpha = 0.4F
                        btn_disable_camera.isEnabled = false

                        tv_microphone_status.text = "Disable"
                        tv_microphone_status.setTextColor(ContextCompat.getColor(this, R.color.red))
                        btn_disable_microphone.alpha = 0.4F
                        btn_disable_microphone.isEnabled = false
                    }
                } else {
                    Intent(this, MonitorService::class.java).also {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(it)
                            return
                        }
                        startService(it)
                    }
                }
            }
        } else {
            requestAppUsagePermission()
        }
    }

    private fun enableButtons(flag: Boolean){
        if (flag){
            btn_enable_camera.isEnabled = true
            btn_disable_camera.isEnabled = true
            btn_disable_microphone.isEnabled = true
            btn_enable_microphone.isEnabled = true
        }else{
            btn_enable_camera.isEnabled = false
            btn_disable_camera.isEnabled = false
            btn_disable_microphone.isEnabled = false
            btn_enable_microphone.isEnabled = false
        }
    }

}