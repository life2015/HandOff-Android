package retrox.antfin.com.handoff.model

import android.content.Context

interface SocketEventController {
    val message: String
    fun handleEvent(context: Context, args: Array<out Any>)
}