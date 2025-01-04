package com.jhimsebastian.studyshare.Fragmentos_Cliente

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jhimsebastian.studyshare.Administrador.MisFunciones
import com.jhimsebastian.studyshare.Cliente.EditarPerfilCliente
import com.jhimsebastian.studyshare.Elegir_rol
import com.jhimsebastian.studyshare.R
import com.jhimsebastian.studyshare.databinding.FragmentAdminCuentaBinding
import com.jhimsebastian.studyshare.databinding.FragmentClienteCuentaBinding

class Fragment_cliente_cuenta : Fragment() {

    private lateinit var binding : FragmentClienteCuentaBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mContext : Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentClienteCuentaBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        cargarInformacion()

        binding.EditarPerfilCliente.setOnClickListener{
            startActivity(Intent(mContext, EditarPerfilCliente::class.java))
        }

        binding.CerrarSesionCliente.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(context, Elegir_rol::class.java))
            activity?.finishAffinity()
        }
    }

    private fun cargarInformacion() {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombres = "${snapshot.child("nombres").value}"
                    val email = "${snapshot.child("email").value}"
                    var t_registro = "${snapshot.child("tiempo_registro").value}"
                    val edad = "${snapshot.child("edad").value}"
                    val rol = "${snapshot.child("rol").value}"
                    val imagen = "${snapshot.child("imagen").value}"

                    if (t_registro == "null"){
                        t_registro = "0"
                    }

                    val formatoFecha = MisFunciones.formatoTiempo(t_registro.toLong())

                    binding.TxtNombresCliente.text = nombres
                    binding.TxtEmailCliente.text = email
                    binding.TxtTiempoRegistroCliente.text = formatoFecha
                    binding.TxtEdadCliente.text = edad
                    binding.TxtRolCliente.text = rol

                    try {
                        Glide.with(mContext)
                            .load(imagen)
                            .placeholder(R.drawable.ic_img_perfil)
                            .into(binding.imgPerfilCliente)

                    }catch (e:Exception){
                        Toast.makeText(mContext, "${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }


}