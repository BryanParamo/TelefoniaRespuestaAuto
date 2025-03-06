package com.example.autoresponder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log

class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.PHONE_STATE") {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

            // Solo actuamos si el teléfono está sonando
            if (TelephonyManager.EXTRA_STATE_RINGING == state) {
                // Intentamos obtener el número entrante, aunque en Android moderno suele ser null
                val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                Log.d("CallReceiver", "Número entrante: $incomingNumber")

                // Obtener el número y el mensaje configurados por el usuario
                val sharedPref = context.getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE)
                val storedPhone = sharedPref.getString(MainActivity.KEY_PHONE, "")
                val autoMessage = sharedPref.getString(MainActivity.KEY_MESSAGE, "")

                // Si se ha configurado un número, enviamos el SMS al número almacenado
                if (!storedPhone.isNullOrEmpty()) {
                    // Si el número entrante coincide con el número configurado, o es null (por restricciones de Android),
                    // procedemos a enviar el mensaje.
                    if (incomingNumber == storedPhone || incomingNumber == null) {
                        Log.d("CallReceiver", "Enviando SMS automático al número configurado...")
                        try {
                            val smsManager = SmsManager.getDefault()
                            smsManager.sendTextMessage(storedPhone, null, autoMessage, null, null)
                            Log.d("CallReceiver", "SMS enviado correctamente")
                        } catch (e: Exception) {
                            Log.e("CallReceiver", "Error al enviar el SMS: ${e.message}")
                        }
                    }
                }
            }
        }
    }
}
