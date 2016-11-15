package info.futureme.abs.rest;

import java.io.IOException;

import info.futureme.abs.util.DLog;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSource;


/**
 * logging utils for network debugging
 * @author Jeffrey
 * @version 1.0
 * @updated 26-一月-2016 15:53:52
 */
public class TimberLoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        DLog.i("send:", "Sending %s request on %s%n%s", request.method(), request.url(), request.headers());
        DLog.v("request:", "REQUEST BODY BEGIN\n%s\nREQUEST BODY END", requestBodyToString(request));

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        DLog.i("receive:", "Received %s response for %s in %.1fms%n%s", request.method(), response.request().url(), (t2 - t1) / 1e6d, response.headers());
        DLog.v("response:", "RESPONSE BODY BEGIN:\n%s\nRESPONSE BODY END", responseBodyToString(response));
        return response;
    }

    public static String requestBodyToString(final Request request){
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            if(copy.body() != null) {
                copy.body().writeTo(buffer);
            }
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    public static String responseBodyToString(final Response response){
        try{
            Buffer buffer = new Buffer();
            if(response.body() != null) {
                BufferedSource source = response.body().source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                buffer = source.buffer().clone();
            }
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
