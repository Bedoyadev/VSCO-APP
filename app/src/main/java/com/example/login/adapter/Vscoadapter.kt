package com.example.login.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.login.R
import com.example.login.adapter.Vscoadapter.VscoViewHolder
import com.example.login.databinding.ActivityReciclerBinding
import com.example.login.models.modelVsco


class Vscoadapter(
    var publicacion: MutableList<modelVsco>,
    private val onItemClick: (modelVsco) -> Unit
) : RecyclerView.Adapter<VscoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VscoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_recicler, parent, false)
        return VscoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VscoViewHolder, position: Int) {
        val item = publicacion[position]
        holder.inicializa(item)
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return publicacion.size
    }

    class VscoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ActivityReciclerBinding.bind(view)

        fun inicializa(item: modelVsco) {
            Glide.with(binding.root.context)
                .load(item.imagen) // 'item.imagen' ahora es un String
                .into(binding.imagenPublicacion)
            binding.tituloPublicacion.text = item.titulo
            binding.descripcionPublicacion.text = item.descripcion

        }
    }
}