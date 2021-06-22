@file:JvmName("BlurViewKT")

package studio.orchard.blurview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import kotlin.math.roundToInt

@SuppressLint("ViewConstructor")
class BlurView : View {
    private val _context: Context
    private var enable = false
    private var target: View? = null
    private var binding: View? = null
    private var blurProcess: BlurProcess? = null
    var name = "Default"
    var mask: Drawable? = defaultMask
        private set
    var opacity = defaultOpacity
        private set
    var radius = defaultRadius
        private set
    var scaling = defaultScaling
        private set
    var roundRectRadiusX = 0f
        private set
    var roundRectRadiusY = 0f
        private set

    constructor(_context: Context): super(_context) {
        this._context = _context
    }

    constructor(_context: Context, _attr: AttributeSet): super(_context, _attr) {
        this._context = _context
    }

    constructor(_context: Context, _attr: AttributeSet, _i: Int): super(_context, _attr, _i) {
        this._context = _context
    }


    companion object {
        @JvmField public val defaultMask = ColorDrawable(Color.parseColor("#C0FFFFFF"))
        @JvmField public val defaultRadius = 30f
        @JvmField public val defaultScaling = 0.3f
        @JvmField public val defaultOpacity = 1f

        @JvmStatic
        public fun process(bitmap: Bitmap): Bitmap {
            return process(bitmap, defaultRadius, defaultScaling, defaultMask)
        }

        @JvmStatic
        public fun process(bitmap: Bitmap, mask: Drawable?): Bitmap {
            return process(bitmap, defaultRadius, defaultScaling, mask)
        }

        @JvmStatic
        public fun process(bitmap: Bitmap, radius: Float, scaling: Float, mask: Drawable?): Bitmap {
            return process(bitmap, radius, (bitmap.width * scaling).roundToInt(), (bitmap.height * scaling).roundToInt(), mask);
        }

        @JvmStatic
        public fun process(bitmap: Bitmap, radius: Float, width: Int, height: Int, mask: Drawable?): Bitmap {
            val canvas = Canvas()
            var output = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            output = Bitmap.createScaledBitmap(output, width, height, false)
            val blurredBitmap: Bitmap? = BlurProcess.ThreadsController.process(output, radius)
            canvas.setBitmap(output)
            canvas.drawBitmap(blurredBitmap!!, 0f, 0f, null)
            mask?.setBounds(0, 0, output.width, output.height)
            mask?.draw(canvas)
            blurredBitmap.recycle()
            return output
        }

    }


    // unchangeable after enable

    fun setTarget(target: View): BlurView {
        if(enable) return this
        this.target = target
        return this
    }

    fun setBinding(binding: View): BlurView {
        if(enable) return this
        this.binding = binding
        return this
    }

    fun setScaling(scaling: Float): BlurView {
        if(enable) return this
        this.scaling = scaling
        return this
    }

    // changeable after enable

    fun setMask(mask: Drawable?): BlurView {
        this.mask = mask
        blurProcess?.mask = mask
        target?.invalidate()
        return this
    }

    fun setRadius(radius: Float): BlurView {
        this.radius = radius
        blurProcess?.radius = radius
        target?.invalidate()
        return this
    }

    fun setOpacity(alpha: Float): BlurView {
        this.opacity = alpha
        super.setAlpha(alpha)
        return this
    }

    fun setName(str: String): BlurView {
        name = str
        blurProcess?.name = str
        return this
    }

    fun setRoundRectRadiusX(radius: Float): BlurView {
        roundRectRadiusX = radius
        blurProcess?.roundRectRadiusX = radius
        target?.invalidate()
        return this
    }

    fun setRoundRectRadiusY(radius: Float): BlurView {
        roundRectRadiusY = radius
        blurProcess?.roundRectRadiusY = radius
        target?.invalidate()
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


