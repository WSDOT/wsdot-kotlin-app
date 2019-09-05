package gov.wa.wsdot.android.wsdot.ui.favorites.recyclerview

import androidx.recyclerview.widget.RecyclerView
import gov.wa.wsdot.android.wsdot.databinding.*

/**
 *  A view holder for a favorites item.
 *  Must have a binding for all possible types of favorites.
 */
class FavoriteViewHolder : RecyclerView.ViewHolder {

    lateinit var headerBinding: HeaderItemBinding
    lateinit var travelTimeItemBinding: TravelTimeItemBinding
    lateinit var cameraItemBinding: CameraItemBinding
    lateinit var ferryScheduleItemBinding: FerryScheduleItemBinding
    lateinit var mountainPassItemBinding: MountainPassItemBinding

    constructor(binding: HeaderItemBinding) : super(binding.root) {
        headerBinding = binding
    }

    constructor(binding: TravelTimeItemBinding) : super(binding.root) {
        travelTimeItemBinding = binding
    }

    constructor(binding: CameraItemBinding) : super(binding.root) {
        cameraItemBinding = binding
    }

    constructor(binding: FerryScheduleItemBinding): super(binding.root) {
        ferryScheduleItemBinding = binding
    }

    constructor(binding: MountainPassItemBinding): super(binding.root) {
        mountainPassItemBinding = binding
    }

}