package com.doudou.http.interceptor;


import com.doudou.log.LogConfig;
import com.doudou.log.LogFormat;
import com.doudou.log.Logger;
import com.xiaosw.api.util.Utils;

import kotlin.text.Charsets;
import okhttp3.*;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class HttpLoggingInterceptor implements Interceptor {
    private static final Charset UTF8 = Charsets.UTF_8;

    public enum Level {
        /**
         * No logs.
         */
        NONE,
        /**
         * Logs request and response lines.
         *
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1 (3-byte body)
         *
         * <-- 200 OK (22ms, 6-byte body)
         * }</pre>
         */
        BASIC,
        /**
         * Logs request and response lines and their respective headers.
         *
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         * <-- END HTTP
         * }</pre>
         */
        HEADERS,
        /**
         * Logs request and response lines and their respective headers and bodies (if present).
         *
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         *
         * Hi?
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         *
         * Hello!
         * <-- END HTTP
         * }</pre>
         */
        BODY
    }

    private volatile Level level = Level.NONE;

    /**
     * Change the level at which this interceptor logs.
     */
    public HttpLoggingInterceptor setLevel(Level level) {
        if (level == null) {
            throw new NullPointerException("level == null. Use Level.NONE instead.");
        }
        this.level = level;
        return this;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Level level = this.level;

        Request request = chain.request();
        if (level == Level.NONE) {
            return chain.proceed(request);
        }

        boolean logBody = level == Level.BODY;
        boolean logHeaders = logBody || level == Level.HEADERS;

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        Connection connection = chain.connection();
        final long startNs = System.nanoTime();
        String nlh = "";
        LogConfig logConfig = Logger.INSTANCE.getLogConfig();
        if (null != logConfig) {
            LogFormat logFormat = logConfig.getFormat();
            if (null != logFormat) {
                String lh = logFormat.getFormatLineHeader();
                if (null != lh) {
                    nlh = lh;
                }
            }
        }
        Logger.INSTANCE.getLogConfig().getFormat().getFirstFormatLineHeader();
        StringBuffer sb = new StringBuffer("request --> id = ").append(startNs).append("\n")
                .append(nlh).append("url = ").append(request.url()).append("\n")
                .append(nlh).append("method = ").append(request.method()).append("\n")
                .append(nlh).append("protocol = ").append(connection != null ? " " + connection.protocol() : "").append("\n");
        if (!logHeaders && hasRequestBody) {
            sb.append(" (" + requestBody.contentLength() + "-byte body)\n");
        }
        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody.contentType() != null) {
                    sb.append(nlh).append("Content-Type: ").append(requestBody.contentType()).append("\n");
                }
                if (requestBody.contentLength() != -1) {
                    sb.append(nlh).append("Content-Length: ").append(requestBody.contentLength()).append("\n");
                }
            }

            Headers headers = request.headers();
            if (null != headers && headers.size() > 0) {
                sb.append(nlh).append("\n")
                        .append(nlh).append("Header:\n");
                for (int i = 0, count = headers.size(); i < count; i++) {
                    String name = headers.name(i);
                    // Skip headers from the request body as they are explicitly logged above.
                    if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                        sb.append(nlh).append(name).append(": ").append(headers.value(i)).append("\n");
                    }
                }
            }

            if (!logBody || !hasRequestBody) {
                sb.append(nlh).append("--> END ").append(request.method());
            } else if (bodyHasUnknownEncoding(request.headers())) {
                sb.append(nlh).append("--> END ").append(request.method()).append(" (encoded body omitted)");
            } else {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }
                if (isPlaintext(buffer)) {
                    String result = buffer.readString(charset);
                    sb.append(nlh).append("Params: ").append(result).append("\n")
                            .append(nlh).append("--> END ").append(request.method()).append(" (").append(requestBody.contentLength()).append("-byte body)");
                } else {
                    sb.append(nlh).append("--> END ").append(request.method()).append(" (binary ")
                            .append(requestBody.contentLength()).append("-byte body omitted)");
                }
            }
            Logger.i(sb.toString());
        }

        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            Logger.e(sb.toString(), Logger.findTag(), e);
            throw e;
        }
        sb = new StringBuffer().append("response --> id = ").append(startNs).append("\n")
                .append(nlh).append("url = ").append(request.url()).append("\n")
                .append(nlh).append("method = ").append(request.method());
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
        sb.append("\n").append(nlh).append("code = ").append(response.code()).append("\n")
                .append(nlh).append("message = ").append(response.message()).append("\n")
                .append(nlh).append("duration = ").append(tookMs).append("ms\n");
        if (logHeaders) {
            Headers headers = response.headers();
            sb.append(nlh).append("\n").append(nlh).append("Header:");
            for (int i = 0, count = headers.size(); i < count; i++) {
                String v = headers.value(i);
                if (!Utils.isEmpty(v)) {
                    v = v.replace("[", "【").replace("]", "】");
                }
                sb.append("\n").append(nlh).append(headers.name(i)).append(": ").append(v);
            }

            if (!logBody || !HttpHeaders.hasBody(response)) {
//                sb.append(nlh).append("<-- END HTTP");
            } else if (bodyHasUnknownEncoding(response.headers())) {
//                sb.append(nlh).append("<-- END HTTP (encoded body omitted)");
            } else {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.buffer();

                Long gzippedLength = null;
                if ("gzip".equalsIgnoreCase(headers.get("Content-Encoding"))) {
                    gzippedLength = buffer.size();
                    GzipSource gzippedResponseBody = null;
                    try {
                        gzippedResponseBody = new GzipSource(buffer.clone());
                        buffer = new Buffer();
                        buffer.writeAll(gzippedResponseBody);
                    } finally {
                        if (gzippedResponseBody != null) {
                            gzippedResponseBody.close();
                        }
                    }
                }

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                if (!isPlaintext(buffer)) {
                    sb.append(nlh).append("<-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
                    Logger.i(sb.toString());
                    return response;
                }

                if (contentLength != 0) {
                    String result = buffer.clone().readString(charset);
                    sb.append(nlh).append("\n").append(nlh).append("result: ").append(result);
                }

                if (gzippedLength != null) {
                    sb.append("\n").append(nlh).append("<-- END HTTP (" + buffer.size() + "-byte, "
                            + gzippedLength + "-gzipped-byte body)");
                } else {
                    sb.append("\n").append(nlh).append("<-- END HTTP (" + buffer.size() + "-byte body)");
                }
            }
        } else {
            sb.append(nlh).append("body size: ").append(bodySize);
        }
        Logger.i(sb.toString());
        return response;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyHasUnknownEncoding(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null
                && !contentEncoding.equalsIgnoreCase("identity")
                && !contentEncoding.equalsIgnoreCase("gzip");
    }
}
