package com.example.loggerclient

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.example.LoggerAIDL
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    val ACTION_AIDL = "com.example.LoggerAIDL"
  private  var loggerAIDL: LoggerAIDL? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("Srervice connected","str")
            loggerAIDL = LoggerAIDL.Stub.asInterface(service)
            val str=  loggerAIDL?.log("Test1")
            Log.d("Client got result",str?:"Error")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            loggerAIDL = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    override fun onStart() {
        super.onStart()
        bindService(createExplicitIntent(), serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }
    private fun createExplicitIntent(): Intent {
        val intent = Intent(ACTION_AIDL)
        val services = packageManager.queryIntentServices(intent, 0)
        if (services.isEmpty()) {
            throw IllegalStateException("App is not installed")
        }
        return Intent(intent).apply {
            val resolveInfo = services[0]
            val packageName = resolveInfo.serviceInfo.packageName
            val className = resolveInfo.serviceInfo.name
            component = ComponentName(packageName, className)
        }
    }
}