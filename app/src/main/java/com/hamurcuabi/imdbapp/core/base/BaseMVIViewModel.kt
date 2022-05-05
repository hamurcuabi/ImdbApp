package com.hamurcuabi.imdbapp.core.base

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamurcuabi.imdbapp.core.utils.NoObserverAttachedException
import com.hamurcuabi.imdbapp.core.utils.Resource
import com.hamurcuabi.imdbapp.core.utils.SingleLiveEvent
import com.hamurcuabi.imdbapp.core.utils.ViewModelContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Create ViewModels by Extending this class.
 *
 * @param STATE ViewState should represent the current state of the view at any given time.
 * So this class should have all the variable content on which our view is dependent.
 * Every time there is any user input/action we will expose modified
 * copy (to maintain the previous state which is not being modified) of this class.
 * We can create this model using Kotlin's data class.
 *
 * @param EFFECT ViewEffect is useful for actions that are fire-and-forget and we do not
 * want to maintain its state. We can create this class using Kotlin's sealed class.
 *
 * @param EVENT Represents all actions/events a user can perform on the view.
 * This is used to pass user input/action to the ViewModel.
 * We can create this event set using Kotlin's sealed class.
 *
 * @property process(viewEvent: EVENT) Process ViewEvents (viewEvent) passed by Activity/Fragment/View
 *                                     and update ViewState and ViewEffect accordingly.
 *
 * @see <a href="https://medium.com/@rohitss/best-architecture-for-android-mvi-livedata-viewmodel-71a3a5ac7ee3">Article which explains this Custom MVI architecture with Architecture Components.</a>
 */
open class BaseMVIViewModel<STATE, EFFECT, EVENT> :
    ViewModel(), ViewModelContract<EVENT> {

    private val _showLoading: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val showLoading: SingleLiveEvent<Boolean> = _showLoading

    var loadingCount = 0

    private val _viewStates: MutableLiveData<STATE> = MutableLiveData()
    fun viewStates(): LiveData<STATE> = _viewStates

    private var _viewState: STATE? = null
    protected var viewState: STATE
        get() = _viewState
            ?: throw UninitializedPropertyAccessException("\"viewState\" was queried before being initialized")
        set(value) {
            _viewState = value
            _viewStates.value = value!!
        }


    private val _viewEffects: SingleLiveEvent<EFFECT> = SingleLiveEvent()
    fun viewEffects(): SingleLiveEvent<EFFECT> = _viewEffects

    private var _viewEffect: EFFECT? = null
    protected var viewEffect: EFFECT
        get() = _viewEffect
            ?: throw UninitializedPropertyAccessException("\"viewEffect\" was queried before being initialized")
        set(value) {
            _viewEffect = value
            _viewEffects.value = value!!
        }

    @CallSuper
    override fun process(viewEvent: EVENT) {
        if (!viewStates().hasObservers()) {
            throw NoObserverAttachedException("No observer attached. In case of custom View \"startObserving()\" function needs to be called manually.")
        }
    }

    fun <T> makeApiCall(
        onFailure: (Throwable?) -> Unit,
        onLoading: () -> Unit,
        onSuccess: (T?) -> Unit,
        showLoading: Boolean = true,
        request: suspend () -> Flow<Resource<T>>,
    ) {
        viewModelScope.launch {
            request().collect {
                when (val response = it) {
                    is Resource.Failure -> {
                        loadingCount--
                        hideLoading()
                        onFailure.invoke(response.throwable)
                    }
                    is Resource.Loading -> {
                        if (showLoading) {
                            showLoading()
                        }
                        onLoading.invoke()
                    }
                    is Resource.Success -> {
                        hideLoading()
                        onSuccess.invoke(response.value)
                    }
                }
            }
        }
    }

    private fun showLoading() {
        loadingCount++
        if (loadingCount > 0) {
            _showLoading.postValue(true)
        }
    }

    private fun hideLoading() {
        loadingCount--
        if (loadingCount == 0) {
            _showLoading.postValue(false)
        }
    }
}
