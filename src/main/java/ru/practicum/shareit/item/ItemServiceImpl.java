package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public List<ItemDto> findItemsByOwner(long userId) {
        return itemRepository.findItemsByOwner(userId).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        Item item = find(itemId);

        if (item.getOwner() != userId) {
            throw new ResourceNotFoundException("Только владелец вещи может вносить изменения");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.mapToItemDto(item);
    }

    private Item find(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Вещь с ID " + itemId + " не найдена"));
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        Item item = find(itemId);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto saveItem(long userId, ItemDto itemDto) {

        if (userId < 1) {
            throw new ValidationException("Id не может быть отрицательным");
        }

        if (itemDto.getName() == null) {
            throw new ValidationException("Имя не может быть пустым");
        }

        if (itemDto.getDescription() == null) {
            throw new ValidationException("Описание не может быть пустым");
        }

        userService.getUserById(userId);

        Item item = ItemMapper.mapToNewItem(itemDto);
        item.setOwner(userId);
        item = itemRepository.save(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public List<ItemDto> searchItems(String searchText) {
        if (searchText.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.findByDescriptionContainingIgnoreCaseAndAvailableIsTrueOrNameContainingIgnoreCaseAndAvailableIsTrue(searchText, searchText);
        return ItemMapper.mapToItemDto(items);
    }
}
