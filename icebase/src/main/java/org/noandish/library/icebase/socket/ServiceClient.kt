package org.noandish.library.icebase.socket

import android.annotation.SuppressLint
import android.app.*
import android.app.Notification.PRIORITY_MIN
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.noandish.library.icebase.R
import android.R.string.cancel




/**
 * Created by AliasgharMirzazade on 25/03/2018.
 */

class ServiceClient : Service() {

    private val NOTIFICATION_CHANNEL_ID = "my_notification_channel"

    private val TAG = "ServiceClient"
    /**
     * STATUS_CONNECTED
     */
    private var isConnected = false
    private var handler: Handler? = null
    private var mSocket: Socket? = null
    private lateinit var socketControler: SocketController
    private val onReciveMessage = Emitter.Listener { args ->
        handler!!.post {
            try {
                val jsonArray = JSONArray(args[0].toString())
                val jsonObject = jsonArray.getJSONObject(0)
                if (jsonObject.has("response") && jsonObject.getInt("response") == 0) {
                    val intent = Intent(BROADCAST_STATUS_SOCKET)
//                    intent.putExtra()
                    sendBroadcast(intent)
                }
                socketControler.OnChanged(Socket.EVENT_MESSAGE)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private val onConnecting = Emitter.Listener {
        handler!!.post {
            val intent = Intent(BROADCAST_STATUS_SOCKET)
            intent.putExtra(STATUS_SOCKET, Socket.EVENT_CONNECTING)
            sendBroadcast(intent)
            socketControler.OnChanged(Socket.EVENT_CONNECTING)

        }
    }

    private val onConnect = Emitter.Listener {
        handler!!.post {
            if (!isConnected) {
                isConnected = true
                val intent = Intent(BROADCAST_STATUS_SOCKET)
                intent.putExtra(STATUS_SOCKET, Socket.EVENT_CONNECT)
                sendBroadcast(intent)
                socketControler.OnChanged(Socket.EVENT_CONNECT)
            }
        }
    }

    private val onDisconnect = Emitter.Listener {
        handler!!.post {
            isConnected = false
            val intent = Intent(BROADCAST_STATUS_SOCKET)
            intent.putExtra(STATUS_SOCKET, Socket.EVENT_DISCONNECT)
            sendBroadcast(intent)
            socketControler.OnChanged(Socket.EVENT_DISCONNECT)
        }
    }

    private val onConnectError = Emitter.Listener {
        handler!!.post {
            val intent = Intent(BROADCAST_STATUS_SOCKET)
            intent.putExtra(STATUS_SOCKET, Socket.EVENT_ERROR)
            sendBroadcast(intent)
            socketControler.OnChanged(Socket.EVENT_DISCONNECT)
        }
    }

    override fun onCreate() {
        super.onCreate()
        handler = Handler()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lockscreenVisibility = Notification.VISIBILITY_SECRET
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        socketControler = (application as SocketIOApplication).socketControler

//        startForeground()
        Log.w("onStartCommand", "onStartCommand");
        handler = Handler()
        mSocket = (application as SocketIOApplication).socketControler.socket
        mSocket!!.on(Socket.EVENT_CONNECT, onConnect)
        mSocket!!.on(Socket.EVENT_DISCONNECT, onDisconnect)
        mSocket!!.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
        mSocket!!.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)
        mSocket!!.on(Socket.EVENT_MESSAGE, onReciveMessage)
        mSocket!!.on(Socket.EVENT_CONNECTING, onConnecting)
        if (!mSocket!!.connected())
            mSocket!!.connect()
        return START_STICKY
    }
    private fun startForeground() {
        val channelId =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotificationChannel("my_service", "My Background Service")
                } else {
                    // If earlier version channel ID is not used
                    // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                    ""
                }

        val notificationBuilder = NotificationCompat.Builder(this, channelId )
        val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_MIN)
                .setCategory(Notification.CATEGORY_SYSTEM)
                .build()

        startForeground(101, notification)
    }
    companion object {
        val STATUS_SOCKET = "status_socket"
        val KEY_REMOVE_MESSAGE = "remove_message"
        val BROADCAST_STATUS_SOCKET = "BROADCAST_STATUS_SOCKET"
    }

}
