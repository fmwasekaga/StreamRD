package com.kagaconnect.streamrd.helpers

import android.content.Context
import android.media.MediaCodec
import android.util.Log
import com.pedro.rtmp.rtmp.RtmpSender
import com.pedro.rtmp.utils.ConnectCheckerRtmp
import java.io.*
import java.net.*
import java.nio.ByteBuffer
import java.util.*

class RtmpServer(context: Context, private val connectCheckerRtmp: ConnectCheckerRtmp,
                 val port: Int): ClientListener {

    private val TAG = "RtmpServer"
    private var server: ServerSocket? = null
    val serverIp = getIPAddress(true)
    var sps: ByteBuffer? = null
    var pps: ByteBuffer? = null
    var vps: ByteBuffer? = null
    var sampleRate = 32000
    var isStereo = true
    private val clients = mutableListOf<RtmpClient>()
    private var isOnlyAudio = false
    private var thread: Thread? = null
    private var user: String? = null
    private var password: String? = null

    fun setAuth(user: String?, password: String?) {
        this.user = user
        this.password = password
    }

    fun startServer() {
        stopServer()
        thread = Thread {
            server = ServerSocket(port)
            while (!Thread.interrupted()) {
                Log.i(TAG, "Server started $serverIp:$port")
                try {
                    val client =
                        RtmpClient(server!!.accept(), serverIp, port, connectCheckerRtmp,640,
                            480, sampleRate, isStereo, isOnlyAudio, user, password,
                            this)
                    client.start()
                    clients.add(client)
                } catch (e: SocketException) {
                    Log.e(TAG, "Error", e)
                    break
                } catch (e: IOException) {
                    Log.e(TAG, e.message ?: "")
                    continue
                }
            }
            Log.i(TAG, "Server finished")
        }
        thread?.start()
    }

    fun stopServer() {
        clients.forEach { it.stopClient() }
        clients.clear()
        thread?.interrupt()
        try {
            thread?.join(100)
        } catch (e: InterruptedException) {
            thread?.interrupt()
        }
        thread = null
        if (server != null) {
            if (!server!!.isClosed) server!!.close()
        }
    }

    fun setLogs(enable: Boolean) {
        clients.forEach { it.rtmpSender.setLogs(enable) }
    }

    fun sendVideo(h264Buffer: ByteBuffer, info: MediaCodec.BufferInfo) {
        clients.forEach {
            if (it.isAlive && it.publishPermitted) {
                it.rtmpSender.sendVideoFrame(h264Buffer.duplicate(), info)
            }
        }
    }

    fun sendAudio(aacBuffer: ByteBuffer, info: MediaCodec.BufferInfo) {
        clients.forEach {
            if (it.isAlive && it.publishPermitted) {
                it.rtmpSender.sendAudioFrame(aacBuffer.duplicate(), info)
            }
        }
    }

    fun setVideoInfo(sps: ByteBuffer, pps: ByteBuffer, vps: ByteBuffer?) {
        this.sps = sps
        this.pps = pps
        this.vps = vps  //H264 has no vps so if not null assume H265
    }

    private fun getIPAddress(useIPv4: Boolean): String {
        try {
            val interfaces: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs: List<InetAddress> =
                    Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress
                        val isIPv4 = sAddr.indexOf(':') < 0
                        if (useIPv4) {
                            if (isIPv4) return sAddr
                        } else {
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                return if (delim < 0) sAddr.toUpperCase() else sAddr.substring(0, delim)
                                    .toUpperCase()
                            }
                        }
                    }
                }
            }
        } catch (ignored: java.lang.Exception) {
        } // for now eat exceptions
        return "0.0.0.0"
    }

    override fun onDisconnected(client: RtspClient) {

    }

    override fun onDisconnected(client: RtmpClient) {
        synchronized(clients) {
            client.stopClient()
            clients.remove(client)
        }
    }
}