package curved.outside.curved_outside_bottom_nav

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.OvershootInterpolator
import androidx.annotation.DrawableRes
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
    private val textPaint = TextPaint().apply {
        isAntiAlias = true
        textSize = 30f
        color = Color.YELLOW
    }
    private var onItemSelectedListener: ((index: Int) -> Unit)? = null
    private var initialTouchX = 0f

    /**
     * the space within the anchor bottom and the view bottom
     */
    private var anchorPaddingBottom: Float = context.resources.displayMetrics.density * 5

    /**
     * set how the arc work for side that curved in each item
     *
     */
    private var curveRatio = 0.2f
    private val items = ArrayList<Item>()

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
    var transitionRatioDurationPerPixels = 1.25f

    private var cacheTextLayouts = ArrayList<StaticLayout>()

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

    fun setTextSize(size: Float) {
        if (size == textPaint.textSize) return
        textPaint.textSize = size
        invalidate()
    }

    /**
     *
     * @param item vararg of items
     * @param onItemSelectedListener the selectedListener that wil lbe called when item's selected
     */
    fun setItems(vararg item: Item, onItemSelectedListener: ((index: Int) -> Unit)? = null) {
        cacheTextLayouts.clear()
        items.addAll(item)
        this.onItemSelectedListener = onItemSelectedListener
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.apply {
            when (actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    initialTouchX = this.x
                }
                MotionEvent.ACTION_UP -> {
                    val selectedItemIndex = (initialTouchX / getItemWidth()).toInt()
                    val itemWidth = getItemWidth()
                    if (
                        this.x >= selectedItemIndex * itemWidth
                        && this.x <= (selectedItemIndex + 1) * itemWidth
                    ) {
                        selectItem(selectedItemIndex)
                    } else return false
                }
            }
        }

        return true
    }

    private fun selectItem(index: Int) {
        onItemSelectedListener?.invoke(index)
        moveAnchorTo(index)
    }

    private fun moveAnchorTo(index: Int) {
        val anchors = getAnchorForTransition(index)
        fromX = anchors[0]
        destinationX = anchors[1]
        transitionAnimation.start()
    }

    /**
     *
     * @return array for fromX and destinationX
     */
    private fun getAnchorForTransition(index: Int): FloatArray {
        val itemWidth = getItemWidth()
        val fromX = currentX
        return floatArrayOf(fromX, itemWidth.toFloat() * (index))
    }

    private fun getItemWidth(): Int {
        return ((measuredWidth - paddingLeft - paddingRight) / items.size.toFloat()).toInt()
    }

    override fun onDraw(canvas: Canvas?) {
        path.reset()
        generatePathIn()
        canvas?.apply {
            drawPath(path, paint)
            drawTitleAndIcon(canvas)
        }
        super.onDraw(canvas)
    }

    private fun drawTitleAndIcon(canvas: Canvas) {
        val itemWidth = getItemWidth().toFloat()
        val padHorCausedByCurve = itemWidth * (curveRatio / 2f)
        canvas.save()
        items.forEachIndexed { index, item ->
            var textLayout = try {
                cacheTextLayouts[index]
            } catch (e: IndexOutOfBoundsException) {
                getDefaultStaticLayout(
                    item.title,
                    (itemWidth - padHorCausedByCurve * 2).toInt()
                ).apply {
                    cacheTextLayouts.add(index, this)
                }
            }

            val dy = measuredHeight -
                    anchorPaddingBottom - textLayout.height
            canvas.translate(
                padHorCausedByCurve, dy
            )
            textLayout.draw(canvas)
            canvas.translate(itemWidth - padHorCausedByCurve, -dy)
        }
        canvas.restore()
    }

    @SuppressLint("WrongConstant")
    private fun getDefaultStaticLayout(text: String, width: Int): StaticLayout {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder
                .obtain(text, 0, text.length, textPaint, width)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setBreakStrategy(Layout.BREAK_STRATEGY_BALANCED)
                .setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_FULL)
                .build()
        } else {
            StaticLayout(
                text, textPaint, width,
                Layout.Alignment.ALIGN_CENTER, 1f,
                0f, true
            )
        }
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
    private fun generateArcs(
        startX: Float, width: Float,
        arcCircleWidth: Float,
        arcCircleHeight: Float
    ) {
        var x = startX
        path.arcTo(
            x - arcCircleWidth / 2f, 0f,
            x + arcCircleWidth / 2f, arcCircleHeight,
            270f, 90f, false
        )
        path.arcTo(
            x + arcCircleWidth / 2,
            measuredHeight - arcCircleHeight - anchorPaddingBottom,
            x + arcCircleWidth / 2 + arcCircleWidth,
            measuredHeight.toFloat() - anchorPaddingBottom,
            180f,
            -90f,
            false
        )

        x += width - arcCircleWidth / 2
        path.arcTo(
            x - arcCircleWidth,
            measuredHeight - arcCircleHeight - anchorPaddingBottom,
            x, measuredHeight.toFloat() - anchorPaddingBottom,
            90f, -90f, false
        )
        path.arcTo(
            x, 0f,
            x + arcCircleWidth,
            arcCircleHeight,
            180f, 90f, false
        )


    }


    data class Item(
        val title: String,
        @DrawableRes val iconResourceId: Int
    )

    companion object {
        private const val TAG = "CurvedOutsideBottomNav"
        private const val SELECTED_ITEM_KEY = "idAnchored"
        private const val SUPER_SAVE_STATE_KEY = "superSaveState"
    }
}