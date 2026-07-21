package com.stackphotonk.medicabox.ui.box

import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.stackphotonk.medicabox.data.DBHelper
import com.stackphotonk.medicabox.databinding.ActivityConectBinding
import org.json.JSONObject
import java.net.Socket

class ConectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConectBinding
    private val db = DBHelper(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConectBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.butReturn.setOnClickListener {
            finish()
        }

        binding.butSend.setOnClickListener {
            val currentUser = db.uid
            val ip = binding.editIp.text.toString()

            if (ip.isEmpty() || ip.isBlank()) {
                Snackbar.make(binding.root, "Preencha o campo", Snackbar.LENGTH_SHORT).show()
            } else {
                val data = JSONObject().apply {
                    put("uid", currentUser)
                }
                sendDataJson(data.toString(), ip)
            }
        }
    }

    private fun sendDataJson(json: String, ip: String) {
        Thread {
            try {
                // Enviar JSON para ESP
                Socket(ip, 3333).use { socket ->
                    socket.getOutputStream().bufferedWriter().use { writer ->
                        writer.write(json)
                        writer.flush()
                    }
                }
                Log.d("APP", "JSON enviado com sucesso")

                // Reconectar WiFi
                val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
                val connectivityManager = applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

                wifiManager.disconnect()
                Thread.sleep(2000)
                wifiManager.startScan()
                Thread.sleep(3000)

                // Aguardar conexão estar ativa (máx 15s)
                var attempts = 0
                while (attempts < 15) {
                    val network = connectivityManager.activeNetwork
                    if (network != null) {
                        Log.d("APP", "WiFi reconectado")
                        break
                    }
                    Thread.sleep(1000)
                    attempts++
                }


                // Atualizar banco de dados
                db.setBox(db.uid!!) { res ->
                    runOnUiThread {
                        val messageRes = if (res) {
                            "Caixa conectada com sucesso!"
                        } else {
                            "Erro ao conectar à caixa"
                        }
                        Toast.makeText(applicationContext, messageRes, Toast.LENGTH_SHORT).show()

                        if (res) finish()
                    }
                }
            } catch (e: Exception) {
                Log.e("APP", "Erro ao enviar JSON", e)
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        "Erro na comunicação: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.start()
    }
}