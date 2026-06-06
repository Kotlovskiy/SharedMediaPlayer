package com.example.core_network.qualifier

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class UnauthorizedOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class AuthorizedOkHttpClient
