package curved.outside.curved_outside_bottom_nav

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.transition.ChangeBounds
import android.transition.Transition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat


class CurvedOutsideBottomNav : ConstraintLayout {

    companion object {
        private const val TAG = "CurvedOutsideBottomNav"
    }

    var onItemSelectedListener: OnItemSelectedListener? = null

    private val MAIN_CONSTRAINT_SET = ConstraintSet()
    private val ITEM_ON_CONSTRAINT_SET = ConstraintSet()
    private val ITEM_OFF_CONSTRAINT_SET = ConstraintSet()

    private var anchorId = 0
    private var radiusValue = 0f
    private var color = 0
    private var currentAnchorLocationId = 0

    constructor(context: Context) : super(context) {
        this.setup()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.setup()
    }

    private fun setup(){
        minHeight = context.resources.getDimensionPixelSize(R.dimen.min_bottom_navigation_height)
    }


    fun addItemsMenu(items: List<CurvedOutsideBotNavItem>) {

        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        radiusValue = context.resources.getDimension(R.dimen.default_corner)
        color = ContextCompat.getColor(context, typedValue.resourceId)

        val titleTextSize = context.resources.getDimensionPixelSize(R.dimen.bottom_navigation_title_size)
        val itemTopBotMargin = context.resources.getDimensionPixelSize(R.dimen.bottom_navigation_top_bot_margin)

        setBackgroundResource(R.drawable.half_background)
        val handlerTopLeftCornerId = View.generateViewId()
        val handlerTopRightCornerid = View.generateViewId()
        anchorId = View.generateViewId()

        this.createHandlerCorner(handlerTopLeftCornerId, handlerTopRightCornerid)
        this.setHandlerIntoAnchor(handlerTopLeftCornerId, ConstraintSet.START)
        this.setHandlerIntoAnchor(handlerTopRightCornerid, ConstraintSet.END)

        val iconId = View.generateViewId()
        val titleId = View.generateViewId()

        setupItemMode(iconId, titleId, itemTopBotMargin)

        for (pos in 0 until items.size) {

            val item = items[pos]
            val container = ConstraintLayout(context)
            val icon = ImageView(context)
            val title = TextView(context)

            icon.id = iconId
            icon.scaleType = ImageView.ScaleType.FIT_XY
            icon.setImageResource(item.iconResourceId)

            title.id = titleId
            title.text = item.title
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize.toFloat())
            title.includeFontPadding = false

            setItemToUnselectedState(icon, title)

            container.id = item.id

            container.addView(icon)
            container.addView(
                title, LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_CONSTRAINT
                )
            )

            ITEM_OFF_CONSTRAINT_SET.applyTo(container)

            container.setOnClickListener {
                moveAnchorTo(it.id)
                onItemSelectedListener?.onClickListener(it)
            }

            this.addView(
                container, LayoutParams(
                    LayoutParams.MATCH_CONSTRAINT, LayoutParams.WRAP_CONTENT
                )
            )

            setPositionContainer(pos, items)

            MAIN_CONSTRAINT_SET.setMargin(item.id, ConstraintSet.BOTTOM, itemTopBotMargin)
            MAIN_CONSTRAINT_SET.setMargin(item.id, ConstraintSet.TOP, itemTopBotMargin)
            if (pos == 0) {
                MAIN_CONSTRAINT_SET.setMargin(item.id, ConstraintSet.START, radiusValue.toInt())
            } else if (pos == items.size - 1) {
                MAIN_CONSTRAINT_SET.setMargin(item.id, ConstraintSet.END, radiusValue.toInt())
            }
        }

        moveAnchorTo(items[0].id)
        MAIN_CONSTRAINT_SET.applyTo(this)

    }

    private fun setupItemMode(iconId: Int, titleId: Int, itemTopBotMargin: Int) {

        ITEM_ON_CONSTRAINT_SET.setDimensionRatio(iconId, "1")
        ITEM_ON_CONSTRAINT_SET.setMargin(iconId, ConstraintSet.BOTTOM, itemTopBotMargin)
        ITEM_OFF_CONSTRAINT_SET.setDimensionRatio(iconId, "1")
        ITEM_OFF_CONSTRAINT_SET.constrainWidth(titleId, ConstraintSet.WRAP_CONTENT)


        ITEM_ON_CONSTRAINT_SET.connect(
            iconId, ConstraintSet.TOP,
            ConstraintSet.PARENT_ID, ConstraintSet.TOP
        )
        ITEM_ON_CONSTRAINT_SET.connect(
            iconId, ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM
        )
        ITEM_ON_CONSTRAINT_SET.connect(
            iconId, ConstraintSet.START,
            ConstraintSet.PARENT_ID, ConstraintSet.START
        )
        ITEM_ON_CONSTRAINT_SET.connect(
            iconId, ConstraintSet.END,
            ConstraintSet.PARENT_ID, ConstraintSet.END
        )
        ITEM_ON_CONSTRAINT_SET.connect(
            titleId, ConstraintSet.TOP,
            ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM
        )
        ITEM_ON_CONSTRAINT_SET.connect(
            titleId, ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM
        )
        ITEM_ON_CONSTRAINT_SET.connect(
            titleId, ConstraintSet.START,
            ConstraintSet.PARENT_ID, ConstraintSet.START
        )
        ITEM_ON_CONSTRAINT_SET.connect(
            titleId, ConstraintSet.END,
            ConstraintSet.PARENT_ID, ConstraintSet.END
        )

        ITEM_OFF_CONSTRAINT_SET.connect(
            iconId, ConstraintSet.TOP,
            ConstraintSet.PARENT_ID, ConstraintSet.TOP
        )
        ITEM_OFF_CONSTRAINT_SET.connect(
            iconId, ConstraintSet.BOTTOM,
            titleId, ConstraintSet.TOP
        )
        ITEM_OFF_CONSTRAINT_SET.connect(
            iconId, ConstraintSet.START,
            ConstraintSet.PARENT_ID, ConstraintSet.START
        )
        ITEM_OFF_CONSTRAINT_SET.connect(
            iconId, ConstraintSet.END,
            ConstraintSet.PARENT_ID, ConstraintSet.END
        )
        ITEM_OFF_CONSTRAINT_SET.connect(
            titleId, ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM
        )
        ITEM_OFF_CONSTRAINT_SET.connect(
            titleId, ConstraintSet.START,
            ConstraintSet.PARENT_ID, ConstraintSet.START
        )
        ITEM_OFF_CONSTRAINT_SET.connect(
            titleId, ConstraintSet.END,
            ConstraintSet.PARENT_ID, ConstraintSet.END
        )
    }

    private fun setPositionContainer(
        pos: Int,
        items: List<CurvedOutsideBotNavItem>
    ) {
        val containerId = items[pos].id

        if (pos == 0) {
            MAIN_CONSTRAINT_SET.connect(
                containerId, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START
            )
        } else {
            MAIN_CONSTRAINT_SET.connect(
                containerId, ConstraintSet.START,
                items[pos - 1].id, ConstraintSet.END
            )
        }

        if (pos == (items.size - 1)) {
            MAIN_CONSTRAINT_SET.connect(
                containerId, ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
        } else {
            MAIN_CONSTRAINT_SET.connect(
                containerId, ConstraintSet.END,
                items[pos + 1].id, ConstraintSet.START
            )
        }
        MAIN_CONSTRAINT_SET.connect(
            containerId, ConstraintSet.TOP,
            ConstraintSet.PARENT_ID, ConstraintSet.TOP
        )
        MAIN_CONSTRAINT_SET.connect(
            containerId, ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM
        )
    }

    private fun moveAnchorTo(id: Int) {

        val changeBounds = ChangeBounds()
        changeBounds.interpolator = OvershootInterpolator()
        TransitionManager.beginDelayedTransition(this, changeBounds as Transition)
        MAIN_CONSTRAINT_SET.connect(
            anchorId, ConstraintSet.START,
            id, ConstraintSet.START
        )
        MAIN_CONSTRAINT_SET.connect(
            anchorId, ConstraintSet.END,
            id, ConstraintSet.END
        )
        MAIN_CONSTRAINT_SET.connect(
            anchorId, ConstraintSet.TOP,
            id, ConstraintSet.TOP
        )
        MAIN_CONSTRAINT_SET.connect(
            anchorId, ConstraintSet.BOTTOM,
            id, ConstraintSet.BOTTOM
        )

        if (currentAnchorLocationId != 0) {
            val currentAnchorLocationView = this.findViewById<ConstraintLayout>(currentAnchorLocationId)
            ITEM_OFF_CONSTRAINT_SET.applyTo(currentAnchorLocationView)
            setItemToUnselectedState(
                currentAnchorLocationView.getChildAt(0) as ImageView,
                currentAnchorLocationView.getChildAt(1) as TextView
            )
        }

        currentAnchorLocationId = id

        val currentAnchorLocationView = this.findViewById<ConstraintLayout>(currentAnchorLocationId)
        setItemToSelectedState(
            currentAnchorLocationView.getChildAt(0) as ImageView,
            currentAnchorLocationView.getChildAt(1) as TextView
        )
        MAIN_CONSTRAINT_SET.applyTo(this)
        ITEM_ON_CONSTRAINT_SET.applyTo(currentAnchorLocationView)
    }

    private fun setItemToSelectedState(icon: ImageView, title: TextView){
        icon.setColorFilter(color)
        title.setTextColor(color)
    }

    private fun setItemToUnselectedState(icon: ImageView, title: TextView){
        icon.setColorFilter(Color.WHITE)
        title.setTextColor(Color.WHITE)
    }

    private fun setHandlerIntoAnchor(handlerId: Int, toPos: Int) {
        val oppositePos = if (toPos == ConstraintSet.END) {
            ConstraintSet.START
        } else {
            ConstraintSet.END
        }

        MAIN_CONSTRAINT_SET.connect(
            handlerId, oppositePos,
            ConstraintSet.PARENT_ID, oppositePos
        )
        MAIN_CONSTRAINT_SET.connect(
            handlerId, toPos,
            anchorId, oppositePos
        )
        MAIN_CONSTRAINT_SET.connect(
            handlerId, ConstraintSet.TOP,
            ConstraintSet.PARENT_ID, ConstraintSet.TOP
        )
        MAIN_CONSTRAINT_SET.connect(
            handlerId, ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM
        )
    }

    private fun createHandlerCorner(handlerTopLeftCornerId: Int, handlerTopRightCornerid: Int) {

        var layoutParamsMatchConstraint = LayoutParams(
            LayoutParams.MATCH_CONSTRAINT, LayoutParams.MATCH_CONSTRAINT
        )

        //creating handlerTopLeftCorner
        var gradientDrawable = GradientDrawable()
        var handler = View(context)
        gradientDrawable.setColor(color)
        gradientDrawable.cornerRadii = floatArrayOf(
            radiusValue, radiusValue, 0f, 0f, 0f, 0f, 0f, 0f
        )
        handler.background = gradientDrawable
        handler.id = handlerTopLeftCornerId
        addView(handler, layoutParamsMatchConstraint)

        //we can't reuse layout params because it's cache its params
        layoutParamsMatchConstraint = LayoutParams(
            LayoutParams.MATCH_CONSTRAINT, LayoutParams.MATCH_CONSTRAINT
        )
        //creating handlerTopRightCorner
        gradientDrawable = GradientDrawable()
        handler = View(context)
        gradientDrawable.setColor(color)
        gradientDrawable.cornerRadii = floatArrayOf(
            0f, 0f, radiusValue, radiusValue, 0f, 0f, 0f, 0f
        )
        handler.background = gradientDrawable
        handler.id = handlerTopRightCornerid
        addView(handler, layoutParamsMatchConstraint)

        layoutParamsMatchConstraint = LayoutParams(
            LayoutParams.MATCH_CONSTRAINT, LayoutParams.MATCH_CONSTRAINT
        )
        //creating handlerBottomCorner
        gradientDrawable = GradientDrawable()
        handler = View(context)
        gradientDrawable.setColor(Color.WHITE)
        gradientDrawable.cornerRadii = floatArrayOf(
            0f, 0f, 0f, 0f, radiusValue, radiusValue, radiusValue, radiusValue
        )
        handler.background = gradientDrawable
        handler.id = anchorId
        addView(handler, layoutParamsMatchConstraint)
    }

    interface OnItemSelectedListener {
        fun onClickListener(view: View)
    }

}