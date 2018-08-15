package retrox.antfin.com.handoff.model

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import org.json.JSONObject
import retrox.antfin.com.handoff.R

class Clipboard(context: Context) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    var lastText = ""
    lateinit var listener: ClipboardManager.OnPrimaryClipChangedListener

    fun listen(textChange: (String) -> Unit) {
        listener = ClipboardManager.OnPrimaryClipChangedListener {
            val text = clipboard.primaryClip.getItemAt(0).text
            if (lastText != text) {
                lastText = text.toString()
                textChange(lastText)
            }
        }
        clipboard.addPrimaryClipChangedListener(listener)
    }

    fun unBind() = clipboard.removePrimaryClipChangedListener(listener)

}

class ClipBoardReceiver : SocketEventController {
    override val message: String
        get() = "DesktopClipBoard"

    override fun handleEvent(context: Context, args: Array<out Any>) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val jsonObject = args[0] as JSONObject
        val text = jsonObject.getString("textData")
        val currentClipText = clipboard.primaryClip.getItemAt(0).text.toString()
        if (currentClipText == text) return
        val clipData = ClipData.newPlainText(null, text)
        clipboard.primaryClip = clipData

        val notification = NotificationCompat.Builder(context,"WebPageNoti")
                .setContentTitle("ClipBoard")
                .setContentText(text)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .build()
        NotificationManagerCompat.from(context).notify(3,notification)
    }

}
