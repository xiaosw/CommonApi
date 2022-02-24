package com.doudou.log

import java.io.Serializable

/**
 * ClassName: [LogConfig]
 * Description:
 *
 * Create by X at 2021/11/23 14:27.
 */
data class LogConfig @JvmOverloads constructor (
    val behavior: Logger.Behavior,
    val preTag: String? = null,
    val maxLen: Int = MAX_LEN,
    val format: LogFormat? = LogFormat(),
    val loggerWrapperClassList: MutableList<Class<*>?>? = null
) : Serializable {
    companion object {
        const val MAX_LEN = 3000
    }
}

data class LogFormat @JvmOverloads constructor (
    val enable: Boolean = true,
    val formatJson: Boolean = true,
    val dividerLine: String = DIVIDER_LINE,
    val firstFormatLineHeader: String = LINE_HEADER_FIRST,
    val formatLineHeader: String = LINE_HEADER,
    val lastFormatLineHeader: String = LINE_HEADER_LAST
) : Serializable {
    val newLine = NEW_LINE
    companion object {
        const val NEW_LINE = "\n"
        const val LINE_HEADER_FIRST = "╔"
        const val LINE_HEADER = "║"
        const val LINE_HEADER_LAST = "╚"

        const val DIVIDER_LINE = "═════════════════════════" +
                "══════════════════════════════════════════" +
                "══════════════════════════════════════════"
    }
}