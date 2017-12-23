package com.apps.yuxco.xmasmode

import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.content_main.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.io.UnsupportedEncodingException

/**
 * Created by ivanalejandro on 12/19/17.
 */

object PahoHelper {

    private fun getMqttConnectOptions(): MqttConnectOptions {

        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isCleanSession = true
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.setWill(Constants.PUBLISH_TOPIC, Constants.DISCONNECT_MESSAGE.toByteArray(), 1, true)
        return mqttConnectOptions
    }

    fun getDisconnectedBufferOptions(): DisconnectedBufferOptions {

        val disconnectedBufferOptions = DisconnectedBufferOptions()
        disconnectedBufferOptions.isBufferEnabled = true
        disconnectedBufferOptions.bufferSize = 100
        disconnectedBufferOptions.isPersistBuffer = true
        disconnectedBufferOptions.isDeleteOldestMessages = false
        return disconnectedBufferOptions
    }

    fun getMqttClient(context: MainActivity, brokerUrl: String, clientId: String): MqttAndroidClient {

        val mqttAndroidClient = MqttAndroidClient(context, brokerUrl, clientId)

        try {
            val token = mqttAndroidClient.connect(getMqttConnectOptions())
            token.actionCallback = object : IMqttActionListener {
                override fun onSuccess(p0: IMqttToken?) {
                    mqttAndroidClient.setBufferOpts(getDisconnectedBufferOptions())
                    Toast.makeText(context, "Connected!", Toast.LENGTH_SHORT).show()
                    Log.d(Constants.LOG_TAG, "Success")

                    subscribe(context, mqttAndroidClient, Constants.PUBLISH_TOPIC, 0)
                }

                override fun onFailure(p0: IMqttToken?, p1: Throwable?) {
                    if(p1?.message != null)
                        ShoeDialog.shoeDialog(context, "Error", p1.message!!.toString(), DialogType.ERROR)
                    Log.e(Constants.LOG_TAG, "Failure ${p1?.message}")
                    p1?.printStackTrace()
                }
            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }

        return mqttAndroidClient
    }

    @Throws(MqttException::class, UnsupportedEncodingException::class)
    fun publishMessage(client: MqttAndroidClient,
                               msg: String,
                               qosa: Int,
                               topic: String) {

        val encodedPayload = msg.toByteArray()
        val message = MqttMessage(encodedPayload)
        message.id = 5866
        message.isRetained = true
        message.qos = qosa
        client.publish(topic, message)
    }

    @Throws(MqttException::class)
    fun subscribe(mainActivity: MainActivity,
                  client: MqttAndroidClient,
                  topic: String, qos: Int) {

        val token = client.subscribe(topic, qos)
        token.actionCallback = object : IMqttActionListener {

            override fun onSuccess(iMqttToken: IMqttToken) {
                Log.d(Constants.LOG_TAG, "Subscribe Successfully " + topic)
            }

            override fun onFailure(iMqttToken: IMqttToken, throwable: Throwable) {
                Log.e(Constants.LOG_TAG, "Subscribe Failed " + topic)
            }
        }

        client.setCallback(object : MqttCallback {
            override fun messageArrived(p0: String?, p1: MqttMessage?) {
                if(p1.toString() == "ON") {
                    mainActivity.swXmas.isChecked = true
                    Log.d(Constants.LOG_TAG, "Switch ON")
                }
                if(p1.toString() == "OFF") {
                    mainActivity.swXmas.isChecked = false
                    Log.d(Constants.LOG_TAG, "Switch OFF")
                }
            }

            override fun connectionLost(p0: Throwable?) {
                Toast.makeText(mainActivity, mainActivity.getString(R.string.msg_lost), Toast.LENGTH_SHORT).show()
            }

            override fun deliveryComplete(p0: IMqttDeliveryToken?) {

            }
        })
    }

    @Throws(MqttException::class)
    fun disconnect(client: MqttAndroidClient) {
        val mqttToken = client.disconnect()
        mqttToken.actionCallback = object : IMqttActionListener {
            override fun onSuccess(iMqttToken: IMqttToken) {
                Log.d(Constants.LOG_TAG, "Successfully disconnected")
            }

            override fun onFailure(iMqttToken: IMqttToken, throwable: Throwable) {
                Log.d(Constants.LOG_TAG, "Failed to disconnected " + throwable.toString())
            }
        }
    }
}