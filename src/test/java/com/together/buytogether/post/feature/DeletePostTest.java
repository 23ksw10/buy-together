package com.together.buytogether.post.feature;

import com.together.buytogether.common.ApiTest;
import com.together.buytogether.common.Scenario;
import com.together.buytogether.member.domain.SessionManager;
import com.together.buytogether.post.domain.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class DeletePostTest extends ApiTest {
    @Autowired
    DeletePost deletePost;
    @Autowired
    PostRepository postRepository;
    @Autowired
    SessionManager sessionManager;

    @Test
    @DisplayName("게시글 삭제")
    void deletePost() {
        Scenario.registerMember().request()
                .signInMember().request()
                .registerPost().cookieValue(sessionManager.getAllSessions().get(0).getId()).request()
                .deletePost().cookieValue(sessionManager.getAllSessions().get(0).getId()).request();
        assertThat(postRepository.findAll()).hasSize(0);

    }


}
