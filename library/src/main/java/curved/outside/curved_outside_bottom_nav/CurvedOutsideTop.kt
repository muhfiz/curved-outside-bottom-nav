package curved.outside.curved_outside_bottom_nav

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt

class CurvedOutsideTop : View {

    private val path = Path()
    private val paint = Paint()
    private var radius = 0f

    constructor(context: Context): super(context){
        init()
    }

    constructor(context: Context, attr: AttributeSet): super(context, attr){
        init()
    }

    constructor(context: Context, radius: Float): super(context){
        this.radius = radius
        init()
    }

    private fun init(){
        paint.color = Color.RED
        paint.style = Paint.Style.FILL
    }

    fun setColor(@ColorInt color: Int){
        paint.color = color
    }

    override fun onDraw(canvas: Canvas?) {
        //the most accurate currently
        if(radius != 0f){
            path.reset()
            path.moveTo(0f, 0f)
            path.cubicTo(
                0f,radius / 2,
                radius / 2, radius,
                radius, radius
            )
            path.lineTo(measuredWidth - radius, radius)
            path.cubicTo(
                measuredWidth - radius / 2, radius,
                measuredWidth.toFloat(), radius / 2,
                measuredWidth.toFloat(), 0f
            )
            path.lineTo(measuredWidth.toFloat(), radius)
            path.lineTo(measuredWidth.toFloat(), measuredHeight.toFloat())
            path.lineTo(0f, measuredHeight.toFloat())
            path.close()
            canvas?.drawPath(path, paint)
        }
        super.onDraw(canvas)
    }
}