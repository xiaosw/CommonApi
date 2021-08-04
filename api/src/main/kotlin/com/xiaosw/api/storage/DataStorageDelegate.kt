package com.xiaosw.api.storage

/**
 * ClassName: [DataStorageDelegate]
 * Description:
 *
 * Create by X at 2021/08/04 11:40.
 */
interface DataStorageDelegate {

    fun getString(key: String, def: String? = null) : String?

    fun getInt(key: String, def: Int = 0) : Int

    fun getLong(key: String, def: Long = 0) : Long

    fun getFloat(key: String, def: Float = 0F) : Float

    fun getBoolean(key: String, def: Boolean = false) : Boolean

    fun edit() : Editor

    interface Editor {

        fun put(key: String, value: String?) : Editor

        fun put(key: String, value: Int) : Editor

        fun put(key: String, value: Long) : Editor

        fun put(key: String, value: Float) : Editor

        fun put(key: String, value: Boolean) : Editor

        fun remove(vararg keys: String?) : Editor

        fun clear() : Editor

        fun commit() : Boolean

        fun apply() : Boolean
    }
}