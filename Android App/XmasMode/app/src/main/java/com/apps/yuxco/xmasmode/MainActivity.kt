package com.apps.yuxco.xmasmode

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.eclipse.paho.android.service.MqttAndroidClient
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var mqttClient: MqttAndroidClient
    lateinit var clientID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        clientID = "xmas_${UUID.randomUUID()}"
        mqttClient = PahoHelper.getMqttClient(this@MainActivity, Constants.BROKER_URL, clientID)

        swXmas.setOnCheckedChangeListener { _, b ->

            xmasOn(b)

            if(b) {
                PahoHelper.publishMessage(mqttClient, "ON", 0, Constants.PUBLISH_TOPIC)
            } else {
                PahoHelper.publishMessage(mqttClient, "OFF", 0, Constants.PUBLISH_TOPIC)
            }
        }

    }

    private fun xmasOn(b: Boolean) {
        if(b) {
            // UI Changes
            tvTitle.setTextColor(Color.WHITE)
            toolbar.setBackgroundColor(resources.getColor(R.color.colorXmas))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                llContent.background = getDrawable(R.drawable.xmas)
                window.statusBarColor = resources.getColor(R.color.colorXmas)
            }else{
                llContent.background = resources.getDrawable(R.drawable.xmas)
            }
        } else {
            // UI Changes
            llContent.background = null
            tvTitle.setTextColor(Color.DKGRAY)
            toolbar.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = resources.getColor(R.color.colorPrimary)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PahoHelper.disconnect(mqttClient)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_about -> {
                ShoeDialog.shoeDialog(this@MainActivity, "About", getString(R.string.about_msg), DialogType.INFO)
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }

        return false
    }

}
