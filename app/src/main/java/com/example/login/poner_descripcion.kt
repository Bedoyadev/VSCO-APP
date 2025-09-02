package com.example.login

import android.os.Bundle
import android.util.Log
import android.util.Patterns

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.login.data.retrofit
import com.example.login.databinding.FragmentPonerDescripcionBinding
import com.example.login.models.Vscoviewmodel
import com.example.login.models.modelVsco
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class poner_descripcion : Fragment() {


    private var _binding: FragmentPonerDescripcionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: Vscoviewmodel by activityViewModels()
    private var imagenOriginal: String? = null
    private var tituloOriginal: String? = null
    private var descripcionOriginal: String? = null

    private var id_publicacion:Int? = null
    private var modoEdicion: Boolean = false


    // Lanza la selección de imagen

    // --- Ciclo de vida del Fragment ---

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPonerDescripcionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ocultar botones de la actividad principal
        (activity as? inicio)?.ocultarBotonesPrincipales()

        // Permitir múltiples líneas en la descripción
        binding.descripcion1.apply {
            isSingleLine = false
            setHorizontallyScrolling(false)
        }

        // Cargar argumentos si vienen en modo edición o crear nueva
        arguments?.let { bundle ->
            val imageUriString = bundle.getString("imagenUri")
            val titulo = bundle.getString("titulo")
            val descripcion = bundle.getString("descripcion")
            val id = bundle.getInt("id")

            val modoedicion = bundle.getBoolean("Editando", false)

            if (modoedicion && imageUriString != null && titulo != null && descripcion != null) {
                modoEdicion = true
                imagenOriginal = imageUriString
                tituloOriginal = titulo
                descripcionOriginal = descripcion
                id_publicacion = id


                binding.eliminar.visibility = View.VISIBLE
                binding.tituloImagen1.setText(titulo)
                binding.descripcion1.setText(descripcion)
                binding.imagen1.setText(imageUriString)
                binding.etiqueta.setText(titulo)
                binding.subirImagen.text = "Guardar Cambios"
            } else if (imageUriString != null) {
                imagenOriginal = imageUriString
                binding.subirImagen.text = "Subir"
            }
        }

        // --- Listeners de botones ---
        binding.eliminar.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("¿Eliminar?")
                .setMessage("¿Estás seguro que desea eliminar esta publicación?")
                .setPositiveButton("Sí") { _, _ ->
                    lifecycleScope.launch {
                        eliminar()
                        parentFragmentManager.popBackStack() // Espera a que termine
                        // Solo después de eliminar
                    }

                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .show()
        }



        binding.retroceso.setOnClickListener {
            mostrarVentanaDescartar()
        }



        binding.subirImagen.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                var validaciones = validar_campos()
                if (validaciones == 2) {
                    editar()
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

    // --- Funciones auxiliares ---

    private fun mostrarVentanaDescartar() {
        AlertDialog.Builder(requireContext())
            .setTitle("¿Descartar?")
            .setMessage("¿Estás seguro que deseas descartar esta foto?")
            .setPositiveButton("Sí") { _, _ ->
                parentFragmentManager.popBackStack()
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    /*
     Maneja la creación de una nueva publicación o la edición de una existente.
     */
    suspend fun editar() {
        var nuevoTitulo = binding.tituloImagen1.text.toString()
        var nuevaDescripcion = binding.descripcion1.text.toString()
        var uriParaActualizar = binding.imagen1.text.toString()
        val correo = arguments?.getString("correo")!!

        // Validar longitud de título
            if (modoEdicion) {
                if (uriParaActualizar == "") {
                    uriParaActualizar = imagenOriginal!!
                    if (nuevoTitulo == "")
                        nuevoTitulo = tituloOriginal!!
                    if (nuevaDescripcion == "")
                        nuevaDescripcion = descripcionOriginal!!
                }
                Log.d("DEBUG_ID", "ID ediccion: ${id_publicacion}")

                // Crear el objeto con los datos actualizados
                val publicacionActualizada =
                    modelVsco(
                        id_publicacion,
                        uriParaActualizar,
                        nuevoTitulo,
                        nuevaDescripcion,
                        correo
                    )

                // Enviar la solicitud de actualización
                val response = retrofit.api_flask.actualizar_publicacion(publicacionActualizada)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        // Notificar al ViewModel para actualizar el estado
                        val publicacion_original = modelVsco(
                            id_publicacion,
                            imagenOriginal!!,
                            tituloOriginal!!,
                            descripcionOriginal!!,
                            correo
                        )
                        viewModel.editar(publicacion_original, publicacionActualizada)
                        Toast.makeText(
                            requireContext(),
                            "Publicación actualizada con éxito",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e(
                            "API_ERROR",
                            "Error de actualización: ${response.code()}, Mensaje: $errorBody"
                        )
                        Toast.makeText(
                            requireContext(),
                            "Error al actualizar la publicación.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {

                val nuevaPublicacion = modelVsco(
                    id = null,
                    imagen = uriParaActualizar,
                    titulo = nuevoTitulo,
                    descripcion = nuevaDescripcion,
                    correo = correo
                )

                val enviar = retrofit.api_flask.insertar_publicacion(nuevaPublicacion)

                if (enviar.isSuccessful && enviar.body() != null) {
                    // El backend devuelve solo el id como Int
                    val idGenerado = enviar.body() // Si Retrofit ya lo infiere como Int
                    nuevaPublicacion.id = idGenerado
                    viewModel.agregarPublicacion(nuevaPublicacion)
                } else {
                    Log.e("API_ERROR", "Error al insertar publicación: ${enviar.code()}")
                }
            }
        }

    suspend private fun validar_campos(): Int {
        var contador: Int = 0
        var nuevoTitulo = binding.tituloImagen1.text.toString()
        var uriParaActualizar = binding.imagen1.text.toString()
        if (nuevoTitulo.isEmpty()) {
            withContext(Dispatchers.Main){
            Toast.makeText(
                requireContext(),
                "El campo del titulo no puede estar vacio",
                Toast.LENGTH_SHORT
            ).show()
        } }else {
            contador++
        }
        if (uriParaActualizar.isEmpty()){
            withContext(Dispatchers.Main){
            Toast.makeText(requireContext(), "EL campo de la url no puede estar vacio", Toast.LENGTH_SHORT).show()
        }}else if(!Patterns.WEB_URL.matcher(uriParaActualizar).matches()) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    requireContext(),
                    "El texto digitado no es una url",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        else{
            contador ++
        }
        return contador
    }



    /*
      Elimina la publicación actual si estamos en modo edición.
     */
    private suspend fun eliminar() {
        try {
            val enviar = retrofit.api_flask.eliminar_publicacion(id_publicacion!!)
            if (enviar.isSuccessful) {
                viewModel.eliminarPublicacion(id_publicacion!!)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Eliminado con éxito", Toast.LENGTH_SHORT).show()
                }

            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error al eliminar", Toast.LENGTH_SHORT).show()
                }

            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Error de red al eliminar", Toast.LENGTH_SHORT).show()
            }
        }
    }


}




