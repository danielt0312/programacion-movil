package com.iti

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    var BotonHola: Button? = null
    var EditTextUsuario: EditText? = null
    var temp: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        BotonHola = findViewById(R.id.boton1)
        EditTextUsuario = findViewById(R.id.editText1)
        temp = findViewById(R.id.textViewLista)
        temp?.setText("*******")

        BotonHola?.setOnClickListener(View.OnClickListener {
            temp?.append(
                """

                    ${EditTextUsuario?.getText()}
                    """.trimIndent()
            )
            EditTextUsuario?.setText("")
            Toast.makeText(
                applicationContext,
                "Hola " + EditTextUsuario?.getText().toString(),
                Toast.LENGTH_SHORT
            ).show()
        })

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v: View, insets: WindowInsetsCompat ->
            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}