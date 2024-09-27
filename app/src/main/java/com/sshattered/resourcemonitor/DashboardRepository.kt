package com.sshattered.resourcemonitor

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.squareup.moshi.Moshi


class DashboardRepository(private val context: Context) {

    fun getDashboardData(ipAddress:String, callback: (result: Dashboard?) -> Unit) {
        val url = "http://$ipAddress:8989/"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                val moshi = Moshi.Builder().build()
                val dashboard = moshi.adapter(Dashboard::class.java).fromJson(response.toString())

                callback.invoke(dashboard)
            },
            { error ->
                Log.d("ResourceMonitor", error.toString())
            })

        checkNotNull(context as Singleton).addToQueue(stringRequest)
    }
}