package com.beat.settingras.data.remote.source.base

import com.beat.settingras.data.remote.source.response.booth.BoothIdItem
import io.reactivex.Single

/**
 * Hardware <-> Booth 통신
 */
interface BaseBoothRepository {
    // Odroid <-> Booth 간 전달
    fun requestBoothId(): Single<BoothIdItem>

}