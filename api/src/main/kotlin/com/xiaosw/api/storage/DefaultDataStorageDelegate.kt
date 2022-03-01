package com.xiaosw.api.storage

import android.content.Context
import android.content.SharedPreferences
import com.xiaosw.api.extend.isNull
import com.xiaosw.api.manager.ThreadManager

/**
 * ClassName: [DefaultDataStorageDelegate]
 * Description:
 *
 * Create by X at 2021/08/04 11:48.
 */
class DefaultDataStorageDelegate(
    private val context: Context,
    private val name: String
) : DataStorageDelegate {

    private val sp by lazy {
        context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }


    override fun getString(key: String, def: String?) = sp.getString(key, def)

    override fun getInt(key: String, def: Int) = sp.getInt(key, def)

    override fun getLong(key: String, def: Long) = sp.getLong(key, def)

    override fun getFloat(key: String, def: Float) = sp.getFloat(key, def)

    override fun getBoolean(key: String, def: Boolean) = sp.getBoolean(key, def)

    override fun edit(): DataStorageDelegate.Editor = EditorImpl(sp)

    private class EditorImpl(private val sp: SharedPreferences) : DataStorageDelegate.Editor {
        private var edit: SharedPreferences.Editor? = null

        private inline fun judgeEdit() {
            if (edit.isNull()) {
                edit = sp.edit()
            }
        }

        override fun put(key: String, value: String?) : DataStorageDelegate.Editor {
            judgeEdit()
            edit?.putString(key, value)
            return this
        }

        override fun put(key: String, value: Int) : DataStorageDelegate.Editor {
            judgeEdit()
            edit?.putInt(key, value)
            return this
        }

        override fun put(key: String, value: Long) : DataStorageDelegate.Editor {
            judgeEdit()
            edit?.putLong(key, value)
            return this
        }

        override fun put(key: String, value: Float) : DataStorageDelegate.Editor {
            judgeEdit()
            edit?.putFloat(key, value)
            return this
        }

        override fun put(key: String, value: Boolean) : DataStorageDelegate.Editor {
            judgeEdit()
            edit?.putBoolean(key, value)
            return this
        }

        override fun remove(vararg keys: String?): DataStorageDelegate.Editor {
            if (keys?.isNotEmpty()) {
                judgeEdit()
                keys.forEach {
                    edit?.remove(it)
                }
            }
            return this
        }

        override fun clear(): DataStorageDelegate.Editor {
            judgeEdit()
            edit?.clear()
            return this
        }

        override fun commit() = edit?.let {
            ThreadManager.execute(ThreadManager.ThreadType.THREAD_TYPE_WORK) {
                it.commit()
            }
            true
        } ?: false

        override fun apply() = edit?.commit() ?: false

    }

}