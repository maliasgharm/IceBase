package org.noandish.library.icebase.socket

/**
 * Created by AliasgharMirzazade on 10/11/2018 AD.
 */
interface ResponseEmit {
    fun onResponseListener(status: Int,response: Response)
}