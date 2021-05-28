package com.kagaconnect.streamrd.helpers

interface ClientListener {
    fun onDisconnected(client: RtspClient)
    fun onDisconnected(client: RtmpClient)
}