package com.stackphotonk.medicabox.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.stackphotonk.medicabox.R
import com.stackphotonk.medicabox.adapter.account.AccountClickListener
import com.stackphotonk.medicabox.adapter.account.AccountListAdapter
import com.stackphotonk.medicabox.data.DBHelper
import com.stackphotonk.medicabox.model.optionsModel
import com.stackphotonk.medicabox.ui.account.NursingActivity
import com.stackphotonk.medicabox.ui.account.UserInfoActivity
import com.stackphotonk.medicabox.ui.box.ConectActivity
import com.stackphotonk.medicabox.ui.login.LoginActivity
import com.stackphotonk.medicabox.ui.nurse.RemoveNurseActivity

class AccountFragment : Fragment() {
    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var adapter : AccountListAdapter
    private lateinit var db : DBHelper
    val options = ArrayList<optionsModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.accountView)

        db = DBHelper(requireContext(), requireActivity())

        fun setList(list: ArrayList<optionsModel>){
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            adapter = AccountListAdapter(list, AccountClickListener { line ->
                val id = line.id

                when (id) {
                    1 -> {
                        startActivity(Intent(requireContext(),UserInfoActivity::class.java))
                    }
                    2 -> {
                        startActivity(Intent(requireContext(), NursingActivity::class.java))
                    }
                    3 -> {
                        startActivity(Intent(requireContext(), RemoveNurseActivity::class.java))
                    }
                    4 -> {
                        val i = Intent(requireContext(), ConectActivity::class.java)
                        startActivity(i)
                    }
                    5 -> {
                        mAuth.signOut()
                        startActivity(Intent(requireContext(), LoginActivity::class.java))
                        activity?.finish()
                    }
                    6 -> {
                        db.isNurse(db.uid!!) { nurse ->
                            if(nurse){
                                db.deleteAccount(db.uid.toString()){
                                    if (it) {
                                        mAuth.signOut()
                                        startActivity(Intent(requireContext(), LoginActivity::class.java))
                                        activity?.finish()
                                    }
                                }
                            } else {
                                db.getNurses(db.uid.toString()){ uid ->
                                    if(uid != "") {
                                        db.deleteNurse(db.uid.toString(), uid) { success ->
                                            if(success) {
                                                db.deleteAccount(db.uid.toString()){
                                                    if (it) {
                                                        mAuth.signOut()
                                                        startActivity(Intent(requireContext(), LoginActivity::class.java))
                                                        activity?.finish()
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        db.deleteAccount(db.uid.toString()){
                                            if (it) {
                                                mAuth.signOut()
                                                startActivity(Intent(requireContext(), LoginActivity::class.java))
                                                activity?.finish()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        Toast.makeText(requireContext(), "Opção não implementada", Toast.LENGTH_SHORT).show()
                    }
                }
            })

            recyclerView.adapter = adapter
        }

        db.isNurse(db.uid!!) { cuid ->
            if(!cuid) {
                db.hasBox(db.uid!!) { has ->
                    if (has) {
                        db.isPacient(db.uid!!) {pac ->
                            if(!pac) {
                                options.add(optionsModel(1, "Informações do Usuário"))
                                options.add(optionsModel(2, "Adicionar Cuidador"))
                                //options.add(optionsModel(3, "Remover Cuidador"))
                                options.add(optionsModel(4, "Reconectar a caixa"))
                                options.add(optionsModel(5, "Sair da Conta"))
                                options.add(optionsModel(6, "Deletar Conta"))

                                setList(options)
                            } else {
                                options.add(optionsModel(1, "Informações do Usuário"))
                                //options.add(optionsModel(2, "Adicionar Cuidador"))
                                options.add(optionsModel(3, "Remover Cuidador"))
                                options.add(optionsModel(4, "Reconectar a caixa"))
                                options.add(optionsModel(5, "Sair da Conta"))
                                options.add(optionsModel(6, "Deletar Conta"))

                                setList(options)
                            }
                        }
                    }  else {
                        db.isPacient(db.uid!!) {pac ->
                            if(!pac) {
                                options.add(optionsModel(1, "Informações do Usuário"))
                                options.add(optionsModel(2, "Adicionar Cuidador"))
                                //options.add(optionsModel(3, "Remover Cuidador"))
                                options.add(optionsModel(4, "Conectar a caixa"))
                                options.add(optionsModel(5, "Sair da Conta"))
                                options.add(optionsModel(6, "Deletar Conta"))

                                setList(options)
                            } else {
                                options.add(optionsModel(1, "Informações do Usuário"))
                                //options.add(optionsModel(2, "Adicionar Cuidador"))
                                options.add(optionsModel(3, "Remover Cuidador"))
                                options.add(optionsModel(4, "Conectar a caixa"))
                                options.add(optionsModel(5, "Sair da Conta"))
                                options.add(optionsModel(6, "Deletar Conta"))

                                setList(options)
                            }
                        }
                    }
                }
            } else {
                options.add(optionsModel(1, "Informações do Usuário"))
                //options.add(optionsModel(2, "Adicionar Cuidador"))
                //options.add(optionsModel(3, "Remover Cuidador"))
                options.add(optionsModel(5, "Sair da Conta"))
                options.add(optionsModel(6, "Deletar Conta"))

                setList(options)
            }
        }
        return view
    }
}