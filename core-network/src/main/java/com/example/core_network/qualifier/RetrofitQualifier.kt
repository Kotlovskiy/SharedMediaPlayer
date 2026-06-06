package com.example.core_network.qualifier

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UnauthorizedRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthorizedRetrofit
