package com.stackphotonk.medicabox.ui.box

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.stackphotonk.medicabox.data.DBHelper
import com.stackphotonk.medicabox.databinding.ActivityTimePickerBinding

class TimePicker : AppCompatActivity() {
    private lateinit var binding: ActivityTimePickerBinding
    private val db = DBHelper(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTimePickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.timePicker.setIs24HourView(true)

        val i = intent
        val belt = i.extras?.getInt("esteira") ?: 0
        val name = i.extras?.getString("name") ?: ""
        val intervalHour = i.extras?.getInt("intervalHour") ?: 0
        val intervalMin = i.extras?.getInt("intervalMin") ?: 0
        val uid = i.extras?.getString("uid") ?: ""
        val notify = i.getBooleanExtra("notify",false)

        binding.timePicker.minute = 0

        binding.butAdd.setOnClickListener {
            val initHour = binding.timePicker.hour
            val initMinute = binding.timePicker.minute
            db.hasBox(uid) {
                if(it) {
                    if(notify) {
                        val id = intent.extras?.getInt("id")
                        db.createNotify(
                            name,
                            uid,
                            id!!,
                            intervalHour,
                            intervalMin,
                            initHour,
                            initMinute
                        ) {
                            if (it) {
                                finish()
                            } else {
                                Snackbar.make(binding.root, "Erro ao adicionar", Snackbar.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    } else {
                        db.createBox(
                            name,
                            uid,
                            belt,
                            intervalHour,
                            intervalMin,
                            initHour,
                            initMinute
                        ) {
                            if (it) {
                                finish()
                            } else {
                                Snackbar.make(binding.root, "Erro ao adicionar", Snackbar.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                } else {
                    val id = intent.extras?.getInt("id")
                    db.createNotify(
                        name,
                        uid,
                        id!!,
                        intervalHour,
                        intervalMin,
                        initHour,
                        initMinute
                    ) {
                        if (it) {
                            finish()
                        } else {
                            Snackbar.make(binding.root, "Erro ao adicionar", Snackbar.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }

    }
}