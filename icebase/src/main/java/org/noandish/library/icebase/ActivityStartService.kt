package org.noandish.library.icebase

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import org.noandish.library.icebase.socket.ServiceClient
import org.noandish.library.icebase.socket.SocketIOApplication.Companion.isMyServiceRunning

/**
 * Created by AliasgharMirzazade on 10/21/2018 AD.
 */

class ActivityStartService : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.w("activitytest", "teest")
        if (!isMyServiceRunning(ServiceClient::class.java, baseContext)) {
            startService(Intent(baseContext, ServiceClient::class.java))
        }
        finish()
    }
}
