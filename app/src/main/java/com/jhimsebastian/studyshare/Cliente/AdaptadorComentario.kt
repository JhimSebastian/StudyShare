package com.jhimsebastian.studyshare.Cliente

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jhimsebastian.studyshare.Administrador.MisFunciones
import com.jhimsebastian.studyshare.Modelos.ModeloComentario
import com.jhimsebastian.studyshare.R
import com.jhimsebastian.studyshare.databinding.FragmentClienteFavoritosBinding
import com.jhimsebastian.studyshare.databinding.ItemComentarioBinding

class AdaptadorComentario : RecyclerView.Adapter<AdaptadorComentario.HolderComentario>{

    val context : Context
    val comentarioArrayList : ArrayList<ModeloComentario>

    private lateinit var binding: ItemComentarioBinding

    constructor(context: Context, comentarioArrayList: ArrayList<ModeloComentario>) {
        this.context = context
        this.comentarioArrayList = comentarioArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderComentario {
        binding = ItemComentarioBinding.inflate(LayoutInflater.from(context), parent, false)
        return  HolderComentario(binding.root)
    }

    override fun getItemCount(): Int {
        return comentarioArrayList.size
    }

    override fun onBindViewHolder(holder: HolderComentario, position: Int) {
        val modelo = comentarioArrayList[position]

        val id = modelo.id
        val idLibro = modelo.idLibro
        val comentario = modelo.comentario
        val uid = modelo.comentario
        val tiempo = modelo.tiempo

        val fecha = MisFunciones.formatoTiempo(tiempo.toLong())

        holder.Tv_fecha.text = fecha
        holder.Tv_comentario.text = comentario

        cargarInformacion(modelo, holder)

        holder.itemView.setOnClickListener{
            dialogEliminarCom(modelo, holder)
        }
    }

    private fun dialogEliminarCom(modelo: ModeloComentario, holder: AdaptadorComentario.HolderComentario) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Eliminar comentario")
            .setMessage("¿Estas seguro(a) de eliminar comentario?")
            .setPositiveButton("Eliminar"){d,e->
                val idLibro = modelo.idLibro
                val idCom = modelo.id

                val ref = FirebaseDatabase.getInstance().getReference("Libros")
                ref.child(idLibro).child("Comentarios").child(idCom)
                    .removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Comentario eliminado", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e->
                        Toast.makeText(context, "Falló la eliminacion del comentario debido a ${e.message}", Toast.LENGTH_SHORT).show()
                    }

            }
            .setNegativeButton("Cancelar"){d,e->
                d.dismiss()
            }
            .show()
    }

    private fun cargarInformacion(modelo: ModeloComentario, holder: AdaptadorComentario.HolderComentario) {
        val uid = modelo.uid

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombre = "${snapshot.child("nombres").value}"
                    val imagen = "${snapshot.child("imagen").value}"

                    holder.Tv_nombres.text = nombre
                    try {
                        Glide.with(context)
                            .load(imagen)
                            .placeholder(R.drawable.ic_img_perfil)
                            .into(holder.Iv_imagen)

                    }catch (e:Exception){
                        holder.Iv_imagen.setImageResource(R.drawable.ic_img_perfil)

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }


    inner class HolderComentario (itemView: View) : RecyclerView.ViewHolder(itemView){
        val Iv_imagen = binding.IvImgPerfil
        val Tv_nombres = binding.TvNombresC
        val Tv_fecha = binding.TvFechaC
        val Tv_comentario = binding.TvComentarioC
    }


}