package retrox.antfin.com.handoff

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import retrox.antfin.com.handoff.service.CoreService

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, CoreService::class.java)
        startService(intent)
        setContentView(R.layout.activity_main)
    }
}
