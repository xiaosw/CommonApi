package com.xsw.track.util

import com.xsw.track.config.TrackConfig
import com.xsw.track.global.TrackGlobal

import java.lang.reflect.Array

class Log {

    static void i(Object... args) {
        if (!TrackConfig.debug) {
            return
        }
        if (args == null || args.length < 1) {
            return
        }
        args.each {
            if (null != it) {
                if (it.class.isArray()) {
                    print "["
                    final def length = Array.getLength(it)
                    if (length > 0) {
                        for (int i = 0; i < length; i++) {
                            def value = Array.get(it, i)
                            if (value != null) {
                                print "${value}\t"
                            } else {
                                print "null\t"
                            }
                        }
                    }
                    print "]\t"
                } else {
                    print "$it\t"
                }
            } else {
                print("null\t")
            }
        }
        println("")
    }

    static void e(String text) {
        if (!TrackConfig.debug || Utils.hasNull(text)) {
            return
        }
        java.lang.System.err.println(text)
    }

    static void e(Throwable tr) {
        if (!TrackConfig.debug || Utils.hasNull(tr)) {
            return
        }
        tr.printStackTrace()
    }

    static void help() {
        if (!TrackConfig.debug) {
            return
        }
        def is = Log.class.getClassLoader().getResourceAsStream("help.groovy")
        def baos = null
        try {
            baos = new ByteArrayOutputStream()
            int len
            byte[] buffer = new byte[1024];
            while((len = is.read(buffer)) != -1){
                baos.write(buffer,0, len)
            }
            def help = new String(baos.toByteArray())
            println("")
            println(help)
            println("")
        } finally {
            Utils.close(is)
            Utils.close(baos)
        }
    }

    static String getAccCodeName(int access) {
        return TrackGlobal.sAccCodeMap.get(access)
    }

    static String getOpName(int opCode) {
        return TrackGlobal.sOpCodeMap.get(opCode)
    }

}