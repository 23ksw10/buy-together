package com.together.buytogether.productcomment.service;

import com.together.buytogether.common.service.CommonMemberService;
import com.together.buytogether.common.service.CommonProductService;
import com.together.buytogether.common.utils.ResponseDTO;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.product.domain.Product;
import com.together.buytogether.productcomment.domain.ProductComment;
import com.together.buytogether.productcomment.domain.ProductCommentRepository;
import com.together.buytogether.productcomment.dto.request.CommentDTO;
import com.together.buytogether.productcomment.dto.response.RegisterCommentResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static com.together.buytogether.member.domain.MemberFixture.aMember;
import static com.together.buytogether.product.domain.ProductFixture.aProduct;
import static com.together.buytogether.productcomment.domain.ProductCommentFixture.aProductComment;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class ProductCommentServiceTest {
    @Mock
    CommonMemberService commonMemberService;

    @Mock
    CommonProductService commonProductService;

    @Mock
    ProductCommentRepository productCommentRepository;

    @InjectMocks
    ProductCommentService productCommentService;

    Long productId;
    Long memberId;
    Long commentId;
    Member member;
    Product product;
    CommentDTO commentDTO;
    ProductComment productComment;

    @BeforeEach
    void setUp() {
        memberId = 1L;
        productId = 1L;
        commentId = 1L;
        member = aMember().build();
        member.setId(memberId);
        product = aProduct().member(member).build();
        product.setProductId(productId);
        commentDTO = new CommentDTO("content");
        productComment = aProductComment().member(member).product(product).build();
        productComment.setCommentId(commentId);
        productComment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("상품에 댓글을 등록할 수 있다")
    void registerCommentSuccess() {
        //given

        given(commonMemberService.getMember(memberId)).willReturn(member);
        given(commonProductService.getProduct(productId)).willReturn(product);
        given(productCommentRepository.save(any(ProductComment.class))).willReturn(productComment);

        //when
        ResponseDTO<RegisterCommentResponseDTO> responseDTO = productCommentService.registerComment(memberId, productId, commentDTO);

        //then
        then(productCommentRepository).should().save(any(ProductComment.class));
        assertThat(responseDTO.getData()).isNotNull();
    }

    @Test
    @DisplayName("상품에 댓글을 수정할 수 있다")
    void updateCommentSuccess() {
        //given
        productComment.setCommentId(commentId);
        CommentDTO commentDTO = new CommentDTO("update-content");

        given(productCommentRepository.getByCommentId(commentId)).willReturn(productComment);

        //when
        productCommentService.updateComment(memberId, commentId, commentDTO);


        //then
        assertEquals(productComment.getContent(), "update-content");
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    public void deleteCommentSuccess() {
        given(productCommentRepository.getByCommentId(commentId))
                .willReturn(productComment);

        productCommentService.deleteComment(commentId, memberId);

        then(productCommentRepository).should().delete(refEq(productComment));
    }

}
