package com.sshattered.resourcemonitor

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.util.Locale
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var repo: DashboardRepository
    private lateinit var drawerLayout: DrawerLayout
    @Volatile var status: Boolean = false
    private lateinit var dThread: Thread
    private lateinit var navSwitch: SwitchCompat;

    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        drawerLayout = findViewById(R.id.drawerLayout)
        repo = DashboardRepository(application)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        navSwitch = navigationView.menu.findItem(R.id.menuStatus).actionView as SwitchCompat
        navSwitch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                if(loadIP()){
                    status = true
                    fetchData()
                }else{
                    Toast.makeText(this, "Set an IP Address", Toast.LENGTH_SHORT).show()
                    navSwitch.toggle()
                }
            }
            else {
                status = false
            }
        }
        sp = getSharedPreferences("rMonitor", MODE_PRIVATE)
        loadIP()
    }

    private fun fetchData(){
        dThread = thread {
            while (status){
                repo.getDashboardData((application as Singleton).getIPAddress()) { result ->
                    runOnUiThread {
                        if (result != null) {
                            findViewById<CircularProgressBar>(R.id.progressCpu).progress = result.cpuUsage.toFloat()
                            findViewById<TextView>(R.id.lblCpuUsage).text = String.format(Locale.getDefault(), "%d%%", result.cpuUsage.toInt())
                            findViewById<CircularProgressBar>(R.id.progressGpu).progress = result.gpuUsage.toFloat()
                            findViewById<TextView>(R.id.lblGpuUsage).text = String.format(Locale.getDefault(),"%d%%", result.gpuUsage.toInt())

                            findViewById<TextView>(R.id.lblFreq).text = String.format(Locale.getDefault(), "%dHz", result.cpuFrequency.toInt())
                            findViewById<TextView>(R.id.lblCpuTemp).text = String.format(Locale.getDefault(), "%.1f°C", result.cpuTemp)
                            findViewById<TextView>(R.id.lblRam).text = String.format(Locale.getDefault(),"%.2fGB", result.ramUsage)
                            findViewById<TextView>(R.id.lblVRam).text = result.vRamUsage.toString()
                            findViewById<TextView>(R.id.lblGpuTemp).text = String.format(Locale.getDefault(), "%.1f°C", result.gpuTemp)
                            findViewById<TextView>(R.id.lblGpuPower).text = String.format(Locale.getDefault(), "%.1fW", result.gpuPower)
                        }
                    }
                }
                Thread.sleep(1000)
            }
        }
    }

    private fun loadIP() : Boolean{
        val ipAddr : String = sp.getString("ipaddr", "").toString()
        (application as Singleton).setIPAddress(ipAddr)
        return ipAddr.isNotEmpty()
    }

    private fun saveIP(ipAddr: String){
        sp.edit().putString("ipaddr", ipAddr).apply()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuExit -> finish()
            R.id.menuSettings -> showIPDialog()
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showIPDialog(){
        val view = layoutInflater.inflate(R.layout.ip_layout, null, false)
        val editIP = view.findViewById<EditText>(R.id.editIp)
        editIP.setText((application as Singleton).getIPAddress())
        val dialog = AlertDialog.Builder(this)
            .setView(view).create()
        dialog.show()
        view.findViewById<Button>(R.id.btnApply).setOnClickListener {
            saveIP(editIP.text.toString())
            dialog.dismiss()

            val width = (resources.displayMetrics.widthPixels * 0.10).toInt()
            val height = (resources.displayMetrics.heightPixels * 0.90).toInt()

            dialog.getWindow()?.setLayout(width, height)
        }
    }

    override fun onPause() {
        super.onPause()
        if(status)
            navSwitch.toggle()
    }

    override fun onResume() {
        super.onResume()
    }
}