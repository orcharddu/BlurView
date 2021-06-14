package studio.orchard.blurviewexample

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.AppBarLayout
import studio.orchard.blurview.BlurView
import studio.orchard.blurviewexample.example.RecyclerViewActivity


class MainActivity : BaseActivity() {

    lateinit var blurView: BlurView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setStatusBarNavigationBar()
        val appBar: AppBarLayout = findViewById(R.id.main_appbar)
        val toolbar: Toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        title = "BlurView Example"

        blurView = findViewById(R.id.main_blurview)
        val targetView = findViewById<FrameLayout>(R.id.main_targetview)
        blurView.setTarget(targetView).setBinding(targetView)
            .setRadius(40f).setScaling(0.3f).setName("MainActivity").enable()

        initView()
    }


    @SuppressLint("SetTextI18n")
    fun initView() {
        val button_1 = findViewById<Button>(R.id.main_example_button_1)
        button_1.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, RecyclerViewActivity::class.java)
            startActivity(intent)
        }

        val button_2 = findViewById<Button>(R.id.main_example_button_2)
        button_2.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, RecyclerViewActivity::class.java)
            startActivity(intent)
        }

        val opacityTextView = findViewById<TextView>(R.id.main_setting_opacity)
        val opacitySeekBar = findViewById<SeekBar>(R.id.main_setting_opacity_seekbar)
        opacityTextView.text = "OPACITY ${blurView.opacity}"
        opacitySeekBar.progress= (blurView.opacity * 100).toInt()
        opacitySeekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    blurView.setOpacity(progress / 100f)
                    opacityTextView.text = "OPACITY ${blurView.opacity}"
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) { }
                override fun onStopTrackingTouch(seekBar: SeekBar?) { }
            }
        )

        val radiusTextView = findViewById<TextView>(R.id.main_setting_radius)
        val radiusSeekBar = findViewById<SeekBar>(R.id.main_setting_radius_seekbar)
        radiusTextView.text = "BLUR RADIUS ${blurView.radius}f"
        radiusSeekBar.progress= (blurView.radius).toInt()
        radiusSeekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    radiusTextView.text = "BLUR RADIUS ${progress.toFloat()}f"
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) { }
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    blurView.setRadius(seekBar?.progress!!.toFloat())
                }
            }
        )

        val maskTextView = findViewById<TextView>(R.id.main_setting_mask)
        val maskSeekBarA = findViewById<SeekBar>(R.id.main_setting_mask_seekbar_a)
        val maskSeekBarR = findViewById<SeekBar>(R.id.main_setting_mask_seekbar_r)
        val maskSeekBarG = findViewById<SeekBar>(R.id.main_setting_mask_seekbar_g)
        val maskSeekBarB = findViewById<SeekBar>(R.id.main_setting_mask_seekbar_b)
        
        maskSeekBarA.progress = (blurView.mask as ColorDrawable).color shr 24 and 0xff
        maskSeekBarR.progress = (blurView.mask as ColorDrawable).color shr 16 and 0xff
        maskSeekBarG.progress = (blurView.mask as ColorDrawable).color shr 8 and 0xff
        maskSeekBarB.progress = (blurView.mask as ColorDrawable).color and 0xff
        maskTextView.text = "MASK A${maskSeekBarA.progress} R${maskSeekBarR.progress} G${maskSeekBarG.progress} B${maskSeekBarB.progress}"

        maskSeekBarA.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    maskTextView.text = "MASK A${maskSeekBarA.progress} R${maskSeekBarR.progress} G${maskSeekBarG.progress} B${maskSeekBarB.progress}"
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) { }
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    blurView.setMask(ColorDrawable(
                        Color.argb(maskSeekBarA.progress, maskSeekBarR.progress, maskSeekBarG.progress, maskSeekBarB.progress)))
                }
            }
        )

        maskSeekBarR.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    maskTextView.text = "MASK A${maskSeekBarA.progress} R${maskSeekBarR.progress} G${maskSeekBarG.progress} B${maskSeekBarB.progress}"
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) { }
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    blurView.setMask(ColorDrawable(
                        Color.argb(maskSeekBarA.progress, maskSeekBarR.progress, maskSeekBarG.progress, maskSeekBarB.progress)))
                }
            }
        )

        maskSeekBarG.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    maskTextView.text = "MASK A${maskSeekBarA.progress} R${maskSeekBarR.progress} G${maskSeekBarG.progress} B${maskSeekBarB.progress}"
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) { }
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    blurView.setMask(ColorDrawable(
                        Color.argb(maskSeekBarA.progress, maskSeekBarR.progress, maskSeekBarG.progress, maskSeekBarB.progress)))
                }
            }
        )

        maskSeekBarB.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    maskTextView.text = "MASK A${maskSeekBarA.progress} R${maskSeekBarR.progress} G${maskSeekBarG.progress} B${maskSeekBarB.progress}"
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) { }
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    blurView.setMask(ColorDrawable(
                        Color.argb(maskSeekBarA.progress, maskSeekBarR.progress, maskSeekBarG.progress, maskSeekBarB.progress)))
                }
            }
        )
    }

}