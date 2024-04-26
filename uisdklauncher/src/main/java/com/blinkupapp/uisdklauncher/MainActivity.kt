package com.blinkupapp.uisdklauncher

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.blinkup.uisdk.BlinkupUISDK

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.launch_default_button).setOnClickListener {
            BlinkupUISDK.launch(this, "Ph1bFOq1moKmm0in2lxsfZ5v-No-Og6wWxEKM-6F1OM=")
        }
        findViewById<Button>(R.id.launch_dark_red_button).setOnClickListener {
            BlinkupUISDK.launch(this, "Ph1bFOq1moKmm0in2lxsfZ5v-No-Og6wWxEKM-6F1OM=", R.style.DarkRedTheme)
        }
    }
}