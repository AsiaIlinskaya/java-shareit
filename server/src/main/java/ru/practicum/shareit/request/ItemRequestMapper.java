package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ItemRequestMapper {
    private final ItemRepository itemRepository;

    public ItemRequestMapper(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        if (itemRequest == null) {
            return null;
        }

        List<ItemDto> itemDtos = itemRequest.getId() != null
                ? ItemMapper.mapToItemDto(itemRepository.findByRequestId_Id(itemRequest.getId()))
                : Collections.emptyList();

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor())
                .created(itemRequest.getCreated())
                .items(itemDtos)
                .build();
    }

    public List<ItemRequestDto> mapToItemRequestDtoList(List<ItemRequest> itemRequests) {
        List<ItemRequestDto> result = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            result.add(this.mapToItemRequestDto(itemRequest));
        }
        return result;
    }

    public ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(itemRequestDto.getRequestor());
        itemRequest.setCreated(itemRequestDto.getCreated() != null
                ? itemRequestDto.getCreated()
                : Timestamp.valueOf(LocalDateTime.now()));
        return itemRequest;
    }
}