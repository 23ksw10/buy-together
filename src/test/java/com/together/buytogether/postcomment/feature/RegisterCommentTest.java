package com.together.buytogether.postcomment.feature;

import com.together.buytogether.common.ApiTest;
import com.together.buytogether.common.Scenario;
import com.together.buytogether.member.domain.SessionManager;
import com.together.buytogether.postcomment.domain.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class RegisterCommentTest extends ApiTest {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    RegisterComment registerComment;
    @Autowired
    SessionManager sessionManager;


    @Test
    @DisplayName("댓글 등록")
    void registerComment() {
        Scenario.registerMember().request()
                .signInMember().request()
                .registerPost().cookieValue(sessionManager.getAllSessions().get(0).getId()).request()
                .registerComment().cookieValue(sessionManager.getAllSessions().get(0).getId()).request();
        assertThat(commentRepository.findAll().size()).isEqualTo(1);
        assertThat(commentRepository.getByCommentId(1L).getContent()).isEqualTo("댓글 내용");

    }

}
