package com.jhimsebastian.studyshare

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.jhimsebastian.studyshare.Fragmentos_Cliente.Fragment_cliente_cuenta
import com.jhimsebastian.studyshare.Fragmentos_Cliente.Fragment_cliente_dashboard
import com.jhimsebastian.studyshare.Fragmentos_Cliente.Fragment_cliente_favoritos
import com.jhimsebastian.studyshare.databinding.ActivityMainClienteBinding

class MainActivityCliente : AppCompatActivity() {

    private lateinit var binding : ActivityMainClienteBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        ComprobarSesion()

        verFragmentoDashboard()

        binding.BottomNavCliente.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Menu_dashboard_cl -> {
                    verFragmentoDashboard()
                    true
                }

                R.id.Menu_favoritos_cl -> {
                    verFragmentoFavoritos()
                    true
                }

                R.id.Menu_cuenta_cl -> {
                    verFragmentoCuenta()
                    true
                }

                else -> {
                    false
                }
            }
        }

    }

    private fun ComprobarSesion() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(this, Elegir_rol::class.java))
            finishAffinity()
        } else {
            Toast.makeText(applicationContext, "Bienvenido(a) ${firebaseUser.email}",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun verFragmentoDashboard(){
        val nombre_titulo = "Dashboard"
        binding.TituloRLCliente.text = nombre_titulo
        val fragment = Fragment_cliente_dashboard()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsCliente.id, fragment, "Fragment dashboard")
        fragmentTransaction.commit()
    }

    private fun verFragmentoFavoritos(){
        val nombre_titulo = "Favoritos"
        binding.TituloRLCliente.text = nombre_titulo
        val fragment = Fragment_cliente_favoritos()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsCliente.id, fragment, "Fragment favoritos")
        fragmentTransaction.commit()
    }

    private fun verFragmentoCuenta(){
        val nombre_titulo = "Cuenta"
        binding.TituloRLCliente.text = nombre_titulo
        val fragment = Fragment_cliente_cuenta()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsCliente.id, fragment, "Fragment cuenta")
        fragmentTransaction.commit()
    }

}