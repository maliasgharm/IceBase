package org.noandish.library.icebaseexample

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import org.noandish.library.icebase.socket.SocketIOApplication

class Application : SocketIOApplication() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        initialized(this)

    }
}