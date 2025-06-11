package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.intf.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

import static ru.practicum.shareit.item.ItemController.USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto saveRequest(@RequestHeader(USER_ID) long userId, @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Saving request for user with ID {}", userId);
        return itemRequestService.saveRequest(userId, itemRequestDto);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getAllRequests(@RequestHeader(USER_ID) long userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "100") int size) {
        log.info("Fetching all requests for user with ID {}", userId);
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto getRequestById(@PathVariable Long requestId,
                                         @RequestHeader(USER_ID) long userId) {
        log.info("Fetching request with ID {} for user with ID {}", requestId, userId);
        return itemRequestService.getRequestById(requestId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> findItemRequestsById(@RequestHeader(USER_ID) long userId) {
        log.info("Fetching item requests for user with ID {}", userId);
        return itemRequestService.findItemRequestsById(userId);
    }
}
