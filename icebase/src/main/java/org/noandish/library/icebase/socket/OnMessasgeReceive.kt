package org.noandish.library.icebase.socket

import org.noandish.library.icebase.database.EventItem

/**
 * Created by salman on 10/15/2018 AD.
 */
interface OnMessasgeReceive {
    public fun OnMessasgeReceive(eventItem: String , message : Any)
}