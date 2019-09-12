package gov.wa.wsdot.android.wsdot.ui.socialmedia

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.db.socialmedia.Tweet
import gov.wa.wsdot.android.wsdot.repository.TwitterRepository
import gov.wa.wsdot.android.wsdot.util.AbsentLiveData
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

class TwitterViewModel @Inject constructor(twitterRepository: TwitterRepository) : ViewModel() {

    private val _accountQuery: MutableLiveData<AccountQuery> = MutableLiveData()
    private val repo = twitterRepository

    val tweets: LiveData<Resource<List<Tweet>>> = Transformations
        .switchMap(_accountQuery) { input ->
            input.ifExists {
                twitterRepository.loadTweetsForAccount(it, false)
            }
        }

    fun setAccountQuery(accountName: String) {
        val update = AccountQuery(accountName)
        if (_accountQuery.value == update) {
            return
        }
        _accountQuery.value = update
    }

    fun refresh() {
        val accountName = _accountQuery.value?.accountName
        if (accountName != null) {
            _accountQuery.value = AccountQuery(accountName)
        }
    }

    data class AccountQuery(val accountName: String) {
        fun <T> ifExists(f: (String) -> LiveData<T>): LiveData<T> {
            return f(accountName)
        }
    }
}