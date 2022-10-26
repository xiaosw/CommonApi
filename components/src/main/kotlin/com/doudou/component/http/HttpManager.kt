package com.doudou.component.http

import com.doudou.component.open.Callback

/**
 * ClassName: [HttpManager]
 * Description:
 *
 * Create by X at 2022/06/24 10:08.
 */
interface HttpManager {

    fun <T> requst(request: HttpRequest, callback: Callback<T>)

}