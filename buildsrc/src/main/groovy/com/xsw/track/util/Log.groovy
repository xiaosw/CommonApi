package com.xsw.track.util

import com.xsw.track.config.TrackConfig

class Log {

    static void i(String text) {
        if (!TrackConfig.isDebug()) {
            return
        }
        if (Utils.hasNull(text)) {
            return
        }
        println(text)
    }

    static void help() {
        if (!TrackConfig.isDebug()) {
            return
        }
        def is = Log.class.getClassLoader().getResourceAsStream("help.groovy")
        def baos = null
        try {
            baos = new ByteArrayOutputStream();
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

}