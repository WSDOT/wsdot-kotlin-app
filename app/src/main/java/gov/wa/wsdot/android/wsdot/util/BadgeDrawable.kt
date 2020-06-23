package gov.wa.wsdot.android.wsdot.util

import android.content.Context
import android.graphics.*
import android.util.TypedValue
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import androidx.core.content.ContextCompat
import gov.wa.wsdot.android.wsdot.R


/**
 * Created by Admin on 2/25/2016.
 */
class BadgeDrawable(context: Context) : Drawable() {

    private val mTextSize: Float
    private val mBadgePaint: Paint
    private val mBadgePaint1: Paint
    private val mTextPaint: Paint
    private val mTxtRect = Rect()

    private var mCount = ""
    private var mWillDraw = false


    init {
        mTextSize = dpToPx(context, 8f) //text size
        mBadgePaint = Paint()
        mBadgePaint.color = Color.parseColor("#ff9800")
        mBadgePaint.isAntiAlias = true
        mBadgePaint.style = Paint.Style.FILL
        mBadgePaint1 = Paint()
        mBadgePaint1.color = Color.parseColor("#EEEEEE")
        mBadgePaint1.isAntiAlias = true
        mBadgePaint1.style = Paint.Style.FILL

        mTextPaint = Paint()
        mTextPaint.color = Color.WHITE
        //mTextPaint.typeface = Typeface.DEFAULT
        mTextPaint.textSize = mTextSize
        mTextPaint.isAntiAlias = true
        mTextPaint.textAlign = Paint.Align.CENTER
    }

    private fun dpToPx(context: Context, value: Float): Float {
        val r = context.resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, r.displayMetrics)
    }


    override fun draw(canvas: Canvas) {
        if (!mWillDraw) {
            return
        }
        val bounds = bounds
        val width = bounds.right - bounds.left
        val height = bounds.bottom - bounds.top
        // Position the badge in the top-right quadrant of the icon.

        /*Using Math.max rather than Math.min */
        //        float radius = ((Math.max(width, height) / 2)) / 2;
        val radius = width * 0.15f
        val centerX = width - radius - 1f + 10
        val centerY = radius - 5
        if (mCount.length <= 2) {
            // Draw badge circle.
            canvas.drawCircle(centerX, centerY, radius + 9, mBadgePaint1)
            canvas.drawCircle(centerX, centerY, radius + 7, mBadgePaint)
        } else {
            canvas.drawCircle(centerX, centerY, radius + 10, mBadgePaint1)
            canvas.drawCircle(centerX, centerY, radius + 8, mBadgePaint)
        }
        // Draw badge count text inside the circle.
        mTextPaint.getTextBounds(mCount, 0, mCount.length, mTxtRect)
        val textHeight = mTxtRect.bottom - mTxtRect.top
        val textY = centerY + textHeight / 2f
        if (mCount.length > 2)
            canvas.drawText("99+", centerX, textY, mTextPaint)
        else
            canvas.drawText(mCount, centerX, textY, mTextPaint)
    }

    /*
     Sets the count (i.e notifications) to display.
      */
    fun setText(count: String) {
        mCount = count
        // Only draw a badge if there are notifications.
        mWillDraw = !count.equals("0", ignoreCase = true)
        invalidateSelf()
    }

    override fun setAlpha(alpha: Int) {
        // do nothing
    }

    override fun setColorFilter(cf: ColorFilter?) {
        // do nothing
    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }

    companion object {
        fun getMenuBadge(context: Context, res: Int, badgeText: String, showBadge: Boolean = true): Drawable {
            val icon = ContextCompat.getDrawable(context, R.drawable.ic_badge_drawable) as LayerDrawable
            val mainIcon = ContextCompat.getDrawable(context, res)
            val badge = BadgeDrawable(context)
            badge.setText(badgeText)
            icon.mutate()
            if (showBadge) {
                icon.setDrawableByLayerId(R.id.ic_badge, badge)
            }
            icon.setDrawableByLayerId(R.id.ic_main_icon, mainIcon)
            return icon
        }
    }

}