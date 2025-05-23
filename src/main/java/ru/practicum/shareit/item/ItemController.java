package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.intf.Create;
import ru.practicum.shareit.intf.Update;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    public static final String USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItemById(@RequestHeader(USER_ID) long userId,
                               @PathVariable("itemId") long itemId) {
        log.info("Вывод предмета под id: {}", itemId);
        return itemService.getItemById(userId, itemId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto saveItem(@RequestHeader(USER_ID) long userId,
                       @Validated(Create.class) @RequestBody ItemDto itemDto) {
        log.info("Добавление предмета у пользователя под id: {}", userId);
        return itemService.saveItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@RequestHeader(USER_ID) long userId, @PathVariable("itemId") long itemId,
                          @Validated(Update.class) @RequestBody ItemDto itemDto) {
        log.info("Обновление предмета у пользователя под id: {}", userId);
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> findItemsByOwner(@RequestHeader(USER_ID) long userId) {
        log.info("Вывод всех предметов у пользователя под id: {}", userId);
        return itemService.findItemsByOwner(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> searchItems(@RequestParam("text") String searchText) {
        log.info("Поиск всех предметов под text: {}", searchText);
        return itemService.searchItems(searchText);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto addComment(@RequestHeader(USER_ID) long userId, @PathVariable("itemId") long itemId,
                                 @RequestBody CommentDto commentDto) {
        log.info("Добавление комментария к предмету под id: {}", itemId);
        return itemService.addComment(userId, itemId, commentDto.getText());
    }
}
