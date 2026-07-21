package com.stackphotonk.medicabox.data

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.stackphotonk.medicabox.model.BeltModel
import com.stackphotonk.medicabox.model.MedicModel
import com.stackphotonk.medicabox.model.pacientModel

class DBHelper (val context: Context, val activity : Activity) {
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val rootRef: DatabaseReference = db.reference
    private val mAuth = FirebaseAuth.getInstance()
    private val currentUser = mAuth.currentUser
    private val data = FirebaseFirestore.getInstance()
    val uid = currentUser?.uid

    /*CALL REALTIME DATABASE MEDICINES*/

    fun obtainAllData(callback: (ArrayList<MedicModel>) -> Unit) {
        val myRef = db.getReference("medicines")
        val medicinesList = ArrayList<MedicModel>()

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                medicinesList.clear()
                for (snapshot in dataSnapshot.children) {
                    val medicines = snapshot.getValue(MedicModel::class.java)
                    medicines?.let { medicinesList.add(it) }
                }
                callback(medicinesList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(arrayListOf(MedicModel(0, "Não foi possível carregar remédio", "")))
            }
        })
    }

    fun getName(id: Int, callback: (String) -> Unit) {
        rootRef.child("medicines").child("$id").child("name")
            .get().addOnSuccessListener { dataSnapshot ->
                callback(dataSnapshot.value?.toString() ?: "")
            }.addOnFailureListener {
                callback("")
            }
    }

    fun getEmail(uidPass: String, callback: (String) -> Unit) {
        data.collection("users").document(uidPass).get().addOnSuccessListener {
            callback(it.getString("email") ?: "")
        }.addOnFailureListener {
            callback("")
        }
    }

    fun getBula(id: Int, callback: (String) -> Unit) {
        rootRef.child("medicines").child("$id").child("bula").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val res = snapshot.getValue(String::class.java)
                callback(res ?: "")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("errorDataBul", "O erro: $error")
            }

        })
    }

    fun getImage(id: Int, callback: (String) -> Unit) {
        rootRef.child("medicines").child("$id").child("image")
            .get().addOnSuccessListener { dataSnapshot ->
                callback(dataSnapshot.value?.toString() ?: "")
            }.addOnFailureListener {
                callback("")
            }
    }
    /*CALL REALTIME DATABASE BOXES*/

    fun isNurse(uid: String, callback: (Boolean) -> Unit) {
        data.collection("users").document(uid).get()
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    val res = task.result.getBoolean("cuidador")
                    if(res != null) {
                        callback(res)
                    } else {
                        callback(false)
                    }
                } else callback(false)
            }.addOnFailureListener { e ->
                Log.e("errorDatabase", "O erro: $e")
            }
    }

    fun isNurseforEmail(email: String, callback: (Boolean, String) -> Unit) {
        data.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result.documents
                    if (documents.isNotEmpty()) {
                        val cuidador = documents[0].getBoolean("cuidador")
                        callback(cuidador ?: false, documents[0].id)
                    } else {
                        callback(false, "") // Nenhum documento encontrado
                    }
                } else {
                    callback(false, "") // Erro na task
                }
            }
    }

    fun isPacient(uidAccount: String, callback: (Boolean) -> Unit) {
        data.collection("users").document(uidAccount).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val cuidador = task.result.getBoolean("isPacient")
                callback(cuidador ?: false)
            }
        }
    }

    fun createBox(
        medicName: String,
        uid: String,
        esteira: Int,
        intervalHour: Int,
        intervalMin: Int,
        hour: Int,
        minutes: Int,
        callback: (Boolean) -> Unit
    ) {
        data.collection("users").document(uid).collection("box").document("$esteira").set(
            mapOf(
                "belt" to esteira,
                "name" to medicName,
                "TIHour" to intervalHour,
                "TIMin" to intervalMin,
                "lastHour" to hour,
                "lastMinute" to minutes
            )
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true)
            }
        }.addOnFailureListener {
            callback(false)
        }
    }

    fun createNotify(medicName: String,
                     uidPass: String,
                     id: Int,
                     intervalHour: Int,
                     intervalMin: Int,
                     hour: Int,
                     minutes: Int,
                     callback: (Boolean) -> Unit) {
        data.collection("users").document(uidPass).collection("medicines").document("$id").set(
            mapOf(
                "name" to medicName,
                "belt" to id,
                "TIHour" to intervalHour,
                "TIMin" to intervalMin,
                "lastHour" to hour,
                "lastMinute" to minutes
            )).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                callback(true)
            } else {
                callback(false)
            }
        }
    }

    fun deleteNotify(uidPass : String, id: Int, callback: (Boolean) -> Unit) {
        data.collection("users").document(uidPass).collection("medicines").document("$id")
            .delete().addOnCompleteListener {
                if(it.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }.addOnFailureListener {
                callback(false)
            }
    }

    fun getAllMedicines(uidPass: String, callback: (ArrayList<BeltModel>) -> Unit) {
        val medicinesList = ArrayList<BeltModel>()

        data.collection("users").document(uidPass).collection("medicines")
            .get().addOnSuccessListener { snapshots ->
                if(snapshots != null) {
                    medicinesList.clear()
                    for(doc in snapshots){
                        val med = doc.toObject(BeltModel::class.java)
                        medicinesList.add(med)
                        Log.d("Identador", "${medicinesList}")
                    }
                    callback(medicinesList)
                } else {
                    callback(arrayListOf(BeltModel(0,"Sem Medicamentos",0,0,0,0)))
                    Log.d("Identador", "$medicinesList")
                }
            }.addOnFailureListener { e ->
                Log.e("errorData", e.toString())
                callback(arrayListOf(BeltModel(0, "Sem Medicamentos", 0)))
            }
    }

    fun getIdPac(uidPass: String, callback: (String) -> Unit) {
        data.collection("users").document(uid!!)
            .collection("pacients").whereEqualTo("uid", uidPass).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val res = it.result.documents.firstOrNull()
                    if (res != null) {
                        callback(res.id)
                    } else {
                        callback("")
                    }
                } else {
                    callback("")
                }
            }.addOnFailureListener { callback("") }
    }

    fun hasBox(uidPass:String,callback: (Boolean) -> Unit) {
        data.collection("users").document(uidPass).get()
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    val res = task.result.getBoolean("hasBox")
                    if(res != null) {
                        callback(res)
                    } else {
                        callback(false)
                    }
                } else callback(false)
            }.addOnFailureListener { e ->
                Log.e("errorDatabase", "O erro: $e")
            }
    }

    fun setBox(uidPass:String,callback: (Boolean) -> Unit) {
        data.collection("users").document(uidPass).update(
            mapOf("hasBox" to true)).addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
        }.addOnFailureListener { e->
            Log.e("errorDatabase", "O erro: $e")
            callback(false)
        }
    }

    fun getNurse(uidPac: String, uidNurse: String, callback: (Boolean) -> Unit) {
        data.collection("users").document(uidPac).collection("cuidador")
            .document(uidNurse).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val res = task.result.getString("uid")
                    if(res != null) {
                        callback(true)
                    } else {
                        callback(false)
                    }
                } else {
                    callback(false)
                }
            }.addOnFailureListener { callback(false) }
    }

    fun getNurses(uidPac:String, callback: (String) -> Unit) {
        val myRef = data.collection("users").document(uidPac).collection("cuidador")
        myRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val res = task.result
                if (res != null) {
                    val id = res.documents.firstOrNull()?.id
                    if(id != null) {
                        myRef.document(id).get().addOnSuccessListener {
                            val userID = it.getString("uid")
                            if (userID != null) {
                                callback(userID)
                            } else {
                                callback("")
                            }
                        }.addOnFailureListener {
                            Log.e("FirebaseError", "Erro na leitura")
                            callback("")
                        }
                    } else {
                        callback("")
                    }
                } else {
                    callback("")
                }
            } else {
                callback("")
            }
        }.addOnFailureListener {
            Log.e("FirebaseError", "Erro na leitura")
            callback("")
        }
    }

    fun getPacient(uid: String, callback: (Boolean) -> Unit) {
        data.collection("users").document(uid).collection("pacients")
            .get().addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    val res = task.result
                    if(!res.isEmpty) {
                        callback(true)
                    } else {
                        callback(false)
                    }
                } else {
                    callback(false)
                }
            }.addOnFailureListener { callback(false) }
    }

    fun deleteNurse(uid: String, uidCuid: String, callback: (Boolean) -> Unit) {
        val meRef = data.collection("users").document(uid)
        val pacientsRef = data.collection("users").document(uidCuid).collection("pacients")

        meRef.update("isPacient",false)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    meRef.collection("cuidador").document(uidCuid).delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                pacientsRef.whereEqualTo("email", currentUser!!.email)
                                    .get()
                                    .addOnSuccessListener { document ->
                                        val doc = document.documents.firstOrNull()
                                        if (doc != null) {
                                            pacientsRef.document(doc.id).delete()
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        callback(true)
                                                    } else {
                                                        callback(false)
                                                    }
                                                }.addOnFailureListener { callback(false) }
                                        }
                                    }.addOnFailureListener { callback(false) }
                            }
                        }.addOnFailureListener { callback(false) }
                }
            }.addOnFailureListener { callback(false) }
    }

    fun getAllPartCollumbyId(uidPac : String, callback: (ArrayList<BeltModel>) -> Unit) {
        val myRef = data.collection("users").document(uidPac).collection("box")
        val beltList = ArrayList<BeltModel>()

        myRef.get().addOnSuccessListener { snap ->
            if(snap != null) {
                beltList.clear()
                for (doc in snap) {
                    val belt = doc.toObject(BeltModel::class.java)
                    beltList.add(belt)
                }
                callback(beltList)
            } else {
                callback(arrayListOf(BeltModel(0, "Sem Medicamentos", 0, 0, 0, 0)))
            }
        }.addOnFailureListener { e ->
            Log.e("errorData", e.toString())
            callback(arrayListOf(BeltModel(0, "Sem Medicamentos", 0)))
        }
    }

    fun getAllPacients(callback: (ArrayList<pacientModel>) -> Unit) {
        val pacientsRef = data.collection("users").document(uid.toString()).collection("pacients")
        val pacientList = ArrayList<pacientModel>()

        pacientsRef.get().addOnSuccessListener { result ->
            for (document in result) {
                val pacient = document.toObject(pacientModel::class.java)
                pacientList.add(pacient)
            }
            callback(pacientList)
        }.addOnFailureListener { if(pacientList.isEmpty())
            callback(arrayListOf(pacientModel("",""))) }
    }

    fun deleteMedicines(belt: Int,uidPac: String, callback: (Boolean) -> Unit) {
        data.collection("users").document(uidPac).collection("box").document("$belt")
            .delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true)
                }
            }.addOnFailureListener {
                callback(false)
            }
    }

    fun updateBox(belt: Int,uidPac:String, TIHour: Int, TIMin: Int, callback: (Boolean) -> Unit) {
        data.collection("users").document(uidPac).collection("box").document("$belt")
            .update(
                mapOf(
                    "belt" to belt,
                    "TIHour" to TIHour,
                    "TIMin" to TIMin
                )
            ).addOnCompleteListener { task ->
                callback(true)
            }.addOnFailureListener {
                callback(false)
            }
    }

    fun updateNotify(uidPass : String, id: Int, intervalHour: Int, intervalMin: Int, callback: (Boolean) -> Unit) {
        data.collection("users").document(uidPass).collection("medicines").document("$id")
            .update(
                mapOf(
                    "belt" to id,
                    "TIHour" to intervalHour,
                    "TIMin" to intervalMin,
                )).addOnCompleteListener { task ->
                callback(true)
            }.addOnFailureListener {
                callback(false)
            }
    }

    fun pacient(uidCuid: String,emailCuid:String, callback: (Boolean) -> Unit) {
        val pacientsRef = data.collection("users").document(uidCuid).collection("pacients")
        val meRef = data.collection("users").document(uid.toString())

        meRef.update("isPacient", true).addOnCompleteListener {
            meRef.collection("cuidador").document(uidCuid).set(
                hashMapOf(
                    "uid" to uidCuid,
                    "email" to emailCuid
                )
            ).addOnCompleteListener {
                pacientsRef.get()
                    .addOnSuccessListener { result ->
                        // Encontra o maior número atual
                        val numeros = result.documents.mapNotNull { it.id.toIntOrNull() }
                        val proximoId = if (numeros.isEmpty()) 0 else (numeros.maxOrNull() ?: 0) + 1

                        pacientsRef.document(proximoId.toString())
                            .set(
                                hashMapOf(
                                    "email" to currentUser!!.email,
                                    "uid" to uid.toString(),
                                    "id" to proximoId
                                )
                            )
                            .addOnSuccessListener {
                                callback(true)
                            }
                            .addOnFailureListener {
                                callback(false)
                            }
                    }
                    .addOnFailureListener {
                        callback(false)
                    }
            }.addOnFailureListener { callback(false) }
        }.addOnFailureListener { callback(false) }
    }

    /*CALL FIRESTORE USERS*/
    fun setUserDataLogin(email: String, uid: String?) {
        data.collection("users").document(uid.toString()).set(
            hashMapOf(
                "email" to email,
                "name" to "",
                "cuidador" to false
            )
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Salvo", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Erro ao salvar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateUserData(
        name: String,
        email: String,
        cuidador: Boolean,
        uid: String,
        callback: (Boolean) -> Unit
    ) {
        data.collection("users").document(uid).update(
            mapOf(
                "email" to email,
                "name" to name,
                "cuidador" to cuidador
            )
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true)
            }
        }.addOnFailureListener {
            callback(false)
        }
    }

    fun deleteAccount(uid: String, callback: (Boolean) -> Unit) {
        mAuth.currentUser!!.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                data.collection("users").document(uid).delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            context,
                            "Conta deletada com sucesso",
                            Toast.LENGTH_SHORT
                        ).show()

                        callback(true)

                    } else {
                        Toast.makeText(
                            context,
                            "Erro ao deletar conta",
                            Toast.LENGTH_SHORT
                        ).show()
                        callback(false)
                    }
                }
            } else {
                Toast.makeText(
                    context,
                    "Erro ao deletar conta",
                    Toast.LENGTH_SHORT
                ).show()
                callback(false)
            }
        }
    }

    /*CALL MAUTH*/

    fun setDisplayName(name: String, callback: (Boolean) -> Unit) {
        val profileUp = userProfileChangeRequest {
            displayName = name
        }

        currentUser?.updateProfile(profileUp)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true)
            } else {
                callback(false)
            }
        }
    }
}