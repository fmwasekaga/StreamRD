package com.kagaconnect.streamrd.helpers

import android.media.MediaCodec
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.pedro.rtmp.amf.v0.AmfNumber
import com.pedro.rtmp.amf.v0.AmfObject
import com.pedro.rtmp.amf.v0.AmfString
import com.pedro.rtmp.rtmp.Handshake
import com.pedro.rtmp.rtmp.RtmpSender
import com.pedro.rtmp.rtmp.message.*
import com.pedro.rtmp.rtmp.message.command.Command
import com.pedro.rtmp.rtmp.message.control.UserControl
import com.pedro.rtmp.utils.AuthUtil
import com.pedro.rtmp.utils.ConnectCheckerRtmp
import com.pedro.rtmp.utils.CreateSSLSocket
import com.pedro.rtmp.utils.RtmpConfig
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import java.net.SocketException
import java.nio.ByteBuffer
import java.util.regex.Pattern

class RtmpClient(private val socket: Socket, serverIp: String, serverPort: Int,
                 connectCheckerRtmp: ConnectCheckerRtmp, private val width: Int,
                 private val height: Int, private val sampleRate: Int,
                 isStereo: Boolean, isOnlyAudio: Boolean, user: String?, password: String?,
                 private val listener: ClientListener) : Thread() {

    private val TAG = "RtmpClient"
    private var connectionSocket: Socket? = socket
    private var reader: BufferedInputStream? = null
    private var writer: BufferedOutputStream? = null

    private val commandsManager =
        RtmpCommandManager(
            serverIp,
            serverPort,
            socket.inetAddress.hostAddress,
        )
    val rtmpSender = RtmpSender(connectCheckerRtmp, commandsManager)

    var publishPermitted = false

    init {

        try{
            commandsManager.isOnlyAudio = isOnlyAudio
            commandsManager.isStereo = isStereo
            commandsManager.sampleRate = sampleRate
            commandsManager.setVideoResolution(width, height)
            //commandsManager.setSPSandPPS();
            commandsManager.setAuth(user, password)

            var url = "rtmp://$serverIp:$serverPort/live/stream0";
            val rtmpUrlPattern = Pattern.compile("^rtmps?://([^/:]+)(?::(\\d+))*/([^/]+)/?([^*]*)$")
            val rtmpMatcher = rtmpUrlPattern.matcher(url)
            if (rtmpMatcher.matches()) {
                //tlsEnabled = rtmpMatcher.group(0).startsWith("rtmps")
            } else {
                //connectCheckerRtmp.onConnectionFailedRtmp(
                //    "Endpoint malformed, should be: rtmp://ip:port/appname/streamname")
                //return
            }

            commandsManager.host = serverIp
            commandsManager.port = serverPort
            commandsManager.appName = getAppName(rtmpMatcher.group(3) ?: "", rtmpMatcher.group(4) ?: "")
            commandsManager.streamName = getStreamName(rtmpMatcher.group(4) ?: "")
            commandsManager.tcUrl = getTcUrl((rtmpMatcher.group(0)
                ?: "").substring(0, (rtmpMatcher.group(0)
                ?: "").length - commandsManager.streamName.length))

            establishConnection()
        }
        catch (ex:Exception){
            Log.i(TAG, "Init error ${ex.message}")
        }
    }

    override fun run() {
        super.run()
        Log.i(TAG, "New client ${commandsManager.clientIp}")

        val reader = this.reader ?: throw IOException("Invalid reader, Connection failed")
        var writer = this.writer ?: throw IOException("Invalid writer, Connection failed")

        //commandsManager.sendPublish(writer);

        while (!interrupted()) {
            try {
                //handleMessages()
            }
            catch (e: InterruptedException) {
                currentThread().interrupt()
            } catch (e: SocketException) { // Client has left
                Log.e(TAG, "Client disconnected", e)
                listener.onDisconnected(this)
                break
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error", e)
            }
        }
    }

    fun stopClient() {
        publishPermitted = false
        rtmpSender.stop()
        interrupt()
        try {
            join(100)
        } catch (e: InterruptedException) {
            interrupt()
        } finally {
            socket.close()
        }
    }

    @Throws(IOException::class)
    private fun establishConnection(): Boolean {
        val reader = BufferedInputStream(socket.getInputStream())
        val writer = BufferedOutputStream(socket.getOutputStream())
        val timestamp = System.currentTimeMillis() / 1000
        val handshake = Handshake()
        if (!handshake.sendHandshake(reader, writer)) return false
        commandsManager.timestamp = timestamp.toInt()
        commandsManager.startTs = System.nanoTime() / 1000
        connectionSocket = socket
        this.reader = reader
        this.writer = writer
        return true
    }

    private fun closeConnection() {
        connectionSocket?.close()
        commandsManager.reset()
    }

    fun sendVideo(h264Buffer: ByteBuffer, info: MediaCodec.BufferInfo) {
        rtmpSender.sendVideoFrame(h264Buffer, info)
    }

    fun sendAudio(aacBuffer: ByteBuffer, info: MediaCodec.BufferInfo) {
        rtmpSender.sendAudioFrame(aacBuffer, info)
    }

    fun hasCongestion(): Boolean {
        return rtmpSender.hasCongestion()
    }

    fun resetSentAudioFrames() {
        rtmpSender.resetSentAudioFrames()
    }

    fun resetSentVideoFrames() {
        rtmpSender.resetSentVideoFrames()
    }

    fun resetDroppedAudioFrames() {
        rtmpSender.resetDroppedAudioFrames()
    }

    fun resetDroppedVideoFrames() {
        rtmpSender.resetDroppedVideoFrames()
    }

    @Throws(RuntimeException::class)
    fun resizeCache(newSize: Int) {
        rtmpSender.resizeCache(newSize)
    }

    fun setLogs(enable: Boolean) {
        rtmpSender.setLogs(enable)
    }

    /**
     * Read all messages from server and response to it
     */
    @Throws(IOException::class)
    private fun handleMessages() {
        val reader = this.reader ?: throw IOException("Invalid reader, Connection failed")
        var writer = this.writer ?: throw IOException("Invalid writer, Connection failed")

        val message = commandsManager.readMessageResponse(reader)
        when (message.getType()) {
            MessageType.SET_CHUNK_SIZE -> {
                val setChunkSize = message as SetChunkSize
                commandsManager.readChunkSize = setChunkSize.chunkSize
                Log.i(TAG, "chunk size configured to ${setChunkSize.chunkSize}")
            }
            MessageType.ACKNOWLEDGEMENT -> {
                val acknowledgement = message as Acknowledgement
            }
            MessageType.WINDOW_ACKNOWLEDGEMENT_SIZE -> {
                val windowAcknowledgementSize = message as WindowAcknowledgementSize
                RtmpConfig.acknowledgementWindowSize = windowAcknowledgementSize.acknowledgementWindowSize
            }
            MessageType.SET_PEER_BANDWIDTH -> {
                val setPeerBandwidth = message as SetPeerBandwidth
                commandsManager.sendWindowAcknowledgementSize(writer)
            }
            MessageType.ABORT -> {
                val abort = message as Abort
            }
            MessageType.AGGREGATE -> {
                val aggregate = message as Aggregate
            }
            MessageType.USER_CONTROL -> {
                val userControl = message as UserControl
                when (val type = userControl.type) {
                    UserControl.Type.PING_REQUEST -> {
                        commandsManager.sendPong(userControl.event, writer)
                    }
                    else -> {
                        Log.i(TAG, "user control command $type ignored")
                    }
                }
            }
            MessageType.COMMAND_AMF0, MessageType.COMMAND_AMF3 -> {
                val command = message as Command
                val commandName = commandsManager.sessionHistory.getName(command.commandId)
                when (command.name) {
                    "_result" -> {
                        when (commandName) {
                            "connect" -> {
                                if (commandsManager.onAuth) {
                                    ///TODO
                                    //connectCheckerRtmp.onAuthSuccessRtmp()
                                    commandsManager.onAuth = false
                                }
                                commandsManager.createStream(writer)
                            }
                            "createStream" -> {
                                try {
                                    commandsManager.streamId = (command.data[3] as AmfNumber).value.toInt()
                                    commandsManager.sendPublish(writer)
                                } catch (e: ClassCastException) {
                                    Log.e(TAG, "error parsing _result createStream", e)
                                }
                            }
                        }
                        Log.i(TAG, "success response received from ${commandName ?: "unknown command"}")
                    }
                    "_error" -> {
                        try {
                            val description = ((command.data[3] as AmfObject).getProperty("description") as AmfString).value
                            when (commandName) {
                                "connect" -> {
                                    if (description.contains("reason=authfail") || description.contains("reason=nosuchuser")) {
                                        ///TODO
                                        //connectCheckerRtmp.onAuthErrorRtmp()
                                    } else if (commandsManager.user != null && commandsManager.password != null &&
                                        description.contains("challenge=") && description.contains("salt=") //adobe response
                                        || description.contains("nonce=")) { //llnw response
                                        closeConnection()
                                        establishConnection()
                                        writer = this.writer ?: throw IOException("Invalid writer, Connection failed")
                                        commandsManager.onAuth = true
                                        if (description.contains("challenge=") && description.contains("salt=")) { //create adobe auth
                                            val salt = AuthUtil.getSalt(description)
                                            val challenge = AuthUtil.getChallenge(description)
                                            val opaque = AuthUtil.getOpaque(description)
                                            commandsManager.sendConnect(AuthUtil.getAdobeAuthUserResult(commandsManager.user
                                                ?: "", commandsManager.password ?: "",
                                                salt, challenge, opaque), writer)
                                        } else if (description.contains("nonce=")) { //create llnw auth
                                            val nonce = AuthUtil.getNonce(description)
                                            commandsManager.sendConnect(AuthUtil.getLlnwAuthUserResult(commandsManager.user
                                                ?: "", commandsManager.password ?: "",
                                                nonce, commandsManager.appName), writer)
                                        }
                                    } else if (description.contains("code=403")) {
                                        if (description.contains("authmod=adobe")) {
                                            closeConnection()
                                            establishConnection()
                                            writer = this.writer ?: throw IOException("Invalid writer, Connection failed")
                                            Log.i(TAG, "sending auth mode adobe")
                                            commandsManager.sendConnect("?authmod=adobe&user=${commandsManager.user}", writer)
                                        } else if (description.contains("authmod=llnw")) {
                                            Log.i(TAG, "sending auth mode llnw")
                                            commandsManager.sendConnect("?authmod=llnw&user=${commandsManager.user}", writer)
                                        }
                                    } else {
                                        ///TODO
                                        //connectCheckerRtmp.onAuthErrorRtmp()
                                    }
                                }
                                else -> {
                                    ///TODO
                                    //connectCheckerRtmp.onConnectionFailedRtmp(description)
                                }
                            }
                        } catch (e: ClassCastException) {
                            Log.e(TAG, "error parsing _error command", e)
                        }
                    }
                    "onStatus" -> {
                        try {
                            when (val code = ((command.data[3] as AmfObject).getProperty("code") as AmfString).value) {
                                "NetStream.Publish.Start" -> {
                                    commandsManager.sendMetadata(writer)
                                    ///TODO
                                    //connectCheckerRtmp.onConnectionSuccessRtmp()

                                    rtmpSender.output = writer
                                    rtmpSender.start()
                                    publishPermitted = true
                                }
                                "NetConnection.Connect.Rejected", "NetStream.Publish.BadName" -> {
                                    ///TODO
                                    //connectCheckerRtmp.onConnectionFailedRtmp("onStatus: $code")
                                }
                                else -> {
                                    Log.i(TAG, "onStatus $code response received from ${commandName ?: "unknown command"}")
                                }
                            }
                        } catch (e: ClassCastException) {
                            Log.e(TAG, "error parsing onStatus command", e)
                        }
                    }
                    else -> {
                        Log.i(TAG, "unknown ${command.name} response received from ${commandName ?: "unknown command"}")
                    }
                }
            }
            MessageType.VIDEO, MessageType.AUDIO, MessageType.DATA_AMF0, MessageType.DATA_AMF3,
            MessageType.SHARED_OBJECT_AMF0, MessageType.SHARED_OBJECT_AMF3 -> {
                Log.e(TAG, "unimplemented response for ${message.getType()}. Ignored")
            }
        }
    }

    private fun getAppName(app: String, name: String): String {
        return if (!name.contains("/")) {
            app
        } else {
            app + "/" + name.substring(0, name.indexOf("/"))
        }
    }

    private fun getStreamName(name: String): String {
        return if (!name.contains("/")) {
            name
        } else {
            name.substring(name.indexOf("/") + 1)
        }
    }

    private fun getTcUrl(url: String): String {
        return if (url.endsWith("/")) {
            url.substring(0, url.length - 1)
        } else {
            url
        }
    }

}