package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemService;
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
    private final ItemService itemService;

    @Override
    @Transactional
    public ItemRequestDto saveRequest(long userId, ItemRequestDto itemRequestDto) {
        if (userService.getUserById(userId) == null) {
            throw new ResourceNotFoundException("Отсутствует user под id:");
        }

        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new ValidationException("Описание не может быть пустым");
        }

        ItemRequest itemRequest = itemRequestMapper.mapToItemRequest(itemRequestDto);
        itemRequest.setRequestor(userId);
        itemRequest.setCreated(Timestamp.valueOf(LocalDateTime.now()));

        ItemRequest savedRequest = repository.save(itemRequest);
        return itemRequestMapper.mapToItemRequestDto(savedRequest);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long userId, int from, int size) {
        if (userService.getUserById(userId) == null) {
            throw new ResourceNotFoundException("Отсутствует user под id:");
        }

        if (from < 0 || size <= 0) {
            throw new ValidationException("Некорректные параметры пагинации");
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        Page<ItemRequest> page = repository.findItemRequestsByRequestorNot(userId, pageable);

        return itemRequestMapper.mapToItemRequestDtoList(page.getContent());
    }

    @Override
    public List<ItemRequestDto> findItemRequestsById(long userId) {
        if (userService.getUserById(userId) == null) {
            throw new ResourceNotFoundException("Отсутствует user под id:");
        }

        List<ItemRequest> requests = repository.findItemRequestsByRequestor(userId);
        return requests.isEmpty()
                ? new ArrayList<>()
                : itemRequestMapper.mapToItemRequestDtoList(requests);
    }

    @Override
    public ItemRequestDto getRequestById(long requestId, long userId) {
        if (userService.getUserById(userId) == null) {
            throw new ResourceNotFoundException("User with ID " + userId + " not found.");
        }

        ItemRequest itemRequest = repository.findItemRequestById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Item request with ID " + requestId + " not found."));

        return itemRequestMapper.mapToItemRequestDto(itemRequest);
    }
}