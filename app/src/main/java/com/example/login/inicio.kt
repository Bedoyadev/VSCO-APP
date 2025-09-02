package com.example.login

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login.adapter.Vscoadapter
import com.example.login.databinding.ActivityInicioBinding
import com.example.login.models.Vscoviewmodel
import com.example.login.models.modelVsco
import androidx.core.os.bundleOf
import com.example.login.data.Correo
import com.example.login.data.Credenciales
import com.example.login.data.retrofit
import com.example.login.services.conexionservices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class inicio : AppCompatActivity() {
    private lateinit var binding: ActivityInicioBinding
    private lateinit var vscoadapter: Vscoadapter // Declara el adapter como una variable de clase
    private val vscoviewmodel: Vscoviewmodel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Este listener se activa cada vez que un fragmento se añade o se quita de la pila
        supportFragmentManager.addOnBackStackChangedListener {
            val fragmentoActual = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (fragmentoActual !is poner_descripcion) {
                // Si no hay fragmentos en la pila (estamos en la pantalla principal), mostramos los botones
                mostrarBotonesPrincipales()
            } else {
                // Si hay un fragmento abierto, ocultamos los botones de la actividad
                ocultarBotonesPrincipales()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val usuario_actual = Credenciales.usuarioActual?.nombre_user
        binding.NomUsuario.text = "Bienvenido, $usuario_actual!"

        // Inicializamos el RecyclerView y configuramos la observación de los datos
        inicializarRecyclerView()
        observarDatosViewModel()
        CerrarSesion()
        BotonAgregar()
        listar_publicaciones()

    }

    private fun inicializarRecyclerView() {
        binding.recycler.layoutManager = LinearLayoutManager(this)
        // Inicializa el adapter con la lista actual del ViewModel (o una vacía si es nula)
        // Y le pasamos la función para cuando se haga clic en un elemento
        vscoadapter =
            Vscoadapter(vscoviewmodel.datalistVsco.value ?: mutableListOf()) { publicacionClicada ->
                lanzarFragmentoEdicion(publicacionClicada) // Llama a la función para abrir el fragmento en modo edición
            }
        binding.recycler.adapter = vscoadapter
    }

    // Esta función "observa" los cambios en la lista de publicaciones de tu ViewModel
    private fun observarDatosViewModel() {
        vscoviewmodel.datalistVsco.observe(this) { listaPublicaciones ->
            // Cuando la lista en el ViewModel cambia (se añade o actualiza algo):
            // 1. Actualizamos la lista que tiene nuestro adapter
            vscoadapter.publicacion = listaPublicaciones ?: mutableListOf()
            // 2. Le decimos al adapter que los datos han cambiado, para que el RecyclerView se redibuje
            vscoadapter.notifyDataSetChanged()
        }
    }

    private fun listar_publicaciones() {
        val correo = intent.getStringExtra("correo") ?: return
        val correo1 = Correo(correo)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call = retrofit.api_flask.consulta_publicaciones(correo1)
                if (call.isSuccessful && call.body() != null) {

                    // Mapear los datos del backend a nuestro modelVsco
                    val publicaciones = call.body()!!.map { pub ->
                        modelVsco(
                            id = pub.id,   // ID real desde el backend
                            imagen = pub.imagen,     // URL o ruta de la imagen
                            titulo = pub.titulo,
                            descripcion = pub.descripcion,
                            correo = pub.correo     // Nombre del usuario que viene del backend
                        )
                    }.toMutableList()

                    withContext(Dispatchers.Main) {
                        vscoviewmodel.agregarPublicaciones(publicaciones)
                    }

                } else {
                    Log.e("listar_publicaciones", "Error, no se encontró información o body es null")
                }
            } catch (e: Exception) {
                Log.e("listar_publicaciones", "No se pudo conectar al backend: ${e.message}")
            }
        }
    }


    // Abre el fragmento 'poner_descripcion' en modo EDICIÓN
    private fun lanzarFragmentoEdicion(publicacion: modelVsco) {
        val correo = intent.getStringExtra("correo")
        Log.d("DEBUG_ID", "ID de la publicación: ${publicacion.id}")
        val fragmento = poner_descripcion().apply {
            arguments = bundleOf(
                // Pasamos los datos de la publicación existente para que se precarguen en el fragmento
                "imagenUri" to publicacion.imagen,
                "titulo" to publicacion.titulo,
                "descripcion" to publicacion.descripcion,
                "Editando" to true,// Indicamos que estamos editando
                "correo" to correo,
                "id" to publicacion.id
            )
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragmento)
            .addToBackStack(null)
            .commit()
    }

    private fun BotonAgregar() {
        binding.botonAgregar.setOnClickListener {
            // Lanza el selector de imágenes para elegir una foto para la nueva publicación
            lanzarfragmento()
        }
    }

    private fun CerrarSesion() {
        binding.salir.setOnClickListener {
            mostrarVentanaCerrarSesion()
        }
    }

    private fun mostrarVentanaCerrarSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que quieres cerrar la sesión?")
            .setPositiveButton("Sí") { _, _ ->
                finish() // Cierra la actividad (y por lo tanto, la sesión)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss() // Cierra el diálogo
            }
            .show()
    }

    // Oculta los elementos de la interfaz de usuario principal
    fun ocultarBotonesPrincipales() {
        binding.salir.visibility = View.GONE
        binding.botonAgregar.visibility = View.GONE
        binding.menu.visibility = View.GONE
        binding.NomUsuario.visibility = View.GONE
        binding.lineaMenu.visibility = View.GONE
        binding.noticias.visibility = View.GONE
        binding.buscar.visibility = View.GONE
        binding.recycler.visibility = View.GONE
        binding.textoNoticias.visibility = View.GONE
        binding.cuadroStudio.visibility = View.GONE
        binding.perfil.visibility = View.GONE
        binding.spaces.visibility = View.GONE
    }

    // Muestra los elementos de la interfaz de usuario principal
    fun mostrarBotonesPrincipales() {
        binding.salir.visibility = View.VISIBLE
        binding.botonAgregar.visibility = View.VISIBLE
        binding.menu.visibility = View.VISIBLE
        binding.NomUsuario.visibility = View.VISIBLE
        binding.lineaMenu.visibility = View.VISIBLE
        binding.noticias.visibility = View.VISIBLE
        binding.buscar.visibility = View.VISIBLE
        binding.recycler.visibility = View.VISIBLE
        binding.textoNoticias.visibility = View.VISIBLE
        binding.cuadroStudio.visibility = View.VISIBLE
        binding.perfil.visibility = View.VISIBLE
        binding.spaces.visibility = View.VISIBLE
    }

    private fun lanzarfragmento() {

        val correo = intent.getStringExtra("correo")
        val fragment = poner_descripcion().apply {
            arguments = bundleOf("correo" to correo)
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()

    }
}