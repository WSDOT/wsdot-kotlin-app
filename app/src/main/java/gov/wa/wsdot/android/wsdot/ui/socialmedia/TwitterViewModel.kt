package gov.wa.wsdot.android.wsdot.ui.socialmedia

import androidx.lifecycle.*
import gov.wa.wsdot.android.wsdot.db.socialmedia.Tweet
import gov.wa.wsdot.android.wsdot.repository.TwitterRepository
import gov.wa.wsdot.android.wsdot.model.common.Resource
import javax.inject.Inject

class TwitterViewModel @Inject constructor(twitterRepository: TwitterRepository) : ViewModel() {

    // 2-way binding value for spinner
    private val _selectedTwitterAccount = MediatorLiveData<Pair<String, String>?>()
    val selectedTwitterAccount: MediatorLiveData<Pair<String, String>?>
        get() = _selectedTwitterAccount

    val twitterAccounts = listOf(
        Pair("All Accounts",""),
        Pair("Ferries", "wsferries"),
        Pair("Snoqualmie Pass", "SnoqualmiePass"),
        Pair("WSDOT", "wsdot"),
        Pair("WSDOT East", "WSDOT_East"),
        Pair("WSDOT North", "wsdot_north"),
        Pair("WSDOT Southwest", "wsdot_sw"),
        Pair("WSDOT Tacoma", "wsdot_tacoma"),
        Pair("WSDOT Traffic", "wsdot_traffic")
    )

    init {
        _selectedTwitterAccount.value = twitterAccounts[0]
    }

    val tweets: LiveData<Resource<List<Tweet>>> = _selectedTwitterAccount.switchMap {
            it?.let { it1 -> twitterRepository.loadTweetsForAccount(it1.second, false) }
        }

    fun refresh() {
        val accountName = selectedTwitterAccount.value
        if (accountName != null) {
            _selectedTwitterAccount.value = accountName
        }
    }

}