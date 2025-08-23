package com.example.login.data

import com.google.gson.annotations.SerializedName
import java.sql.Date
import java.time.LocalDate

//Data class para almacenar credenciales (correo, contraseña y nombre de usuario).
data class Credencial(val correo: String, val usuario: String, val contrasena: String, val cumple: String)

data class Datos_acceso(val correo: String, val contrasena: String)

data class Registro(val resultado: Boolean)

data class  Nombre_usuario(@SerializedName("nombre") val nombre_user:String)

object Credenciales {
    var usuarioActual: Nombre_usuario? = null

    }

