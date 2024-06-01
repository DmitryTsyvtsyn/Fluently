package io.github.dmitrytsyvtsyn.fluently.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<Event, State, Effect>(initialState: State) : ViewModel() {

    private val _viewState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val viewState: StateFlow<State> = _viewState

    private val _effect: MutableSharedFlow<Effect> = MutableSharedFlow()
    val effect = _effect.asSharedFlow()

    abstract fun handleEvent(event: Event)

    protected fun setState(update: State.() -> State) {
        val newState = viewState.value.update()
        _viewState.value = newState
    }

    protected fun setEffect(effect: Effect) {
        viewModelScope.launch { _effect.emit(effect) }
    }

}