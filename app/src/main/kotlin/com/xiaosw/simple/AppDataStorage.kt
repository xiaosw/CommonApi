package com.xiaosw.simple

import android.content.Context
import com.doudou.log.logi
import com.tencent.mmkv.MMKV
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.storage.DataStorageDelegate

/**
 * ClassName: [AppDataStorage]
 * Description:
 *
 * Create by X at 2021/08/04 14:59.
 */
class AppDataStorage(context: Context = AndroidContext.get()) : DataStorageDelegate {

    private val from by lazy {
        "MMKV"
    }

    init {
        MMKV.initialize(context)
    }

    private val editorImpl by lazy {
        EditorImpl()
    }

    override fun getString(key: String, def: String?) = MMKV.defaultMMKV().decodeString(key, def).also {
        logi("data storage get [$key : $it] from $from")
    }

    override fun getInt(key: String, def: Int) = MMKV.defaultMMKV().decodeInt(key, def).also {
        logi("data storage get [$key : $it] $from")
    }

    override fun getLong(key: String, def: Long) = MMKV.defaultMMKV().decodeLong(key, def).also {
        logi("data storage get [$key : $it] from $from")
    }

    override fun getFloat(key: String, def: Float) = MMKV.defaultMMKV().decodeFloat(key, def).also {
        logi("data storage get [$key : $it] from $from")
    }

    override fun getBoolean(key: String, def: Boolean) = MMKV.defaultMMKV().decodeBool(key, def).also {
        logi("data storage get [$key : $it] from $from")
    }

    override fun edit(): DataStorageDelegate.Editor {
        return editorImpl
    }

    private class EditorImpl : DataStorageDelegate.Editor {
        private val from by lazy {
            "MMKV"
        }

        override fun put(key: String, value: String?): DataStorageDelegate.Editor {
            MMKV.defaultMMKV().encode(key, value)
            logi("data storage put [$key : $value] from $from")
            return this
        }

        override fun put(key: String, value: Int): DataStorageDelegate.Editor {
            MMKV.defaultMMKV().encode(key, value)
            logi("data storage put [$key : $value] from $from")
            return this
        }

        override fun put(key: String, value: Long): DataStorageDelegate.Editor {
            MMKV.defaultMMKV().encode(key, value)
            logi("data storage put [$key : $value] from $from")
            return this
        }

        override fun put(key: String, value: Float): DataStorageDelegate.Editor {
            MMKV.defaultMMKV().encode(key, value)
            logi("data storage put [$key : $value] from $from")
            return this
        }

        override fun put(key: String, value: Boolean): DataStorageDelegate.Editor {
            MMKV.defaultMMKV().encode(key, value)
            logi("data storage put [$key : $value] from $from")
            return this
        }

        override fun remove(vararg keys: String?) : DataStorageDelegate.Editor {
            MMKV.defaultMMKV().removeValuesForKeys(keys)
            logi("data storage remove [$keys] from $from")
            return this
        }

        override fun clear(): DataStorageDelegate.Editor {
            MMKV.defaultMMKV().clearAll()
            logi("data storage clear from $from")
            return this
        }

        override fun commit(): Boolean {
            logi("data storage commit from $from")
            return true
        }

        override fun apply(): Boolean {
            logi("data storage apply from $from")
            return true
        }

    }
}