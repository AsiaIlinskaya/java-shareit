package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto saveRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllRequests(long userId, int from, int size);

    List<ItemRequestDto> findItemRequestsById(long userId);

    ItemRequestDto getRequestById(long requestId, long userId);
}
