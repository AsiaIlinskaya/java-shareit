package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemDto getItemById(long userId, long itemId);

    ItemDto saveItem(long userId, ItemDto itemDto);

    List<ItemDto> findItemsByOwner(long userId);

    List<ItemDto> searchItems(String searchText);

}
