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
    private var target: View? = null
    private var binding: View? = null
    private var blurProcess: BlurProcess? = null
    private var mask: Drawable? = ColorDrawable(Color.parseColor("#C0FFFFFF"))
    private var radius = 30f
    private var scaling = 0.3f
    private var roundRectRadiusX = 0f
    private var roundRectRadiusY = 0f

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

    fun setOpacity(alpha: Float): BlurView {
        super.setAlpha(alpha)
        return this
    }

    fun setName(str: String): BlurView {
        name = str
        return this
    }

    fun setRoundRectRadiusX(radius: Float): BlurView {
        roundRectRadiusX = radius
        return this
    }

    fun setRoundRectRadiusY(radius: Float): BlurView {
        roundRectRadiusY = radius
        return this
    }

    fun enable() {
        if(enable) return
        target?: throw IllegalArgumentException("Target view should be initialized")
        binding?: throw IllegalArgumentException("Binding view should be initialized")
        // remove background of bindingView
        binding?.setBackgroundColor(Color.TRANSPARENT)
        // set blurView size by bindingView
        bindingViewGlobalLayout { width, height ->
            val lp = layoutParams
            lp.width = width
            lp.height = height
            layoutParams = lp
        }
        blurProcess = BlurProcess(target!!, this)
        blurProcess?.name = name
        blurProcess?.mask = mask
        blurProcess?.scaling = scaling
        blurProcess?.radius = radius
        blurProcess?.roundRectRadiusX = roundRectRadiusX
        blurProcess?.roundRectRadiusY = roundRectRadiusY
        viewTreeObserver.addOnPreDrawListener {
            blurProcess!!.preDraw()
        }
        /*
        target.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            blurProcess.onTargetSizeChanged(v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom)
        }
        */
        enable = true
    }

    private inline fun bindingViewGlobalLayout(crossinline callback: (Int, Int) -> Unit) {
        binding?.viewTreeObserver?.addOnGlobalLayoutListener (
            object: ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    callback(binding!!.width, binding!!.height)
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        blurProcess?.onSizeChanged(w, h)
        target?.invalidate()
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        if(enable) {
            blurProcess?.draw(canvas)
            super.onDraw(canvas)
            return
        } else {
            mask?.setBounds(0, 0, width, height)
            mask?.draw(canvas)
        }
        super.onDraw(canvas)
    }

    override fun onAttachedToWindow() {
        blurProcess?.isAttached = true
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        blurProcess?.isAttached = false
        super.onDetachedFromWindow()
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
    }
}


