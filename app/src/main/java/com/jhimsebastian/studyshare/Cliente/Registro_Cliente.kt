package com.jhimsebastian.studyshare.Cliente

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
import com.google.firebase.database.FirebaseDatabase
import com.jhimsebastian.studyshare.Administrador.Login_Admin
import com.jhimsebastian.studyshare.MainActivity
import com.jhimsebastian.studyshare.MainActivityCliente
import com.jhimsebastian.studyshare.R
import com.jhimsebastian.studyshare.databinding.ActivityRegistrarAdminBinding
import com.jhimsebastian.studyshare.databinding.ActivityRegistroClienteBinding

class Registro_Cliente : AppCompatActivity() {

    private lateinit var binding : ActivityRegistroClienteBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var progressDialog : ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener {
            Toast.makeText(applicationContext, "atras", Toast.LENGTH_SHORT). show()
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnRegistrarCl.setOnClickListener {
            Toast.makeText(applicationContext, "validar", Toast.LENGTH_SHORT). show()
            ValidarInformacion()
        }

        binding.TxtTengoCuenta.setOnClickListener {
            startActivity(Intent(this@Registro_Cliente, Login_Cliente::class.java))
        }


    }

    var nombres = ""
    var edad = ""
    var email = ""
    var password = ""
    var r_password = ""
    private fun ValidarInformacion() {
        nombres = binding.EtNombresCl.text.toString().trim()
        edad = binding.EtEdadCl.text.toString().trim()
        email = binding.EtEmailCl.text.toString().trim()
        password = binding.EtPasswordCl.text.toString().trim()
        r_password = binding.EtRPasswordCl.text.toString().trim()

        if (nombres.isEmpty()){
            binding.EtNombresCl.error = "Ingrese nombres"
            binding.EtNombresCl.requestFocus()
        }

        else if (edad.isEmpty()){
            binding.EtEdadCl.error = "Ingrese edad"
            binding.EtEdadCl.requestFocus()
        }

        else if (email.isEmpty()){
            binding.EtEmailCl.error = "Ingrese email"
            binding.EtEmailCl.requestFocus()
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.EtEmailCl.error = "Email no es válido"
            binding.EtEmailCl.requestFocus()
        }

        else if (password.isEmpty()){
            binding.EtPasswordCl.error = "Ingrese la contraseña"
            binding.EtPasswordCl.requestFocus()
        }

        else if (password.length <6){
            binding.EtPasswordCl.error = "La contraseña debe tener mas de 6 caracteres"
            binding.EtPasswordCl.requestFocus()
        }

        else if (r_password.isEmpty()) {
            binding.EtRPasswordCl.error = "Repita la contraseña "
            binding.EtRPasswordCl.requestFocus()
        }

        else if (password != r_password){
            binding.EtRPasswordCl.error = "Las contraseñas no coinciden"
            binding.EtRPasswordCl.requestFocus()
        }

        else{
            CrearCuentaAdmin(email, password)
        }

    }

    private fun CrearCuentaAdmin(email: String, password: String) {

        progressDialog.setMessage("Creando cuenta")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                AgregarInfoBD()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Ha fallado la creacion de la cuenta debido a ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun AgregarInfoBD() {
        progressDialog.setMessage("Guardando Información...")
        val tiempo = System.currentTimeMillis()
        val uid = firebaseAuth.uid

        val datos_cliente : HashMap<String, Any?> = HashMap()
        datos_cliente["uid"] = uid
        datos_cliente["nombres"] = nombres
        datos_cliente["edad"] = edad
        datos_cliente["email"] = email
        datos_cliente["rol"] = "cliente"
        datos_cliente["tiempo_registro"] = tiempo
        datos_cliente["imagen"] = ""

        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uid!!)
            .setValue(datos_cliente)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Se ha creado su cuenta", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this, MainActivityCliente::class.java))
                finishAffinity()
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No se pudo guardar la información debido a ${e.message}", Toast.LENGTH_SHORT)
                    .show()

            }

    }
}