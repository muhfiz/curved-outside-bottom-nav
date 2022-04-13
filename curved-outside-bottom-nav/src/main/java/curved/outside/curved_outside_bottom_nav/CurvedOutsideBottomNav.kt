package curved.outside.curved_outside_bottom_nav

import android.animation.TimeInterpolator
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.Parcelable
import android.transition.ChangeBounds
import android.transition.Transition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import kotlin.math.ceil


class CurvedOutsideBottomNav @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    var onItemSelectedListener: ((prevPos: Int, pos: Int) -> Unit)? = null

    private var anchorId = View.generateViewId()
    private val mainConstraintSet = ConstraintSet().apply {
        setTranslationZ(
            anchorId,
            context.resources.getDimension(
                R.dimen.z_curved_outside_bottom_nav_anchor_item
            )
        )
    }
    private var radiusValue = 0f
    private var selectedColor = 0
    private var activeBackgroundColor = Color.WHITE
    private val changeBounds = ChangeBounds()


    private val itemPaddingVertical =
        context.resources.getDimensionPixelSize(R.dimen.bottom_navigation_top_bot_margin)

    private val itemViews: ArrayList<Item> = ArrayList()

    init {

        setupChangeBoundsTransition()
        attrs?.let {
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.CurvedOutsideBottomNav,
                0, 0
            ).apply {
                try {
                    activeBackgroundColor = getColor(
                        R.styleable.CurvedOutsideBottomNav_activeBackgroundColor,
                        Color.WHITE
                    )
                } finally {
                    recycle()
                }
            }
        }

    }

<<<<<<< HEAD
    private fun setup(){
        minHeight = context.resources.getDimensionPixelSize(R.dimen.min_bottom_navigation_height)
    }
=======
>>>>>>> old-state

    fun setItemsMenu(items: List<CurvedOutsideBotNavItem>, startAnchor: Int = 0, titleFont: Typeface? = null) {

        itemViews.clear()

        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        radiusValue = context.resources.getDimension(R.dimen.default_corner)
        selectedColor = ContextCompat.getColor(context, typedValue.resourceId)

        val titleTextSize =
            context.resources.getDimensionPixelSize(R.dimen.bottom_navigation_title_size)

        val handlerTopLeftCornerId = View.generateViewId()
        val handlerTopRightCornerId = View.generateViewId()

        this.createHandlerCorner(handlerTopLeftCornerId, handlerTopRightCornerId)
        this.setHandlerIntoAnchor(handlerTopLeftCornerId, ConstraintSet.START)
        this.setHandlerIntoAnchor(handlerTopRightCornerId, ConstraintSet.END)

        for (pos in items.indices) {

            val item = items[pos]
            val icon = ImageView(context).apply {
                id = View.generateViewId()
                setImageResource(item.iconResourceId)
            }
            val title = TextView(context).apply {
                id = View.generateViewId()
                text = item.title
                setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize.toFloat())
                includeFontPadding = false
                gravity = Gravity.CENTER
                if(titleFont != null){
                    typeface = titleFont
                }
            }

            itemViews.add(
                Item(icon, title)
            )

            this@CurvedOutsideBottomNav.addView(icon)
            this@CurvedOutsideBottomNav.addView(title)

            setItemToUnselectedState(icon, title)

            icon.setPadding(
                0, itemPaddingVertical,
                0, 0
            )
            title.setPadding(
                0, 0, 0,
                itemPaddingVertical
            )

            val onClickListener: (View) -> Unit = {
                selectItem(pos)
            }

            icon.setOnClickListener(onClickListener)
            title.setOnClickListener(onClickListener)

        }
        setPositionItemViews()
        mainConstraintSet.constrainHeight(
            anchorId, (radiusValue + itemPaddingVertical).toInt()
        )

        moveAnchorTo(startAnchor, false)
        mainConstraintSet.applyTo(this)
    }

    private fun setPositionItemViews() {
        for (pos in itemViews.indices) {

            mainConstraintSet.apply {
                if (pos == 0) {
                    connect(
                        itemViews[pos].icon.id, ConstraintSet.START,
                        ConstraintSet.PARENT_ID, ConstraintSet.START
                    )
                    setHorizontalChainStyle(
                        itemViews[pos].icon.id, ConstraintSet.CHAIN_PACKED
                    )
                    setMargin(
                        itemViews[pos].icon.id,
                        ConstraintSet.START,
                        context.resources.getDimensionPixelSize(
                            R.dimen.margin_start_curved_outside_bottom_nav_first_item
                        )
                    )
                } else {
                    connect(
                        itemViews[pos].icon.id, ConstraintSet.START,
                        itemViews[pos - 1].icon.id, ConstraintSet.END
                    )
                }
                connect(
                    itemViews[pos].icon.id, ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID, ConstraintSet.TOP
                )
                if (pos == itemViews.size - 1) {
                    connect(
                        itemViews[pos].icon.id, ConstraintSet.END,
                        ConstraintSet.PARENT_ID, ConstraintSet.END
                    )
                    setMargin(
                        itemViews[pos].icon.id,
                        ConstraintSet.END,
                        context.resources.getDimensionPixelSize(
                            R.dimen.margin_end_curved_outside_bottom_nav_end_item
                        )
                    )
                } else {
                    connect(
                        itemViews[pos].icon.id, ConstraintSet.END,
                        itemViews[pos + 1].icon.id, ConstraintSet.START
                    )
                }
                connect(
                    itemViews[pos].icon.id, ConstraintSet.BOTTOM,
                    itemViews[pos].title.id, ConstraintSet.TOP
                )
                constrainWidth(
                    itemViews[pos].icon.id, ConstraintSet.MATCH_CONSTRAINT
                )
                constrainMaxWidth(
                    itemViews[pos].icon.id,
                    context.resources.getDimensionPixelSize(
                        R.dimen.width_max_curved_outside_bottom_nav_item
                    )
                )
                constrainedHeight(itemViews[pos].icon.id, true)
                constrainHeight(
                    itemViews[pos].icon.id,
                    context.resources.getDimensionPixelSize(
                        R.dimen.height_curved_outside_bottom_nav_off_icon_item
                    )
                )

                connect(
                    itemViews[pos].title.id, ConstraintSet.START,
                    itemViews[pos].icon.id, ConstraintSet.START
                )
                connect(
                    itemViews[pos].title.id, ConstraintSet.END,
                    itemViews[pos].icon.id, ConstraintSet.END
                )
                connect(
                    itemViews[pos].title.id, ConstraintSet.TOP,
                    itemViews[pos].icon.id, ConstraintSet.BOTTOM
                )
                connect(
                    itemViews[pos].title.id, ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM
                )
                constrainHeight(
                    itemViews[pos].title.id, ConstraintSet.WRAP_CONTENT
                )
                constrainWidth(
                    itemViews[pos].title.id, ConstraintSet.MATCH_CONSTRAINT
                )
            }

        }

    }

    fun selectItem(index: Int) {
        if (indexSelected != index) {
            onItemSelectedListener?.invoke(indexSelected, index)
            moveAnchorTo(index)
        }
    }

    private fun setupChangeBoundsTransition() {
        changeBounds.interpolator = OvershootInterpolator()
    }

    var indexSelected = -1
        private set

    private fun moveAnchorTo(index: Int, animated: Boolean = true) {

<<<<<<< HEAD
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
=======
        if (animated) {
            TransitionManager.beginDelayedTransition(
                this, changeBounds as Transition
            )

        }
>>>>>>> old-state

        mainConstraintSet.apply {

            connect(
                anchorId, ConstraintSet.START,
                itemViews[index].icon.id, ConstraintSet.START
            )
            connect(
                anchorId, ConstraintSet.END,
                itemViews[index].icon.id, ConstraintSet.END
            )
            connect(
                anchorId, ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM
            )

            connect(
                itemViews[index].icon.id, ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM
            )
            constrainHeight(
                itemViews[index].icon.id,
                ConstraintSet.MATCH_CONSTRAINT
            )

            itemViews[index].icon.setPadding(
                0, itemPaddingVertical,
                0, itemPaddingVertical * 2
            )
            itemViews[index].icon.setColorFilter(selectedColor)

            connect(
                itemViews[index].title.id, ConstraintSet.TOP,
                ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM
            )

            clear(
                itemViews[index].title.id, ConstraintSet.BOTTOM
            )


            if (indexSelected != -1) {
                connect(
                    itemViews[indexSelected].icon.id, ConstraintSet.BOTTOM,
                    itemViews[indexSelected].title.id, ConstraintSet.TOP
                )
                constrainHeight(
                    itemViews[indexSelected].icon.id,
                    context.resources.getDimensionPixelSize(
                        R.dimen.height_curved_outside_bottom_nav_off_icon_item
                    )
                )
                itemViews[indexSelected].icon.setPadding(
                    0, itemPaddingVertical,
                    0, 0
                )

                connect(
                    itemViews[indexSelected].title.id, ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM
                )
                connect(
                    itemViews[indexSelected].title.id, ConstraintSet.TOP,
                    itemViews[indexSelected].icon.id, ConstraintSet.BOTTOM
                )

                setItemToUnselectedState(
                    itemViews[indexSelected].icon,
                    itemViews[indexSelected].title
                )

            }

            indexSelected = index

        }.applyTo(this)


    }

    private fun setItemToUnselectedState(icon: ImageView, title: TextView) {
        icon.setColorFilter(Color.WHITE)
        title.setTextColor(Color.WHITE)
    }

    private fun setHandlerIntoAnchor(handlerId: Int, toPos: Int) {
        val oppositePos = if (toPos == ConstraintSet.END) {
            ConstraintSet.START
        } else {
            ConstraintSet.END
        }
        mainConstraintSet.connect(
            handlerId, oppositePos,
            ConstraintSet.PARENT_ID, oppositePos
        )
        mainConstraintSet.connect(
            handlerId, toPos,
            anchorId, oppositePos
        )
        mainConstraintSet.connect(
            handlerId, ConstraintSet.TOP,
            ConstraintSet.PARENT_ID, ConstraintSet.TOP
        )
        mainConstraintSet.connect(
            handlerId, ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM
        )
    }

    private fun createHandlerCorner(handlerTopLeftCornerId: Int, handlerTopRightCornerid: Int) {

        val colorPrimary = ContextCompat.getColor(
            context, TypedValue().apply {
                context.theme.resolveAttribute(R.attr.colorPrimary, this, true)
            }.resourceId
        )

        var layoutParamsMatchConstraint = LayoutParams(
            LayoutParams.MATCH_CONSTRAINT, LayoutParams.MATCH_CONSTRAINT
        )
        val strokeWidth = ceil(context.resources.displayMetrics.density).toInt()
        LayerDrawable(
            arrayOf(
                GradientDrawable().apply {
                    setStroke(strokeWidth, colorPrimary)
                },
                GradientDrawable().apply {
                    setColor(activeBackgroundColor)
                }
            )
        ).apply {
            setLayerInset(1, 0, strokeWidth, 0, 0)
            background = this
        }

        //creating handlerTopLeftCorner
        var gradientDrawable = GradientDrawable()
        var handler = View(context)
        gradientDrawable.setColor(selectedColor)
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
        gradientDrawable.setColor(selectedColor)
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
        val anchor = CurvedOutsideTop(context, radiusValue)
        anchor.setColor(colorPrimary)
        anchor.id = anchorId
        addView(anchor, layoutParamsMatchConstraint)
    }

    override fun onSaveInstanceState(): Parcelable? {
        return Bundle().apply {
            putParcelable(SUPER_SAVE_STATE_KEY, super.onSaveInstanceState())
            putInt(SELECTED_ITEM_KEY, indexSelected)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            state.getInt(SELECTED_ITEM_KEY, -1).let {
                if (it != -1) {
                    selectItem(it)
                }
            }
            return super.onRestoreInstanceState(state.getParcelable(SUPER_SAVE_STATE_KEY))
        }
        super.onRestoreInstanceState(state)
    }


    data class Item(
        val icon: ImageView,
        val title: TextView
    )


    companion object {
        private const val TAG = "CurvedOutsideBottomNav"
        private const val SELECTED_ITEM_KEY = "idAnchored"
        private const val SUPER_SAVE_STATE_KEY = "superSaveState"
    }
}