package com.jhimsebastian.studyshare.Cliente

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.Display.Mode
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.jhimsebastian.studyshare.Administrador.Constantes
import com.jhimsebastian.studyshare.Administrador.MisFunciones
import com.jhimsebastian.studyshare.LeerLibro
import com.jhimsebastian.studyshare.Modelos.ModeloComentario
import com.jhimsebastian.studyshare.R
import com.jhimsebastian.studyshare.databinding.ActivityDetalleLibroClienteBinding
import com.jhimsebastian.studyshare.databinding.ActivityListaPdfClienteBinding
import com.jhimsebastian.studyshare.databinding.DialogAgregarComentarioBinding
import java.io.FileOutputStream

class DetalleLibro_Cliente : AppCompatActivity() {

    private lateinit var binding : ActivityDetalleLibroClienteBinding
    private var idLibro = ""
    private var tituloLibro = ""
    private var urlLibro = ""

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog : ProgressDialog

    private var esFavorito = false

    private lateinit var comentarioArrayList: ArrayList<ModeloComentario>
    private lateinit var adaptadorComentario: AdaptadorComentario
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleLibroClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idLibro = intent.getStringExtra("idLibro")!!

        MisFunciones.incrementarVisitas(idLibro)


        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Por favor espere")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnLeerLibroC.setOnClickListener {
            val intent = Intent (this@DetalleLibro_Cliente, LeerLibro::class.java)
            intent.putExtra("idLibro", idLibro)
            startActivity(intent)
        }

        binding.BtnDescargarLibroC.setOnClickListener {
            descargarLibro()

        }

        binding.BtnFavoritosLibroCliente.setOnClickListener {
            if (esFavorito){
                MisFunciones.eliminarFavoritos(this@DetalleLibro_Cliente, idLibro)
            }else{
                agregarFavoritos()
            }
        }

        binding.IbAgergarComentario.setOnClickListener {
        DialogComentar()
        }

        comprobarFavorito()
        cargarDetalleLibro()
        listarComentarios()

    }

    private fun listarComentarios() {
        comentarioArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro).child("Comentarios")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    comentarioArrayList.clear()
                    for (ds in snapshot.children){
                        val modelo = ds.getValue(ModeloComentario::class.java)
                        comentarioArrayList.add(modelo!!)
                    }
                    adaptadorComentario = AdaptadorComentario(this@DetalleLibro_Cliente, comentarioArrayList)
                    binding.RvComentarios.adapter = adaptadorComentario

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

    }


    private var comentario = ""

    private fun DialogComentar() {
        val agregar_com_binding = DialogAgregarComentarioBinding.inflate(LayoutInflater.from(this))

        val builder = AlertDialog.Builder(this)
        builder.setView(agregar_com_binding.root)

        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)

        agregar_com_binding.IbCerrar.setOnClickListener {
            alertDialog.dismiss()

        }

        agregar_com_binding.BtnComentar.setOnClickListener {
            comentario = agregar_com_binding.EtAgregarComentario.text.toString().trim()
            if (comentario.isEmpty()){
                Toast.makeText(applicationContext, "Agergue un comnetario", Toast.LENGTH_SHORT).show()
            }else{
                alertDialog.dismiss()
                agregarComentario()
            }
        }
    }

    private fun agregarComentario() {
        progressDialog.setMessage("Agregando comentario")
        progressDialog.show()

        val tiempo = "${System.currentTimeMillis()}"

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$tiempo"
        hashMap["idLibro"] = "$idLibro"
        hashMap["tiempo"] = "$tiempo"
        hashMap["comentario"] = "$comentario"
        hashMap["uid"] = "${firebaseAuth.uid}"

        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro).child("Comentarios").child(tiempo)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Su comentario se a publicado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun comprobarFavorito() {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("Favoritos").child(idLibro)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    esFavorito = snapshot.exists()
                    if (esFavorito){
                        binding.BtnFavoritosLibroCliente.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            R.drawable.ic_agregar_favorito,
                            0,
                            0
                        )
                        binding.BtnFavoritosLibroCliente.text = "Eliminar de favoritos"
                    }else{
                        binding.BtnFavoritosLibroCliente.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            R.drawable.ic_no_favorito,
                            0,
                            0
                        )
                        binding.BtnFavoritosLibroCliente.text = "Agregar a favoritos"
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun agregarFavoritos(){
        val tiempo = System.currentTimeMillis()

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = idLibro
        hashMap["tiempo"] = tiempo

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("Favoritos").child(idLibro)
            .setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "Libro añadido a favoritos", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {e->
                Toast.makeText(applicationContext, "No se agrego a favoritos debido a ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }

    private fun descargarLibro() {
        progressDialog.setMessage("Descargando Libro")
        progressDialog.show()

        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(urlLibro)
        storageReference.getBytes(Constantes.Maximo_bytes_pdf)
            .addOnSuccessListener {bytes->
                guardarLibroDisp(bytes)

            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()

            }


    }

    private fun guardarLibroDisp(bytes : ByteArray){
        val nombreLibro_extension = "$tituloLibro.pdf"
        try {
            val carpeta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            carpeta.mkdir()

            val archivo_ruta = carpeta.path+"/"+nombreLibro_extension
            val out =FileOutputStream(archivo_ruta)
            out.write(bytes)
            out.close()

            Toast.makeText(applicationContext, "Libro guardado con éxito", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
            incrementarNumDesc()

        }catch (e:Exception){
            Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
        }

    }

    private fun cargarDetalleLibro() {
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //obtener la informacion del libro a traves del id
                    val categoria = "${snapshot.child("categoria").value}"
                    val contadorDescargas = "${snapshot.child("contadorDescargas").value}"
                    val contadorVisitas = "${snapshot.child("contadorVisitas").value}"
                    val descripcion = "${snapshot.child("descripcion").value}"
                    val tiempo = "${snapshot.child("tiempo").value}"
                    tituloLibro = "${snapshot.child("titulo").value}"
                    urlLibro = "${snapshot.child("url").value}"

                    // formato del tiempo
                    val fecha = MisFunciones.formatoTiempo(tiempo.toLong())
                    //cargar categoria del libro
                    MisFunciones.CargarCategoria(categoria, binding.categoriaD)
                    // cargar la miniatura del libro, contador de paginas
                    MisFunciones.CargarPdfUrl("$urlLibro", "$tituloLibro", binding.VisualizadorPDF, binding.progressBar, binding.paginasD)

                    //cargar tamaño
                    MisFunciones.CargarTamanioPdf("$urlLibro", "$tituloLibro", binding.tamanioD)

                    //seteamosinformacion restante
                    binding.tituloLibroD.text = tituloLibro
                    binding.descripcionD.text = descripcion
                    binding.vistasD.text = contadorVisitas
                    binding.descargasD.text = contadorDescargas
                    binding.fechaD.text = fecha

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun incrementarNumDesc(){
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var contDescarActual = "${snapshot.child("contadorDescargas").value}"

                    if (contDescarActual == "" || contDescarActual == "null"){
                        contDescarActual = "0"

                    }

                    val nuevaDes = contDescarActual.toLong() + 1

                    val hashMap = HashMap<String, Any>()
                    hashMap["contadorDescargas"] = nuevaDes

                    val BDRef = FirebaseDatabase.getInstance().getReference("Libros")
                    BDRef.child(idLibro)
                        .updateChildren(hashMap)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }


}