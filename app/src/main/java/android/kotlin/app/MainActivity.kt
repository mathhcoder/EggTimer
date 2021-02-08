package android.kotlin.app

import android.kotlin.app.ui.EggTimerFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.lang.reflect.Array.newInstance

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, EggTimerFragment.newInstance())
                .commitNow()
        }
    }
}