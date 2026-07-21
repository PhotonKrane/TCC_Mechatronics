package com.stackphotonk.medicabox.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.stackphotonk.medicabox.R
import com.stackphotonk.medicabox.adapter.pills.PillsClickListener
import com.stackphotonk.medicabox.adapter.pills.PillsListAdapter
import com.stackphotonk.medicabox.data.DBHelper
import com.stackphotonk.medicabox.databinding.ActivityMainBinding
import com.stackphotonk.medicabox.fragments.AccountFragment
import com.stackphotonk.medicabox.fragments.BoxFragment
import com.stackphotonk.medicabox.fragments.BoxNurseFragment
import com.stackphotonk.medicabox.fragments.HomeFragment
import com.stackphotonk.medicabox.fragments.NotifyFragment
import com.stackphotonk.medicabox.fragments.NotifyNurseFragment
import com.stackphotonk.medicabox.model.MedicModel
import com.stackphotonk.medicabox.ui.box.MedicDetailActivity
import com.stackphotonk.medicabox.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: PillsListAdapter
    private var db : DBHelper = DBHelper(this, this)
    private var click = false

    override fun onStart() {
        super.onStart()
        val btNav = findViewById<BottomNavigationView>(R.id.botNav)
        val ediSearch = findViewById<EditText>(R.id.seachBarEditor)
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        backList()

        ediSearch.visibility = View.GONE

        btNav.menu.findItem(R.id.itemHome).isVisible = false
        btNav.menu.findItem(R.id.itemBox).isVisible = false

        val i = intent
        val name = i.getStringExtra("name") ?: ""
        val click = i.getBooleanExtra("byNotify", false)
        val hasBox = i.getBooleanExtra("hasBox", false)

        binding.root.post {
            if (click) {
                if (hasBox) {
                    btNav.selectedItemId = R.id.itemBox
                    Snackbar.make(
                        binding.root,
                        "Hora de tomar o medicamento: $name",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    db.isNurse(db.uid!!) { cuidador ->
                        if (cuidador) {
                            btNav.menu.findItem(R.id.itemHome).isVisible = true
                            btNav.menu.findItem(R.id.itemBox).isVisible = true
                        } else {
                            db.hasBox(db.uid!!) {
                                if (it) {
                                    btNav.menu.findItem(R.id.itemHome).isVisible = false
                                    btNav.menu.findItem(R.id.itemBox).isVisible = true
                                } else {
                                    btNav.menu.findItem(R.id.itemBox).isVisible = false
                                }
                            }
                        }
                    }
                } else {
                    btNav.selectedItemId = R.id.itemNotify
                    Snackbar.make(
                        binding.root,
                        "Hora de tomar o medicamento: $name",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    db.isNurse(db.uid!!) { cuidador ->
                        if (cuidador) {
                            btNav.menu.findItem(R.id.itemHome).isVisible = true
                            btNav.menu.findItem(R.id.itemBox).isVisible = true
                        } else {
                            db.hasBox(db.uid!!) {
                                if (it) {
                                    btNav.menu.findItem(R.id.itemHome).isVisible = false
                                    btNav.menu.findItem(R.id.itemBox).isVisible = true
                                } else {
                                    btNav.menu.findItem(R.id.itemBox).isVisible = false
                                }
                            }
                        }
                    }
                }
            } else {
                db.isNurse(db.uid!!) { cuidador ->
                    if (cuidador) {
                        btNav.selectedItemId = R.id.itemHome
                        btNav.menu.findItem(R.id.itemHome).isVisible = true
                        btNav.menu.findItem(R.id.itemBox).isVisible = true
                    } else {
                        db.hasBox(db.uid!!) {
                            if (it) {
                                btNav.selectedItemId = R.id.itemBox
                                btNav.menu.findItem(R.id.itemHome).isVisible = false
                                btNav.menu.findItem(R.id.itemBox).isVisible = true
                            } else {
                                btNav.selectedItemId = R.id.itemNotify
                                btNav.menu.findItem(R.id.itemBox).isVisible = false
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        backList()

        db = DBHelper(this, this)

        var medicList = ArrayList<MedicModel>()
        medicList.add(MedicModel(0, "Não foi possível carregar remédio", ""))

        db.obtainAllData {
            if (it.isEmpty()) {
                setList(medicList)
            } else {
                medicList = it
                setList(it)
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        binding.botNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.itemHome -> replaceFragment(HomeFragment())
                R.id.itemAccount -> replaceFragment(AccountFragment())
                R.id.itemNotify ->
                    db.isNurse(db.uid!!) {
                        if(it) {
                            replaceFragment(NotifyNurseFragment())
                        } else {
                            replaceFragment(NotifyFragment())
                        }
                    }
                R.id.itemAdd -> callList()
                R.id.itemBox -> {
                    db.isNurse(db.uid!!) { cuidador ->
                        if (cuidador) {
                            replaceFragment(BoxNurseFragment())
                        } else {
                            replaceFragment(BoxFragment())
                        }
                    }
                }
                else -> {
                    db.isNurse(db.uid!!) { cuidador ->
                        if (cuidador) {
                            binding.botNav.selectedItemId = R.id.itemHome
                        } else {
                            binding.botNav.selectedItemId = R.id.itemBox
                        }
                    }
                }
            }
            true
        }

        binding.butBackground.setOnClickListener {
            backList()
            db.isNurse(db.uid!!) { cuidador ->
                if (cuidador) {
                    binding.botNav.selectedItemId = R.id.itemHome
                } else {
                    db.hasBox(db.uid!!) {
                        if (it) {
                            binding.botNav.selectedItemId = R.id.itemBox
                            binding.botNav.menu.findItem(R.id.itemHome).isVisible = false
                            binding.botNav.menu.findItem(R.id.itemBox).isVisible = true
                        } else {
                            binding.botNav.selectedItemId = R.id.itemNotify
                            binding.botNav.menu.findItem(R.id.itemBox).isVisible = false
                        }
                    }
                }
            }
            adapter.notifyDataSetChanged()
        }

        binding.seachBarEditor.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val search = s.toString()
                if (search.isNotEmpty()) {
                    val filteredList = medicList.filter { medic ->
                        medic.name.contains(search, ignoreCase = true)
                    }

                    if (filteredList.isNotEmpty()) {
                        setList(ArrayList(filteredList))
                    } else {
                        val medicList = arrayListOf(MedicModel(0, "Nenhum remédio encontrado", ""))
                        setList(medicList)
                    }
                } else {
                    setList(ArrayList(medicList))
                }
                adapter.notifyDataSetChanged()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.seachBarVec.setOnClickListener {
            if(click) {
                binding.seachBarEditor.visibility = View.VISIBLE
                click = false
            } else {
                binding.seachBarEditor.visibility = View.GONE
                click = true
            }
        }
    }

    private fun backList() {
        binding.recView.animate().translationY(4000F)
        binding.butBackground.animate().translationY(4000F)
        binding.searchBar.animate().translationY(4000F)
        binding.seachBarEditor.setText("")
    }

    private fun callList() {
        binding.recView.animate().translationY(0F)
            .setDuration(
                resources.getInteger(android.R.integer.config_longAnimTime).toLong()
            )
        binding.butBackground.animate().translationY(0F)
            .setDuration(
                resources.getInteger(android.R.integer.config_longAnimTime).toLong()
            )
        binding.searchBar.animate().translationY(0F)
            .setDuration(
                resources.getInteger(android.R.integer.config_longAnimTime).toLong()
            )

        adapter.notifyDataSetChanged()
    }

    private fun setList(list: ArrayList<MedicModel>) {
        binding.recView.layoutManager = LinearLayoutManager(this)

        adapter = PillsListAdapter(list, PillsClickListener { medic ->
            if (medic.id != 0){
                val i = Intent(this, MedicDetailActivity::class.java)
                i.putExtra("id", medic.id)
                binding.seachBarEditor.setText("")
                startActivity(i)
            }
        })

        binding.recView.adapter = adapter
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragManager = supportFragmentManager
        val fragTransaction = fragManager.beginTransaction()
        fragTransaction.replace(R.id.frameLayout, fragment)
        fragTransaction.commit()
    }
}