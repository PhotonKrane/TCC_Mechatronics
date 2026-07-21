package com.stackphotonk.medicabox.ui.nurse

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.stackphotonk.medicabox.data.DBHelper
import com.stackphotonk.medicabox.databinding.ActivityRemoveNurseBinding

class RemoveNurseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRemoveNurseBinding
    private val db = DBHelper(this,this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRemoveNurseBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.returnButton.setOnClickListener {
            finish()
        }

        binding.btnSend.setOnClickListener {
            val email = binding.confirmEmail.text.toString()

            db.isNurseforEmail(email) { isNurse, uid ->
                db.getNurse(db.uid.toString(), uid) {
                    if(it) {
                        if (isNurse) {
                            db.deleteNurse(db.uid!!, uid) { success ->
                                if (success) {
                                    Toast.makeText(
                                        this,
                                        "Cuidador deletado com sucesso",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                } else {
                                    Toast.makeText(this, "Erro ao deletar cuidador", Toast.LENGTH_SHORT)
                                        .show()
                                    finish()
                                }
                            }
                        } else {
                            Toast.makeText(this, "Erro ao deletar cuidador", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } else {
                        Toast.makeText(this, "Erro ao deletar cuidador", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}