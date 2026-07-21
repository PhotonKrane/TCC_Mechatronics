package com.stackphotonk.medicabox.ui.box

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.stackphotonk.medicabox.adapter.account.AccountClickListener
import com.stackphotonk.medicabox.adapter.account.AccountListAdapter
import com.stackphotonk.medicabox.databinding.ActivityBoxOrNotifyBinding
import com.stackphotonk.medicabox.model.optionsModel

class BoxOrNotifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBoxOrNotifyBinding
    private lateinit var adapter: AccountListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBoxOrNotifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val i = intent
        val medicID = i.extras?.getInt("id")
        val uid = i.extras?.getString("uid")
        Log.d("values", "${medicID}")
        Log.d("values", "${uid}")

        binding.recView.layoutManager = LinearLayoutManager(this)

        val optionsList = ArrayList<optionsModel>()

        optionsList.add(optionsModel(0, "Configurar para Caixa"))
        optionsList.add(optionsModel(1, "Configurar para Aplicativo"))

        adapter = AccountListAdapter(optionsList, AccountClickListener {
            when(it.id) {
                0 -> {
                    val j = Intent(this, BoxAddActivity::class.java)
                    j.putExtra("id", medicID)
                    j.putExtra("uid", uid)
                    j.putExtra("notify", false)
                    startActivity(j)
                    finish()
                }
                1 -> {
                    val medicName = i.extras?.getString("name")
                    Log.d("values", "${medicName}")
                    val j = Intent(this, TimeConfigActivity::class.java)
                    j.putExtra("id", medicID)
                    j.putExtra("uid", uid)
                    j.putExtra("name", medicName)
                    j.putExtra("notify", true)
                    startActivity(j)
                    finish()
                }
            }
        })

        binding.recView.adapter = adapter
    }
}