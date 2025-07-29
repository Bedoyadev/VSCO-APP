package com.example.login

// Data class para almacenar credenciales (correo, contraseña y nombre de usuario).
data class Credencial(val correo: String, val contrasena: String, val usuario: String)

object Credenciales {
    val listaCredenciales = mutableListOf(Credencial("vivancho89@gmail.com", "america14", "ñiñin"))
    var usuarioActual: Credencial? = null


    // Agrega una nueva credencial a la lista.
    fun agregarCredencial(correo: String, contrasena: String, usuario: String) {
        listaCredenciales.add(Credencial(correo, contrasena, usuario))
    }
}
