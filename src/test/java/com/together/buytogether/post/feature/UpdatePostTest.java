package com.together.buytogether.post.feature;

import com.together.buytogether.common.ApiTest;
import com.together.buytogether.common.Scenario;
import com.together.buytogether.member.domain.SessionManager;
import com.together.buytogether.post.domain.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdatePostTest extends ApiTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private SessionManager sessionManager;


    @Test
    @DisplayName("게시글 수정")
    void updatePost() {

        Scenario.registerMember().request()
                .signInMember().request()
                .registerPost().cookieValue(sessionManager.getAllSessions().get(0).getId()).request()
                .updatePost().cookieValue(sessionManager.getAllSessions().get(0).getId()).request();


        assertThat(postRepository.findAll()).hasSize(1);
        assertThat(postRepository.getByPostId(1L).getTitle()).isEqualTo("newTitle");
        assertThat(postRepository.getByPostId(1L).getContent()).isEqualTo("newContent");

    }

}
