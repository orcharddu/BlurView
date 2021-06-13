@file:JvmName("BlurViewKT")

package studio.orchard.blurview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver

@SuppressLint("ViewConstructor")
class BlurView : View {
    private val _context: Context
    private var enable = false
    var name = "Default"
    private lateinit var target: View
    private lateinit var binding: View
    private lateinit var blurProcess: BlurProcess
    private var mask: Drawable? = ColorDrawable(Color.parseColor("#C0FFFFFF"))
    private var radius: Float = 30f
    private var scaling: Float = 0.3f

    constructor(_context: Context): super(_context) {
        this._context = _context
    }

    constructor(_context: Context, _attr: AttributeSet): super(_context, _attr) {
        this._context = _context
    }

    constructor(_context: Context, _attr: AttributeSet, _i: Int): super(_context, _attr, _i) {
        this._context = _context
    }


    fun setTarget(target: View): BlurView {
        this.target = target
        return this
    }

    fun setBinding(binding: View): BlurView {
        this.binding = binding
        return this
    }

    fun setMask(mask: Drawable?): BlurView {
        this.mask = mask
        return this
    }

    fun setRadius(radius: Float): BlurView {
        this.radius = radius
        return this
    }

    fun setScaling(scaling: Float): BlurView {
        this.scaling = scaling
        return this
    }

    fun setName(str: String): BlurView {
        name = str
        return this
    }


    fun enable() {
        if(!::target.isInitialized) throw IllegalArgumentException("Target view should be initialized")
        if(!::binding.isInitialized) throw IllegalArgumentException("Binding view should be initialized")
        // remove background of bindingView
        binding.setBackgroundColor(Color.TRANSPARENT)
        // set blurView size by bindingView
        bindingViewGlobalLayout { width, height ->
            val lp = layoutParams
            lp.width = width
            lp.height = height
            layoutParams = lp
            // trigger "targetView.isDirty" in preDraw() since the
            target.postInvalidate()
        }

        blurProcess = BlurProcess(target, this)
        blurProcess.name = name
        blurProcess.mask = mask
        blurProcess.scaling = scaling
        blurProcess.radius = radius
        viewTreeObserver.addOnPreDrawListener {
            blurProcess.preDraw()
        }

        enable = true

    }

    private inline fun bindingViewGlobalLayout(crossinline callback: (Int, Int) -> Unit) {
        binding.viewTreeObserver.addOnGlobalLayoutListener (
            object: ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    callback(binding.width, binding.height)
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        blurProcess.onSizeChanged(w, h, oldw, oldh)
        target.invalidate()

        super.onSizeChanged(w, h, oldw, oldh)
    }



    override fun onDraw(canvas: Canvas) {
//        if (!isDrawCanvas()) {
            if (enable) {

                blurProcess.draw(canvas)
                super.onDraw(canvas)
                return
            } else {
                mask?.setBounds(0, 0, width, height)
                mask?.draw(canvas)
            }
            super.onDraw(canvas)

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    fun isDrawCanvas(): Boolean {
        return blurProcess.isDrawCanvas
    }

    override fun draw(canvas: Canvas?) {
        if (isDrawCanvas()) {
            blurProcess.clear()
        } else {
            super.draw(canvas)
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        if (!isDrawCanvas()) {
            super.dispatchDraw(canvas)
        }
    }

}


