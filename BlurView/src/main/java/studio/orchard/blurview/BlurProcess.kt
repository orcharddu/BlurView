@file:JvmName("BlurProcess")
package studio.orchard.blurview

import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.View
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BlurProcess(private val targetView: View, private val blurView: View) {
    private var handler = Handler(thread.looper)
    private val canvas = Canvas()
    private val paint = Paint()
    private val rectF = RectF()
    var name = "Default"
    var mask: Drawable? = null
    var radius = 30f
    var scaling = 0.3f
    var roundRectRadiusX = 0f
    var roundRectRadiusY = 0f

    @Volatile var blurredBitmap: Bitmap? = null
    @Volatile var targetBitmap: Bitmap? = null
    @Volatile var isAttached = true

    companion object {
        var thread = HandlerThread("BlurView", 10)

        init {
            thread.start()
        }
    }

    fun onSizeChanged(w: Int, h: Int) {
        if(blurView.width > 0 && blurView.height > 0) {
            targetBitmap = Bitmap.createBitmap((w * scaling).toInt(), (h * scaling).toInt(), Bitmap.Config.ARGB_8888)
            canvas.setBitmap(targetBitmap)
        }
    }

    fun preDraw(): Boolean {
        if(isAttached && targetView.isDirty && blurView.visibility == View.VISIBLE
            && blurView.width > 0 && blurView.height > 0) {
            targetBitmap?.eraseColor(Color.WHITE)?: return true
            val targetLocation = IntArray(2)
            targetView.getLocationInWindow(targetLocation)
            val blurLocation = IntArray(2)
            blurView.getLocationInWindow(blurLocation)
            canvas.save()
            canvas.translate((targetLocation[0] - blurLocation[0]) * scaling,
                (targetLocation[1] - blurLocation[1]) * scaling)
            canvas.scale(scaling, scaling)
            targetView.draw(canvas)
            canvas.restore()
            handler.post(Task(this, targetBitmap!!, radius))
            blurView.invalidate()
        }
        return true
    }

    fun draw(canvas: Canvas) {
        canvas.save()
        if(blurredBitmap != null) {
            canvas.scale(blurView.width.toFloat() / blurredBitmap!!.width,blurView.height.toFloat() / blurredBitmap!!.height)
            if(roundRectRadiusX == 0f && roundRectRadiusY == 0f) {
                canvas.drawBitmap(blurredBitmap!!, 0f, 0f, null)
            } else {
                paint.shader = BitmapShader(blurredBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                rectF.set(0f, 0f, blurredBitmap!!.width.toFloat(), blurredBitmap!!.height.toFloat())
                canvas.drawRoundRect(rectF, roundRectRadiusX, roundRectRadiusY, paint)
            }
            mask?.setBounds(0, 0, targetBitmap!!.width, targetBitmap!!.height)
            mask?.draw(canvas)
        } else {
            mask?.setBounds(0, 0, blurView.width, blurView.height)
            mask?.draw(canvas)
        }
        canvas.restore()

    }

    class Task(private val blurProcess: BlurProcess, private val bitmap: Bitmap, private val radius: Float): Runnable {
        override fun run() {
            val result: Bitmap? = ThreadsController.process(bitmap, radius)
            blurProcess.blurredBitmap = result
            blurProcess.blurView.postInvalidate()
        }
    }

    object ThreadsController {
        var threads: Int = Runtime.getRuntime().availableProcessors()
            set(threads) {
                field = threads
                executor.shutdown()
                executor = Executors.newFixedThreadPool(threads)
            }
        var executor: ExecutorService = Executors.newFixedThreadPool(threads)
            private set
        init {
            Log.d("TAG_", "hiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii")
        }
        fun process(bitmap: Bitmap, radius: Float): Bitmap? = StackBlur.blur(bitmap, radius, this)
    }

}