package com.apps.yuxco.xmasmode

/**
 * Created by ivanalejandro on 12/19/17.
 */

object Constants {
    private val IP = "192.168.100.14"

    val LOG_TAG = "Xmas Log"
    val BROKER_URL = "tcp://$IP:1883"
    val PUBLISH_TOPIC = "xmas-mode"
    val DISCONNECT_MESSAGE = "go2sleep"
}