package com.together.buytogether.post.domain;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.together.buytogether.post.dto.request.LikeRequestDTO;

@Repository
public class PostLikeJdbcRepository {
	private final JdbcTemplate jdbcTemplate;

	public PostLikeJdbcRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public int[] batchInsertPostLikes(List<LikeRequestDTO> likeRequests) {
		String sql = "INSERT INTO post_like (member_id, post_id, like_status) VALUES (?, ?, ?)";

		return jdbcTemplate.batchUpdate(sql,
			new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					LikeRequestDTO dto = likeRequests.get(i);
					ps.setLong(1, dto.memberId());
					ps.setLong(2, dto.postId());
					ps.setString(3, PostLikeStatus.OPEN.name());
				}

				@Override
				public int getBatchSize() {
					return likeRequests.size();
				}
			}
		);
	}

}
