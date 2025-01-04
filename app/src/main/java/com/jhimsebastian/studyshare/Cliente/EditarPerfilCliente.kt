package com.jhimsebastian.studyshare.Cliente

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.jhimsebastian.studyshare.R
import com.jhimsebastian.studyshare.databinding.ActivityEditarPerfilAdminBinding
import com.jhimsebastian.studyshare.databinding.ActivityEditarPerfilClienteBinding

class EditarPerfilCliente : AppCompatActivity() {

    private lateinit var binding : ActivityEditarPerfilClienteBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog : ProgressDialog

    private var imagenUri : Uri?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarPerfilClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor...")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        cargarInformacion()

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnActualizarInfo.setOnClickListener {
            validarInformacion()
        }

        binding.FABCambiarImg.setOnClickListener{
            mostrarOpciones()
        }


    }




    private var nombres = ""
    private var edad = ""
    private fun validarInformacion() {
        nombres = binding.EtCNombres.text.toString().trim()
        edad = binding.EtCEdad.text.toString().trim()

        if (nombres.isEmpty()){
            Toast.makeText(this, "Ingrese un nombre", Toast.LENGTH_SHORT).show()
        }else if (edad.isEmpty()){
            Toast.makeText(this, "Ingrese una edad", Toast.LENGTH_SHORT).show()
        }else{
            actualizarInformacion()
        }
    }

    private fun actualizarInformacion() {
        progressDialog.setMessage("Actualizando infomación")
        val hashMap : HashMap<String, Any> = HashMap()
        hashMap["nombres"] = "$nombres"
        hashMap["edad"] = "$edad"

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Se actualizó la información", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this, "No se realizo la actualización debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun cargarInformacion() {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombres = "${snapshot.child("nombres").value}"
                    val edad = "${snapshot.child("edad").value}"
                    val imagen = "${snapshot.child("imagen").value}"

                    binding.EtCNombres.setText(nombres)
                    binding.EtCEdad.setText(edad)

                    try {
                        Glide.with(this@EditarPerfilCliente)
                            .load(imagen)
                            .placeholder(R.drawable.ic_img_perfil)
                            .into(binding.imgPerfilCliente)

                    }catch (e:Exception){
                        Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun mostrarOpciones() {
        val popupMenu = PopupMenu(this, binding.imgPerfilCliente)
        popupMenu.menu.add(Menu.NONE,0,0,"Galería")
        popupMenu.menu.add(Menu.NONE,1,1,"Cámara")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener {item->
            val id = item.itemId
            if (id == 0){
                //Elegir una imagen de la galeria
                seleccionarImgGaleria()
            }else if (id == 1){
                //tomaruna una fotografia
                tomarFotografia()
            }
            true

        }
    }

    private fun tomarFotografia() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Titulo_temp")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Descripcion_temp")
        imagenUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imagenUri)
        ARLCamara.launch(intent)

    }

    private val ARLCamara = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{ resultado->
            if (resultado.resultCode == Activity.RESULT_OK){
                subirImagenStorage()
            }
            else{
                Toast.makeText(applicationContext, "Cancelado", Toast.LENGTH_SHORT).show()
            }

        }
    )


    private fun seleccionarImgGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        ARLGaleria.launch(intent)
    }

    private val ARLGaleria = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { resultado->
            if (resultado.resultCode == RESULT_OK){
                val data = resultado.data
                imagenUri = data!!.data

                //binding.imgPerfilAdmin.setImageURI(imagenUri)
                subirImagenStorage()

            }else{
                Toast.makeText(applicationContext, "Cancelado", Toast.LENGTH_SHORT).show()
            }

        }
    )

    private fun subirImagenStorage() {
        progressDialog.setMessage("Subiendo imagen a Storage")
        progressDialog.show()

        val rutaImagen = "ImagenesPerfilAdministradores/"+firebaseAuth.uid

        val ref = FirebaseStorage.getInstance().getReference(rutaImagen)
        ref.putFile(imagenUri!!)
            .addOnSuccessListener { taskSnapshot->
                val uriTask : Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val urlImagen = "${uriTask.result}"
                subirImagenDatabase(urlImagen)
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()

            }

    }

    private fun subirImagenDatabase(urlImagen : String) {
        progressDialog.setMessage("Actualizando imagen")

        val hashMap : HashMap<String, Any> = HashMap()
        if (imagenUri != null){
            hashMap["imagen"] = urlImagen
        }

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Su imagen se a actualizado", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()

            }

    }

}