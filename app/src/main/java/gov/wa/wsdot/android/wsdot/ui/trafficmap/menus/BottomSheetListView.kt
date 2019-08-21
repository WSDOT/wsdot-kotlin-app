package gov.wa.wsdot.android.wsdot.ui.trafficmap.menus

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.AbsListView
import android.widget.ListView

class BottomSheetListView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ListView(context, attrs, defStyleAttr) {


    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {

        if (canScroll(this)) {
            parent.requestDisallowInterceptTouchEvent(true)
        }

        return super.onInterceptTouchEvent(ev)

    }


    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (canScroll(this)) {
            parent.requestDisallowInterceptTouchEvent(true)
        }

        return super.onTouchEvent(ev)
    }

    private fun canScroll(view: AbsListView): Boolean {


        var can = false

        if (view.childCount > 0) {

            val onTop = (view.firstVisiblePosition != 0) && (view.getChildAt(0).top != 0)
            val allItemsVisible = onTop && view.lastVisiblePosition == view.childCount

            if (onTop || allItemsVisible) {
                can = true
            }

        }

        return can

    }

}