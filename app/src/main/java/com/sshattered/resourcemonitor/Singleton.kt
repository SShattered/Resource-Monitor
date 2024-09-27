package com.sshattered.resourcemonitor

import android.app.Application
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class Singleton : Application() {
    private lateinit var queue: RequestQueue
    private var ipAddr: String = ""

    override fun onCreate() {
        super.onCreate()
        queue = Volley.newRequestQueue(this)
    }

    fun addToQueue(stringRequest: StringRequest){
        queue.add(stringRequest)
    }

    fun setIPAddress(ipAddress: String){
        this.ipAddr = ipAddress
    }

    fun getIPAddress() : String{
        return this.ipAddr
    }
}