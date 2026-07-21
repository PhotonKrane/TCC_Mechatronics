package com.stackphotonk.medicabox.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stackphotonk.medicabox.R
import com.stackphotonk.medicabox.adapter.account.AccountClickListener
import com.stackphotonk.medicabox.adapter.account.AccountListAdapter
import com.stackphotonk.medicabox.adapter.medicines.MedicinesClickListener
import com.stackphotonk.medicabox.adapter.medicines.MedicinesListAdapter
import com.stackphotonk.medicabox.data.DBHelper
import com.stackphotonk.medicabox.model.BeltModel
import com.stackphotonk.medicabox.model.optionsModel
import com.stackphotonk.medicabox.notifications.MyReceiver
import com.stackphotonk.medicabox.ui.notify.NotifyInfoActivity

class NotifyFragment : Fragment() {
    private lateinit var db : DBHelper
    private lateinit var adapter: MedicinesListAdapter
    private lateinit var adapter2: AccountListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notify, container, false)
        val notView = view.findViewById<RecyclerView>(R.id.boxView)
        val optView = view.findViewById<RecyclerView>(R.id.recView)
        val butBackground = view.findViewById<View>(R.id.butBackground)

        db = DBHelper(requireContext(), requireActivity())

        backList(view)

        val options = ArrayList<optionsModel>()

        options.add(optionsModel(1, "Último Consumo: "))
        options.add(optionsModel(2, "Editar Remédio"))
        options.add(optionsModel(4, "Deletar Remédio"))

        fun setList(list: ArrayList<BeltModel>, uidPass:String){
            notView.layoutManager = LinearLayoutManager(requireContext())

            adapter = MedicinesListAdapter(list, MedicinesClickListener { medicine ->
                if(medicine.belt != 0) callList(view)

                var hours = medicine.lastHour.toString()
                if(hours == "0") {
                    hours = "00"
                }

                var minutes = medicine.lastMinute.toString()
                if(minutes == "0") {
                    minutes = "00"
                }

                options[0].info = "Último Consumo: $hours:$minutes"

                optView.layoutManager = LinearLayoutManager(requireContext())
                adapter2 = AccountListAdapter(options, AccountClickListener {
                    val id = it.id

                    when(id) {
                        2 -> {
                            val i = Intent(requireContext(), NotifyInfoActivity::class.java)
                            i.putExtra("name", medicine.name)
                            i.putExtra("uid", uidPass)
                            i.putExtra("TIHour", medicine.TIHour)
                            i.putExtra("TIMin", medicine.TIMin)
                            i.putExtra("lastHour", medicine.lastHour)
                            i.putExtra("lastMin", medicine.lastMinute)
                            i.putExtra("id",medicine.belt)
                            startActivity(i)
                        }

                        4 -> {
                            db.deleteNotify(uidPass, medicine.belt) {
                                if(it) {
                                    db.isNurse(db.uid!!) {
                                        if(!it) {
                                            val id = medicine.belt * 10000
                                            cancelNotify(id)
                                        } else {
                                            db.getIdPac(uidPass!!) {
                                                if(!it.isEmpty()) {
                                                    val id = medicine.belt * 24345 + (it.toInt()+1)
                                                    cancelNotify(id)
                                                }
                                            }
                                        }
                                    }

                                    list.remove(medicine)

                                    if (list.isEmpty()) {
                                        list.add(BeltModel(0, "Sem Medicamentos", 0, 0, 0, 0))
                                    }

                                    Toast.makeText(requireContext(), "Medicamento Deletado", Toast.LENGTH_SHORT).show()
                                    adapter.notifyDataSetChanged()
                                    backList(view)
                                }
                            }
                        }
                    }
                })
                optView.adapter = adapter2
            })

            notView.adapter = adapter
        }

        db.isNurse(db.uid!!) { nurse ->
            if(!nurse) {
                db.getAllMedicines(db.uid!!) { boxList ->
                    if(boxList.isEmpty()) {
                        boxList.add(BeltModel(0,"Sem Medicamentos",0,0,0,0))
                    }
                    setList(boxList,db.uid!!)
                }
            } else {
                val uid = arguments?.getString("uid")

                db.getAllMedicines(uid!!) { boxList ->
                    if(boxList.isEmpty()) {
                        boxList.add(BeltModel(0,"Sem Medicamentos",0,0,0,0))
                    }
                    setList(boxList,uid)
                }
            }
        }

        butBackground.setOnClickListener{
            backList(view)
        }

        return view
    }

    private fun backList(view: View) {
        val recView: RecyclerView = view.findViewById(R.id.recView)
        val butBackground: View = view.findViewById(R.id.butBackground)

        recView.animate().translationY(4000F)
        butBackground.animate().translationY(4000F)
    }

    private fun callList (view: View) {
        val recView: RecyclerView = view.findViewById(R.id.recView)
        val butBackground: View = view.findViewById(R.id.butBackground)

        recView.animate().translationY(0F).setDuration(resources.getInteger(android.R.integer.config_longAnimTime).toLong())
        butBackground.animate().translationY(0F).setDuration(resources.getInteger(android.R.integer.config_longAnimTime).toLong())

    }

    private fun cancelNotify(id: Int) {
        val alarmManager = requireContext().getSystemService(ALARM_SERVICE) as AlarmManager

        val intent = Intent(requireContext(), MyReceiver::class.java).apply {
            putExtra("alarm_id", id)
        }
        val repeatAlarmIntent = PendingIntent.getBroadcast(requireActivity(), id, intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(repeatAlarmIntent)
    }
}