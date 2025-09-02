package com.example.login.services


import com.example.login.data.Credencial
import com.example.login.data.Datos_acceso
import com.example.login.data.Nombre_usuario
import com.example.login.data.Registro
import com.example.login.data.Correo
import com.example.login.data.nueva_publicacion
import com.example.login.models.modelVsco
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface conexionservices {

    companion object{
        val url: String = "http://192.168.0.103:5000"
    }

    @POST("/login") // La ruta de tu API de Flask
    suspend fun login(@Body envio: Datos_acceso): Response<Nombre_usuario>
    @POST("/registro")
    suspend fun registro (@Body registro1: Credencial): Response<Registro>

    @POST("/consultarPublicaciones")
    suspend fun consulta_publicaciones(@Body correo: Correo): Response<List<modelVsco>>

    @POST("/insertar_publicaciones")
    suspend fun insertar_publicacion(@Body publicacion: modelVsco): Response<Int>

    @PUT("/actualizar_publicaciones")
    suspend fun actualizar_publicacion(@Body publicacion: modelVsco): Response<Void>

    @DELETE("/eliminar_publicacion/{id}")
    suspend fun eliminar_publicacion(@Path("id") id: Int): Response<Any>
}