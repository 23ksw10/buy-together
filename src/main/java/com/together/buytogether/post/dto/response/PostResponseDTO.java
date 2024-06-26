package com.together.buytogether.post.dto.response;


import lombok.Builder;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

public record PostResponseDTO(
        String memberName,
        Long postId,
        String title,
        String content,
        LocalDateTime expiredAt) {
    @Builder
    public PostResponseDTO {
        Assert.hasText(memberName, "회원 이름은 필수 값입니다");
        Assert.notNull(postId, "글 번호는 필수 값입니다");
        Assert.hasText(title, "글 제목은 필수 값입니다");
        Assert.hasText(content, "글 내용은 필수 값입니다");
        Assert.notNull(expiredAt, "만료일은 필수 값입니다");
    }
}
