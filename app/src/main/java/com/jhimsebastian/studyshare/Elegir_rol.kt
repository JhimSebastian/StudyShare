package com.jhimsebastian.studyshare

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.jhimsebastian.studyshare.Administrador.Registrar_Admin
import com.jhimsebastian.studyshare.Cliente.Registro_Cliente
import com.jhimsebastian.studyshare.databinding.ActivityElegirRolBinding
import com.jhimsebastian.studyshare.databinding.ActivityMainBinding

class Elegir_rol : AppCompatActivity() {


    private lateinit var binding: ActivityElegirRolBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityElegirRolBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        binding.BtnRolAdministrador.setOnClickListener {
            //Toast.makeText(applicationContext, "Rol administrador", Toast.LENGTH_SHORT). show()
            startActivity(Intent(this@Elegir_rol, Registrar_Admin::class.java))
        }

        binding.BtnRolCliente.setOnClickListener {
            startActivity(Intent(this@Elegir_rol, Registro_Cliente::class.java))
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}