package com.apps.yuxco.xmasmode

import android.content.Context

/**
 * Created by ivanalejandro on 12/19/17.
 */

object ShoeDialog {

    fun shoeDialog(context: Context, title: String, message: String, type: Int) {

        android.app.AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", { dialogInterface, _ ->

                    if(type == DialogType.INFO) dialogInterface.dismiss()
                    if(type == DialogType.ERROR) System.exit(0)
                }).create().show()
    }
}

object DialogType {
    val INFO = 0
    val ERROR = 1
}