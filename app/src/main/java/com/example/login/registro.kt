package com.example.login

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.login.databinding.ActivityRegistroBinding
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class registro : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        eventoRetroceder()
        eventoRegistrarse()
        binding.cumple1.keyListener = null

        // Configura el DatePicker para el campo de fecha (cumple)
        binding.cumple1.setOnClickListener {
            val calendario = Calendar.getInstance()
            val agno = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, agnoSeleccionado, mesSeleccionado, diaSeleccionado ->
                    // Verifica que el año seleccionado no sea mayor a 2010.
                    if (agnoSeleccionado > 2010) {
                        Toast.makeText(this, "El año seleccionado máximo es 2010", Toast.LENGTH_SHORT).show()
                    } else {
                        binding.cumple1.setText("$diaSeleccionado/${mesSeleccionado + 1}/$agnoSeleccionado")
                    }
                },
                agno, mes, dia
            )
            datePickerDialog.datePicker.maxDate = calendario.timeInMillis

            datePickerDialog.show()
        }
    }


    fun eventoRetroceder() {
        binding.retroceso.setOnClickListener {
            mostrarVentanaRetroceso()
        }
    }

    fun eventoRegistrarse() {
        binding.regis.setOnClickListener {
            val cuenta = validarCampos()
            if (cuenta == 4) {
                // Agrega la credencial al objeto global.
                Credenciales.agregarCredencial(
                    binding.email1.text.toString(),
                    binding.contra2.text.toString(),
                    binding.nomPerfil.text.toString()
                )

                Toast.makeText(this, "Se ha registrado exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    fun validarCampos(): Int {
        var contador = 0
        val emailValue = binding.email1.text.toString()
        val usuarioValue = binding.nomPerfil.text.toString()
        val contraValue = binding.contra2.text.toString()
        val cumpleValue = binding.cumple1.text.toString()

        if (emailValue.isEmpty()) {
            Toast.makeText(this, "El campo de email está vacío", Toast.LENGTH_SHORT).show()
        } else if (correoValido(emailValue)) {
           contador++
        }else{
            Toast.makeText(this, "El 'correo' digitado no esta en formato correo", Toast.LENGTH_SHORT).show()
        }
        if (usuarioValue.isEmpty()) {
            Toast.makeText(this, "El campo de usuario está vacío", Toast.LENGTH_SHORT).show()
        }else if (usuarioValue.length > 10) {
            Toast.makeText(this, "El usuario puede tener un máximo de 10 caracteres", Toast.LENGTH_SHORT).show()
        } else {
            contador++
        }
        if (contraValue.isEmpty()) {
            Toast.makeText(this, "El campo de contraseña está vacío", Toast.LENGTH_SHORT).show()
        } else if (contraValue.length < 8) {
            Toast.makeText(this, "La contraseña debe tener mínimo 8 caracteres", Toast.LENGTH_SHORT).show()
        } else {
            contador++
        }
        if (cumpleValue.isEmpty()) {
            Toast.makeText(this, "El campo de fecha está vacío", Toast.LENGTH_SHORT).show()
        } else {
            contador++
        }
        return contador
    }
    fun correoValido(correo: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    }



    private fun mostrarVentanaRetroceso() {
        AlertDialog.Builder(this)
            .setTitle("Cancelar Registro")
            .setMessage("¿Estás seguro que deseas cancelar el registro?")
            .setPositiveButton("Sí") { _, _ ->
                finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}


