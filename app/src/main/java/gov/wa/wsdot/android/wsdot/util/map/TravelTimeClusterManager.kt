package gov.wa.wsdot.android.wsdot.util.map

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.MarkerManager
import com.google.maps.android.clustering.ClusterManager
import gov.wa.wsdot.android.wsdot.model.map.TravelTimeClusterItem

class TravelTimeClusterManager(context: Context?, map: GoogleMap?, markerManager: MarkerManager?) :
    ClusterManager<TravelTimeClusterItem>(context, map, markerManager)
