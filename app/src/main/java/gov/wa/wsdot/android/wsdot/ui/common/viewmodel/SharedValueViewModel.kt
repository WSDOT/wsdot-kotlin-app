package gov.wa.wsdot.android.wsdot.ui.common.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// generic view model for passing data between fragments. Typically for pickers and other menus.
abstract class SharedValueViewModel<T>: ViewModel() {

    private val _value = MutableLiveData<T>()

    val value: LiveData<T>
        get() = _value

    fun setValue(_value: T?) {
        this._value.value = _value
    }

}
