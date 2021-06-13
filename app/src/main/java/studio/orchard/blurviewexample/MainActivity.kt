package studio.orchard.blurviewexample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.AppBarLayout
import studio.orchard.blurview.BlurView
import studio.orchard.blurviewexample.example.RecyclerViewActivity


class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setStatusBarNavigationBar()
        val appBar: AppBarLayout = findViewById(R.id.main_appbar)
        val toolbar: Toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        title = "BlurView Example"

        val blurView = findViewById<BlurView>(R.id.main_blurview)
        val targetView = findViewById<FrameLayout>(R.id.main_targetview)
        blurView.setTarget(targetView).setBinding(targetView).setName("MainActivity").enable()

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, RecyclerViewActivity::class.java)
            startActivity(intent)
        }
    }

}