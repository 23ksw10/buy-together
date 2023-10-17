package com.together.buytogether.postcomment.feature;

import com.together.buytogether.common.ApiTest;
import com.together.buytogether.common.Scenario;
import com.together.buytogether.member.domain.SessionManager;
import com.together.buytogether.postcomment.domain.PostCommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteCommentTest extends ApiTest {

    @Autowired
    private PostCommentRepository postCommentRepository;
    @Autowired
    private SessionManager sessionManager;

    @Test
    @DisplayName("댓글 삭제")
    void deleteComment() {
        Scenario.registerMember().request()
                .signInMember().request()
                .registerPost().cookieValue(sessionManager.getAllSessions().get(0).getId()).request()
                .registerComment().cookieValue(sessionManager.getAllSessions().get(0).getId()).request()
                .deleteComment().cookieValue(sessionManager.getAllSessions().get(0).getId()).request();


        assertThat(postCommentRepository.findAll()).hasSize(0);
    }


}
