package com.example.hello.ui.dialog

sealed class Effect {
    object ShowInternetError: Effect()
    object ShowInvalidDataError: Effect()
    object UnauthorizedError: Effect()
    object ShowForbiddenError: Effect()
    object ShowNotFoundError: Effect()
    object ShowConflictError: Effect()
    object ShowServerError: Effect()
    object ShowUnknownError: Effect()
    data class NavigateToRoom(val id: String, val name: String): Effect()
}
