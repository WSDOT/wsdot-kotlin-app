package gov.wa.wsdot.android.wsdot.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import javax.inject.Inject

abstract class SharedValueViewModel<T>: ViewModel() {

    private val _value = MutableLiveData<T>()

    val value: LiveData<T>
        get() = _value

    fun setValue(_value: T?) {
        this._value.value = _value
    }
}

class SharedDateViewModel @Inject constructor() : SharedValueViewModel<Date>() {
    init {
        setValue(Calendar.getInstance().time)
    }
}