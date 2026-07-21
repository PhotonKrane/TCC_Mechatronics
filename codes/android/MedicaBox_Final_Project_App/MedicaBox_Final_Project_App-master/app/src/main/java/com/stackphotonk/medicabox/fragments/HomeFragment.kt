package com.stackphotonk.medicabox.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stackphotonk.medicabox.R
import com.stackphotonk.medicabox.adapter.pacients.pacientClickListener
import com.stackphotonk.medicabox.adapter.pacients.pacientListAdapter
import com.stackphotonk.medicabox.data.DBHelper
import com.stackphotonk.medicabox.model.pacientModel

class HomeFragment : Fragment() {
    private lateinit var adapter : pacientListAdapter
    private lateinit var db : DBHelper

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        db = DBHelper(requireContext(), requireActivity())

            fun setList(list: ArrayList<pacientModel>){
                recyclerView.layoutManager = LinearLayoutManager(requireContext())

                adapter = pacientListAdapter(list, pacientClickListener { pacient ->
                    if(pacient.uid != ""){
                        Toast.makeText(requireContext(), "UID do paciente: ${pacient.uid}", Toast.LENGTH_SHORT).show()
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
}