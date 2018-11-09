package org.noandish.library.icebase.socket

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import android.util.Log
import java.net.URISyntaxException
import io.socket.client.IO
import io.socket.client.Socket
import org.noandish.library.icebase.ActivityStartService


open class SocketIOApplication : MultiDexApplication(), OnChangeStatusSocketListener, OnMessasgeReceive {
    private val socketIOApplication: SocketIOApplication? = null
    override fun OnMessasgeReceive(eventItem: String, message: Any) {
        Log.w("SocketIOApplication", "OnMessageReceive" + eventItem)
        if (socketIOApplication!=null)
            socketIOApplication.OnMessasgeReceive(eventItem,message)
    }

    override fun OnChanged(status: String) {
        Log.w("SocketIOApplication", "OnChanged " + status)
        if (socketIOApplication!=null)
            socketIOApplication.OnChanged(status)
    }

    internal var socket: Socket
        set
    var context: Context? = null
        protected set
        get

    public fun initialized(socketIOApplication: SocketIOApplication) {
        this.context = socketIOApplication.context
    }

    public var socketControler: SocketController

    init {
        try {
            socket = IO.socket(Utils.SERVICEYAR_SERVER_URL)
            socketControler = SocketController(this, socket)
            Log.w("SocketIOApplication", "Connected")
        } catch (e: URISyntaxException) {
            Log.w("SocketIOApplication", "Connecting Error")
            throw RuntimeException(e)
        }

    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        startActivity(Intent(base, ActivityStartService::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
//        }else{
//            startService(Intent(base,ServiceClient::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
//        }
    }

    companion object {

        public fun isMyServiceRunning(serviceClass: Class<*>, context: Context): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
            return false
        }
    }
}


