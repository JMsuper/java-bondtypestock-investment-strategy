package com.example.financeapi.util;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Component
public class FinanceAPI {


    String key = "L%2Fhjnn%2Fxajcw2XmxzNMQMsB2ATjPu%2B2pAMzxLSn7ES0Yi6SGcjslJq7SP1xrgLJyH8Ca1rk5BaERd9XIWZx0KA%3D%3D";

    String uri = "https://apis.data.go.kr/1160100/service/GetStockSecuritiesInfoService/getStockPriceInfo";


    public String getStockPrice(String stockCode) throws SSLException {
        String requestUri = uri + "?serviceKey=" + key + "&resultType=json&likeSrtnCd=" + stockCode;

//        String requestUri = uri + "?serviceKey=" + key;
        SslContext context = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(context));

        WebClient wc = WebClient
                .builder()
                .baseUrl(requestUri)
                .clientConnector(new ReactorClientHttpConnector(httpClient)).build();
        Mono<String> hello = wc.get()
                .retrieve()
                .bodyToMono(String.class);
        return hello.block();
    }
}

