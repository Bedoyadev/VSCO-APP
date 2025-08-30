package com.example.login.services


import com.example.login.data.Credencial
import com.example.login.data.Datos_acceso
import com.example.login.data.Nombre_usuario
import com.example.login.data.Registro
import com.example.login.data.nueva_publicacion
import com.example.login.models.modelVsco
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface conexionservices {

    companion object{
        val url: String = "http://192.168.18.162:5000"
    }

    @POST("/login") // La ruta de tu API de Flask
    suspend fun login(@Body envio: Datos_acceso): Response<Nombre_usuario>
    @POST("/registro")
    suspend fun registro (@Body registro1: Credencial): Response<Registro>

    @GET("/consultarPublicaciones")
    suspend fun consulta_publicaciones(): Response<List<modelVsco>>

    @Multipart
    @POST("/insertar_publicaciones")
    suspend fun insertar_publicacion(@Part photo: MultipartBody.Part,
                                     @Part("titulo") titulo: RequestBody,
                                     @Part("descripcion") descripcion: RequestBody): Response<nueva_publicacion>
}