package com.beat.settingras.data.remote.source.base

import com.beat.settingras.data.remote.source.response.common.CenterVersionInfoItem
import com.beat.settingras.data.remote.source.response.common.VersionCheckItem
import com.beat.settingras.data.remote.source.response.common.VersionStatusItem
import io.reactivex.Single

/**
 * Hardware <-> 중앙 서버 통신 관련 API 작성
 */
interface BaseCommonRepository {
    fun getDefaultHeader(): MutableMap<String, Any?>
    fun getDefaultHeader(isOAuthRequest: Boolean): MutableMap<String, Any?>

    // Odroid <-> 중앙서버 간 전달
    fun requestVersionInfo(map:MutableMap<String,Any?>):Single<CenterVersionInfoItem>
    // 주기적 버전 체크
    fun requestVersionCheck(map:MutableMap<String,Any?>):Single<VersionCheckItem>
    // 업데이트 가능 여부
    fun requestVersionStatus(map:MutableMap<String,Any?>):Single<VersionStatusItem>
}