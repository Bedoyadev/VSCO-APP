package com.example.login

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class RegistroActivity : AppCompatActivity() {
    var email1: EditText? = null
    var nom_perfil: EditText? = null
    var contra2: EditText? = null
    var cumple1: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

        email1 = findViewById(R.id.email1)
        nom_perfil = findViewById(R.id.nom_perfil)
        contra2 = findViewById(R.id.contra2)
        cumple1 = findViewById(R.id.cumple1)

        // Configurar botón de registro
        findViewById<Button>(R.id.regis).setOnClickListener {
            clickBtnInsertar(it)
        }

        // Configurar botón de retroceso
        findViewById<ImageButton>(R.id.retroceso).setOnClickListener {
            finish() // Regresar a la actividad anterior
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun clickBtnInsertar(view: View) {
        // Validación de campos vacíos
        if (email1 == null || nom_perfil == null || contra2 == null || cumple1 == null) {
            Toast.makeText(this, "Error interno: campos no inicializados.", Toast.LENGTH_LONG).show()
            return
        }
        if (email1?.text.isNullOrEmpty() ||
            nom_perfil?.text.isNullOrEmpty() ||
            contra2?.text.isNullOrEmpty() ||
            cumple1?.text.isNullOrEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_LONG).show()
            return
        }

        val url = "http://192.168.1.131/android_mysql/insertar.php"

        Toast.makeText(this, "Conectando al servidor...", Toast.LENGTH_SHORT).show()

        val queue = Volley.newRequestQueue(this)
        val resultadoPost = object : StringRequest(
            Request.Method.POST, url,
            { response: String ->
                Toast.makeText(this, "Usuario insertado exitosamente: $response", Toast.LENGTH_LONG).show()
            },
            { error: VolleyError ->
                val mensajeError = when {
                    error.networkResponse == null -> "Error de conexión: verifica tu internet"
                    error.networkResponse.statusCode == 404 -> "URL no encontrada (404)"
                    error.networkResponse.statusCode >= 500 -> "Error en el servidor (${error.networkResponse.statusCode})"
                    else -> "Error: ${error.message ?: "desconocido"}"
                }
                Toast.makeText(this, mensajeError, Toast.LENGTH_LONG).show()
                error.printStackTrace() // Para ver el error completo en el logcat
            }
        ) {
            override fun getParams(): Map<String, String> {
                val parametros = HashMap<String, String>()
                parametros["email1"] = email1?.text.toString()
                parametros["nom_perfil"] = nom_perfil?.text.toString()
                parametros["contra2"] = contra2?.text.toString()
                parametros["cumple1"] = cumple1?.text.toString()
                return parametros
            }
        }
        queue.add(resultadoPost)
    }
}