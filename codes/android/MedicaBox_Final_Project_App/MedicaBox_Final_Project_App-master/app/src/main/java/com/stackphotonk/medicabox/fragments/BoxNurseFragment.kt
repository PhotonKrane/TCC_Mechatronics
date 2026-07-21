package com.stackphotonk.medicabox.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stackphotonk.medicabox.R
import com.stackphotonk.medicabox.adapter.pacients.pacientClickListener
import com.stackphotonk.medicabox.adapter.pacients.pacientListAdapter
import com.stackphotonk.medicabox.data.DBHelper
import com.stackphotonk.medicabox.model.pacientModel

class BoxNurseFragment : Fragment() {
    private lateinit var adapter : pacientListAdapter
    private lateinit var db : DBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_box_nurse, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        db = DBHelper(requireContext(), requireActivity())

        fun setList(list: ArrayList<pacientModel>){
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            adapter = pacientListAdapter(list, pacientClickListener { pacient ->
                if(pacient.uid != "") {
                    db.hasBox(pacient.uid) {
                        if(it) {
                            val fragmentDestino = BoxFragment()

                            val bundle = Bundle()
                            bundle.putString("uid", pacient.uid)

                            fragmentDestino.arguments = bundle

                            replaceFragment(fragmentDestino)
                        } else {
                            Toast.makeText(requireContext(), "Este paciente não tem uma caixa", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })

            recyclerView.adapter = adapter
        }

        db.getAllPacients { list ->
            if(list.isNotEmpty()) {
                setList(list)
            } else {
                setList(arrayListOf(pacientModel("","Sem Pacientes")))
            }
        }
        return view
    }
    private fun replaceFragment(fragment: Fragment) {
        val fragManager = requireActivity().supportFragmentManager
        val fragTransaction = fragManager.beginTransaction()
        fragTransaction.replace(R.id.frameLayout, fragment)
        fragTransaction.commit()
    }
}