package com.stackphotonk.medicabox.ui.box

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.stackphotonk.medicabox.data.DBHelper
import com.stackphotonk.medicabox.databinding.ActivityMedicDetailBinding

class MedicDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMedicDetailBinding
    private lateinit var db: DBHelper
    private lateinit var medicName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicDetailBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val i = intent
        val medicID = i.extras?.getInt("id")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else{
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        db = DBHelper(this, this)


        db.getName(medicID!!.toInt()) { name ->
            binding.medicName.text = name
            medicName = name
        }

        db.getImage(medicID) { image ->
            Glide.with(applicationContext).load(image).into(binding.image)
        }

        db.getBula(medicID) { bula ->
            binding.webView.loadUrl("$bula")
        }

        binding.butOK.setOnClickListener {
            db.hasBox(db.uid!!) {
                if(it) {
                    db.isNurse(db.uid!!) { nurse ->
                        if (!nurse) {
                            val j = Intent(this, BoxOrNotifyActivity::class.java)
                            j.putExtra("id", medicID)
                            j.putExtra("uid", db.uid.toString())
                            j.putExtra("name", medicName)
                            startActivity(j)
                            finish()
                        } else {
                            val j = Intent(this, PacSelectActivity::class.java)
                            j.putExtra("id", medicID)
                            j.putExtra("name", medicName)
                            startActivity(j)
                            finish()
                        }
                    }
                } else {
                    db.isNurse(db.uid!!) {
                        if(!it) {
                            val j = Intent(this, TimeConfigActivity::class.java)
                            j.putExtra("id", medicID)
                            j.putExtra("uid", db.uid.toString())
                            j.putExtra("name", medicName)
                            startActivity(j)
                            finish()
                        } else {
                            val j = Intent(this, PacSelectActivity::class.java)
                            j.putExtra("id", medicID)
                            j.putExtra("name", medicName)
                            startActivity(j)
                            finish()
                        }
                    }
                }
            }
        }
    }
}