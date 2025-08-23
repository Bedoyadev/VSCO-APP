package com.example.login.data


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.login.services.conexionservices

object retrofit {

    // El objeto 'lazy' asegura que la instancia de Retrofit se crea una sola vez
    // cuando se accede por primera vez, ahorrando recursos.
    val api_flask: conexionservices by lazy {
        Retrofit.Builder()
            .baseUrl(conexionservices.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(conexionservices::class.java)
    }
}