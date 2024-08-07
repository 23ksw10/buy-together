package com.together.buytogether.post.repository;

import static com.together.buytogether.member.domain.MemberFixture.*;
import static com.together.buytogether.post.domain.PostFixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.together.buytogether.config.JpaAuditingConfig;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;

@DisplayName("Post JPA 연결 테스트")
@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTest {
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private MemberRepository memberRepository;
	private Post post;
	private Member savedMember;

	@BeforeEach
	void setUp() {
		Member member = aMember().build();
		savedMember = memberRepository.save(member);
		post = aPost().member(savedMember).build();
	}

	@Test
	@DisplayName("insert 테스트")
	void insertPost() {
		postRepository.save(post);
		assertThat(postRepository.count()).isEqualTo(1);
	}

	@Test
	@DisplayName("select 테스트")
	void selectPost() {
		postRepository.save(post);
		List<Post> posts = postRepository.findAll();
		assertThat(posts).isNotNull().hasSize(1);
	}

	@Test
	@DisplayName("update 테스트")
	void updatePost() {
		Post savedPost = postRepository.save(post);
		savedPost.update("newTitle", post.getContent(), post.getStatus(), post.getExpiredAt());
		Post updatedPost = postRepository.saveAndFlush(savedPost);

		assertThat(updatedPost).hasFieldOrPropertyWithValue("title", "newTitle");
	}

	@Test
	@DisplayName("delete 테스트")
	void deletePost() {
		postRepository.save(post);
		assertThat(postRepository.count()).isEqualTo(1);

		postRepository.delete(post);
		assertThat(postRepository.count()).isEqualTo(0);
	}
}
