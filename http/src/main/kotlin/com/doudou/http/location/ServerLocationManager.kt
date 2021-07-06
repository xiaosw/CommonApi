package com.doudou.http.location

import com.doudou.http.HttpManager
import com.doudou.http.HttpMethod
import com.doudou.http.HttpRequest
import com.doudou.http.data.ServerLocation
import com.doudou.http.params.ClientParams
import com.xiaosw.api.delegate.CallbackDelegate
import com.xiaosw.api.delegate.safeCallFail
import com.xiaosw.api.delegate.safeCallSuccess

/**
 * ClassName: [ClientParams]
 * Description:
 *
 * Create by X at 2021/06/25 17:16.
 */
object ServerLocationManager {

    private var serverLocation: ServerLocation? = null
    fun initServerLocation(url: String?, callBack: CallbackDelegate<ServerLocation>?) {
        HttpManager.request(HttpRequest.Builder()
                .setUrl(url)
                .setMethod(HttpMethod.GET)
                .build(), object : CallbackDelegate<ServerLocation> {
            override fun onSuccess(result: ServerLocation) {
                serverLocation = result
                callBack.safeCallSuccess(result)
            }

            override fun onFailure(code: Int?, errorMsg: String?) {
                callBack.safeCallFail(code, errorMsg)
            }
        })
    }

    fun getServerLocation() : ServerLocation? = serverLocation?.data ?: null
}