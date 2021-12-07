package com.doudou.log

import com.doudou.log.annotation.Level
import com.doudou.log.record.ILogRecord
import java.io.Serializable

/**
 * ClassName: [LogConfig]
 * Description:
 *
 * Create by X at 2021/11/23 14:27.
 */
data class LogConfig @JvmOverloads constructor (
    @Level val level: Int,
    val preTag: String? = null,
    val maxLen: Int = MAX_LEN,
    val format: LogFormat? = LogFormat(),
    val record: ILogRecord? = null
) : Serializable {
    companion object {
        const val MAX_LEN = 5000
    }
}

data class LogFormat @JvmOverloads constructor (
    val enable: Boolean = true,
    val formatJson: Boolean = true,
    val newLine: String = NEW_LINE,
    val dividerLine: String = DIVIDER_LINE,
    val firstFormatLineHeader: String = LINE_HEADER_FIRST,
    val formatLineHeader: String = LINE_HEADER,
    val lastFormatLineHeader: String = LINE_HEADER_LAST
) : Serializable {
    companion object {
        const val NEW_LINE = "\n"
        const val LINE_HEADER_FIRST = "$NEW_LINE╔"
        const val LINE_HEADER = "║"
        const val LINE_HEADER_LAST = "$NEW_LINE╚"

        const val DIVIDER_LINE = "═════════════════════════" +
                "══════════════════════════════════════════" +
                "══════════════════════════════════════════"
    }
}