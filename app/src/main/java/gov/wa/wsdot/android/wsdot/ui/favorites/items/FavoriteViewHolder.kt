package gov.wa.wsdot.android.wsdot.ui.favorites.items

import androidx.recyclerview.widget.RecyclerView
import gov.wa.wsdot.android.wsdot.databinding.CameraItemBinding
import gov.wa.wsdot.android.wsdot.databinding.TravelTimeItemBinding

class FavoriteViewHolder : RecyclerView.ViewHolder {

    lateinit var travelTimeItemBinding: TravelTimeItemBinding
    lateinit var cameraItemBinding: CameraItemBinding

    constructor(binding: TravelTimeItemBinding) : super(binding.root) {
        travelTimeItemBinding = binding
    }

    constructor(binding: CameraItemBinding) : super(binding.root) {
        cameraItemBinding = binding
    }
}