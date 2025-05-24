package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.user.UserService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;

    @Override
    public String getNameAuthorByCommentId(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + commentId));

        Long authorId = comment.getAuthorId();
        return userService.getUserNameById(authorId);
    }
}
