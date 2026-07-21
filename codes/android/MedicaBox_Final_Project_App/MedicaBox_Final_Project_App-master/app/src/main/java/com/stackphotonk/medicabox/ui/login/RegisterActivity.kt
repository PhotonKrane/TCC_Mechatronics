package com.stackphotonk.medicabox.ui.login

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.stackphotonk.medicabox.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.butReg.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val senha = binding.editPass.text.toString()
            val confPass = binding.editConfPass.text.toString()

            if (email == "" || email.isBlank() || senha == "" || senha.isBlank()) {
                Toast.makeText(applicationContext,"Preencha todos os campos", Toast.LENGTH_SHORT).show()
            } else {

                if (senha == confPass){
                    binding.logProgBar.visibility = View.VISIBLE
                    mAuth.createUserWithEmailAndPassword(email,senha).addOnCompleteListener { task->
                        if(task.isSuccessful) {
                            binding.logProgBar.visibility = View.INVISIBLE
                            Toast.makeText(applicationContext,"Usuário Criado",Toast.LENGTH_SHORT).show()
                            mAuth.signOut()
                            finish()
                        } else {
                            Toast.makeText(applicationContext,"Usuário não Criado", Toast.LENGTH_SHORT).show()
                            binding.logProgBar.visibility = View.INVISIBLE
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "Senhas não coincidem", Toast.LENGTH_SHORT).show()
                }
            }
        }


        binding.checkbox.setOnClickListener {
            if (binding.checkbox.isChecked()) {
                binding.editPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.editConfPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.editPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.editConfPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
    }
}

