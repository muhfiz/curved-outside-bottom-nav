package curved.outside.curved_outside_bottom_nav

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import kotlin.math.abs


class CurvedOutsideBottomNav @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val path = Path()
    private val paint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.MAGENTA
        isAntiAlias = true
    }

    /**
     * set how the arc work for side that curved in each item
     *
     */
    private var curveRatio = 0.2f
    private val items = ArrayList<CurvedOutsideBotNavItem>()

    /**
     * Set to true when you want to use the close line in the top
     * of the selected item
     */
    var closeLine = true
        set(value) {
            field = value;
            invalidate()
        }

    /**
     * set the duration of animation when changes the position
     * per pixels change
     */
    var transitionRatioDurationPerPixels = 1.5f

    var closeLineWidth = context.resources.displayMetrics.density * 1
    private var fromX = 0f
    private var currentX = 0f
    private var destinationX = 0f
    private val transitionAnimation by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = (abs(fromX - destinationX) * transitionRatioDurationPerPixels).toLong()
            interpolator = OvershootInterpolator()
            addUpdateListener {
                currentX = fromX + (it.animatedValue as Float) *
                        (-1 * (fromX - destinationX))
                this@CurvedOutsideBottomNav.invalidate()
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "onAnimationStart: ")
                    }
                }

                override fun onAnimationEnd(p0: Animator?) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "onAnimationEnd: ")
                    }
                    fromX = currentX
                }

                override fun onAnimationCancel(p0: Animator?) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "onAnimationCancel: ")
                    }
                }

                override fun onAnimationRepeat(p0: Animator?) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "onAnimationRepeat: ")
                    }
                }

            })
        }

    }

    init {
        var currentPos = 1;
        setOnClickListener {
            moveAnchorTo(currentPos)
            currentPos++;
        }
    }

    fun setItems(vararg item: CurvedOutsideBotNavItem) {
        items.addAll(item)
    }

    private fun moveAnchorTo(pos: Int) {
        val anchors = getAnchorForTransition(pos)
        fromX = anchors[0]
        destinationX = anchors[1]
        transitionAnimation.start()
    }

    /**
     *
     * @return array for fromX and destinationX
     */
    private fun getAnchorForTransition(pos: Int): FloatArray {
        val itemWidth = getItemWidth()
        val fromX = itemWidth * (pos - 1)
        return floatArrayOf(fromX.toFloat(), (fromX + itemWidth).toFloat())
    }

    private fun getItemWidth(): Int {
        return ((measuredWidth - paddingLeft - paddingRight) / items.size.toFloat()).toInt()
    }

    override fun onDraw(canvas: Canvas?) {
        path.reset()
        generatePathIn()
        canvas?.drawPath(path, paint)
        super.onDraw(canvas)
    }

    private fun generatePathIn() {
        var eachWidth = getItemWidth().toFloat()
        val arcCircleWidth = eachWidth * curveRatio
        path.moveTo(0f, 0f)
        generateArcs(
            currentX, eachWidth,
            arcCircleWidth,
            arcCircleWidth * 1.1f
        )
        path.lineTo(measuredWidth.toFloat(), 0f)
        path.lineTo(measuredWidth.toFloat(), measuredHeight.toFloat())
        path.lineTo(0f, measuredHeight.toFloat())
        path.close()

        if (closeLine) {
            path.moveTo(0f, 0f)
            path.lineTo(measuredWidth.toFloat(), 0f)
            path.rLineTo(0f, closeLineWidth)
            path.lineTo(0f, closeLineWidth)
            path.close()
        }
    }

    /**
     * call to populate the path with the arcs
     */
    fun generateArcs(
        startX: Float, width: Float,
        arcCircleWidth: Float,
        arcCircleHeight: Float,
        padBottom: Float = 10f
    ) {
        var x = startX
        path.arcTo(
            x - arcCircleWidth / 2f, 0f,
            x + arcCircleWidth / 2f, arcCircleHeight,
            270f, 90f, false
        )
        path.arcTo(
            x + arcCircleWidth / 2, measuredHeight - arcCircleHeight - padBottom,
            x + arcCircleWidth / 2 + arcCircleWidth, measuredHeight.toFloat() - padBottom,
            180f, -90f, false
        )

        x += width - arcCircleWidth / 2
        path.arcTo(
            x - arcCircleWidth,
            measuredHeight - arcCircleHeight - padBottom,
            x, measuredHeight.toFloat() - padBottom,
            90f, -90f, false
        )
        path.arcTo(
            x, 0f,
            x + arcCircleWidth,
            arcCircleHeight,
            180f, 90f, false
        )


    }


    companion object {
        private const val TAG = "CurvedOutsideBottomNav"
        private const val SELECTED_ITEM_KEY = "idAnchored"
        private const val SUPER_SAVE_STATE_KEY = "superSaveState"
    }
}