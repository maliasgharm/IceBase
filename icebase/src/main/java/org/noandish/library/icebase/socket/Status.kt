package org.noandish.library.icebase.socket

import io.socket.client.Manager

object Status {

    /**
     * Called on a connection.
     */
    val EVENT_CONNECT = "connect"

    val EVENT_CONNECTING = "connecting"

    /**
     * Called on a disconnection.
     */
    val EVENT_DISCONNECT = "disconnect"

    /**
     * Called on a connection error.
     *
     *
     * Parameters:
     *
     *  * (Exception) error data.
     *
     */
    val EVENT_ERROR = "error"

    val EVENT_MESSAGE = "message"

    val EVENT_CONNECT_ERROR = Manager.EVENT_CONNECT_ERROR

    val EVENT_CONNECT_TIMEOUT = Manager.EVENT_CONNECT_TIMEOUT

    val EVENT_RECONNECT = Manager.EVENT_RECONNECT

    val EVENT_RECONNECT_ERROR = Manager.EVENT_RECONNECT_ERROR

    val EVENT_RECONNECT_FAILED = Manager.EVENT_RECONNECT_FAILED

    val EVENT_RECONNECT_ATTEMPT = Manager.EVENT_RECONNECT_ATTEMPT

    val EVENT_RECONNECTING = Manager.EVENT_RECONNECTING

    val EVENT_PING = Manager.EVENT_PING

    val EVENT_PONG = Manager.EVENT_PONG

}