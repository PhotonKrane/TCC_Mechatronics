package com.stackphotonk.medicabox.ui.box

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.stackphotonk.medicabox.R
import com.stackphotonk.medicabox.data.DBHelper
import com.stackphotonk.medicabox.databinding.ActivityBoxInfoBinding
import com.stackphotonk.medicabox.notifications.MyReceiver
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class BoxInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBoxInfoBinding
    private val db : DBHelper = DBHelper(this,this)

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoxInfoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.timePicker.setIs24HourView(true)

        binding.partAllCollum1.isEnabled = false
        binding.partAllCollum2.isEnabled = false
        binding.partAllCollum3.isEnabled = false
        binding.partAllCollum4.isEnabled = false
        binding.partAllCollum5.isEnabled = false

        val belt = intent.getIntExtra("belt", 0)
        val TIHour = intent.getIntExtra("TIHour", 0)
        val TIMin = intent.getIntExtra("TIMin", 0)
        val lastHour = intent.getIntExtra("lastHour", 0)
        val lastMin = intent.getIntExtra("lastMin", 0)
        val name = intent.getStringExtra("name")

        when (belt) {
            1 -> {
                binding.partAllCollum1.isChecked = true
                binding.img1.setBackgroundResource(R.drawable.img_collum_select)
            }

            2 -> {
                binding.partAllCollum2.isChecked = true
                binding.img2.setBackgroundResource(R.drawable.img_collum_select)
            }

            3 -> {
                binding.partAllCollum3.isChecked = true
                binding.img3.setBackgroundResource(R.drawable.img_collum_select)
            }

            4 -> {
                binding.partAllCollum4.isChecked = true
                binding.img4.setBackgroundResource(R.drawable.img_collum_select)
            }

            5 -> {
                binding.partAllCollum5.isChecked = true
                binding.img5.setBackgroundResource(R.drawable.img_collum_select)
            }
        }

        binding.timePicker.hour = TIHour
        binding.timePicker.minute = TIMin

        binding.butSave.setOnClickListener {
            val TIhour = binding.timePicker.hour
            val TImin = binding.timePicker.minute

            if(TIhour == 0 && TImin == 0) {
                Toast.makeText(applicationContext,"Selecione um tempo de intervalo!", Toast.LENGTH_SHORT).show()
            } else {
                db.isNurse(db.uid!!) {
                    if(!it) {
                        db.updateBox(belt,db.uid, TIhour,TImin) { success ->
                            if (success) {
                                Toast.makeText(applicationContext, "Salvo", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(applicationContext, "Erro ao salvar", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        val uid = intent.getStringExtra("uid")
                        db.updateBox(belt,uid!!, TIhour,TImin) { success ->
                            if (success) {
                                Toast.makeText(applicationContext, "Salvo", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(applicationContext, "Erro ao salvar", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            }
        }

        binding.congifNotify.setOnClickListener {
            db.isNurse(db.uid!!) {
                if(!it) {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
                    val title = "MedicaBox"
                    val resumeName = name?.split(" ")?.firstOrNull()
                    if(resumeName != null) {
                        val message = "Hora de tomar o medicamento ${resumeName}"
                        Log.d("NAME_VALUE", "Sending name: ${resumeName}")
                        configAlarm(TIHour,TIMin,lastHour,lastMin,belt,title, message, resumeName)
                    } else {
                        val message = "Hora de tomar o medicamento"
                        Log.d("NAME_VALUE", "Sending name: ${resumeName}")
                        configAlarm(TIHour,TIMin,lastHour,lastMin,belt,title, message, "")
                    }
                } else {
                    val uid = intent.getStringExtra("uid")
                    db.getEmail(uid!!) { email ->
                        if(email.isNotEmpty()) {
                            val resumeEmail = email.split("@").firstOrNull()
                            val title = "MedicaBox: $resumeEmail"
                            val resumeName = name?.split(" ")?.firstOrNull()
                            if(resumeName != null) {
                                val message = "Hora de tomar o medicamento ${resumeName}"
                                db.getIdPac(uid) {
                                    if(!it.isEmpty()) {
                                        val id = belt * 100 + (it.toInt()+1)
                                        Log.d("IDDEB", "Setting $id")
                                        configAlarm(TIHour,TIMin,lastHour,lastMin,id,title, message, resumeName)
                                    }
                                }

                            } else {
                                val title = "MedicaBox"
                                val message = "Hora de tomar o medicamento"
                                db.getIdPac(uid) {
                                    if(!it.isEmpty()) {
                                        val id = belt * 100 + (it.toInt()+1)
                                        configAlarm(TIHour,TIMin,lastHour,lastMin,id,title, message, "")
                                    }
                                }
                            }
                        } else {
                            val title = "MedicaBox"
                            val resumeName = name?.split(" ")?.firstOrNull()
                            if(resumeName != null) {
                                val message = "Hora de tomar o medicamento ${resumeName}"
                                Log.d("NAME_VALUE", "Sending name: ${resumeName}")
                                configAlarm(TIHour,TIMin,lastHour,lastMin,belt,title, message, resumeName)
                            } else {
                                val message = "Hora de tomar o medicamento"
                                Log.d("NAME_VALUE", "Sending name: ${resumeName}")
                                configAlarm(TIHour,TIMin,lastHour,lastMin,belt,title, message, "resumeName")
                            }
                        }
                    }
                }
            }
        }

        binding.cancelNotify.setOnClickListener {
            db.isNurse(db.uid!!) {
                if(!it) {
                    cancelNotify(belt)
                } else {
                    val uid = intent.getStringExtra("uid")
                    db.getIdPac(uid!!) {
                        if(!it.isEmpty()) {
                            val id = belt * 100 + (it.toInt()+1)
                            cancelNotify(id)
                        }
                    }
                }
            }

            Toast.makeText(applicationContext,"Alarme desfeito", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configAlarm(
        hour: Int,
        minute: Int,
        startHour: Int,
        startMinute: Int,
        id: Int = 0,
        title: String = "MedicaBox",
        message: String = "Hora do Remédio!",
        resumeName : String
    ) {
        Log.d("AlarmDebug", "Entrando na func")

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
        }

        val interval = (hour * 60 * 60 * 1000L) + (minute * 60 * 1000L)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, MyReceiver::class.java).apply {
            putExtra("alarm_id", id)
            putExtra("title", title)
            putExtra("message", message)
            putExtra("hasBox", true)
            putExtra("name", resumeName)
        }

        val repeatAlarmIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_IMMUTABLE)

        // Define o horário base de início (21:00 por exemplo)
        val startCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, startHour)
            set(Calendar.MINUTE, startMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val now = System.currentTimeMillis()
        var startTimeMillis = startCalendar.timeInMillis

        Log.d("AlarmDebug", "StartTime antes do cálculo: ${Date(startTimeMillis)}")
        Log.d("AlarmDebug", "Agora: ${Date(now)}")
        Log.d("AlarmDebug", "Horário inicial configurado: $startHour:$startMinute")
        Log.d("AlarmDebug", "Intervalo: ${hour}h ${minute}min = ${interval}ms")

        if(startTimeMillis > now) {
            startTimeMillis -= TimeUnit.DAYS.toMillis(1)
            Log.d("AlarmDebug", "Agr o dia ta certo porr antes do cálculo: ${Date(startTimeMillis)}")
        }

        if(startTimeMillis <= now) {
           while(startTimeMillis <= now) {
               startTimeMillis += interval
           }
       }
        Log.d("AlarmDebug", "StartTime FINAL (vai agendar para): ${Date(startTimeMillis)}")

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            startTimeMillis,
            interval,
            repeatAlarmIntent
        )

        val formatDate = SimpleDateFormat("HH:mm", Locale.getDefault())

        Toast.makeText(this, "Alarme ativado para ${formatDate.format(Date(startTimeMillis))}", Toast.LENGTH_SHORT).show()
    }

    private fun cancelNotify(id: Int) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, MyReceiver::class.java).apply {
            putExtra("alarm_id", id)
        }
        val repeatAlarmIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(repeatAlarmIntent)
    }
}