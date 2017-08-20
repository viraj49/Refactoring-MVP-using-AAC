package tank.viraj.without.aac

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Created by Viraj Tank, 15-08-2017.
 */
class MyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout, MyFragment())
                    .commit()
        }
    }
}