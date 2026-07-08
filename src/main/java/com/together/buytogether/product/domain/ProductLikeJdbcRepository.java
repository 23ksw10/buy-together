package com.together.buytogether.product.domain;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.together.buytogether.product.dto.request.LikeRequestDTO;

@Repository
public class ProductLikeJdbcRepository {
	private final JdbcTemplate jdbcTemplate;

	public ProductLikeJdbcRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public int[] batchInsertProductLikes(List<LikeRequestDTO> likeRequests) {
		String sql = "INSERT INTO product_like (member_id, product_id, like_status) VALUES (?, ?, ?)";

		return jdbcTemplate.batchUpdate(sql,
			new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					LikeRequestDTO dto = likeRequests.get(i);
					ps.setLong(1, dto.memberId());
					ps.setLong(2, dto.productId());
					ps.setString(3, ProductLikeStatus.OPEN.name());
				}

				@Override
				public int getBatchSize() {
					return likeRequests.size();
				}
			}
		);
	}
}
