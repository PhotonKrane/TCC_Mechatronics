package com.stackphotonk.medicabox.ui.box

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.stackphotonk.medicabox.adapter.pacients.pacientClickListener
import com.stackphotonk.medicabox.adapter.pacients.pacientListAdapter
import com.stackphotonk.medicabox.data.DBHelper
import com.stackphotonk.medicabox.databinding.ActivityPacSelectBinding
import com.stackphotonk.medicabox.model.pacientModel

class PacSelectActivity : AppCompatActivity() {
    private lateinit var adapter : pacientListAdapter
    private lateinit var binding: ActivityPacSelectBinding
    private lateinit var db : DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPacSelectBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        db = DBHelper(this,this)
        val i = intent
        val medicID = i.extras?.getInt("id")
        val medicName = i.extras?.getString("name")
        Log.d("values", "${medicID}")
        Log.d("values", "${medicName}")

        fun setList(list: ArrayList<pacientModel>){
            binding.recyclerView.layoutManager = LinearLayoutManager(this)

            adapter = pacientListAdapter(list, pacientClickListener { pacient ->
                if(pacient.uid != "") {
                    db.hasBox(pacient.uid) {
                        if(it) {
                            val j = Intent(this, BoxOrNotifyActivity::class.java)
                            j.putExtra("id", medicID)
                            j.putExtra("uid", pacient.uid)
                            j.putExtra("name", medicName)
                            startActivity(j)
                            finish()
                        } else {
                            val j = Intent(this, TimeConfigActivity::class.java)
                            j.putExtra("id", medicID)
                            j.putExtra("uid", pacient.uid)
                            j.putExtra("name", medicName)
                            startActivity(j)
                            finish()
                        }
                    }
                } else {
                    Toast.makeText(applicationContext,"Você não tem pacientes!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            })

            binding.recyclerView.adapter = adapter
        }

        db.getAllPacients { list ->
            if(list.isNotEmpty()) {
                setList(list)
            } else {
                setList(arrayListOf(pacientModel("","Sem Pacientes")))
            }
        }

    }
}