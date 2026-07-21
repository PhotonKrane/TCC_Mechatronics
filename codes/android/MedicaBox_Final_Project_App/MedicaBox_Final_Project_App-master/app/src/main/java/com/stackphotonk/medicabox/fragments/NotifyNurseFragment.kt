package com.stackphotonk.medicabox.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stackphotonk.medicabox.R
import com.stackphotonk.medicabox.adapter.pacients.pacientClickListener
import com.stackphotonk.medicabox.adapter.pacients.pacientListAdapter
import com.stackphotonk.medicabox.data.DBHelper
import com.stackphotonk.medicabox.model.pacientModel

class NotifyNurseFragment : Fragment() {
    private lateinit var db : DBHelper
    private lateinit var adapter : pacientListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notify_nurse, container, false)
        val pacView = view.findViewById<RecyclerView>(R.id.pacientView)

        db = DBHelper(requireContext(), requireActivity())

        fun setListPac(list:ArrayList<pacientModel>) {
            pacView.layoutManager = LinearLayoutManager(requireContext())

            adapter = pacientListAdapter(list, pacientClickListener{
                if(it.uid != "") {
                    val fragDestiny = NotifyFragment()

                    val bundle = Bundle()
                    bundle.putString("uid", it.uid)

                    fragDestiny.arguments = bundle

                    replaceFragment(fragDestiny)
                }
            })

            pacView.adapter = adapter
        }

        db.getAllPacients {
            if(it.isEmpty()) {
                it.add(pacientModel("","Sem Pacientes"))
            }
            setListPac(it)
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