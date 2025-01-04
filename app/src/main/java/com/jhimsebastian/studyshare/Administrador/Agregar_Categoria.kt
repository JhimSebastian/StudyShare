package com.jhimsebastian.studyshare.Administrador

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.jhimsebastian.studyshare.MainActivity
import com.jhimsebastian.studyshare.R
import com.jhimsebastian.studyshare.databinding.ActivityAgregarCategoriaBinding

class Agregar_Categoria : AppCompatActivity() {

    private lateinit var binding : ActivityAgregarCategoriaBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog : ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarCategoriaBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.AgregarCategoriaBD.setOnClickListener {
            ValidarDatos()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private  var categoria = ""
    private fun ValidarDatos() {
        categoria = binding.EtCategoria.text.toString().trim()
        if (categoria.isEmpty()){
            Toast.makeText(applicationContext, "Ingrese una categotia", Toast.LENGTH_SHORT).show()
        }else{
            AgregarCategotiaBD()
        }
    }

    private fun AgregarCategotiaBD() {
        progressDialog.setMessage("Agregando categoria")
        progressDialog.show()

        val tiempo = System.currentTimeMillis()

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$tiempo"
        hashMap["categoria"] = categoria
        hashMap["tiempo"] = tiempo
        hashMap["UID"] = "${firebaseAuth.uid}"

        val ref = FirebaseDatabase.getInstance().getReference("Categorias")
        ref.child("$tiempo")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Se agrego categoria a la BD", Toast.LENGTH_SHORT).show()
                binding.EtCategoria.setText("")
                startActivity(Intent(this@Agregar_Categoria, MainActivity::class.java))
                finishAffinity()

            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No se agergo la categoria debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}