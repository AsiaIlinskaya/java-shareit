package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemDto getItemById(long userId, long itemId);

    ItemDto saveItem(long userId, ItemDto itemDto);

    List<ItemDto> findItemsByOwner(long userId);

    CommentDto addComment(long userId, long itemId, String text);

    List<ItemDto> searchItems(String searchText);

}
