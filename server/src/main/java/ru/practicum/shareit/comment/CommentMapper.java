package ru.practicum.shareit.comment;

import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorId(comment.getAuthor().getId())
                .created(comment.getCreated())
                .authorName(comment.getAuthor().getName())
                .build();
    }

    public static Comment toComment(CommentDto commentDto) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .created(commentDto.getCreated())
                .build();
    }
}
