package com.beat.settingras.di

import com.beat.settingras.ui.model.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule  = module{
    viewModel { ServerViewModel() }
    viewModel { VideoViewModel() }
    viewModel { WebViewModel(get(),get()) }
    viewModel { SslLoginViewModel() }
    viewModel { SslProcessViewModel(get()) }
}