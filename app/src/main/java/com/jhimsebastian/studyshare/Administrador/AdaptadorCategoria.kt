package com.jhimsebastian.studyshare.Administrador

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.jhimsebastian.studyshare.Modelos.ModeloCategoria
import com.jhimsebastian.studyshare.databinding.ItemCategoriaAdminBinding

class AdaptadorCategoria : RecyclerView.Adapter<AdaptadorCategoria.HolderCategoria>, Filterable{

    private lateinit var binding: ItemCategoriaAdminBinding

    private val m_context : Context
    public var categoriaArraylist : ArrayList<ModeloCategoria>

    private var  filtroLista : ArrayList<ModeloCategoria>
    private var  filtro : FiltroCategoria? = null

    constructor(m_context: Context, categoriaArraylist: ArrayList<ModeloCategoria>) {
        this.m_context = m_context
        this.categoriaArraylist = categoriaArraylist
        this.filtroLista = categoriaArraylist
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategoria {
        binding = ItemCategoriaAdminBinding.inflate(LayoutInflater.from(m_context), parent, false)
        return HolderCategoria(binding.root)
    }

    override fun getItemCount(): Int {
        return  categoriaArraylist.size
    }

    override fun onBindViewHolder(holder: HolderCategoria, position: Int) {
        val modelo = categoriaArraylist[position]
        val id = modelo.id
        val categoria = modelo.categoria
        val tiempo = modelo.tiempo
        val uid = modelo.uid

        holder.categoriaTv.text = categoria

        holder.eliminarCatIb.setOnClickListener {
            val builder = AlertDialog.Builder(m_context)
            builder.setTitle("Eliminar categoria")
                .setMessage("¿Estás seguro de eleiminar esta categoria?")
                .setPositiveButton("Confirmar"){a,d->
                    Toast.makeText(m_context, "Eliminando categoria", Toast.LENGTH_SHORT).show()
                    EliminarCategoria(modelo, holder)
                }
                .setNegativeButton("Cancelar"){a,d->
                    a.dismiss()
                }
            builder.show()
        }

        holder.itemView.setOnClickListener{
            val intent = Intent(m_context, ListaPdfAdmin::class.java)
            intent.putExtra("idCategoria", id)
            intent.putExtra("tituloCategoria", categoria)
            m_context.startActivity(intent)
        }
    }

    private fun EliminarCategoria(modelo: ModeloCategoria, holder: AdaptadorCategoria.HolderCategoria) {
        val id = modelo.id
        val ref = FirebaseDatabase.getInstance().getReference("Categorias")
        ref.child(id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(m_context, "Categoria eliminada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{e->
                Toast.makeText(m_context, "No se pudo eliminar debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    inner class HolderCategoria (itemView: View) : RecyclerView.ViewHolder(itemView){
        var categoriaTv : TextView =binding.ItemNombreCatA
        var eliminarCatIb : ImageButton = binding.EliminarCat
    }

    override fun getFilter(): Filter {
        if (filtro == null){
            filtro = FiltroCategoria(filtroLista, this)
        }
        return  filtro as FiltroCategoria
    }


}