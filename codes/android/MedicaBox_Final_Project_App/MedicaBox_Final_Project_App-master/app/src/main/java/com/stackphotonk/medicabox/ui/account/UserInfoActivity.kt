package com.stackphotonk.medicabox.ui.account

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.stackphotonk.medicabox.data.DBHelper
import com.stackphotonk.medicabox.databinding.ActivityUserInfoBinding

class UserInfoActivity : AppCompatActivity() {
    private lateinit var db : DBHelper
    private lateinit var binding: ActivityUserInfoBinding
    private val mAuth = FirebaseAuth.getInstance()
    private val fire = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DBHelper(this, this)

        var cuidador = false
        var clicado = false

        fire.collection("users").document(mAuth.currentUser?.uid.toString())
            .addSnapshotListener { value, error ->
                if (value != null) {
                    var name = value.getString("name")
                    var email = value.getString("email")
                    var cuid = value.getBoolean("cuidador")

                    if (name == null) {
                        name = ""
                    } else if (email == null) {
                        email = ""
                    }

                    binding.editName.setText(name)
                    binding.editEmail.setText(email)

                    if (cuid == true) {
                        cuidador = true
                        binding.textCuidador.text = "Você é um cuidador"
                    } else if (cuid == false) {
                        cuidador = false
                        binding.textCuidador.text = "Você não é um cuidador"
                    } else if (cuid == null) {
                        cuidador = false
                        binding.textCuidador.text = "Você não é um cuidador"
                    }
                }
            }

        binding.textCuidador.setOnClickListener {
            db.getPacient(db.uid.toString()) {
                if(!it) {
                    if (cuidador) {
                        cuidador = false
                        binding.textCuidador.text = "Você não é um cuidador"
                        Toast.makeText(applicationContext,"Agora você não é um cuidador", Toast.LENGTH_SHORT).show()
                    } else {
                        cuidador = true
                        binding.textCuidador.text = "Você é um cuidador"
                        Toast.makeText(applicationContext,"Agora você é um cuidador", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext,"Você tem um paciente!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.showEye.setOnClickListener {
            if(clicado) {
                binding.textUid.text = "UID: ************"
                clicado = false
            } else {
                binding.textUid.text = "UID: ${db.uid!!}"
                clicado = true
            }
        }

        binding.butSave.setOnClickListener {
            val nome = binding.editName.text.toString()
            val mail = binding.editEmail.text.toString()
            val uid = mAuth.currentUser?.uid.toString()

            db.updateUserData(nome, mail, cuidador, uid) {
                if (it) {
                    db.setDisplayName(nome) {
                        if (it) {
                            Toast.makeText(applicationContext, "Salvo", Toast.LENGTH_SHORT).show()
                            binding.editName.isEnabled = false
                            binding.editEmail.isEnabled = false
                            binding.textCuidador.isClickable = false
                            binding.textCuidador.isFocusable = false
                            finish()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Erro ao alterar nome na sua conta",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(applicationContext,"Erro ao atualizar dados", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.butEdit.setOnClickListener {
            binding.editName.isEnabled = true
            binding.editEmail.isEnabled = true
            binding.textCuidador.isClickable = true
            binding.textCuidador.isFocusable = true
            binding.showEye.isClickable = true
            binding.showEye.isFocusable = true
        }

        binding.butBack.setOnClickListener {
            binding.editName.isEnabled = false
            binding.editEmail.isEnabled = false
            binding.textCuidador.isClickable = false
            binding.textCuidador.isFocusable = false
            binding.showEye.isClickable = false
            binding.showEye.isFocusable = false
        }

        binding.butCancel.setOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        binding.textCuidador.isClickable = false
        binding.textCuidador.isFocusable = false

        binding.editName.isEnabled = false
        binding.editEmail.isEnabled = false

        binding.showEye.isClickable = false
        binding.showEye.isFocusable = false
    }
}