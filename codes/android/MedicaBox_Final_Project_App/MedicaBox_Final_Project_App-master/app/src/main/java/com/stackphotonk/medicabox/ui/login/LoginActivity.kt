package com.stackphotonk.medicabox.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.stackphotonk.medicabox.data.DBHelper
import com.stackphotonk.medicabox.databinding.ActivityLoginBinding
import com.stackphotonk.medicabox.ui.MainActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val mAuth = FirebaseAuth.getInstance()
    private val db = DBHelper(this, this)
    private val fire = FirebaseFirestore.getInstance()
    private var snap : ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = this.getSharedPreferences("valores", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        val mail = sharedPref.getString("email", "")
        val pass = sharedPref.getString("pass", "")

        binding.editPass.setText(pass)
        binding.editEmail.setText(mail)

        binding.butLog.setOnClickListener {
            val mail = binding.editEmail.text.toString()
            val senha = binding.editPass.text.toString()

            if (mail == "" || mail.isBlank() || senha == "" || senha.isBlank()) {
                Toast.makeText(applicationContext,"Preencha todos os campos", Toast.LENGTH_SHORT).show()
            } else {
                binding.logProgBar.visibility = View.VISIBLE
                mAuth.signInWithEmailAndPassword(mail,senha)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            snap = fire.collection("users").document(mAuth.currentUser?.uid.toString())
                                .addSnapshotListener { value, _ ->
                                    val aut = mAuth.currentUser

                                    val email = value?.getString("email")

                                    if (aut?.isEmailVerified == true) {
                                        editor.putString("email", "")
                                        editor.putString("pass", "")
                                        editor.apply()

                                        if (mAuth.currentUser != null) {
                                            if (email == null) {
                                                db.setUserDataLogin(mail, mAuth.currentUser?.uid)
                                                editor.putString("email", "")
                                                editor.putString("pass", "")
                                                editor.apply()

                                                startActivity(Intent(this, MainActivity::class.java))
                                                finish()
                                            } else {
                                                editor.putString("email", "")
                                                editor.putString("pass", "")
                                                editor.apply()

                                                startActivity(Intent(this, MainActivity::class.java))
                                                finish()
                                            }
                                        } else {
                                            Toast.makeText(applicationContext, "Erro ao fazer login", Toast.LENGTH_SHORT).show()
                                        }
                                    } else if (aut?.isEmailVerified == false) {
                                        editor.putString("email",mail)
                                        editor.putString("pass",senha)
                                        editor.apply()

                                        startActivity(Intent(this, AuthActivity::class.java))
                                        finish()
                                    }
                                }
                            snap

                            binding.logProgBar.visibility = View.INVISIBLE
                        } else {
                            Toast.makeText(applicationContext,"Email e/ou senha não existem", Toast.LENGTH_SHORT).show()
                            binding.logProgBar.visibility = View.INVISIBLE
                        }
                    }
            }
        }

        binding.checkbox.setOnClickListener {
            if (binding.checkbox.isChecked()) {
                binding.editPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.editPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }

        binding.textDontAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))

        }

        binding.textRecoverPass.setOnClickListener {
            startActivity(Intent(this, RecoverPassActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()

        val sharedPref = this.getSharedPreferences("valores", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("email", "")
        editor.putString("pass", "")
        editor.apply()
        snap?.remove()
        snap = null
    }

    override fun onPause() {
        super.onPause()
        snap?.remove()
        snap = null
    }

}