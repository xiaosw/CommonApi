package com.doudou.log.annotation

import androidx.annotation.IntDef
import com.doudou.log.Logger
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@IntDef(Logger.VERBOSE, Logger.DEBUG, Logger.INFO, Logger.WARN, Logger.ERROR, Logger.NONE)
@Retention(RetentionPolicy.SOURCE)
annotation class Level()
