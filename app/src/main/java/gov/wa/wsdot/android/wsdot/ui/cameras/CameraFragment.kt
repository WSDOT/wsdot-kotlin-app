package gov.wa.wsdot.android.wsdot.ui.cameras

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.CameraFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import javax.inject.Inject

class CameraFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var cameraViewModel: CameraViewModel

    val args: CameraFragmentArgs by navArgs()

    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        cameraViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(CameraViewModel::class.java)
        cameraViewModel.setCameraQuery(args.cameraId)

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<CameraFragmentBinding>(
            inflater,
            R.layout.camera_fragment,
            container,
            false
        )

        cameraViewModel.camera.observe(viewLifecycleOwner, Observer { camera ->
            if (camera.data != null) {
                isFavorite = camera.data.favorite
                activity?.invalidateOptionsMenu()
            }
        })

        dataBinding.lifecycleOwner = viewLifecycleOwner
        dataBinding.cameraViewModel = cameraViewModel

        return dataBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.camera_menu, menu)
        setFavoriteMenuIcon(menu.findItem(R.id.action_favorite))
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_favorite -> {
                Log.e("debug", "added favorite")
                cameraViewModel.updateFavorite(args.cameraId)
                return false
            }
            else -> {}
        }
        return false
    }

    private fun setFavoriteMenuIcon(menuItem: MenuItem){
        if (isFavorite) {
            menuItem.icon = resources.getDrawable(R.drawable.ic_menu_favorite_pink, null)
        } else {
            menuItem.icon = resources.getDrawable(R.drawable.ic_menu_favorite_gray, null)
        }
    }

}