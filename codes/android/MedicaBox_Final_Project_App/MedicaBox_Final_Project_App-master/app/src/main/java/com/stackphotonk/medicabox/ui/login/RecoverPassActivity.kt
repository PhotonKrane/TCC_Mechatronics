package com.stackphotonk.medicabox.ui.login

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.stackphotonk.medicabox.databinding.ActivityRecoverPassBinding

class RecoverPassActivity : AppCompatActivity() {
    private lateinit var binding :ActivityRecoverPassBinding
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecoverPassBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.butLog.setOnClickListener {
            val email = binding.editEmail.text.toString()
            if (email.isBlank() || email == "") {
                Toast.makeText(applicationContext,"Preencha seu Email", Toast.LENGTH_SHORT).show()
            } else {
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isComplete){
                        Toast.makeText(applicationContext,"Email Enviado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext,"Falha ao enviar email", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            finish()
        }
    }
}