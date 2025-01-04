package com.jhimsebastian.studyshare.Cliente

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.jhimsebastian.studyshare.MainActivityCliente
import com.jhimsebastian.studyshare.R
import com.jhimsebastian.studyshare.databinding.ActivityLoginClienteBinding

class Login_Cliente : AppCompatActivity() {

    private lateinit var binding: ActivityLoginClienteBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnLoginCliente.setOnClickListener {
            ValidarInformacion()
        }

        binding.BtnLoginGoogle.setOnClickListener {
            iniciarSesionGoogle()
        }
    }

    private fun iniciarSesionGoogle() {
        mGoogleSignInClient.signOut().addOnCompleteListener {
            val googleSignIntent = mGoogleSignInClient.signInIntent
            googleSignInARL.launch(googleSignIntent)
        }
    }

    private val googleSignInARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { resultado ->
        if (resultado.resultCode == RESULT_OK) {
            val data = resultado.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val cuenta = task.getResult(ApiException::class.java)
                autenticarGoogleFirebase(cuenta.idToken)
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(applicationContext, "Cancelado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun autenticarGoogleFirebase(idToken: String?) {
        val credencial = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credencial)
            .addOnSuccessListener { authResult ->
                if (authResult.additionalUserInfo!!.isNewUser) {
                    GuardarInformacionBD()
                } else {
                    startActivity(Intent(this, MainActivityCliente::class.java))
                    finishAffinity()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun GuardarInformacionBD() {
        progressDialog.setMessage("Se está registrando su Información")
        progressDialog.show()

        // obtener la informacionde la cuenta de google
        val uidGoogle = firebaseAuth.uid
        val emailGoogle = firebaseAuth.currentUser?.email
        val nombreGoogle = firebaseAuth.currentUser?.displayName

        //convertir a string el nombre de usuario
        val nombre_usuario_google = nombreGoogle.toString()
        //obtenemos el tiempo
        val tiempo = System.currentTimeMillis()

        val datos_cliente = HashMap<String, Any?>()
        datos_cliente["uid"] = uidGoogle
        datos_cliente["nombre"] = nombre_usuario_google
        datos_cliente["email"] = emailGoogle
        datos_cliente["edad"] = ""
        datos_cliente["tiempo"] = tiempo
        datos_cliente["imagen"] = ""
        datos_cliente["rol"] = "cliente"

        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uidGoogle!!)
            .setValue(datos_cliente)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(applicationContext, MainActivityCliente::class.java))
                Toast.makeText(applicationContext, "Se ha registrado correctamente", Toast.LENGTH_SHORT).show()
                finishAffinity()
            }
            .addOnFailureListener { e ->
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private var email = ""
    private var password = ""

    private fun ValidarInformacion() {
        email = binding.ETEmailCl.text.toString().trim()
        password = binding.ETPasswordClPasswordCl.text.toString().trim()

        if (email.isEmpty()) {
            binding.ETEmailCl.error = "Ingrese su correo"
            binding.ETEmailCl.requestFocus()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.ETEmailCl.error = "Correo inválido"
            binding.ETEmailCl.requestFocus()
        } else if (password.isEmpty()) {
            binding.ETPasswordClPasswordCl.error = "Ingrese la contraseña"
            binding.ETEmailCl.requestFocus()
        } else {
            LoginCliente()
        }
    }

    private fun LoginCliente() {
        progressDialog.setMessage("Iniciando sesión")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this@Login_Cliente, MainActivityCliente::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No se pudo iniciar sesión debido a ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }
}
