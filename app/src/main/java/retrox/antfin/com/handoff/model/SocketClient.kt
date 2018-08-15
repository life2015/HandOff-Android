package retrox.antfin.com.handoff.model

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

class SocketClient(val url: String, val context: Context) {
    val TAG = "Socket Client"
    val socket = IO.socket(url)
    val eventControllers = mutableListOf<SocketEventController>()
    val gson = Gson()
    var webPageHandler: (WebPageModel) -> Unit = {}

    init {
        eventControllers.add(ClipBoardReceiver())
        eventControllers.forEach { controller: SocketEventController ->
            socket.on(controller.message) {
                controller.handleEvent(context, it)
            }
        }

        socket.on(Socket.EVENT_CONNECT) {
            args -> Log.d(TAG, "Connected")
        }
        socket.on("message") { args ->
            val jsonStr = args.getOrElse(0) { return@on } as JSONObject
            val webPageBean = gson.fromJson(jsonStr.toString(), WebPageModel::class.java)
            Log.d(TAG, webPageBean.toString())
            webPageHandler.invoke(webPageBean)
        }
        socket.connect()
    }
}