package gov.wa.wsdot.android.wsdot.ui.ferries.vesselwatch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import gov.wa.wsdot.android.wsdot.db.ferries.TerminalAlert
import gov.wa.wsdot.android.wsdot.model.common.Resource
import gov.wa.wsdot.android.wsdot.repository.FerriesRepository

import javax.inject.Inject

class TerminalViewModel @Inject constructor(ferriesRepository: FerriesRepository) : ViewModel() {

    private val _terminalQuery: MutableLiveData<terminalAlertsQuery> = MutableLiveData()
    private val _showTerminals: MutableLiveData<Boolean> = MutableLiveData()

    val terminals = ferriesRepository.loadTerminals()

    val terminal: LiveData<Resource<TerminalAlert>> = _terminalQuery.switchMap { input ->
        input.ifExists { terminalId ->
            ferriesRepository.loadTerminal(terminalId)
        }
    }

    fun refresh(terminalId1: Int) {
        val terminalId = _terminalQuery.value?.terminalId
        if (terminalId != null) {
            _terminalQuery.value = terminalAlertsQuery(terminalId)
        }
    }

    fun setTerminalQuery(terminalId: Int) {
        val update = terminalAlertsQuery(terminalId)
        if (_terminalQuery.value == update) { return }
        _terminalQuery.value = update

    }



    fun setShowTerminals(showTerminals: Boolean) {
        _showTerminals.value = showTerminals
    }

    data class terminalAlertsQuery(val terminalId: Int) {
        fun <T> ifExists(f: (Int) -> LiveData<T>): LiveData<T> {
            return f(terminalId)
        }
    }

}