package gov.wa.wsdot.android.wsdot.util.network

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

/**
 * A generic class that can provide a resource backed the sqlite database.
 *
 * You can read more about it in the [Architecture
 * Guide](https://developer.android.com/arch).
 * @param <ResultType>
 * @param <RequestType>
</RequestType></ResultType> */
abstract class DBResource<ResultType>
@MainThread constructor() {

    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)
        @Suppress("LeakingThis")
        val dbSource = loadFromDb()
        result.addSource(dbSource) { data ->
            setValue(Resource.success(data))
        }
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    fun asLiveData() = result as LiveData<Resource<ResultType>>

}