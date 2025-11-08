package co.edu.uco.apigatewayservice.filter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Component
public class SanitizeJsonBodyGlobalFilter implements GlobalFilter, Ordered {

    private static final Set<String> SENSITIVE_KEYS = Arrays.stream(new String[] {
            "access_token", "refresh_token", "id_token", "token",
            "authorization", "Authorization", "bearer", "Bearer"
    }).map(key -> key.toLowerCase(Locale.ROOT)).collect(Collectors.toSet());

    private final ObjectMapper objectMapper;

    public SanitizeJsonBodyGlobalFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();

        originalResponse.beforeCommit(() -> {
            applySecurityHeaders(originalResponse.getHeaders());
            return Mono.empty();
        });

        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                HttpHeaders headers = getHeaders();
                applySecurityHeaders(headers);

                if (!shouldSanitize(headers)) {
                    return super.writeWith(body);
                }

                return Flux.from(body)
                        .collectList()
                        .flatMap(dataBuffers -> {
                            if (dataBuffers.isEmpty()) {
                                headers.remove(HttpHeaders.CONTENT_LENGTH);
                                return super.writeWith(Mono.just(bufferFactory.wrap(new byte[0])));
                            }

                            int totalLength = dataBuffers.stream()
                                    .mapToInt(DataBuffer::readableByteCount)
                                    .sum();
                            byte[] content = new byte[totalLength];
                            int offset = 0;
                            for (DataBuffer dataBuffer : dataBuffers) {
                                int readable = dataBuffer.readableByteCount();
                                dataBuffer.read(content, offset, readable);
                                offset += readable;
                                DataBufferUtils.release(dataBuffer);
                            }

                            String originalBody = new String(content, StandardCharsets.UTF_8);
                            String sanitizedBody = sanitizeJson(originalBody);
                            byte[] bytesToWrite = sanitizedBody.equals(originalBody)
                                    ? content
                                    : sanitizedBody.getBytes(StandardCharsets.UTF_8);

                            headers.remove(HttpHeaders.CONTENT_LENGTH);
                            return super.writeWith(Mono.just(bufferFactory.wrap(bytesToWrite)));
                        });
            }

            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
                return writeWith(Flux.from(body).flatMapSequential(Function.identity()));
            }
        };

        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    private boolean shouldSanitize(HttpHeaders headers) {
        MediaType mediaType = headers.getContentType();
        return mediaType != null && MediaType.APPLICATION_JSON.isCompatibleWith(mediaType);
    }

    private void applySecurityHeaders(HttpHeaders headers) {
        headers.remove(HttpHeaders.AUTHORIZATION);
        headers.remove(HttpHeaders.WWW_AUTHENTICATE);
        headers.setCacheControl("no-store");
        headers.setPragma("no-cache");
    }

    private String sanitizeJson(String body) {
        if (body == null || body.isBlank()) {
            return body;
        }

        try {
            Object json = objectMapper.readValue(body, Object.class);
            Object sanitized = sanitizeObject(json);
            if (sanitized == json) {
                return body;
            }
            return objectMapper.writeValueAsString(sanitized);
        } catch (Exception ex) {
            return body;
        }
    }

    private Object sanitizeObject(Object value) {
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> sanitizedMap = new LinkedHashMap<>();
            boolean modified = false;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                Object keyObj = entry.getKey();
                if (keyObj == null) {
                    continue;
                }
                String key = String.valueOf(keyObj);
                if (isSensitiveKey(key)) {
                    modified = true;
                    continue;
                }
                Object originalValue = entry.getValue();
                Object sanitizedValue = sanitizeObject(originalValue);
                if (sanitizedValue != originalValue) {
                    modified = true;
                }
                sanitizedMap.put(key, sanitizedValue);
            }
            if (!modified && sanitizedMap.size() == map.size()) {
                boolean allSame = true;
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    String key = String.valueOf(entry.getKey());
                    if (!sanitizedMap.containsKey(key) || sanitizedMap.get(key) != entry.getValue()) {
                        allSame = false;
                        break;
                    }
                }
                if (allSame) {
                    return map;
                }
            }
            return sanitizedMap;
        }
        if (value instanceof Collection<?> collection) {
            boolean modified = false;
            List<Object> sanitizedList = new ArrayList<>(collection.size());
            for (Object item : collection) {
                Object sanitizedItem = sanitizeObject(item);
                if (sanitizedItem != item) {
                    modified = true;
                }
                sanitizedList.add(sanitizedItem);
            }
            if (!modified && sanitizedList.size() == collection.size()) {
                boolean allSame = true;
                int index = 0;
                for (Object item : collection) {
                    if (sanitizedList.get(index++) != item) {
                        allSame = false;
                        break;
                    }
                }
                if (allSame) {
                    return collection;
                }
            }
            return sanitizedList;
        }
        return value;
    }

    private boolean isSensitiveKey(String key) {
        if (key == null) {
            return false;
        }
        return SENSITIVE_KEYS.contains(key.toLowerCase(Locale.ROOT));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 5;
    }
}
