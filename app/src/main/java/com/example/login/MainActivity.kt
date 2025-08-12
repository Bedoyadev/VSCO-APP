package com.example.login

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils

import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.login.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
        evento_inciar_sesion()
        registrarse()
    }


    fun verificar_credenciales() {
        val  correo = binding.email.text.toString()
        val  contrasena = binding.contrasena.text.toString()
        if (correo.isEmpty()) {
            Toast.makeText(this, "El campo de correo está vacío.", Toast.LENGTH_SHORT).show()
        } else if (contrasena.isEmpty()) {
            Toast.makeText(this, "El campo de contraseña está vacío.", Toast.LENGTH_SHORT).show()
        } else {
            // Buscar la credencial que coincida con el correo y contraseña ingresados
            val credEncontrada = Credenciales.listaCredenciales.find {
                it.correo == correo && it.contrasena == contrasena
            }
            if (credEncontrada != null) {
                // Asignamos el usuario actual en el objeto global
                Credenciales.usuarioActual = credEncontrada
                // Iniciamos la Activity "inicio" (donde se mostrará solo el nombre)
                val intent = Intent(this, inicio::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Correo o contraseña incorrecta", Toast.LENGTH_SHORT).show()
                // En caso de error, se redirige a la pantalla de registro
                val intent = Intent(this, registro::class.java)
                startActivity(intent)
            }
        }
    }

    fun evento_inciar_sesion() {
        binding.iniciarSesion.setOnClickListener {
            verificar_credenciales()
        }
    }

    fun registrarse() {
        binding.registrarse.setOnClickListener {
            val click = Intent(this, registro::class.java)
            startActivity(click)
        }
    }


}