package com.jhimsebastian.studyshare

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.firebase.auth.FirebaseAuth
import com.jhimsebastian.studyshare.Fragmentos_Admin.Fragment_admin_cuenta
import com.jhimsebastian.studyshare.Fragmentos_Admin.Fragment_admin_dashboard
import com.jhimsebastian.studyshare.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()  //esto no esta
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        ComprobarSesion()
        VerFragmentoDashboard()

        binding.BottomNvAdmin.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Menu_panel -> {
                    VerFragmentoDashboard()
                    true
                }

                R.id.Menu_cuenta -> {
                    VerFragmentoCuenta()
                    true
                }

                else -> {
                    false
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun VerFragmentoDashboard() {
        val nombre_titulo = "Dashboard"
        binding.TituloRLAdmin.text = nombre_titulo

        val fragment = Fragment_admin_dashboard()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsAdmin.id, fragment, "Fragment dashboard")
        fragmentTransaction.commit()
    }

    private fun VerFragmentoCuenta() {
        val nombre_titulo = "Mi cuenta"
        binding.TituloRLAdmin.text = nombre_titulo

        val fragment = Fragment_admin_cuenta()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsAdmin.id, fragment, "Fragment mi cuenta")
        fragmentTransaction.commit()
    }

    private fun ComprobarSesion() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(this, Elegir_rol::class.java))
            finishAffinity()
        } else {
            /*Toast.makeText(applicationContext, "Bienvenido(a) ${firebaseUser.email}",
                Toast.LENGTH_SHORT).show()*/
        }
    }
}
