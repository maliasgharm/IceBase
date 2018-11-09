package org.noandish.library.icebase.socket

/**
 * Created by salman on 10/9/2018 AD.
 */

class Response{
    var errorMessage : String? = null
    var any : Any? = null

    companion object {
        val STATUS_SUCCESS = 0X012341
        val STATUS_ERROR_CONNECTION = 0X012342
        val STATUS_TIME_OUT_NOT_RESPONSE = 0X012344
    }
}
