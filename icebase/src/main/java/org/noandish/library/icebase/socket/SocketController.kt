package org.noandish.library.icebase.socket

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.util.Log
import io.socket.client.Ack
import io.socket.client.Socket
import org.json.JSONArray
import org.json.JSONObject
import org.noandish.library.database.*
import org.noandish.library.icebase.BuildConfig
import org.noandish.library.icebase.database.AddressForConnect
import org.noandish.library.icebase.database.EventItem
import org.noandish.library.icebase.socket.Response.Companion.STATUS_ERROR_CONNECTION
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by AliasgharMirzazade on 10/9/2018 AD.
 */
// ***************************************************fix cancel connection  *****************************************

class SocketController(val application: SocketIOApplication, val socket: Socket) : OnChangeStatusSocketListener, OnMessasgeReceive {
    var array_response = ArrayList<ParamsResponse>()
    var socketId = 0
    var bln_progress_error = false
    var time_out = 15000L
        set
        get

    override fun OnMessasgeReceive(eventItem: String, message: Any) {
        application.OnMessasgeReceive(eventItem, message)
    }

    override fun OnChanged(status: String) {
        application.OnChanged(status)
        when (status) {
            Socket.EVENT_CONNECT -> {
                requestConnect()
            }
            Socket.EVENT_CONNECTING -> {

            }
            Socket.EVENT_CONNECT_ERROR -> {
                errorAllConnection(STATUS_ERROR_CONNECTION)
            }
            Socket.EVENT_CONNECT_TIMEOUT -> {
                errorAllConnection(STATUS_ERROR_CONNECTION)
            }
            Socket.EVENT_DISCONNECT -> {
                errorAllConnection(STATUS_ERROR_CONNECTION)
            }
            Socket.EVENT_ERROR -> {
                errorAllConnection(STATUS_ERROR_CONNECTION)
            }
            Socket.EVENT_MESSAGE -> {

            }
            Socket.EVENT_RECONNECT -> {

            }
            Socket.EVENT_RECONNECTING -> {

            }
            Socket.EVENT_RECONNECT_ATTEMPT -> {

            }
            Socket.EVENT_RECONNECT_ERROR -> {

            }
            Socket.EVENT_PING -> {

            }
            Socket.EVENT_PONG -> {

            }
        }
    }

    private fun errorAllConnection(status: Int) {
        if (bln_progress_error || array_response == null || array_response.size <= 0)
            return
        bln_progress_error = true
        for (i in 0 until array_response.size) {
            if (array_response.size <= 0 || i >= array_response.size)
                return
            val response = array_response[i]
            val result = Response()
            result.errorMessage = "Error connection $status"
            response.responseEmit.onResponseListener(status, result)
           val bln =  array_response.remove(response)
            Log.d("SocketController", "remove $bln")
        }
        bln_progress_error = false
    }

    @SuppressLint("HardwareIds")
    private fun requestConnect() {
        var jsonObjectR = JSONObject()
        if (application.context != null) {
            jsonObjectR.put("app_package_name", application.context!!.applicationInfo.packageName)
            if (Build.VERSION.SDK_INT >= 28) {
                jsonObjectR.put("app_version_code", application.context!!.packageManager.getPackageInfo(application.context!!.packageName, 0).longVersionCode)
            } else {
                jsonObjectR.put("app_version_code", application.context!!.packageManager.getPackageInfo(application.context!!.packageName, 0).versionCode)
            }

            jsonObjectR.put("android_id", Settings.Secure.getString(application.context!!.contentResolver,
                    Settings.Secure.ANDROID_ID))
            application.baseContext.getSharedPreferences(application.baseContext.packageName, Context.MODE_PRIVATE).edit().putString("request", jsonObjectR.toString()).apply()
        } else {
            jsonObjectR = JSONObject(application.baseContext.getSharedPreferences(application.baseContext.packageName, Context.MODE_PRIVATE).getString("request", "{}"))
        }
        jsonObjectR.put("version_code", BuildConfig.VERSION_CODE)
        jsonObjectR.put("last_token", "")
        jsonObjectR.put("id_last_socket", application.baseContext.getSharedPreferences(application.baseContext.packageName, Context.MODE_PRIVATE).getString("id_last_socket", socket.id()))
        application.baseContext.getSharedPreferences(application.baseContext.packageName, Context.MODE_PRIVATE).edit().putString("id_last_socket", socket.id()).apply()
        if (socket.connected()) {
            Log.w("SocketController", "request:connect ${jsonObjectR}")
            socket.emit("android:connect", jsonObjectR, Ack {
                Log.w("SocketController", "request_connect${it[0]}")
                val database = Database(application.baseContext, AddressForConnect.getTable())

                if (it[0] is JSONArray || it[0] is String) {
                    val jsonArrayMain = JSONArray(it[0].toString())
                    val jsonObject = jsonArrayMain.getJSONObject(0)
                    val jsonArray = jsonObject.getJSONArray("server")
                    if (jsonArray.length() > 0) {
                        database.deleteAll(object : DeleteResponse {
                            override fun deleted(success: Int) {
                                val array = ArrayList<HashMap<String, Any>>()
                                for (i in 0 until jsonArray.length()) {
                                    val objects = jsonArray.getJSONObject(i)
                                    array.add(AddressForConnect(objects.getString("address"), objects.getJSONArray("ports")).tableItemClass())
                                }
                                database.insertAll(array, object : InsertAllResponse {
                                    override fun insertedAll(response: ArrayList<InsertResponseItem>) {
                                        database.readAll(object : ReadResponseAll {
                                            override fun read(items: ArrayList<HashMap<String, Any>>) {
                                                Log.w("SocketController", "count servers : ${AddressForConnect.hashMapToAddressFConnect(items).size}")
                                            }
                                        })
                                    }
                                })
                            }

                        })
                    }

                }
            })
        }
    }

    fun emit(event: String, jsonObject_value: JSONObject, responseEmit: ResponseEmit?): Int {
        val newId = newID()
        var responseEmit = responseEmit
        val jsonObject = JSONObject()
        jsonObject.put("event", event)
        jsonObject.put("params", jsonObject_value)
        jsonObject.put("date_request", Date().time)

        val eventItem = EventItem(event, jsonObject.toString())
        var bln_recived = false
        Handler().postDelayed({
            if (!bln_recived) {
                val response = Response()
                response.errorMessage = "not response for this event $event . error : ${Response.STATUS_TIME_OUT_NOT_RESPONSE}"

                responseEmit!!.onResponseListener(Response.STATUS_TIME_OUT_NOT_RESPONSE, response)
                responseEmit = null
            }
        }, time_out - 100)
        Log.w("SocketController", "android:message${jsonObject.toString()}")
        val handler = Handler()
        val paramsResponse = ParamsResponse(newId, responseEmit!!, eventItem)
        array_response.add(paramsResponse)
        socket.emit("android:message", jsonObject, Ack { args ->
            handler.post {
                Log.w("SocketController", "result_$event : ${args[0]}")
                bln_recived = true
                val response = Response()
                response.any = args[0]
                if (checkIdEmit(newId) && responseEmit != null)
                    responseEmit!!.onResponseListener(Response.STATUS_SUCCESS, response)

                array_response.remove(paramsResponse)
            }
        })

        return newId
    }

    private fun newID(): Int {
        if (array_response.size <= 0)
            return 0
        return array_response[array_response.size - 1].id + 1
    }

    private fun checkIdEmit(id: Int): Boolean {
        val match = array_response.filter { it.id == id }
        return match.isNotEmpty()
    }

    fun cancelEmit(id: Int) {
        val match = array_response.filter { it.id == id }
        if (match.isNotEmpty())
            array_response.remove(match[0])
    }

    /**
     * [emitWithTryToSend] this function for emit if slow speed network and field  send
     * @return id saved in database as int
     */
    @Throws(Exception::class)
    fun emitWithTryToSend(event: String, jsonObject: JSONObject, responseEmit: ResponseEmit): Int {
        val database = Database(application.baseContext, EventItem.getTable())
        database.insert(EventItem(event, jsonObject.toString()).tableItemClass(), object : InsertResponse {
            override fun inserted(id: Long) {
                if (id >= 0) {
                    socket.emit(event, jsonObject, Ack { args ->
                        val response = Response()
                        response.any = args
                        responseEmit.onResponseListener(Response.STATUS_SUCCESS, response)
                    })
                } else {
                    throw Exception("Error Insert this emit to database $event -> $jsonObject ")
                }
            }
        })
        return 0
    }

    fun checkSent(id: Int, onCallBackCheckSendListener: OnCallBackMultiCheckSentListener) {

        val database = Database(application.baseContext, EventItem.getTable())
        database.read(id, object : ReadResponse {
            override fun read(items: HashMap<String, Any>) {
                onCallBackCheckSendListener.callBackSent(items.size <= 0)
            }
        })
    }

    fun checkMultiIdSent(ids: ArrayList<Int>, onCallBackCheckSendListener: OnCallCheckMultiSent) {
        val database = Database(application.baseContext, EventItem.getTable())
        database.readAll(object : ReadResponseAll {
            override fun read(items: ArrayList<HashMap<String, Any>>) {
                for (id in ids) {
                    for (item in EventItem.hashMapToEventItem(items)) {
                        if (item.id == id) {
                            ids.remove(id)
                        }
                    }
                }
                onCallBackCheckSendListener.onCallBack(ids)
            }
        })
    }

    fun addListenerForLastEmit(id: Int, responseEmit: ResponseEmit) {

    }

}
