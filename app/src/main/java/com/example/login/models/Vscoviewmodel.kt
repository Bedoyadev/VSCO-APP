package com.example.login.models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.login.data.nueva_publicacion


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

    fun agregarPublicaciones(publicaciones: MutableList<modelVsco>) {
        val lista = _datalistVsco.value ?: mutableListOf()
        lista.addAll(publicaciones)
        _datalistVsco.postValue(lista)
    }

    fun editar(
        publicacion_original: modelVsco,
        nuevaPublicacion: modelVsco
    ) {

        val publicaciones = _datalistVsco.value ?: mutableListOf()
        val indiceEncontrado = publicaciones.indexOfFirst { publicacion ->
            publicacion.id == publicacion_original.id
        }

        // Si encontramos una coincidencia, actualizamos la publicación el menos -1 es para verficar que se encontro una publicacion
        if (indiceEncontrado != -1) {
            publicaciones[indiceEncontrado] = nuevaPublicacion
            _datalistVsco.postValue(publicaciones) // Notificar a los observadores (RecyclerView, etc.)
        }
    }
    fun eliminarPublicacion(
        id_publi: Int
    ) {
        val publicaciones = _datalistVsco.value ?: mutableListOf()
        Log.d("DEBUG_ELIMINAR", "Indice encontrado: $id_publi")
        val indiceAEliminar = publicaciones.indexOfFirst { publicacion ->
            publicacion.id == id_publi
        }

        if (indiceAEliminar != -1) {
            publicaciones.removeAt(indiceAEliminar)
            _datalistVsco.postValue(publicaciones) // Actualiza la lista y notifica a los observers
        }
    }

}