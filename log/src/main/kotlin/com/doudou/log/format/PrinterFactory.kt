package com.doudou.log.format

import com.doudou.log.LogFormat

/**
 * ClassName: [PrinterFactory]
 * Description:
 *
 * Create by X at 2021/11/23 15:38.
 */
internal object PrinterFactory {

    private val mPrinters by lazy {
        mutableMapOf<String, Printer?>()
    }

    fun create(format: LogFormat?, hasJson: Boolean) : Printer = format?.let {
        if (!it.enable) {
            return@let normal()
        }
        if (hasJson) {
            return@let json(it)
        }
        format(it)
    } ?: normal()

    fun normal() = mPrinters[NormalPrinter::class.java.name] ?: NormalPrinter().also {
        mPrinters[NormalPrinter::class.java.name] = it
    }

    fun format(format: LogFormat) = mPrinters[FormatPrinter::class.java.name] ?: FormatPrinter(format).also {
        mPrinters[FormatPrinter::class.java.name] = it
    }

    fun json(format: LogFormat) = mPrinters[JsonPrinter::class.java.name] ?: JsonPrinter(format).also {
        mPrinters[JsonPrinter::class.java.name] = it
    }

}