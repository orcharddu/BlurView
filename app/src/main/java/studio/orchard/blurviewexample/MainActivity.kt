package studio.orchard.blurviewexample

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
        blurView.setTarget(targetView).setBinding(targetView)
            .setMask(ColorDrawable(Color.parseColor("#64B4B4B4")))
            .setRadius(40f).setScaling(0.3f).setName("MainActivity").enable()

        blurView.setTarget(targetView).setBinding(targetView)
            .setMask(ColorDrawable(Color.parseColor("#64B4B4B4")))
            .setRadius(100f).setScaling(0.3f).setName("MainActivity").setOpacity(0.8f).enable()





        val button_1 = findViewById<Button>(R.id.main_button_1)
        button_1.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, RecyclerViewActivity::class.java)
            startActivity(intent)
        }

        val button_2 = findViewById<Button>(R.id.main_button_2)
        button_2.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, RecyclerViewActivity::class.java)
            startActivity(intent)
        }

    }

}