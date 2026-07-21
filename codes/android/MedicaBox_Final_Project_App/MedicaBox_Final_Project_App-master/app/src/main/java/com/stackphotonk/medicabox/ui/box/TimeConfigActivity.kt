package com.stackphotonk.medicabox.ui.box

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.stackphotonk.medicabox.data.DBHelper
import com.stackphotonk.medicabox.databinding.ActivityTimeConfigBinding

class TimeConfigActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimeConfigBinding
    private val db = DBHelper(this, this)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeConfigBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.timePicker.setIs24HourView(true)

        val i = intent
        val belt = i.extras?.getInt("esteira") ?: 0
        val name = i.extras?.getString("name") ?: ""
        val uid = i.extras?.getString("uid") ?: ""

        binding.timePicker.hour = 0
        binding.timePicker.minute = 0

        binding.butProx.setOnClickListener {
            val intervalHour = binding.timePicker.hour
            val intervalMin = binding.timePicker.minute

            if (!(intervalHour == 0 && intervalMin == 0)) {
                db.hasBox(uid) {
                    if(it) {
                        val notify = i.getBooleanExtra("notify",false)
                        val id = intent.getIntExtra("id",0)
                        val j = Intent(this, TimePicker::class.java)
                        j.putExtra("esteira", belt)
                        j.putExtra("name", name)
                        j.putExtra("id", id)
                        j.putExtra("uid", uid)
                        j.putExtra("intervalHour", intervalHour)
                        j.putExtra("intervalMin", intervalMin)
                        j.putExtra("notify", notify)
                        startActivity(j)
                        finish()
                    } else {
                        val id = intent.getIntExtra("id",0)
                        val j = Intent(this, TimePicker::class.java)
                        j.putExtra("id", id)
                        j.putExtra("name", name)
                        j.putExtra("uid", uid)
                        j.putExtra("intervalHour", intervalHour)
                        j.putExtra("intervalMin", intervalMin)
                        startActivity(j)
                        finish()
                    }
                }
            } else {
                Toast.makeText(applicationContext,"Selecione um tempo de intervalo!",Toast.LENGTH_SHORT).show()
            }
        }
    }
}