package com.beat.settingras.data.remote.source.base

interface BaseRepository {
    fun getDefaultHeader(): MutableMap<String, Any?>
    fun getDefaultHeader(isOAuthRequest: Boolean): MutableMap<String, Any?>
}