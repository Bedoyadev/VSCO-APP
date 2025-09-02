package com.example.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.login.databinding.ActivityMainBinding
import com.android.volley.Response
import com.android.volley.Request.Method
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

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

        val email1 = findViewById<EditText>(R.id.email)
        val contra2 = findViewById<EditText>(R.id.contrasena)
        val boton_logear = findViewById<Button>(R.id.iniciar_sesion)

        boton_logear.setOnClickListener {
            val email = email1.text.toString()
            val contra = contra2.text.toString()

            if (email != "" && contra != "") {
                login_bd_volley(email, contra)
            } else {
                Toast.makeText(this, "Faltan campos por llenar.", Toast.LENGTH_LONG).show()
            }
        }
        registrarse()
    }

    fun registrarse() {
        binding.registrarse.setOnClickListener {
            val click = Intent(this, RegistroActivity::class.java)
            startActivity(click)
        }
    }

    fun login_bd_volley(usuario: String, contra: String) {
        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.0.158/android_mysql/login.php"
        Log.d("LOGIN_URL", "Conectando a: $url")

        val peticion_post = object : StringRequest(
            Method.POST, url, Response.Listener { response ->
                Log.d("LOGIN_RESPONSE", "Respuesta del servidor: $response")
                if (response.trim().contains("1")) {
                    val intent = Intent(this, inicio::class.java)
                    intent.putExtra("ID", usuario)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Usuario o contraseña incorrectos. Respuesta: $response", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Log.e("LOGIN_ERROR", "Error: ${error.message}")
                Toast.makeText(this, "Error de conexión $error", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                val parametros = HashMap<String, String>()
                parametros["usuario"] = usuario
                parametros["contrasena"] = contra
                Log.d("LOGIN_PARAMS", "Enviando: usuario=$usuario, contraseña=$contra")
                return parametros
            }
        }
        queue.add(peticion_post)
    }
}