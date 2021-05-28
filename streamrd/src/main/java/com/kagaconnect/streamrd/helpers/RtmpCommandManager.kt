package com.kagaconnect.streamrd.helpers

import android.util.Base64
import android.util.Log
import com.pedro.rtmp.rtmp.CommandsManager
import java.io.BufferedReader
import java.io.IOException
import java.net.SocketException
import java.util.regex.Pattern

class RtmpCommandManager (private val serverIp: String,
                          private val serverPort: Int,
                          val clientIp: String?)
    : CommandsManager() {

    private val TAG = "RtmpCommandManager"
}