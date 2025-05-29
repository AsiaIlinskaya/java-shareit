package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
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
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentService commentService;

    private final UserRepository userRepository;

    @Override
    public List<ItemDto> findItemsByOwner(long userId) {
        List<Item> items = itemRepository.findItemsByOwner(userId);
        return items.stream()
                .map(item -> {
                    ItemDto itemDto = ItemMapper.mapToItemDto(item);
                    itemDto.setNextBooking(findNextBookingByItemId(item.getId()));
                    itemDto.setLastBooking(findLastBookingByItemId(item.getId()));
                    enrichItemWithComments(itemDto, item.getComments());
                    return itemDto;
                })
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
        Item item = itemRepository.findById(itemId).orElseThrow(()
                -> new ResourceNotFoundException("Item not found with ID: " + itemId));
        ItemDto itemDto = ItemMapper.mapToItemDto(item);

        if (item.getOwner() != userId) {
            itemDto.setNextBooking(null);
            itemDto.setLastBooking(null);
            List<CommentDto> commentDtos = getNameAuthor(item);
            itemDto.setComments(commentDtos);
        } else {
            itemDto.setNextBooking(findNextBookingByItemId(itemId));
            itemDto.setLastBooking(findLastBookingByItemId(itemId));
            List<CommentDto> commentDtos = getNameAuthor(item);
            itemDto.setComments(commentDtos);
        }
        return itemDto;
    }


    private List<CommentDto> getNameAuthor(Item item) {
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : item.getComments()) {
            CommentDto commentDto = CommentMapper.mapToCommentDto(comment);
            String authorName = commentService.getNameAuthorByCommentId(comment.getId());
            commentDto.setAuthorName(authorName);
            commentDtos.add(commentDto);
        }
        return commentDtos;
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

        ItemDto dto = ItemMapper.mapToItemDto(item);
        dto.setNextBooking(findNextBookingByItemId(item.getId()));
        dto.setLastBooking(findLastBookingByItemId(item.getId()));
        dto.setComments(CommentMapper.mapToCommentDto(commentRepository.findAllByItemId(item.getId())));
        return dto;
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
        boolean hasCompletedBooking = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());

        if (!hasCompletedBooking) {
            throw new ValidationException("User with ID " + userId + " has no completed bookings for item with ID " + itemId
                    + ". Cannot add comment until the booking is completed.");
        }

        if (text.isBlank()) {
            throw new ValidationException("Comment text cannot be empty");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + itemId));

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Comment comment = new Comment();
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        if (item.getComments() == null) {
            item.setComments(new ArrayList<>());
        }
        item.getComments().add(savedComment);
        itemRepository.save(item);
        CommentDto commentDto = CommentMapper.mapToCommentDto(savedComment);
        commentDto.setAuthorName(author.getName());
        return commentDto;
    }

    private void enrichItemWithComments(ItemDto itemDto, List<Comment> comments) {
        if (comments != null) {
            itemDto.setComments(comments.stream()
                    .map(comment -> {
                        CommentDto commentDto = CommentMapper.mapToCommentDto(comment);
                        commentDto.setAuthorName(comment.getAuthor().getName());
                        return commentDto;
                    })
                    .collect(Collectors.toList()));
        }
    }

    private BookingDto findLastBookingByItemId(long itemId) {
        Booking lastBookings = bookingRepository.findFirstBookingByItemIdAndStatusAndStartIsBefore(itemId,
                BookingStatus.APPROVED, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
        BookingDto lastBookingsDTO = bookingMapper.mapToBookingDto(lastBookings);
        return lastBookingsDTO;
    }

    private BookingDto findNextBookingByItemId(long itemId) {
        Booking nextBookings = bookingRepository.findFirstBookingByItemIdAndStatusAndStartIsAfter(itemId,
                BookingStatus.APPROVED, LocalDateTime.now(), Sort.by(Sort.Direction.ASC, "start"));
        BookingDto nextBookingsDTO = bookingMapper.mapToBookingDto(nextBookings);
        return nextBookingsDTO;
    }
}
