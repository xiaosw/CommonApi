package com.doudou.log.inject

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.doudou.log.AndroidContext
import com.doudou.log.Logger
import com.doudou.log.internal.LogFactory

/**
 * ClassName: [AutoInjectProvider]
 * Description:
 *
 * Create by X at 2022/06/21 10:00.
 */
class AutoInjectProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        AndroidContext.init(context)
        Logger.logFactory = LogFactory()
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ) : Cursor? = null

    override fun getType(uri: Uri) : String? = null

    override fun insert(uri: Uri, values: ContentValues?) : Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = -1

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ) = -1
}