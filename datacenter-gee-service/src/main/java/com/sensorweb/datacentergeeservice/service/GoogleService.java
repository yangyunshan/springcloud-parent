package com.sensorweb.datacentergeeservice.service;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.google.auth.http.HttpTransportFactory;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class GoogleService {

    public GoogleCredentials getCredentials() throws IOException {
        HttpTransportFactory httpTransportFactory = getHttpTransportFactory("127.0.0.1", 10808, "", "");
        return GoogleCredentials.getApplicationDefault(httpTransportFactory);

    }

    public HttpTransportFactory getHttpTransportFactory(String proxyHost, int proxyPort, String proxyName, String proxyPassword) {
        HttpHost proxyHostDetails = new HttpHost(proxyHost, proxyPort);
        HttpRoutePlanner httpRoutePlanner = new DefaultProxyRoutePlanner(proxyHostDetails);
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(proxyHostDetails.getHostName(), proxyHostDetails.getPort()),
                new UsernamePasswordCredentials(proxyName, proxyPassword)
        );
        HttpClient httpClient = ApacheHttpTransport.newDefaultHttpClientBuilder()
                .setRoutePlanner(httpRoutePlanner)
                .setProxyAuthenticationStrategy(ProxyAuthenticationStrategy.INSTANCE)
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();
        final HttpTransport httpTransport = new ApacheHttpTransport(httpClient);
        return new HttpTransportFactory() {
            @Override
            public HttpTransport create() {
                return httpTransport;
            }
        };
    }
}
