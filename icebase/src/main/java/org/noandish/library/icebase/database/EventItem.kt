package org.noandish.library.icebase.database

import org.noandish.library.database.Row
import org.noandish.library.database.Table

/**
 * Created by AliasgharMirzazade on 10/9/2018 AD.
 */

class EventItem(val event: String, val jsonObject: String) {

    var id: Int? = null
    fun tableItemClass(): HashMap<String, Any> {
        val hashMap = HashMap<String, Any>()
        hashMap.put(KEY_EVENT, event)
        hashMap.put(KEY_JSON_OBJECT, jsonObject)
        return hashMap
    }

    companion object {
        private val KEY_EVENT = "event"
        private val KEY_JSON_OBJECT = "jsonObject"
        private val NAME_TABLE = "EventItem"

        public fun getTable(): Table {
            val rows = ArrayList<Row>()
            rows.add(Row(KEY_EVENT, Row.TYPE_STRING))
            rows.add(Row(KEY_JSON_OBJECT, Row.TYPE_STRING))
            return Table(NAME_TABLE, rows)
        }

        public fun hashMapToEventItem(items: ArrayList<HashMap<String, Any>>): ArrayList<EventItem> {
            val listEventItem = ArrayList<EventItem>()
            for (item in items) {
                listEventItem.add(hashMapToEventItem(item))
            }
            return listEventItem
        }

        public fun hashMapToEventItem(items: HashMap<String, Any>): EventItem {
            return EventItem(items[KEY_EVENT] as String, items[KEY_JSON_OBJECT] as String)
        }
    }
}