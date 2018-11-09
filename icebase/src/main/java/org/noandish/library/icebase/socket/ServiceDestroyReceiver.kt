package org.noandish.library.icebase.socket

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Created by salman on 10/16/2018 AD.
 */

class ServiceDestroyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("debug", "ServeiceDestroy onReceive...")
        Log.d("debug", "action:" + intent.action)
        Log.d("debug", "Starting Service")
        if (!SocketIOApplication.isMyServiceRunning(ServiceClient::class.java, context))
            context.startService(Intent(context, ServiceClient::class.java))

    }
}
