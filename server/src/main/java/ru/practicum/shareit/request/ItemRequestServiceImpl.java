package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserService userService;
    private final ItemRequestRepository repository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    @Transactional
    public ItemRequestDto saveRequest(long userId, ItemRequestDto itemRequestDto) {
        userService.getUserById(userId);
        ItemRequest itemRequest = itemRequestMapper.mapToItemRequest(itemRequestDto);
        itemRequest.setRequestor(userId);
        itemRequest.setCreated(Timestamp.valueOf(LocalDateTime.now()));

        ItemRequest savedRequest = repository.save(itemRequest);
        return itemRequestMapper.mapToItemRequestDto(savedRequest);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long userId, int from, int size) {
        userService.getUserById(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        Page<ItemRequest> page = repository.findItemRequestsByRequestorNot(userId, pageable);
        return itemRequestMapper.mapToItemRequestDtoList(page.getContent());
    }

    @Override
    public List<ItemRequestDto> findItemRequestsById(long userId) {
        userService.getUserById(userId);
        List<ItemRequest> requests = repository.findItemRequestsByRequestor(userId);
        return requests.isEmpty()
                ? new ArrayList<>()
                : itemRequestMapper.mapToItemRequestDtoList(requests);
    }

    @Override
    public ItemRequestDto getRequestById(long requestId, long userId) {
        userService.getUserById(userId);
        ItemRequest itemRequest = repository.findItemRequestById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Item request with ID " + requestId + " not found."));

        return itemRequestMapper.mapToItemRequestDto(itemRequest);
    }
}