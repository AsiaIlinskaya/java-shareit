package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

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

    @Override
    @Transactional
    public CommentDto addComment(long userId, long itemId, String text) {
        // Проверяем, что пользователь завершил бронирование этого предмета
        boolean hasCompletedBooking = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());

        if (!hasCompletedBooking) {
            throw new ValidationException("User with ID " + userId + " has no completed bookings for item with ID " + itemId
                    + ". Cannot add comment until the booking is completed.");
        }

        if (text.isBlank()) {
            throw new ValidationException("Comment text cannot be empty");
        }

        // Получаем полные сущности вместо DTO
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + itemId));

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Создаем и сохраняем комментарий
        Comment comment = new Comment();
        comment.setText(text);
        comment.setItem(item);  // Устанавливаем связь с Item
        comment.setAuthor(author);  // Устанавливаем связь с User
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        // Добавляем комментарий в список комментариев предмета
        if (item.getComments() == null) {
            item.setComments(new ArrayList<>());
        }
        item.getComments().add(savedComment);
        itemRepository.save(item);  // Обновляем Item

        // Маппим в DTO
        CommentDto commentDto = CommentMapper.mapToCommentDto(savedComment);
        commentDto.setAuthorName(author.getName());  // Устанавливаем имя автора

        return commentDto;
    }
}
