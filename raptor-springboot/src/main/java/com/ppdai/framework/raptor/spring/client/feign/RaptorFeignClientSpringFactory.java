package com.ppdai.framework.raptor.spring.client.feign;

import com.ppdai.framework.raptor.common.URLParamType;
import com.ppdai.framework.raptor.rpc.URL;
import com.ppdai.framework.raptor.spring.client.RaptorClientFactory;
import feign.*;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.slf4j.Slf4jLogger;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author yinzuolong
 */
@Setter
@Getter
public class RaptorFeignClientSpringFactory extends RaptorClientFactory.BaseFactory {

    private static final String LIBRARY = "spring";

    private Encoder encoder;
    private Decoder decoder;
    private ErrorDecoder errorDecoder;
    private Contract contract;
    private Client client;
    private Retryer retryer;
    private List<RequestInterceptor> requestInterceptors;
    private Request.Options options;

    @Override
    public <T> T create(Class<T> type, URL url) {
        Feign.Builder builder = feignBuilder();
        builder.logger(new Slf4jLogger(type));
        builder.options(createOptions(url));
        return builder.target(type, url.getUri());
    }

    private Request.Options createOptions(URL url) {
        Request.Options options = this.options;
        if (options == null) {
            options = new Request.Options();
        }
        int connectTimeout = options.connectTimeoutMillis();
        int readTimeoutMillis = options.readTimeoutMillis();
        String connectTimeoutParam = url.getParameter(URLParamType.connectTimeout.getName());
        if (!StringUtils.isEmpty(connectTimeoutParam)) {
            connectTimeout = Integer.parseInt(connectTimeoutParam);
        }
        String socketTimeoutParam = url.getParameter(URLParamType.socketTimeout.getName());
        if (!StringUtils.isEmpty(socketTimeoutParam)) {
            readTimeoutMillis = Integer.parseInt(socketTimeoutParam);
        }
        return new Request.Options(connectTimeout, readTimeoutMillis);
    }

    @Override
    public boolean support(Class<?> type, URL url) {
        return LIBRARY.equalsIgnoreCase(getLibrary(type));
    }

    protected Feign.Builder feignBuilder() {
        Feign.Builder builder = Feign.builder();
        if (this.encoder != null) {
            builder.encoder(encoder);
        }
        if (this.decoder != null) {
            builder.decoder(decoder);
        }
        if (this.contract != null) {
            builder.contract(contract);
        }
        if (this.client != null) {
            builder.client(client);
        }
        if (this.errorDecoder != null) {
            builder.errorDecoder(errorDecoder);
        }
        if (this.requestInterceptors != null) {
            builder.requestInterceptors(requestInterceptors);
        }
        if (this.retryer != null) {
            builder.retryer(retryer);
        }
        return builder;
    }
}
