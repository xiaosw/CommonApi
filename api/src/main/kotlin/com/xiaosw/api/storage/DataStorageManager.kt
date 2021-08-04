package com.xiaosw.api.storage

import com.xiaosw.api.AndroidContext
import com.xiaosw.api.init.Initializer1Delegate

/**
 * ClassName: [DataStorageManager]
 * Description:
 *
 * Create by X at 2021/08/04 11:27.
 */
class DataStorageManager private constructor(): Initializer1Delegate<DataStorageDelegate>(), DataStorageDelegate {

    private lateinit var mDelegate: DataStorageDelegate

    override fun onInit(delegate: DataStorageDelegate?) : Boolean {
        mDelegate = delegate
            ?: DefaultDataStorageDelegate(AndroidContext.get(), AndroidContext.get().packageName.replace(".", "_"))
        return true
    }

    fun put(key: String, value: String?) = edit().put(key, value).apply()

    fun put(key: String, value: Int) = edit().put(key, value).apply()

    fun put(key: String, value: Long) = edit().put(key, value).apply()

    fun put(key: String, value: Float) = edit().put(key, value).apply()

    fun put(key: String, value: Boolean) = edit().put(key, value).apply()

    override fun getString(key: String, def: String?) = mDelegate.getString(key)

    override fun getInt(key: String, def: Int) = mDelegate.getInt(key)

    override fun getLong(key: String, def: Long) = mDelegate.getLong(key)

    override fun getFloat(key: String, def: Float) = mDelegate.getFloat(key)

    override fun getBoolean(key: String, def: Boolean) = mDelegate.getBoolean(key)

    override fun edit() = mDelegate.edit()

    companion object {

        private val GLOBAL by lazy {
            DataStorageManager()
        }

        fun init(delegate: DataStorageDelegate? = null) = GLOBAL.init(delegate)

        fun put(key: String, value: String?) = GLOBAL.put(key, value)

        fun put(key: String, value: Int) = GLOBAL.put(key, value)

        fun put(key: String, value: Long) = GLOBAL.put(key, value)

        fun put(key: String, value: Float) = GLOBAL.put(key, value)

        fun put(key: String, value: Boolean) = GLOBAL.put(key, value)

        fun getString(key: String, def: String? = null) = GLOBAL.getString(key, def)

        fun getInt(key: String, def: Int = 0) = GLOBAL.getInt(key, def)

        fun getLong(key: String, def: Long = 0) = GLOBAL.getLong(key, def)

        fun getFloat(key: String, def: Float = 0F) = GLOBAL.getFloat(key, def)

        fun getBoolean(key: String, def: Boolean = false) = GLOBAL.getBoolean(key, def)

        fun edit() = GLOBAL.edit()

        fun newInstance(delegate: DataStorageDelegate? = null) = DataStorageManager().also {
            it.init(delegate)
        }

    }
}