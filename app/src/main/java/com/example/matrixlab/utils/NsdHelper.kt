package com.example.matrixlab.utils

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log

class NsdHelper(context: Context, private val onServiceResolved: (String) -> Unit) {

    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val serviceType = "_http._tcp."
    private val targetServiceName = "MatrixServer"

    private val resolveListener = object : NsdManager.ResolveListener {
        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            Log.e("NSD", "Resolve failed: $errorCode")
        }

        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
            Log.d("NSD", "Resolve Succeeded. $serviceInfo")
            val host = serviceInfo.host.hostAddress
            val port = serviceInfo.port
            val baseUrl = "http://$host:$port/"
            onServiceResolved(baseUrl)
        }
    }

    private val discoveryListener = object : NsdManager.DiscoveryListener {
        override fun onDiscoveryStarted(regType: String) {
            Log.d("NSD", "Service discovery started")
        }

        override fun onServiceFound(serviceInfo: NsdServiceInfo) {
            Log.d("NSD", "Service discovery success $serviceInfo")
            // A descoberta no Android às vezes retorna o tipo com um ponto no final ou sem.
            // O nome do serviço é o que definimos no servidor Python (MatrixServer).
            if (serviceInfo.serviceName.contains(targetServiceName, ignoreCase = true)) {
                nsdManager.resolveService(serviceInfo, resolveListener)
            }
        }

        override fun onServiceLost(serviceInfo: NsdServiceInfo) {
            Log.e("NSD", "service lost: $serviceInfo")
        }

        override fun onDiscoveryStopped(serviceType: String) {
            Log.i("NSD", "Discovery stopped: $serviceType")
        }

        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e("NSD", "Discovery failed: Error code:$errorCode")
            nsdManager.stopServiceDiscovery(this)
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e("NSD", "Discovery failed: Error code:$errorCode")
            nsdManager.stopServiceDiscovery(this)
        }
    }

    fun startDiscovery() {
        nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    fun stopDiscovery() {
        try {
            nsdManager.stopServiceDiscovery(discoveryListener)
        } catch (e: Exception) {
            Log.e("NSD", "Error stopping discovery", e)
        }
    }
}
