package com.beat.settingras.data.remote.source.repository

import com.beat.settingras.data.remote.source.base.BaseBoothRepository
import com.beat.settingras.data.remote.source.response.booth.BoothIdItem
import com.beat.settingras.data.remote.source.api.BoothAPI
import io.reactivex.Single

class RemoteBoothRepository(private val boothAPI: BoothAPI) : AbstractBaseRepository(),
    BaseBoothRepository {
    override fun requestBoothId(): Single<BoothIdItem> {
        return boothAPI.requestBoothId()
    }
}