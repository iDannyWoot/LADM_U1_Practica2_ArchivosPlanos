package mx.edu.ittepic.ladm_u1_practica2_archivosplanos

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import mx.edu.ittepic.ladm_u1_practica2_archivosplanos.databinding.FragmentPacientesBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PacientesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PacientesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding : FragmentPacientesBinding
    var listaDatosContactos: MutableList<String> = mutableListOf()
    var posicionActualizar = -1

    private fun insertar(){
        if(validarCampos()){
            var concatenacion = "Nombre: " + binding.nombrecompleto.text.toString()+"\n"+
                    "Edad: " + binding.edad.text.toString() +"\n"+
                    "Dirección: " + binding.direccion.text.toString() +"\n"+
                    "Ocupación: " + binding.ocupacion.text.toString() +"\n"+
                    "Teléfono: " + binding.telefono.text.toString()
            listaDatosContactos.add(concatenacion)
            binding.lista.adapter = ArrayAdapter<String>(binding.root.context,
                android.R.layout.simple_list_item_1,listaDatosContactos)
            binding.nombrecompleto.setText("")
            binding.edad.setText("")
            binding.direccion.setText("")
            binding.ocupacion.setText("")
            binding.telefono.setText("")
        }
    }

    private fun validarCampos(): Boolean {

        if(binding.nombrecompleto.getText().toString() == "" &&
            binding.edad.getText().toString() == "" &&
            binding.direccion.getText().toString() == "" &&
            binding.ocupacion.getText().toString() == "" &&
            binding.telefono.getText().toString() == ""){

            Toast.makeText(context,"NO AGREGASTE INFORMACION",Toast.LENGTH_LONG)
                .show()
            return false
        }
        return true


    }


    private fun guardarEnArchivo() {
        try{
            var archivo = OutputStreamWriter(
                binding.root.context.openFileOutput("datos.txt", Activity.MODE_PRIVATE))
            var bufferContenido = ""

            for(dato in listaDatosContactos){

                bufferContenido += dato + ","
            }

            bufferContenido = bufferContenido.substring(0,
                bufferContenido.lastIndexOf(","))

            archivo.write(bufferContenido)
            archivo.flush()
            archivo.close()

            Toast.makeText(context,"Se guardó correctamente.", Toast.LENGTH_LONG).show()

        }catch(e:Exception){

            AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(e.message)
                .setPositiveButton("Ok"){
                        d,i ->
                }
                .show()
        }
    }

    private fun abrirDesdeArchivo() {
        try {
            var archivo = BufferedReader(
                InputStreamReader(
                    binding.root.context.openFileInput("datos.txt")
                )
            )

            var bufferContenido = ""
            var interactivo = archivo.lineSequence().iterator()
            while (interactivo.hasNext()) {
                bufferContenido += interactivo.next() + "\n"
            }

            var vector = bufferContenido.split(",")

            for (v in vector) {
                listaDatosContactos.add(v)
            }
            binding.lista.adapter = ArrayAdapter<String>(
                binding.root.context,
                android.R.layout.simple_list_item_1, listaDatosContactos
            )


        } catch (e: Exception) {
            AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(e.message)
                .setPositiveButton("OK") { d, i -> }
                .show()

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPacientesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.insertar.setOnClickListener {
            insertar()

        }
        binding.guardar.setOnClickListener {
             guardarEnArchivo()
        }

        binding.lista.setOnItemClickListener { adapterView, view, i, l ->
            AlertDialog.Builder(context)
                .setTitle("ATENCION")
                .setMessage("¿Que deseas hacer? Con \n ${listaDatosContactos.get(i)}")
                .setPositiveButton("ELIMINAR"){d,f->
                    listaDatosContactos.removeAt(i)
                    binding.lista.adapter = ArrayAdapter<String>(binding.root.context,
                        android.R.layout.simple_list_item_1, listaDatosContactos)

                }
                .setNegativeButton("NADA"){d,i->}
                .setNeutralButton("ACTUALIZAR"){d,f->
                    posicionActualizar = i
                    var temporal = listaDatosContactos.get(i).split(",")
                    binding.nombrecompleto.setText(temporal[0])
                    binding.edad.setText(temporal[1])
                    binding.direccion.setText(temporal[2])
                    binding.ocupacion.setText(temporal[3])
                    binding.telefono.setText(temporal[4])
                }

                .show()
        }



        binding.actualizar.setOnClickListener {
            if(validarCampos()) {
                if (posicionActualizar == -1) {
                    AlertDialog.Builder(context)
                        .setTitle("Error")
                        .setMessage("No está en modo actualización")
                        .show()
                    return@setOnClickListener
                } else {
                    AlertDialog.Builder(context)
                        .setTitle("Confirmación")
                        .setMessage(
                            "Estas seguro que deseas actualizar a ${
                                listaDatosContactos.get(
                                    posicionActualizar
                                )
                            }"
                        )
                        .setPositiveButton("Si") { d, f ->
                            var concatenacion = binding.nombrecompleto.text.toString() + "\n" +
                                    binding.edad.text.toString() + "\n" +
                                    binding.direccion.text.toString() + "\n" +
                                    binding.ocupacion.text.toString() + "\n" +
                                    binding.telefono.text.toString()

                            listaDatosContactos.set(posicionActualizar, concatenacion)
                            binding.lista.adapter = ArrayAdapter<String>(
                                binding.root.context,
                                android.R.layout.simple_list_item_1, listaDatosContactos
                            )
                            binding.nombrecompleto.setText("")
                            binding.edad.setText("")
                            binding.direccion.setText("")
                            binding.ocupacion.setText("")
                            binding.telefono.setText("")
                            posicionActualizar = -1
                        }
                        .setNegativeButton("Cancelar") { d, f ->
                            posicionActualizar = -1
                            binding.nombrecompleto.setText("")
                            binding.edad.setText("")
                            binding.direccion.setText("")
                            binding.ocupacion.setText("")
                            binding.telefono.setText("")
                            d.cancel()
                        }
                        .show()
                }
            }


        }

        binding.mostrar.setOnClickListener {
            listaDatosContactos.clear()
            abrirDesdeArchivo()

        }
        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PacientesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PacientesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}