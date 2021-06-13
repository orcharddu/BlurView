@file:JvmName("BlurProcess")
package studio.orchard.blurview

import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.View
import android.view.ViewGroup
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BlurProcess(private val targetView: View, private val blurView: View) {
    var handler = Handler(thread.looper)
    private val canvas = Canvas()
    private val paint = Paint()
    private val rectF = RectF()
    var name = "Default"
    var mask: Drawable? = null
    var radius = 30f
    var scaling = 0.3f
    var blurEraseColor = -1
    var roundRectRadiusX = 0f
    var roundRectRadiusY = 0f
    var isDrawCanvas = false

    var viewList: MutableList<View> = mutableListOf()
    @Volatile var blurredBitmap: Bitmap? = null
    @Volatile var targetBitmap: Bitmap? = null
    @Volatile var isDrawing = true
    @Volatile var isAttached = true

    companion object {
        var thread = HandlerThread("BlurView", 10);
        init {
            thread.start()
        }

    }

    fun start() {
        isDrawing = true
    }

    fun attach() {
        isAttached = true
    }

    fun detach() {
        isAttached = false
    }

    fun destroy() {
        //need implement
    }



    var iss = false

    fun onBlurViewSizeChanged() {
        if (iss) return

        // Specify a bitmap for the canvas to draw into.

        iss = true
    }

    var first = false

    fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if(blurView.width > 0 && blurView.height > 0) {
            targetBitmap = Bitmap.createBitmap((w * scaling).toInt(), (h * scaling).toInt(), Bitmap.Config.ARGB_8888)
            canvas.setBitmap(targetBitmap)
        }
    }

    fun preDraw(): Boolean {
        if(isAttached && targetView.isDirty && blurView.visibility == View.VISIBLE
            && blurView.width > 0 && blurView.height > 0) {
            preDrawCanvas()
            blurView.invalidate()
        }
        return true
    }

    private fun preDrawCanvas() {
        targetBitmap?.eraseColor(Color.WHITE)?:return
        Log.d("TAG_$name", "${blurView.width}  ${blurView.height}")
        val targetLocation = IntArray(2)
        targetView.getLocationInWindow(targetLocation)
        val blurLocation = IntArray(2)
        blurView.getLocationInWindow(blurLocation)
        canvas.save()
        val dx = (targetLocation[0] - blurLocation[0]) * scaling
        val dy = (targetLocation[1] - blurLocation[0]) * scaling
        canvas.translate(dx, dy)
        canvas.scale(scaling, scaling)
        isDrawCanvas = true
        // draw targetView into canvas
        targetView.draw(canvas)

        canvas.restore()
        clearViewVisible()

        isDrawCanvas = false;
        handler.post(Task(this, targetBitmap!!, radius))
    }

    fun draw(canvas: Canvas?) {
        blurredBitmap?: return
        canvas?.save()


        canvas?.scale(blurView.width.toFloat() / blurredBitmap!!.width,
            blurView.height.toFloat() / blurredBitmap!!.height)

        if(roundRectRadiusX == 0f && roundRectRadiusY == 0f) {
            canvas?.drawBitmap(blurredBitmap!!, 0f, 0f, null)
        } else {
            paint.shader = BitmapShader(blurredBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            rectF.set(0f, 0f, blurredBitmap!!.width.toFloat(), blurredBitmap!!.height.toFloat())
            canvas?.drawRoundRect(rectF, roundRectRadiusX, roundRectRadiusY, paint)
        }

        mask?.setBounds(0, 0, blurredBitmap!!.width, blurredBitmap!!.height)
        mask?.draw(canvas!!)
        canvas?.restore()

    }



    class Task(val handler: BlurProcess, val bitmap: Bitmap, val radius: Float): Runnable {

        override fun run() {
            if(handler.isDrawCanvas) return
            val result: Bitmap? = ThreadsController.process(bitmap, radius)
            //need mutex lock here
            handler.blurredBitmap = result
            if(handler.isDrawing) handler.blurView.postInvalidate()
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
        fun process(bitmap: Bitmap, radius: Float): Bitmap? = StackBlur.blur(bitmap, radius, this)
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private fun clearViewVisible() {
        for (view in viewList) {
            view.visibility = View.VISIBLE
        }
    }

    fun clear() {
        viewList.clear()
        listView(targetView.rootView, viewList)
    }

    private fun listView(view: View?, list: MutableList<View>) {
        if (view != null && view.visibility == View.VISIBLE) {
            list.add(view)
            view.visibility = View.VISIBLE
            if (view is ViewGroup) {
                val childCount = view.childCount
                for (i in 0 until childCount) {
                    listView(view.getChildAt(i), list)
                }
            }
        }
    }

}