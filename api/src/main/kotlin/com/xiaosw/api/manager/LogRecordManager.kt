package com.xiaosw.api.manager

import com.doudou.log.record.ILogRecord
import com.xiaosw.api.register.Register
import com.xiaosw.api.register.RegisterDelegate

/**
 * ClassName: [LogRecordManager]
 * Description:
 *
 * Create by X at 2021/12/07 16:13.
 */
object LogRecordManager : Register<ILogRecord> {

    internal val logRecord by lazy {
        object : ILogRecord {
            override fun onRecord(priority: Int, tag: String, msg: String) {
                mCallback.forEach {
                    it.onRecord(priority, tag, msg)
                }
            }
        }
    }

    private val mCallback by lazy {
        RegisterDelegate.createWeak<ILogRecord>()
    }

    override fun register(logRecord: ILogRecord) = mCallback.register(logRecord)

    override fun unregister(logRecord: ILogRecord) = mCallback.unregister(logRecord)

    override fun clear() = mCallback.clear()

}