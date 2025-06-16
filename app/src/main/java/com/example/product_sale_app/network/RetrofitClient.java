package com.example.product_sale_app.network;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://192.168.50.72:7050/";

    private static Retrofit retrofit = null;
    private static OkHttpClient okHttpClient = null;

    private static OkHttpClient getOkHttpClientInstance() {
        if (okHttpClient == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
            httpClientBuilder.addInterceptor(loggingInterceptor);

            // --- For Development ONLY: To trust self-signed HTTPS certificates ---
            // This setup trusts all certificates and specific hostnames (like your IP)
            try {
                final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                            @Override
                            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                            @Override
                            public X509Certificate[] getAcceptedIssuers() {
                                return new X509Certificate[]{};
                            }
                        }
                };
                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                httpClientBuilder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);

                // Be specific about which hostnames to trust if not using a valid CA
                httpClientBuilder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        // Allow your specific IP and localhost for development
                        return hostname.equals("192.168.50.72") || hostname.equals("localhost");
                    }
                });
                // If you absolutely must trust all hostnames (less secure):
                // httpClientBuilder.hostnameVerifier((hostname, session) -> true);

            } catch (Exception e) {
                throw new RuntimeException("Failed to configure SSL for OkHttpClient", e);
            }
            // --- End Development ONLY SSL ---

            httpClientBuilder.connectTimeout(30, TimeUnit.SECONDS);
            httpClientBuilder.readTimeout(30, TimeUnit.SECONDS);
            httpClientBuilder.writeTimeout(30, TimeUnit.SECONDS);

            okHttpClient = httpClientBuilder.build();
        }
        return okHttpClient;
    }

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getOkHttpClientInstance()) // Use the shared OkHttpClient
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Generic method to create any service
    public static <S> S createService(Class<S> serviceClass) {
        return getRetrofitInstance().create(serviceClass);
    }
}
