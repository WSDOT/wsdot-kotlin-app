package gov.wa.wsdot.android.wsdot.util.map

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.Log
import androidx.preference.PreferenceManager
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
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
import gov.wa.wsdot.android.wsdot.model.map.CameraClusterItem


/**
 *  Custom Renderer for [CameraClusterItem] & [gov.wa.wsdot.android.wsdot.db.traffic.Camera] items.
 *  Checks user prefs for display preference (shown/hidden)
 */
class CameraRenderer(context: Context?, map: GoogleMap?, clusterManager: ClusterManager<CameraClusterItem>?) :
    DefaultClusterRenderer<CameraClusterItem>(context, map, clusterManager) {

    private var mContext: Context? = context

    private val mIcons = SparseArray<BitmapDescriptor>()
    private val mIconGenerator: IconGenerator = IconGenerator(context)

    private var mDensity: Float = 0.0f
    private lateinit var mColoredCircleBackground: ShapeDrawable

    init {
        context?.let {
            mDensity = it.resources.displayMetrics.density
            mIconGenerator.setContentView(makeSquareTextView(it) as View?)
            mIconGenerator.setTextAppearance(R.style.amu_ClusterIcon_TextAppearance)
            mIconGenerator.setBackground(makeClusterBackground())
        }

    }

    override fun onBeforeClusterItemRendered(item: CameraClusterItem?, markerOptions: MarkerOptions?) {

        if (item != null) {

            if (item.isSelected) {
                BitmapDescriptorFactory.fromResource(R.drawable.ic_list_wsdot_snoqualmie_pass)
                    .let { icon ->
                        markerOptions?.let {
                            it.icon(icon)
                            it.visible(isVisible())
                        }
                    }
                item.isSelected = false
            } else {
                BitmapDescriptorFactory.fromResource(R.drawable.camera).let { icon ->
                    markerOptions?.let {
                        it.icon(icon)
                        it.visible(isVisible())
                    }
                }
            }
        } else {
            BitmapDescriptorFactory.fromResource(R.drawable.camera).let { icon ->
                markerOptions?.let {
                    it.icon(icon)
                    it.visible(isVisible())
                }
            }
        }
    }

    override fun onBeforeClusterRendered(cluster: Cluster<CameraClusterItem>?, markerOptions: MarkerOptions?) {

        markerOptions?.let {

            it.visible(isVisible())

            val bucket = getBucket(cluster)
            var descriptor: BitmapDescriptor? = mIcons.get(bucket)
            if (descriptor == null) {
                mColoredCircleBackground.paint.color = getColor(bucket)
                descriptor = BitmapDescriptorFactory.fromBitmap(mIconGenerator.makeIcon(getClusterText(bucket)))
                mIcons.put(bucket, descriptor)
            }

            markerOptions.icon(descriptor)
        }
    }

    override fun shouldRenderAsCluster(cluster: Cluster<CameraClusterItem>?): Boolean {
        mContext?.let {
            val settings = PreferenceManager.getDefaultSharedPreferences(mContext!!)
            val shouldCluster = settings.getBoolean(it.getString(R.string.user_preference_traffic_map_cluster_cameras), true)
            if (!shouldCluster) {
                return shouldCluster
            } else {
                // if cluster items all in same location, always render as cluster
                cluster?.let { cluster ->
                    val itemsWithDiffLocations = cluster.items.filter {item ->
                        item.mCamera.latitude != cluster.items.first().mCamera.latitude
                                || item.mCamera.longitude != cluster.items.first().mCamera.longitude
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
            return settings.getBoolean(it.getString(R.string.user_preference_traffic_map_show_cameras), true)
        }
        return true
    }

    private fun makeSquareTextView(context: Context): SquareTextView {
        val squareTextView = SquareTextView(context)
        val layoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        squareTextView.layoutParams = layoutParams
        squareTextView.id = R.id.amu_text
        val twelveDpi = (12 * mDensity).toInt()
        squareTextView.setPadding(twelveDpi, twelveDpi, twelveDpi, twelveDpi)
        return squareTextView
    }

    private fun makeClusterBackground(): LayerDrawable {

        mColoredCircleBackground = ShapeDrawable(OvalShape())
        val outline = ShapeDrawable(OvalShape())
        outline.paint.color = -0x7f000001 // Transparent white.

        var background = LayerDrawable(arrayOf<Drawable>(outline, mColoredCircleBackground))

        mContext?.let {
            val icon = it.resources.getDrawable(R.drawable.camera_cluster, it.theme)
            background = LayerDrawable(arrayOf<Drawable>(outline, mColoredCircleBackground, icon))
        }

        val strokeWidth = (mDensity * 3).toInt()
        background.setLayerInset(1, strokeWidth, strokeWidth, strokeWidth, strokeWidth)
        return background
    }

    override fun getColor(clusterSize: Int): Int {
        return Color.parseColor(when (clusterSize) {
            in 0..10 -> "#00766C"
            in 11..20 -> "#00675B"
            in 21..50 -> "#00796B"
            in 51..100 -> "#005b4F"
            in 101..200 -> "#00695C"
            in 201..500 -> "#004D40"
            else -> "#00251A"
        })
    }
}