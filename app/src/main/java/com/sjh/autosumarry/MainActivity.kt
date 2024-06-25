package com.sjh.autosumarry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sjh.autosumarry.core.designsystem.theme.AutoSumarryTheme
import com.sjh.autosumarry.feature.AutoSummaryApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutoSumarryTheme {
                AutoSummaryApp()
            }
        }
    }
}
