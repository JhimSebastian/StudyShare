package com.jhimsebastian.studyshare.Cliente

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jhimsebastian.studyshare.Modelos.Modelopdf
import com.jhimsebastian.studyshare.databinding.ActivityTopDescargadosBinding

class TopDescargados : AppCompatActivity() {

    private lateinit var binding : ActivityTopDescargadosBinding
    private lateinit var pdfArrayList : ArrayList<Modelopdf>
    private lateinit var adaptadorPdfCliente : AdaptadorPdfCliente
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopDescargadosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        topDescargados()

    }

    private fun topDescargados() {
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.orderByChild("contadorDescargas").limitToLast(10)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()
                    for (ds in snapshot.children){
                        val modelo = ds.getValue(Modelopdf::class.java)
                        pdfArrayList.add(modelo!!)
                    }
                    pdfArrayList.reverse()
                    adaptadorPdfCliente = AdaptadorPdfCliente(this@TopDescargados, pdfArrayList)
                    binding.RvTopDescargados.adapter = adaptadorPdfCliente
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })


    }
}