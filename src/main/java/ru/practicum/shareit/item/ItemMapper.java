package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Mapper
@Component
public class ItemMapper {

    public static ItemDto mapToItemDto(Item item) {
        if (item == null) {
            return null;
        }

        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();

        if (item.getComments() != null) {
            List<CommentDto> commentDtos = CommentMapper.mapToCommentDto(item.getComments());
            itemDto.setComments(commentDtos);
        }
        return itemDto;
    }

    public static List<ItemDto> mapToItemDto(Iterable<Item> items) {
        List<ItemDto> result = new ArrayList<>();

        for (Item item : items) {
            result.add(mapToItemDto(item));
        }

        return result;
    }

    public static Item mapToNewItem(ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(0);
        item.setRequest(0);
        return item;
    }
}
