package com.jhimsebastian.studyshare

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Bienvenida : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() //no esta
        setContentView(R.layout.activity_bienvenida)
        firebaseAuth = FirebaseAuth.getInstance()
        VerBienvenida()


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun VerBienvenida(){
        object : CountDownTimer(2000, 1000){
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                ComprobarSecion()
            }

        }.start()
    }

    fun ComprobarSecion(){
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null){
            startActivity(Intent(this, Elegir_rol::class.java ))
            finishAffinity()
        }else{
            val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
            reference.child(firebaseUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val rol = snapshot.child("rol").value
                        if (rol == "admin"){
                            startActivity(Intent(this@Bienvenida, MainActivity::class.java))
                            finishAffinity()
                        }
                        else if (rol == "cliente"){
                            startActivity(Intent(this@Bienvenida, MainActivityCliente::class.java))
                            finishAffinity()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })

        }
    }

}