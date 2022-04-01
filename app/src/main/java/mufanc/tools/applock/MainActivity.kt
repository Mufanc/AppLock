package mufanc.tools.applock

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (MyApplication.isModuleActivated) {
            Toast.makeText(this, "Module Activated!", Toast.LENGTH_SHORT).show()
        }
    }
}