package io.github.dmitrytsyvtsyn.fluently.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

interface ViewEvent

interface ViewState

interface ViewSideEffect

abstract class BaseViewModel<Event : ViewEvent, State : ViewState, Effect : ViewSideEffect> : ViewModel() {

    private val initialState: State by lazy { initialState() }

    private val _viewState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val viewState: StateFlow<State> = _viewState

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            _event.collect { handleEvents(it) }
        }
    }

    fun pushEvent(event: Event) = viewModelScope.launch {
        _event.emit(event)
    }

    protected abstract fun initialState(): State

    protected abstract fun handleEvents(event: Event)

    protected fun setState(reducer: State.() -> State) {
        val newState = viewState.value.reducer()
        _viewState.value = newState
    }

    protected fun setEffect(effect: Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }

}