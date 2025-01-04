package com.jhimsebastian.studyshare.Administrador

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jhimsebastian.studyshare.LeerLibro
import com.jhimsebastian.studyshare.R
import com.jhimsebastian.studyshare.databinding.ActivityDetalleLibroBinding

class DetalleLibro : AppCompatActivity() {


    private lateinit var binding :ActivityDetalleLibroBinding
    private var idLibro = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleLibroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idLibro = intent.getStringExtra("idLibro")!!

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnLeerLibro.setOnClickListener {
            val intent = Intent(this@DetalleLibro, LeerLibro::class.java)
            intent.putExtra("idLibro", idLibro)
            startActivity(intent)
        }

        cargarDetalleLibro()


        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun cargarDetalleLibro() {
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //obtener la informacion del libro a traves del id
                    val categoria = "${snapshot.child("categoria").value}"
                    val contadorDescargas = "${snapshot.child("contadorDescargas").value}"
                    val contadorVisitas = "${snapshot.child("contadorVisitas").value}"
                    val descripcion = "${snapshot.child("descripcion").value}"
                    val tiempo = "${snapshot.child("tiempo").value}"
                    val titulo = "${snapshot.child("titulo").value}"
                    val url = "${snapshot.child("url").value}"

                    // formato del tiempo
                    val fecha = MisFunciones.formatoTiempo(tiempo.toLong())
                    //cargar categoria del libro
                    MisFunciones.CargarCategoria(categoria, binding.categoriaD)
                    // cargar la miniatura del libro, contador de paginas
                    MisFunciones.CargarPdfUrl("$url", "$titulo", binding.VisualizadorPDF, binding.progressBar, binding.paginasD)

                    //cargar tamaño
                    MisFunciones.CargarTamanioPdf("$url", "$titulo", binding.tamanioD)

                    //seteamosinformacion restante
                    binding.tituloLibroD.text = titulo
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
}