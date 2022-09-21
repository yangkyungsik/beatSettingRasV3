package com.beat.settingras

import com.beat.settingras.di.commonRepositoryModule
import com.beat.settingras.di.networkModule
import com.beat.settingras.di.viewModelModule
import org.junit.Rule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.mock.MockProviderRule
import org.mockito.Mockito

abstract class AbstractKoinTest : KoinTest {
    @get:Rule
    val koinTestRule = KoinTestRule.create{
        androidLogger(Level.NONE)
        modules()
        modules(listOf(networkModule, commonRepositoryModule, viewModelModule))
    }

    @get:Rule
    val mockProvider = MockProviderRule.create{
            clazz-> Mockito.mock(clazz.java)
    }

}