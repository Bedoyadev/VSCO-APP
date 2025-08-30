package com.example.login

import android.os.Bundle

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.login.databinding.FragmentPonerDescripcionBinding
import com.example.login.models.Vscoviewmodel
import com.example.login.models.modelVsco

class poner_descripcion : Fragment() {


    private var _binding: FragmentPonerDescripcionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: Vscoviewmodel by activityViewModels()
    private var imagenOriginal: String? = null
    private var tituloOriginal: String? = null
    private var descripcionOriginal: String? = null
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
            val modoedicion = bundle.getBoolean("Editando", false)

            if (modoedicion && imageUriString != null && titulo != null && descripcion != null) {
                modoEdicion = true
                imagenOriginal = imageUriString
                tituloOriginal = titulo
                descripcionOriginal = descripcion

                binding.cambiarImagenButton.visibility = View.VISIBLE
                binding.eliminar.visibility = View.VISIBLE
                binding.tituloImagen1.setText(titulo)
                binding.descripcion1.setText(descripcion)
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
                    eliminar()
                    parentFragmentManager.popBackStack()
                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        binding.retroceso.setOnClickListener {
            mostrarVentanaDescartar()
        }



        binding.subirImagen.setOnClickListener {
            editar()
            parentFragmentManager.popBackStack()
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
    fun editar() {
        val nuevoTitulo = binding.tituloImagen1.text.toString()
        val nuevaDescripcion = binding.descripcion1.text.toString()
        val uriParaActualizar = binding.imagen1.text.toString()

        // Validar longitud de título
        if (nuevoTitulo.length > 20) {
            Toast.makeText(
                requireContext(),
                "El titulo puede tener un maximo de 20 caracteres",
                Toast.LENGTH_SHORT
            ).show()
        }

        if (modoEdicion) {
            val publicacion_original = modelVsco(imagenOriginal!!, tituloOriginal!!, descripcionOriginal!!)
            if (imagenOriginal != null && tituloOriginal != null && descripcionOriginal != null) {
                viewModel.editar(publicacion_original,
                    nuevoTitulo = nuevoTitulo,
                    nuevaDescripcion = nuevaDescripcion,
                    nuevaImagen = uriParaActualizar
                )
            }
        } else {
            // Agregar nueva publicación
            val nuevaPublicacion = modelVsco(imagenOriginal!!, nuevoTitulo, nuevaDescripcion)
            viewModel.agregarPublicacion(nuevaPublicacion)
        }
    }

    /*
      Elimina la publicación actual si estamos en modo edición.
     */
    private fun eliminar() {
        if (modoEdicion)

            if (imagenOriginal != null && tituloOriginal != null && descripcionOriginal != null) {
                viewModel.eliminarPublicacion(
                    imagen = imagenOriginal!!,
                    titulo = tituloOriginal!!,
                    descripcion = descripcionOriginal!!
                )
            }
    }

}