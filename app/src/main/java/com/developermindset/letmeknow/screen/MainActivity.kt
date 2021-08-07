package com.developermindset.letmeknow.screen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.developermindset.letmeknow.R
import com.developermindset.letmeknow.utils.StoreData

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun initialization(){
        val isServiceEnable = StoreData(this).getIsServiceEnable()
    }
}