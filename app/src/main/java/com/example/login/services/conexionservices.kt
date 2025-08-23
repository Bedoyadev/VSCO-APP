package com.example.login.services


import com.example.login.data.Credencial
import com.example.login.data.Datos_acceso
import com.example.login.data.Nombre_usuario
import com.example.login.data.Registro
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface conexionservices {

    companion object{
        val url: String = "http://192.168.18.162:5000"
    }

    @POST("/login") // La ruta de tu API de Flask
    suspend fun login(@Body envio: Datos_acceso): Response<Nombre_usuario>
    @POST("/registro")
    suspend fun registro (@Body registro1: Credencial): Response<Registro>
}