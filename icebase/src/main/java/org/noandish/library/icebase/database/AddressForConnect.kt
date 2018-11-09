package org.noandish.library.icebase.database

import org.json.JSONArray
import org.json.JSONObject
import org.noandish.library.database.Row
import org.noandish.library.database.Table

/**
 * Created by AliasgharMirzazade on 10/9/2018 AD.
 */

class AddressForConnect(val address_server: String,val ports:JSONArray) {
    var id: Int? = null
    fun tableItemClass(): HashMap<String, Any> {
        val hashMap = HashMap<String, Any>()
        hashMap.put(KEY_ADDRESS_SERVER, address_server)
        hashMap.put(KEY_PORTS, ports)
        return hashMap
    }

    companion object {
        private val KEY_ADDRESS_SERVER = "event"
        private val KEY_PORTS = "jsonObject"

        private val NAME_TABLE = "AddressForConnect" // table name

        fun getTable(): Table {
            val rows = ArrayList<Row>()
            rows.add(Row(KEY_ADDRESS_SERVER, Row.TYPE_STRING))
            rows.add(Row(KEY_PORTS, Row.TYPE_STRING))
            return Table(NAME_TABLE, rows)
        }

        fun hashMapToAddressFConnect(items: ArrayList<HashMap<String, Any>>): ArrayList<AddressForConnect> {
            val listEventItem = ArrayList<AddressForConnect>()
            for (item in items) {
                listEventItem.add(hashMapToAddressFConnect(item))
            }
            return listEventItem
        }

        fun hashMapToAddressFConnect(items: HashMap<String, Any>): AddressForConnect {
            return AddressForConnect(items[KEY_ADDRESS_SERVER] as String, JSONArray(items[KEY_PORTS] as String) )
        }
    }
}