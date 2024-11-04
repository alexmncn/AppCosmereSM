package com.example.appsandersonsm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class DetallesLibroActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var progressTextView: TextView
    private lateinit var iniciarButton: Button
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles_libro)

        progressBar = findViewById(R.id.progressBar)
//        progressTextView = findViewById(R.id.progressTextView)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

//        updateProgress(50)

//        iniciarButton.setOnClickListener {
//            val newProgress = progressBar.progress + 10
//            if (newProgress <= progressBar.max) {
//                updateProgress(newProgress)
//            }
//        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_settings -> {
                    startActivity(Intent(this, AjustesActivity::class.java))
                    true
                }
                R.id.nav_map -> {
                    startActivity(Intent(this, MapaInteractivoActivity::class.java))
                    true
                }
                R.id.nav_book -> true
                else -> false
            }
        }

        bottomNavigationView.menu.findItem(R.id.nav_book).isChecked = true
    }

//    private fun updateProgress(progress: Int) {
//        progressBar.progress = progress
//        progressTextView.text = "$progress%"
//    }
}
