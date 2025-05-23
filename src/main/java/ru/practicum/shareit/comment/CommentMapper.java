package ru.practicum.shareit.comment;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;

import java.util.ArrayList;
import java.util.List;

@Mapper
@Component
public class CommentMapper {
    public static CommentDto mapToCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreatedAt())
                .build();
    }

    public static List<CommentDto> mapToCommentDto(Iterable<Comment> comments) {
        List<CommentDto> result = new ArrayList<>();

        for (Comment comment : comments) {
            result.add(mapToCommentDto(comment));
        }

        return result;
    }
}
