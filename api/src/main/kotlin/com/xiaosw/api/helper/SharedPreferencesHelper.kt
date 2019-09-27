package com.xiaosw.api.helper

import android.content.Context
import android.content.SharedPreferences
import com.xiaosw.api.AndroidContext

/**
 * @ClassName [SharedPreferencesHelper]
 * @Description
 *
 * @Date 2019-08-24.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
object SharedPreferencesHelper {

    private const val NAME = "app_data"

    /**
     * Get SharedPreferences real case object
     * @param context
     * @param name SharedPreferences file name
     * @return
     */

    @JvmStatic
    @JvmOverloads
    fun getSharedPreference(context: Context = AndroidContext.get(),
                            name: String = NAME
    ): SharedPreferences {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    /**
     * Save a value like Boolean type.
     * @param key
     * @param value
     * @param context
     * @param name SharedPreferences file name
     * @return
     */
    @JvmStatic
    @JvmOverloads
    fun putBoolean(
        key: String,
        value: Boolean,
        context: Context = AndroidContext.get(),
        name: String = NAME
    ) = getSharedPreference(context, name).edit().putBoolean(key, value).commit()

    /**
     * Save a value like int type.
     * @param key
     * @param value
     * @param context
     * @param name SharedPreferences file name
     * @return
     */
    @JvmStatic
    @JvmOverloads
    fun putInt(
        key: String,
        value: Int,
        context: Context = AndroidContext.get(),
        name: String = NAME
    ) = getSharedPreference(context, name).edit().putInt(key, value).commit()

    /**
     * Save a value like float type.
     * @param key
     * @param value
     * @param context
     * @param name SharedPreferences file name
     * @return
     */
    @JvmStatic
    @JvmOverloads
    fun putFloat(
        key: String,
        value: Float,
        context: Context = AndroidContext.get(),
        name: String = NAME
    ) = getSharedPreference(context, name).edit().putFloat(key, value).commit()

    /**
     * Save a value like long type
     * @param key
     * @param value
     * @param context
     * @param name SharedPreferences file name
     * @return
     */
    @JvmStatic
    @JvmOverloads
    fun putLong(
        key: String,
        value: Long,
        context: Context = AndroidContext.get(),
        name: String = NAME
    ) = getSharedPreference(context, name).edit().putLong(key, value).commit()

    /**
     * Save a value like String type.
     * @param key
     * @param value
     * @param context
     * @param name SharedPreferences file name
     * @return
     */
    @JvmStatic
    @JvmOverloads
    fun putString(
        key: String,
        value: String?,
        context: Context = AndroidContext.get(),
        name: String = NAME
    ) = getSharedPreference(context, name).edit().putString(key, value).commit()


    /**
     * Get String value
     * @param key
     * @param defValue Default value
     * @param context
     * @param name SharedPreferences file name
     * @return
     */
    @JvmStatic
    @JvmOverloads
    fun getString(
        key: String,
        defValue: String = "",
        context: Context = AndroidContext.get(),
        name: String = NAME
    ) = getSharedPreference(context, name).getString(key, defValue)

    /**
     * Get int value
     * @param key
     * @param defValue Default value
     * @param context
     * @param name SharedPreferences file name
     * @return
     */
    @JvmStatic
    @JvmOverloads
    fun getInt(
        key: String,
        defValue: Int = -1,
        context: Context = AndroidContext.get(),
        name: String = NAME
    ) = getSharedPreference(context, name).getInt(key, defValue)

    /**
     * Get float value
     *
     * @param key Name
     * @param defValue Default value
     * @param context
     * @param name SharedPreferences file name
     * @return
     */
    @JvmStatic
    @JvmOverloads
    fun getFloat(
        key: String,
        defValue: Float = -1f,
        context: Context = AndroidContext.get(),
        name: String = NAME
    )  = getSharedPreference(context, name).getFloat(key, defValue)

    /**
     * Get boolean value
     *
     * @param context
     * @param key Name
     * @param defValue Default value
     * @param context
     * @param name SharedPreferences file name
     * @return
     */
    @JvmStatic
    @JvmOverloads
    fun getBoolean(
        key: String,
        defValue: Boolean = false,
        context: Context = AndroidContext.get(),
        name: String = NAME
    ) = getSharedPreference(context, name).getBoolean(key, defValue)

    /**
     * Get long value
     *
     * @param context
     * @param key Name
     * @param defValue Default value
     * @param context
     * @param name SharedPreferences file name
     * @return
     */
    @JvmStatic
    @JvmOverloads
    fun getLong(
        key: String,
        defValue: Long = -1,
        context: Context = AndroidContext.get(),
        name: String = NAME
    ) = getSharedPreference(context, name).getLong(key, defValue)

    /**
     * Delete content corresponding to Key
     *
     * @param context
     * @param key Name
     * @param context
     * @param name SharedPreferences file name
     * @return
     */
    @JvmStatic
    @JvmOverloads
    fun remove(
        key: String,
        context: Context = AndroidContext.get(),
        name: String = NAME
    ) = getSharedPreference(context, name).edit().remove(key).commit()

    /**
     * Delete all the values in SharedPreferences.
     *
     * @param context
     * @param name SharedPreferences file name
     * @return
     */
    @JvmStatic
    @JvmOverloads
    fun clear(
        context: Context = AndroidContext.get(),
        name: String = NAME
    ) = getSharedPreference(context, name).edit().clear().commit()

}