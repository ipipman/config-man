package cn.ipman.config.client.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import lombok.Generated;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/5/4 19:18
 */
public interface HttpUtils {
    Logger log = LoggerFactory.getLogger(HttpUtils.class);
    HttpInvoker Default = new OkHttpInvoker();

    static HttpInvoker getDefault() {
        if (((OkHttpInvoker) Default).isInitialized()) {
            return Default;
        } else {
            int timeout = Integer.parseInt(System.getProperty("utils.http.timeout", "1000"));
            int maxIdleConnections = Integer.parseInt(System.getProperty("utils.http.maxconn", "128"));
            int keepAliveDuration = Integer.parseInt(System.getProperty("utils.http.keepalive", "300"));
            ((OkHttpInvoker) Default).init(timeout, maxIdleConnections, keepAliveDuration);
            return Default;
        }
    }

    static String get(String url) {
        return getDefault().get(url);
    }

    static String post(String requestString, String url) {
        return getDefault().post(requestString, url);
    }

    static <T> T httpGet(String url, Class<T> clazz) {
        try {
            log.debug(" =====>>>>>> httpGet: " + url);
            String respJson = get(url);
            log.debug(" =====>>>>>> response: " + respJson);
            return JSON.parseObject(respJson, clazz);
        } catch (Throwable var3) {
            throw var3;
        }
    }

    static <T> T httpGet(String url, TypeReference<T> typeReference) {
        try {
            log.debug(" =====>>>>>> httpGet: " + url);
            String respJson = get(url);
            log.debug(" =====>>>>>> response: " + respJson);
            return JSON.parseObject(respJson, typeReference, new Feature[0]);
        } catch (Throwable var3) {
            throw var3;
        }
    }

    static <T> T httpPost(String requestString, String url, Class<T> clazz) {
        try {
            log.debug(" =====>>>>>> httpPost: " + url);
            String respJson = post(requestString, url);
            log.debug(" =====>>>>>> response: " + respJson);
            return JSON.parseObject(respJson, clazz);
        } catch (Throwable var4) {
            throw var4;
        }
    }

    static <T> T httpPost(String requestString, String url, TypeReference<T> typeReference) {
        try {
            log.debug(" =====>>>>>> httpPost: " + url);
            String respJson = post(requestString, url);
            log.debug(" =====>>>>>> response: " + respJson);
            return JSON.parseObject(respJson, typeReference, new Feature[0]);
        } catch (Throwable var4) {
            throw var4;
        }
    }

    static void main(String[] args) {
        System.setProperty("utils.http.timeout", "60000");
        System.out.println(httpGet("https://httpbin.org/get", HttpBin.class));
    }

    public interface HttpInvoker {
        String post(String var1, String var2);

        String get(String var1);
    }

    public static class OkHttpInvoker implements HttpInvoker {
        @Generated
        private static final Logger log = LoggerFactory.getLogger(OkHttpInvoker.class);
        static final MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");
        boolean initialized = false;
        OkHttpClient client;

        public OkHttpInvoker() {
        }

        public void init(int timeout, int maxIdleConnections, int keepAliveDuration) {
            this.client = (new OkHttpClient.Builder()).connectionPool(new ConnectionPool(maxIdleConnections, (long) keepAliveDuration, TimeUnit.SECONDS)).readTimeout((long) timeout, TimeUnit.MILLISECONDS).writeTimeout((long) timeout, TimeUnit.MILLISECONDS).connectTimeout((long) timeout, TimeUnit.MILLISECONDS).retryOnConnectionFailure(true).build();
            this.initialized = true;
        }

        public String post(String requestString, String url) {
            log.debug(" ===> post  url = {}, requestString = {}", requestString, url);
            Request request = (new Request.Builder()).url(url).post(RequestBody.create(requestString, JSONTYPE)).build();

            try {
                String respJson = this.client.newCall(request).execute().body().string();
                log.debug(" ===> respJson = " + respJson);
                return respJson;
            } catch (Exception var5) {
                throw new RuntimeException(var5);
            }
        }

        public String get(String url) {
            log.debug(" ===> get url = " + url);
            Request request = (new Request.Builder()).url(url).get().build();

            try {
                String respJson = this.client.newCall(request).execute().body().string();
                log.debug(" ===> respJson = " + respJson);
                return respJson;
            } catch (Exception var4) {
                throw new RuntimeException(var4);
            }
        }

        @Generated
        public boolean isInitialized() {
            return this.initialized;
        }
    }

    public static class HttpBin {
        String origin;
        String url;
        Map<String, String> args;
        Map<String, String> headers;

        @Generated
        public HttpBin() {
        }

        @Generated
        public String getOrigin() {
            return this.origin;
        }

        @Generated
        public String getUrl() {
            return this.url;
        }

        @Generated
        public Map<String, String> getArgs() {
            return this.args;
        }

        @Generated
        public Map<String, String> getHeaders() {
            return this.headers;
        }

        @Generated
        public void setOrigin(String origin) {
            this.origin = origin;
        }

        @Generated
        public void setUrl(String url) {
            this.url = url;
        }

        @Generated
        public void setArgs(Map<String, String> args) {
            this.args = args;
        }

        @Generated
        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }

        @Generated
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof HttpBin)) {
                return false;
            } else {
                cn.ipman.config.client.utils.HttpUtils.HttpBin other = (cn.ipman.config.client.utils.HttpUtils.HttpBin) o;
                if (!other.canEqual(this)) {
                    return false;
                } else {
                    label59:
                    {
                        Object this$origin = this.getOrigin();
                        Object other$origin = other.getOrigin();
                        if (this$origin == null) {
                            if (other$origin == null) {
                                break label59;
                            }
                        } else if (this$origin.equals(other$origin)) {
                            break label59;
                        }

                        return false;
                    }

                    Object this$url = this.getUrl();
                    Object other$url = other.getUrl();
                    if (this$url == null) {
                        if (other$url != null) {
                            return false;
                        }
                    } else if (!this$url.equals(other$url)) {
                        return false;
                    }

                    Object this$args = this.getArgs();
                    Object other$args = other.getArgs();
                    if (this$args == null) {
                        if (other$args != null) {
                            return false;
                        }
                    } else if (!this$args.equals(other$args)) {
                        return false;
                    }

                    Object this$headers = this.getHeaders();
                    Object other$headers = other.getHeaders();
                    if (this$headers == null) {
                        if (other$headers != null) {
                            return false;
                        }
                    } else if (!this$headers.equals(other$headers)) {
                        return false;
                    }

                    return true;
                }
            }
        }

        @Generated
        protected boolean canEqual(Object other) {
            return other instanceof HttpBin;
        }

        @Generated
        public int hashCode() {
            boolean PRIME = true;
            int result = 1;
            Object $origin = this.getOrigin();
            result = result * 59 + ($origin == null ? 43 : $origin.hashCode());
            Object $url = this.getUrl();
            result = result * 59 + ($url == null ? 43 : $url.hashCode());
            Object $args = this.getArgs();
            result = result * 59 + ($args == null ? 43 : $args.hashCode());
            Object $headers = this.getHeaders();
            result = result * 59 + ($headers == null ? 43 : $headers.hashCode());
            return result;
        }

        @Generated
        public String toString() {
            String var10000 = this.getOrigin();
            return "HttpUtils.HttpBin(origin=" + var10000 + ", url=" + this.getUrl() + ", args=" + this.getArgs() + ", headers=" + this.getHeaders() + ")";
        }
    }
}
