package com.stackphotonk.medicabox.ui.account

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.stackphotonk.medicabox.data.DBHelper
import com.stackphotonk.medicabox.databinding.ActivityNursingBinding

class NursingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNursingBinding
    private val db = DBHelper(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNursingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.returnButton.setOnClickListener {
            finish()
        }

        binding.btnSend.setOnClickListener {
            val email = binding.editUid.text.toString()

            db.isPacient(db.uid!!) { isPacient ->
                if (!isPacient) {
                    db.isNurseforEmail(email) { isNurse, uid ->
                        if (isNurse) {
                            if (uid != db.uid && uid != "") {
                                db.pacient(uid, email) { success ->
                                    if (success) {
                                        Toast.makeText(
                                            this,
                                            "Cuidador adicionado com sucesso",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    } else {
                                        Snackbar.make(
                                            binding.root,
                                            "Erro ao adicionar cuidador",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        } else {
                            Snackbar.make(
                                binding.root,
                                "Essa conta não é um cuidador",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Snackbar.make(
                        binding.root,
                        "Você já tem um cuidador",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}