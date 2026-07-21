package com.stackphotonk.medicabox.ui.box

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.stackphotonk.medicabox.R
import com.stackphotonk.medicabox.data.DBHelper
import com.stackphotonk.medicabox.databinding.ActivityBoxAddBinding

class BoxAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBoxAddBinding
    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoxAddBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        db = DBHelper(this, this)

        val j = intent
        val id = j.extras!!.getInt("id")
        val uid = j.extras!!.getString("uid")

        binding.partAllCollum1.setOnClickListener {
            val checkAll = binding.partAllCollum1

            if (checkAll.isChecked) {
                binding.img1.setBackgroundResource(R.drawable.img_collum_select)
                binding.partAllCollum2.isEnabled = false
                binding.partAllCollum3.isEnabled = false
                binding.partAllCollum4.isEnabled = false
                binding.partAllCollum5.isEnabled = false
            } else {
                binding.img1.setBackgroundResource(R.drawable.img_bcg_collum)
                binding.partAllCollum2.isEnabled = true
                binding.partAllCollum3.isEnabled = true
                binding.partAllCollum4.isEnabled = true
                binding.partAllCollum5.isEnabled = true
            }
        }

        binding.partAllCollum2.setOnClickListener {
            val checkAll = binding.partAllCollum2

            if (checkAll.isChecked) {
                binding.img2.setBackgroundResource(R.drawable.img_collum_select)
                binding.partAllCollum1.isEnabled = false
                binding.partAllCollum3.isEnabled = false
                binding.partAllCollum4.isEnabled = false
                binding.partAllCollum5.isEnabled = false
            } else {
                binding.img2.setBackgroundResource(R.drawable.img_bcg_collum)
                binding.partAllCollum1.isEnabled = true
                binding.partAllCollum3.isEnabled = true
                binding.partAllCollum4.isEnabled = true
                binding.partAllCollum5.isEnabled = true
            }
        }

        binding.partAllCollum3.setOnClickListener {
            val checkAll = binding.partAllCollum3

            if (checkAll.isChecked) {
                binding.img3.setBackgroundResource(R.drawable.img_collum_select)
                binding.partAllCollum1.isEnabled = false
                binding.partAllCollum2.isEnabled = false
                binding.partAllCollum4.isEnabled = false
                binding.partAllCollum5.isEnabled = false
            } else {
                binding.img3.setBackgroundResource(R.drawable.img_bcg_collum)
                binding.partAllCollum1.isEnabled = true
                binding.partAllCollum2.isEnabled = true
                binding.partAllCollum4.isEnabled = true
                binding.partAllCollum5.isEnabled = true
            }
        }

        binding.partAllCollum4.setOnClickListener {
            val checkAll = binding.partAllCollum4

            if (checkAll.isChecked) {
                binding.img4.setBackgroundResource(R.drawable.img_collum_select)
                binding.partAllCollum1.isEnabled = false
                binding.partAllCollum2.isEnabled = false
                binding.partAllCollum3.isEnabled = false
                binding.partAllCollum5.isEnabled = false
            } else {
                binding.img4.setBackgroundResource(R.drawable.img_bcg_collum)
                binding.partAllCollum1.isEnabled = true
                binding.partAllCollum2.isEnabled = true
                binding.partAllCollum3.isEnabled = true
                binding.partAllCollum5.isEnabled = true
            }
        }

        binding.partAllCollum5.setOnClickListener {
            val checkAll = binding.partAllCollum5

            if (checkAll.isChecked) {
                binding.img5.setBackgroundResource(R.drawable.img_collum_select)
                binding.partAllCollum1.isEnabled = false
                binding.partAllCollum2.isEnabled = false
                binding.partAllCollum3.isEnabled = false
                binding.partAllCollum4.isEnabled = false
            } else {
                binding.img5.setBackgroundResource(R.drawable.img_bcg_collum)
                binding.partAllCollum1.isEnabled = true
                binding.partAllCollum2.isEnabled = true
                binding.partAllCollum3.isEnabled = true
                binding.partAllCollum4.isEnabled = true
            }
        }

        binding.butAdd.setOnClickListener {
            db.getName(id) { medicName ->
                when {
                    binding.partAllCollum1.isChecked -> {
                        val i = Intent(this, TimeConfigActivity::class.java)
                        i.putExtra("esteira", 1)
                        i.putExtra("name", medicName)
                        i.putExtra("notify", j.getBooleanExtra("notify",false))
                        i.putExtra("uid", uid)
                        startActivity(i)
                        finish()
                    }

                    binding.partAllCollum2.isChecked -> {
                        val i = Intent(this, TimeConfigActivity::class.java)
                        i.putExtra("esteira", 2)
                        i.putExtra("notify", j.getBooleanExtra("notify",false))
                        i.putExtra("name", medicName)
                        i.putExtra("uid", uid)
                        startActivity(i)
                        finish()
                    }

                    binding.partAllCollum3.isChecked -> {
                        val i = Intent(this, TimeConfigActivity::class.java)
                        i.putExtra("esteira", 3)
                        i.putExtra("name", medicName)
                        i.putExtra("notify", j.getBooleanExtra("notify",false))
                        i.putExtra("uid", uid)
                        startActivity(i)
                        finish()
                    }

                    binding.partAllCollum4.isChecked -> {
                        val i = Intent(this, TimeConfigActivity::class.java)
                        i.putExtra("esteira", 4)
                        i.putExtra("name", medicName)
                        i.putExtra("notify", j.getBooleanExtra("notify",false))
                        i.putExtra("uid", uid)
                        startActivity(i)
                        finish()
                    }

                    binding.partAllCollum5.isChecked -> {
                        val i = Intent(this, TimeConfigActivity::class.java)
                        i.putExtra("esteira", 5)
                        i.putExtra("name", medicName)
                        i.putExtra("notify", j.getBooleanExtra("notify",false))
                        i.putExtra("uid", uid)
                        startActivity(i)
                        finish()
                    }

                    else -> {
                        Snackbar.make(binding.root, "Selecione uma opção", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }


        }
    }
}