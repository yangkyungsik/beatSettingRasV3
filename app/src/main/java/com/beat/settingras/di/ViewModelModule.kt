package com.beat.settingras.di

import com.beat.settingras.ui.model.SslLoginViewModel
import com.beat.settingras.ui.model.ServerViewModel
import com.beat.settingras.ui.model.VideoViewModel
import com.beat.settingras.ui.model.WebViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule  = module{
    viewModel { ServerViewModel() }
    viewModel { VideoViewModel() }
    viewModel { WebViewModel(get(),get()) }
    viewModel {SslLoginViewModel()}
}