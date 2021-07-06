package com.doudou.http.data

import java.io.Serializable

data class ServerLocation(
        val code: String? = null, //状态码
        val msg: String? = null,
        val data: ServerLocation? = null,
        val country: String? = null,
        val province: String? = null,
        val cityName: String? = null,
        val city: String? = null,
        val district: String? = null,
        val countryName: String? = null,
        val provinceName: String? = null,
        val position: String? = null
) : Serializable