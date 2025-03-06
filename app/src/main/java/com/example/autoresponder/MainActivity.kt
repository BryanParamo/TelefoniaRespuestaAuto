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
import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.telecom.TelecomManager
import androidx.annotation.RequiresApi



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

        // Dentro de tu MainActivity, por ejemplo en onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            if (!roleManager.isRoleAvailable(RoleManager.ROLE_DIALER)) {
                // El rol de dialer no está disponible (poco probable en Android Q+)
            } else if (roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) {
                // Ya somos la aplicación de teléfono predeterminada
            } else {
                // Solicitar que el usuario establezca la app como teléfono predeterminado
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
                startActivityForResult(intent, 1001)
            }
        } else {
            // Para versiones anteriores a Android Q, usa el intent tradicional
            val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
            intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
            startActivityForResult(intent, 1001)
        }

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
        }

        // Agregar componentes al layout
        layout.addView(etPhone)
        layout.addView(etMessage)
        layout.addView(btnSave)

        // Establecer el layout como la vista principal de la actividad
        setContentView(layout)

        sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Cargar datos previamente guardados, si existen
        etPhone.setText(sharedPref.getString(KEY_PHONE, ""))
        etMessage.setText(sharedPref.getString(KEY_MESSAGE, ""))

        btnSave.setOnClickListener {
            val phone = etPhone.text.toString().trim()
            val message = etMessage.text.toString().trim()

            if (phone.isNotEmpty() && message.isNotEmpty()) {
                // Guardar datos en SharedPreferences
                sharedPref.edit().apply {
                    putString(KEY_PHONE, phone)
                    putString(KEY_MESSAGE, message)
                    apply()
                }
                Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
