package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.intf.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static ru.practicum.shareit.item.ItemController.USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> saveRequest(@RequestHeader(USER_ID) long userId,
                                              @Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Saving request for user with ID {}", userId);
        return itemRequestClient.saveRequest(userId, itemRequestDto);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllRequests(@RequestHeader(USER_ID) long userId,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "20") int size) {
        log.info("Fetching all requests for user with ID {}", userId);
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getRequestById(@PathVariable Long requestId,
                                                 @RequestHeader(USER_ID) long userId) {
        log.info("Fetching request with ID {} for user with ID {}", requestId, userId);
        return itemRequestClient.getRequestById(requestId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> findItemRequestsById(@RequestHeader(USER_ID) long userId) {
        log.info("Fetching item requests for user with ID {}", userId);
        return itemRequestClient.findItemRequestsById(userId);
    }
}
