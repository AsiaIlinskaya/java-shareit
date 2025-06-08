package ru.practicum.shareit.request;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault()))
                        .build()
        );
    }

    public ResponseEntity<Object> saveRequest(long userId, ItemRequestDto itemRequestDto) {
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> getAllRequests(long userId, int from, int size) {
        return get("/all?from=" + from + "&size=" + size, userId);
    }

    public ResponseEntity<Object> getRequestById(Long requestId, Long userId) {
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> findItemRequestsById(Long userId) {
        return get("", userId);
    }


}
