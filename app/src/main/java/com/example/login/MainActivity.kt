package com.example.login


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope

import com.example.login.data.Credenciales
import com.example.login.data.Datos_acceso
import com.example.login.data.retrofit
import com.example.login.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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

    //Esta funcion sirve para verificar credenciales.
    suspend fun verificar_credenciales() {
        val correo = binding.email.text.toString()
        val contrasena = binding.contrasena.text.toString()
        if (correo.isEmpty()) {
            Toast.makeText(
                this,
                "El campo de correo está vacío. Por favor digite su correo.",
                Toast.LENGTH_SHORT
            ).show()
        } else if (contrasena.isEmpty()) {
            Toast.makeText(this, "El campo de contraseña está vacío.", Toast.LENGTH_SHORT).show()
        }
        try {
                val datos = Datos_acceso(correo, contrasena)
                val enviar = retrofit.api_flask.login(datos)

                // Regresamos al hilo principal para actualizar la interfaz de usuario
                lifecycleScope.launch(Dispatchers.Main) {
                    if (enviar.isSuccessful) {
                        enviar.body()?.let { usuario ->
                            Credenciales.usuarioActual = usuario
                            val intent = Intent(this@MainActivity, inicio::class.java)
                            intent.putExtra("correo", correo)
                            startActivity(intent)
                        }
                    } else {
                        // Este 'else' maneja el caso de login fallido
                        Toast.makeText(
                            this@MainActivity,
                            "Correo o contraseña incorrecta",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@MainActivity, registro::class.java)
                        startActivity(intent)
                    }
                }
            } catch (e: Exception) {
                lifecycleScope.launch(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Error de conexión: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }


                fun evento_inciar_sesion() {
                    binding.iniciarSesion.setOnClickListener {
                        lifecycleScope.launch(Dispatchers.IO) {
                            verificar_credenciales()
                        }

                    }
                }

                fun registrarse() {
                    binding.registrarse.setOnClickListener {
                        val click = Intent(this, registro::class.java)
                        startActivity(click)
                    }
                }


            }
