package com.example.financial

import android.app.Application
import com.example.financial.data.AppContainer
import com.example.financial.data.DefaultAppContainer
import com.example.financial.worker.FinancialNotification

class FinancialApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        FinancialNotification.createNotificationChannel(this)
    }
}
