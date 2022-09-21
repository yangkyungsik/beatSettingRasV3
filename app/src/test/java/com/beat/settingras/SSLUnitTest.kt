package com.beat.settingras

import com.beat.settingras.data.remote.source.mock.MockRemoteSSLRepository
import com.beat.settingras.ui.model.SslProcessViewModel
import kotlinx.coroutines.GlobalScope
import org.junit.Before
import org.junit.Test
import org.koin.test.inject

class SSLUnitTest : AbstractKoinTest() {
    val viewmodel:SslProcessViewModel by inject()
    @Before
    fun setup(){

    }

    @Test
    fun test1(){

    }
}