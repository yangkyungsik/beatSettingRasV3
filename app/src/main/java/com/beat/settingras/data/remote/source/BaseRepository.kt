package com.beat.settingras.data.remote.source

interface BaseRepository {
    fun getDefaultParams(): MutableMap<String, Any>
    fun getDefaultParams(isOAuthRequest: Boolean): MutableMap<String, Any>
}