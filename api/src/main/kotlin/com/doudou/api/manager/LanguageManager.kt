package com.doudou.api.manager

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import com.doudou.log.loge
import com.doudou.log.logi
import com.xiaosw.api.R
import com.xiaosw.api.extend.optionalImpl
import com.xiaosw.api.storage.DataStorageManager
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * ClassName: [LanguageManager]
 * Description:
 */
object LanguageManager {

    private const val KEY_LAST_SET_LANGUAGE_FROM_USER = "last_set_language_from_user"

    private val isInit = AtomicBoolean(false)
    private lateinit var mApp : Application
    private val mLocalCache = mutableMapOf<String, Locale>().also {
        it[Locale.ENGLISH.toLanguageKey()] = Locale.ENGLISH
        it[Locale.CHINA.toLanguageKey()] = Locale.CHINA
        it[Locale.CHINESE.toLanguageKey()] = Locale.CHINESE
        it[Locale.TAIWAN.toLanguageKey()] = Locale.TAIWAN
        it[Locale.GERMANY.toLanguageKey()] = Locale.GERMANY
        it[Locale.KOREAN.toLanguageKey()] = Locale.KOREAN
        it[Locale.JAPANESE.toLanguageKey()] = Locale.JAPANESE
    }

    var sCurrentLocale = Locale.ENGLISH

    fun init(context: Context
             , languageLangId: Int = R.string.str_language_lang
             , languageCountryId: Int = R.string.str_language_country
    ) : Boolean {
        if (isInit.get()) {
            loge { "initialized!" }
            return true
        }
        if (null == context) {
            loge { "context is null!" }
            return false
        }
        mApp = (context as? Application) ?: (context.applicationContext as Application)
        mApp.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks by optionalImpl(){
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                checkCurrent(activity)
            }
        })
        val lastCacheKey = DataStorageManager.getString(KEY_LAST_SET_LANGUAGE_FROM_USER, "")
        logi { "user set language is: $lastCacheKey" }
        if (!lastCacheKey.isNullOrEmpty()) {
            val split = lastCacheKey.split("_")
            if (split != null && split.size == 2) {
                changeLanguageLocked(mApp, false, split[0], split[1])
                return true
            }
        }
        changeLanguageLocked(context,false, context.resources.getString(languageLangId)
            , context.resources.getString(languageCountryId))
        return true
    }

    /**
     * Change language
     *
     * @param lang [Locale.getLanguage]
     * @param country [Locale.getCountry]
     */
    fun switchAppLanguage(context: Context, lang: String, country: String? = null) = try {
        changeLanguageLocked(context, true, lang, country)
    } catch (e: Exception) {
        loge(e)
        false
    }

    /**
     * Check current
     *
     * @param context
     */
    fun checkCurrent(context: Context?) : Boolean {
        try {
            if (null == context) {
                return false
            }
            if (context.getLocale().internalEquals(sCurrentLocale)) {
                logi { "$context to language and current language same! $sCurrentLocale" }
                return true
            }
            return changeLanguageLocked(context, false, sCurrentLocale.language
                , sCurrentLocale.country)
        } catch (e: Exception) {
            loge(e)
            return false
        }
    }

    private fun changeLanguageLocked(
        context: Context
        , fromUser: Boolean
        , lang: String
        , country: String? = null
    ) : Boolean {
        try {
            val fromLocal = context.getLocale()
            val toKey = toLanguageKey(lang, country)
            sCurrentLocale = mLocalCache[toKey] ?: Locale(lang, country).also {
                mLocalCache[toKey] = it
            }
            if (fromUser) {
                DataStorageManager.put(KEY_LAST_SET_LANGUAGE_FROM_USER, toKey)
            }
            if (fromLocal.internalEquals(sCurrentLocale)) {
                logi { "$context to language and current language same! $sCurrentLocale, $fromUser" }
                return true
            }
            changeLanguage(context, sCurrentLocale, fromUser)
            if (context !is Application) {
                changeLanguage(context.applicationContext, sCurrentLocale, fromUser)
            }
            return true
        } catch (e: Exception) {
            loge(e)
            return true
        }
    }

    private fun changeLanguage(context: Context, locale: Locale, fromUser: Boolean) {
        try {
            val fromLocal = context.getLocale()
            if (fromLocal.internalEquals(locale)) {
                return
            }
            with(context.resources) {
                configuration.setLocale(locale)
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    updateConfiguration(configuration, displayMetrics)
//                    return
//                }
//                context.createConfigurationContext(configuration)
            }
            val localeAfter = context.getLocale()
            logi { "$context change language $fromLocal: ${fromLocal.language}, ${fromLocal.country} to $localeAfter: ${localeAfter.language}, ${localeAfter.country}. $fromUser" }
        } catch (e: Exception) {
            loge(e)
        }
    }

    private fun Locale?.internalEquals(other: Locale?) = this?.let {
        (language?.equals(other?.language, true) == true)
                && (country?.equals(other?.country) == true)
    } ?: (other == null)

    private fun Context.getLocale() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        resources.configuration.locales.get(0)
    } else resources.configuration.locale

    private fun Locale.toLanguageKey() = toLanguageKey(language, country)

    private fun toLanguageKey(language: String, country: String?) =
        "${language}_${country ?: ""}"

}