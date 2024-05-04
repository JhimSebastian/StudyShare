package com.jhimsebastian.studyshare.Administrador

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.jhimsebastian.studyshare.MainActivity
import com.jhimsebastian.studyshare.R
import com.jhimsebastian.studyshare.databinding.ActivityLoginAdminBinding
import com.jhimsebastian.studyshare.databinding.ActivityRegistrarAdminBinding
import javax.xml.validation.Validator

class Login_Admin : AppCompatActivity() {

    private lateinit var binding : ActivityLoginAdminBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnLoginAdmin.setOnClickListener {
            ValidarInformacion()
        }

        enableEdgeToEdge()
        //setContentView(R.layout.activity_login_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }
    }

    private var email = ""
    private var password = ""
    private fun ValidarInformacion() {
        email = binding.EtEmailAdmin.text.toString().trim()
        password = binding.EtPasswordAdmin.text.toString().trim()

        if (email.isEmpty()){
            binding.EtEmailAdmin.error = "Ingrese su correo"
            binding.EtEmailAdmin.requestFocus()
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.EtEmailAdmin.error = "Correo inválido"
            binding.EtEmailAdmin.requestFocus()
        }

        else if (password.isEmpty()){
            binding.EtPasswordAdmin.error = "Ingrese la contraseña"
            binding.EtEmailAdmin.requestFocus()
        }

        else{
            LoginAdmin()
        }

    }

    private fun LoginAdmin() {
        progressDialog.setMessage("Iniciando sesión")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this@Login_Admin, MainActivity::class.java))
                finishAffinity()
            }

            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No se pudo iniciar sesión debido a ${e.message}",
                Toast.LENGTH_SHORT).show()
            }
    }
}