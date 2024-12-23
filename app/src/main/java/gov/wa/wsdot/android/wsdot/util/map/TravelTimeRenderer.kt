package gov.wa.wsdot.android.wsdot.util.map

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.preference.PreferenceManager
import android.util.SparseArray
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import com.google.maps.android.ui.SquareTextView
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.model.map.TravelTimeClusterItem


/**
 *  Custom Renderer for [TravelTimeClusterItem] & [gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTime] items.
 *  Checks user prefs for display preference (shown/hidden)
 */
class TravelTimeRenderer(context: Context?, map: GoogleMap?, clusterManager: ClusterManager<TravelTimeClusterItem>?) :
    DefaultClusterRenderer<TravelTimeClusterItem>(context, map, clusterManager) {

    private var mContext: Context? = context

    private val mIcons = SparseArray<BitmapDescriptor>()
    private val mIconGenerator: IconGenerator = IconGenerator(context)

    private lateinit var mColoredCircleBackground: ShapeDrawable

    init {
        context?.let {
            mIconGenerator.setContentView(makeSquareTextView(it) as View?)
            mIconGenerator.setBackground(makeClusterBackground())
        }

    }

    override fun onBeforeClusterItemRendered(item: TravelTimeClusterItem?, markerOptions: MarkerOptions?) {

        if (item != null) {
            BitmapDescriptorFactory.fromResource(R.drawable.traveltimes).let { icon ->
                markerOptions?.let {
                    it.icon(icon)
                    it.zIndex(0.0f)
                    it.visible(isVisible())
                }
            }
        }
    }

    override fun onBeforeClusterRendered(cluster: Cluster<TravelTimeClusterItem>?, markerOptions: MarkerOptions?) {

        markerOptions?.let {

            it.visible(isVisible())

            val bucket = getBucket(cluster)
            var descriptor: BitmapDescriptor? = mIcons.get(bucket)
            if (descriptor == null) {
                descriptor = BitmapDescriptorFactory.fromBitmap(mIconGenerator.makeIcon(getClusterText(bucket)))
                mIcons.put(bucket, descriptor)
            }

            markerOptions.icon(descriptor)
            it.zIndex(0.0f)

        }
    }

    override fun shouldRenderAsCluster(cluster: Cluster<TravelTimeClusterItem>?): Boolean {
        mContext?.let {
            val settings = PreferenceManager.getDefaultSharedPreferences(mContext!!)
            val shouldCluster = settings.getBoolean(it.getString(R.string.user_preference_traffic_map_show_travel_times), true)
            if (!shouldCluster) {
                return shouldCluster
            } else {
                // if cluster items all in same location, always render as cluster
                cluster?.let { cluster ->
                    val itemsWithDiffLocations = cluster.items.filter {item ->
                        item.mTravelTime.startLocationLatitude != cluster.items.first().mTravelTime.startLocationLatitude
                                || item.mTravelTime.startLocationLongitude != cluster.items.first().mTravelTime.startLocationLongitude
                    }
                    if (itemsWithDiffLocations.isEmpty() && cluster.items.size > 1) { return true }
                }
            }
        }
        return super.shouldRenderAsCluster(cluster)
    }

    private fun isVisible(): Boolean {
        mContext?.let {
            val settings = PreferenceManager.getDefaultSharedPreferences(mContext!!)
            return settings.getBoolean(it.getString(R.string.user_preference_traffic_map_show_travel_times), true)
        }
        return true
    }

    private fun makeSquareTextView(context: Context): SquareTextView {
        val squareTextView = SquareTextView(context)
        return squareTextView
    }

    private fun makeClusterBackground(): LayerDrawable {

        mColoredCircleBackground = ShapeDrawable(OvalShape())
        val outline = ShapeDrawable(OvalShape())

        var background = LayerDrawable(arrayOf<Drawable>(outline, mColoredCircleBackground))

        mContext?.let {
            val icon = it.resources.getDrawable(R.drawable.traveltimes, it.theme)

            background = LayerDrawable(arrayOf<Drawable>(outline, mColoredCircleBackground, icon))
        }


        return background
    }

    override fun getColor(clusterSize: Int): Int {
        return Color.parseColor(when (clusterSize) {
            else -> "#00251A"
        })
    }
}