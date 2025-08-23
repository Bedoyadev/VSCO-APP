package com.example.login.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.net.Uri

class Vscoviewmodel: ViewModel() {
    private val _datalistVsco: MutableLiveData<MutableList<modelVsco>> =
        MutableLiveData(mutableListOf())

    val datalistVsco: MutableLiveData<MutableList<modelVsco>>
        get() = _datalistVsco

    fun agregarPublicacion(publicacion: modelVsco) {
        val lista = _datalistVsco.value ?: mutableListOf()
        lista.add(publicacion)
        _datalistVsco.postValue(lista)
    }

    fun editar(
        publicacion_original: modelVsco,
        nuevoTitulo: String,
        nuevaDescripcion: String,
        nuevaImagen: Uri
    ) {

        val publicaciones = _datalistVsco.value ?: mutableListOf()
        val indiceEncontrado = publicaciones.indexOfFirst { publicacion ->
            publicacion == publicacion_original
        }

        // Si encontramos una coincidencia, actualizamos la publicación el menos -1 es para verficar que se encontro una publicacion
        if (indiceEncontrado != -1) {
            val actualizarPublicacion = modelVsco(
                imagen = nuevaImagen,
                titulo = nuevoTitulo,
                descripcion = nuevaDescripcion
            )

            publicaciones[indiceEncontrado] = actualizarPublicacion
            _datalistVsco.postValue(publicaciones) // Notificar a los observadores (RecyclerView, etc.)
        }
    }
    fun eliminarPublicacion(
        imagen: Uri,
        titulo: String,
        descripcion: String
    ) {
        val publicaciones = _datalistVsco.value ?: mutableListOf()

        val indiceAEliminar = publicaciones.indexOfFirst { publicacion ->
            publicacion.imagen == imagen &&
                    publicacion.titulo == titulo &&
                    publicacion.descripcion == descripcion
        }

        if (indiceAEliminar != -1) {
            publicaciones.removeAt(indiceAEliminar)
            _datalistVsco.postValue(publicaciones) // Actualiza la lista y notifica a los observers
        }
    }

}