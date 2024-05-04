package com.jhimsebastian.studyshare.Fragmentos_Admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jhimsebastian.studyshare.Administrador.Agregar_Categoria
import com.jhimsebastian.studyshare.R
import com.jhimsebastian.studyshare.databinding.FragmentAdminCuentaBinding
import com.jhimsebastian.studyshare.databinding.FragmentAdminDashboardBinding

class Fragment_admin_dashboard : Fragment() {


    private lateinit var binding: FragmentAdminDashboardBinding
    private lateinit var mContext : Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAdminDashboardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.BtnAgregarCategoria.setOnClickListener {
            startActivity(Intent(mContext, Agregar_Categoria::class.java))
        }

        binding.AgregarPdf.setOnClickListener {

        }
    }


}