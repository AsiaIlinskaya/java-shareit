package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.intf.Create;
import ru.practicum.shareit.intf.Update;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    public static final String USER_ID = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItemById(@RequestHeader(USER_ID) long userId,
                                              @PathVariable("itemId") long itemId) {
        log.info("Forwarding request to get item by id: {}", itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> saveItem(@RequestHeader(USER_ID) long userId,
                                           @RequestBody @Validated(Create.class) ItemDto itemDto) {
        log.info("Forwarding request to save item: {}", itemDto);
        return itemClient.saveItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID) long userId,
                                             @PathVariable("itemId") long itemId,
                                             @RequestBody @Validated(Update.class) ItemDto itemDto) {
        log.info("Forwarding request to update item with id: {}", itemId);
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> findItemsByOwner(@RequestHeader(USER_ID) long userId) {
        log.info("Forwarding request to find items by owner");
        return itemClient.findItemsByOwner(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> searchItems(@RequestParam("text") String searchText) {
        log.info("Forwarding request to search items with text: {}", searchText);
        return itemClient.searchItems(searchText);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID) long userId,
                                             @PathVariable("itemId") long itemId,
                                             @RequestBody CommentDto commentDto) {
        log.info("Forwarding request to add comment to item with id: {}", itemId);
        return itemClient.addCommentToItem(userId, itemId, commentDto);
    }
}
