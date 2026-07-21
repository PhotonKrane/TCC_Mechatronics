package com.stackphotonk.medicabox.fragments

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.stackphotonk.medicabox.R
import com.stackphotonk.medicabox.adapter.account.AccountClickListener
import com.stackphotonk.medicabox.adapter.account.AccountListAdapter
import com.stackphotonk.medicabox.adapter.belt.BeltClickListener
import com.stackphotonk.medicabox.adapter.belt.BeltListAdapter
import com.stackphotonk.medicabox.data.DBHelper
import com.stackphotonk.medicabox.model.BeltModel
import com.stackphotonk.medicabox.model.optionsModel
import com.stackphotonk.medicabox.notifications.MyReceiver
import com.stackphotonk.medicabox.ui.box.BoxInfoActivity

class BoxFragment : Fragment() {
    private lateinit var db : DBHelper
    private lateinit var adapter : BeltListAdapter
    private lateinit var adapter2: AccountListAdapter

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_box, container, false)
        val butBackground: Button = view.findViewById(R.id.butBackground)

        backList(view)

        val optionsList = ArrayList<optionsModel>()

        optionsList.add(optionsModel(0, "Andar: "))
        optionsList.add(optionsModel(1, "Último Consumo: "))
        optionsList.add(optionsModel(2, "Editar Remédio"))
        optionsList.add(optionsModel(4, "Deletar Remédio"))

        db = DBHelper(requireContext(), requireActivity())

        db.isNurse(db.uid!!) { nurse ->
            if(!nurse) {
                db.getAllPartCollumbyId(db.uid!!) { boxList ->
                    if(boxList.isEmpty()) {
                        boxList.add(BeltModel(0,"Sem Medicamentos",0,0,0,0))
                    }
                    setList(boxList,optionsList,view)
                }
            } else {
                val uid = arguments?.getString("uid")

                db.getAllPartCollumbyId(uid!!) { boxList ->
                    if(boxList.isEmpty()) {
                        boxList.add(BeltModel(0,"Sem Medicamentos",0,0,0,0))
                    }
                    setListCuid(uid,boxList,optionsList,view)
                }
            }
        }

        butBackground.setOnClickListener{
            backList(view)
        }

        return view
    }

    fun backList(view: View) {
        val recView: RecyclerView = view.findViewById(R.id.recView)
        val butBackground: View = view.findViewById(R.id.butBackground)

        recView.animate().translationY(4000F)
        butBackground.animate().translationY(4000F)
    }

    fun callList (view: View) {
        val recView: RecyclerView = view.findViewById(R.id.recView)
        val butBackground: View = view.findViewById(R.id.butBackground)

        recView.animate().translationY(0F).setDuration(resources.getInteger(android.R.integer.config_longAnimTime).toLong())
        butBackground.animate().translationY(0F).setDuration(resources.getInteger(android.R.integer.config_longAnimTime).toLong())

    }

    private fun setList(listMed:ArrayList<BeltModel>,listOpt:ArrayList<optionsModel>,view: View) {
        val boxView: RecyclerView = view.findViewById(R.id.boxView)
        val recView: RecyclerView = view.findViewById(R.id.recView)

        adapter = BeltListAdapter(listMed, BeltClickListener { belt ->
            if(belt.belt != 0) callList(view)

            var hours = belt.lastHour.toString()
            if(hours == "0") {
                hours = "00"
            }

            var minutes = belt.lastMinute.toString()
            if(minutes == "0") {
                minutes = "00"
            }

            listOpt[0].info = "Andar: ${belt.belt}"
            listOpt[1].info = "Último Consumo: $hours:$minutes"

            adapter2 = AccountListAdapter(listOpt, AccountClickListener {
                val id = it.id

                when (id) {
                    2 -> {
                        val i = Intent(requireContext(), BoxInfoActivity::class.java)
                        i.putExtra("name", belt.name)
                        i.putExtra("belt", belt.belt)
                        i.putExtra("TIHour", belt.TIHour)
                        i.putExtra("TIMin", belt.TIMin)
                        i.putExtra("lastHour",belt.lastHour)
                        i.putExtra("lastMin",belt.lastMinute)
                        startActivity(i)

                    }

                    4 -> {
                        db.deleteMedicines(belt.belt,db.uid!!) { success ->
                            if (success) {
                                cancelNotify(belt.belt)

                                listMed.remove(belt)

                                if (listMed.isEmpty()) {
                                    listMed.add(BeltModel(0, "Sem Remédios", 0))
                                }

                                Toast.makeText(requireContext(), "Medicamento Deletado", Toast.LENGTH_SHORT).show()
                                adapter.notifyDataSetChanged()
                                backList(view)
                            } else {
                                Snackbar.make(view, "Falha ao deletar remédio", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            })

            recView.layoutManager = LinearLayoutManager(requireContext())
            recView.adapter = adapter2
        })

        boxView.layoutManager = LinearLayoutManager(requireContext())
        boxView.adapter = adapter
    }

    private fun setListCuid(uid: String,listMed:ArrayList<BeltModel>,listOpt:ArrayList<optionsModel>,view: View) {
        val boxView: RecyclerView = view.findViewById(R.id.boxView)
        val recView: RecyclerView = view.findViewById(R.id.recView)

        adapter = BeltListAdapter(listMed, BeltClickListener { belt ->
            if(belt.belt != 0) callList(view)

            var hours = belt.lastHour.toString()
            if(hours == "0") {
                hours = "00"
            }

            var minutes = belt.lastMinute.toString()
            if(minutes == "0") {
                minutes = "00"
            }

            listOpt[0].info = "Andar: ${belt.belt}"
            listOpt[1].info = "Último Consumo: $hours:$minutes"

            adapter2 = AccountListAdapter(listOpt, AccountClickListener {
                val id = it.id

                when (id) {
                    2 -> {
                        val i = Intent(requireContext(), BoxInfoActivity::class.java)
                        i.putExtra("name", belt.name)
                        i.putExtra("belt", belt.belt)
                        i.putExtra("uid", uid)
                        i.putExtra("TIHour", belt.TIHour)
                        i.putExtra("TIMin", belt.TIMin)
                        i.putExtra("lastHour",belt.lastHour)
                        i.putExtra("lastMin",belt.lastMinute)
                        startActivity(i)

                    }

                    4 -> {
                        db.deleteMedicines(belt.belt,uid) { success ->
                            if (success) {
                                db.getIdPac(uid) {
                                    if(!it.isEmpty()) {
                                        val id = belt.belt * 100 + (it.toInt()+1)
                                        cancelNotify(id)
                                    }
                                }

                                listMed.remove(belt)

                                if (listMed.isEmpty()) {
                                    listMed.add(BeltModel(0, "Sem Medicamentos", 0, 0, 0, 0))
                                }

                                Snackbar.make(view, "Remédio deletado", Snackbar.LENGTH_SHORT).show()
                                adapter.notifyDataSetChanged()
                                backList(view)
                            } else {
                                Snackbar.make(view, "Falha ao deletar remédio", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            })

            recView.layoutManager = LinearLayoutManager(requireContext())
            recView.adapter = adapter2
        })

        boxView.layoutManager = LinearLayoutManager(requireContext())
        boxView.adapter = adapter
    }

    private fun cancelNotify(id: Int) {
        val alarmManager = requireContext().getSystemService(ALARM_SERVICE) as AlarmManager

        val intent = Intent(requireActivity(), MyReceiver::class.java).apply {
            putExtra("alarm_id", id)
        }
        val repeatAlarmIntent = PendingIntent.getBroadcast(requireContext(), id, intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(repeatAlarmIntent)
    }
}