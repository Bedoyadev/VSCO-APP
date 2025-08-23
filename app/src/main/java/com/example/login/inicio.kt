package com.example.login

import android.os.Bundle
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
import com.example.login.data.Credenciales

class inicio : AppCompatActivity() {
    private lateinit var binding: ActivityInicioBinding
    private lateinit var vscoadapter: Vscoadapter // Declara el adapter como una variable de clase
    private val vscoviewmodel: Vscoviewmodel by viewModels()

    // Este launcher es para cuando seleccionas una NUEVA imagen para una NUEVA publicación.
    private val seleccionarImagenParaNuevaPublicacion =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                // Si se seleccionó una imagen, abrimos el fragmento para añadir la descripción
                val fragmento = poner_descripcion().apply {
                    arguments = bundleOf(
                        "imagenUri" to uri.toString(),
                        "esEditando" to false// Indicamos que es una nueva publicación
                    )
                }
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragmento)
                    .addToBackStack(null) // 
                    .commit()
            }
        }

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

    }
    private fun inicializarRecyclerView() {
        binding.recycler.layoutManager = LinearLayoutManager(this)
        // Inicializa el adapter con la lista actual del ViewModel (o una vacía si es nula)
        // Y le pasamos la función para cuando se haga clic en un elemento
        vscoadapter = Vscoadapter(vscoviewmodel.datalistVsco.value ?: mutableListOf()) { publicacionClicada ->
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

    // Abre el fragmento 'poner_descripcion' en modo EDICIÓN
    private fun lanzarFragmentoEdicion(publicacion: modelVsco) {
        val fragmento = poner_descripcion().apply {
            arguments = bundleOf(
                // Pasamos los datos de la publicación existente para que se precarguen en el fragmento
                "imagenUri" to publicacion.imagen.toString(),
                "titulo" to publicacion.titulo,
                "descripcion" to publicacion.descripcion,
                "Editando" to true // Indicamos que estamos editando
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
            seleccionarImagenParaNuevaPublicacion.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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
}