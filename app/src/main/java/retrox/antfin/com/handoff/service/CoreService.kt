package retrox.antfin.com.handoff.service

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import org.json.JSONObject
import retrox.antfin.com.handoff.R
import retrox.antfin.com.handoff.model.Clipboard
import retrox.antfin.com.handoff.model.SocketClient

class CoreService : Service() {
    lateinit var socketClient: SocketClient

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "WebPageNoti"
            val channelName = "WebPageSync"
            val importance = NotificationManager.IMPORTANCE_LOW

            createNotificationChannel(channelId, channelName, importance)

//            createNotificationChannel("ReyiChatMessage", "Reyi消息提醒", NotificationManager.IMPORTANCE_MAX)
//
//            createNotificationChannel("ServiceErrorMessage", "守护程序错误提示", NotificationManager.IMPORTANCE_LOW)

        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        socketClient = SocketClient("http://10.15.26.218:8081", this)
        val notificationManager = NotificationManagerCompat.from(this)
        socketClient.webPageHandler = {
            val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it.url))
            myIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val pendingIntent = PendingIntent.getActivity(this, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            val notification = NotificationCompat.Builder(this,"WebPageNoti")
                    .setContentTitle(it.title)
                    .setContentText(it.url.split("?")[0])
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .build()
            notificationManager.notify(1,notification)
        }
        val clipboardManager = Clipboard(this)
        clipboardManager.listen {
            Log.d("Clip", it);
            val jsonObject = JSONObject()
            jsonObject.put("textData", it)
            socketClient.socket.emit("MobileClipBoardText", jsonObject)
        }

        postStatusNoti("HandOff 正在运行")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
//        clipboardManager.unBind()
        socketClient.socket.disconnect()
    }

    fun postStatusNoti(message: String) {
        val notification = NotificationCompat.Builder(this, "WebPageNoti")
                .setContentTitle("HandOff")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.abc_btn_radio_material))
                .build()
        startForeground(2, notification)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
        val channel = NotificationChannel(channelId, channelName, importance)
        val notificationManager = getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}