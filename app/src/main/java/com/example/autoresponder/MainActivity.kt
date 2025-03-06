package com.example.autoresponder

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var etPhone: EditText
    private lateinit var etMessage: EditText
    private lateinit var btnSave: Button
    private lateinit var sharedPref: SharedPreferences

    companion object {
        const val PREFS_NAME = "AutoResponderPrefs"
        const val KEY_PHONE = "phone_number"
        const val KEY_MESSAGE = "auto_message"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Creación del layout de forma programática
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Crear y configurar los componentes
        etPhone = EditText(this).apply {
            hint = "Número de teléfono"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        etMessage = EditText(this).apply {
            hint = "Mensaje de respuesta"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        btnSave = Button(this).apply {
            text = "Guardar"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                val phone = etPhone.text.toString().trim()
                val message = etMessage.text.toString().trim()

                // Validación simple: solo dígitos y longitud entre 7 y 15 (puedes ajustar según tus necesidades)
                val phoneRegex = Regex("^\\d{7,15}\$")
                if (phone.isNotEmpty() && message.isNotEmpty() && phoneRegex.matches(phone)) {
                    // Guardar datos en SharedPreferences
                    sharedPref.edit().apply {
                        putString(KEY_PHONE, phone)
                        putString(KEY_MESSAGE, message)
                        apply()
                    }
                    Toast.makeText(this@MainActivity, "Datos guardados", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Complete todos los campos con datos válidos", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Agregar componentes al layout
        layout.addView(etPhone)
        layout.addView(etMessage)
        layout.addView(btnSave)

        // Establecer el layout como la vista principal de la actividad
        setContentView(layout)

        // Inicializar SharedPreferences
        sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Cargar datos previamente guardados, si existen
        etPhone.setText(sharedPref.getString(KEY_PHONE, ""))
        etMessage.setText(sharedPref.getString(KEY_MESSAGE, ""))
    }
}
