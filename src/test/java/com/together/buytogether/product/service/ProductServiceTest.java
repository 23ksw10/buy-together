package com.together.buytogether.product.service;

import static com.together.buytogether.member.domain.MemberFixture.*;
import static com.together.buytogether.product.domain.ProductFixture.*;
import static com.together.buytogether.product.domain.RegisterProductDTOFixture.*;
import static com.together.buytogether.product.domain.UpdateProductDTOFixture.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.service.CommonMemberService;
import com.together.buytogether.common.service.CommonProductService;
import com.together.buytogether.cache.event.RedisCacheEventPublisher;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.product.domain.Product;
import com.together.buytogether.product.domain.ProductRepository;
import com.together.buytogether.product.domain.ProductStatus;
import com.together.buytogether.product.dto.request.RegisterProductDTO;
import com.together.buytogether.product.dto.request.UpdateProductDTO;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
	Long memberId;
	Long productId;
	@InjectMocks
	private ProductService productService;
	@Mock
	private ProductRepository productRepository;
	@Mock
	private CommonMemberService commonMemberService;
	@Mock
	private CommonProductService commonProductService;
	@Mock
	private RedisCacheEventPublisher redisCacheEventPublisher;

	@BeforeEach
	void setUp() {
		memberId = 1L;
		productId = 1L;
	}

	@Test
	@DisplayName("상품을 등록할 수 있다")
	public void registerProductSuccess() {
		//given
		Member member = aMember().build();
		Product product = aProduct().build();
		product.setProductId(productId);
		RegisterProductDTO registerProductDTO = aRegisterProductDTO().build();
		given(commonMemberService.getMember(any(Long.class))).willReturn(member);
		given(productRepository.save(any(Product.class))).willReturn(product);

		//when
		productService.registerProduct(memberId, registerProductDTO);

		//then
		then(productRepository).should().save(any(Product.class));
		then(commonMemberService).should().getMember(any(Long.class));

	}

	@Test
	@DisplayName("존재하지 않는 회원은 상품을 등록할 수 없다")
	public void registerProductFail() {
		//given
		given(commonMemberService.getMember(any(Long.class))).willThrow(CustomException.class);

		//when
		assertThrows(CustomException.class, () -> productService.registerProduct(memberId, any(RegisterProductDTO.class)));

		//then
		then(productRepository).shouldHaveNoInteractions();

	}

	@Test
	@DisplayName("상품을 수정한다")
	public void updateProductSuccess() {
		//given
		Member member = aMember().build();
		member.setId(1L);
		UpdateProductDTO updateProductDTO = aUpdateProductDTO().title("newTitle").build();
		Product product = aProduct().setMemberId(memberId).build();
		given(commonProductService.getProduct(productId)).willReturn(product);

		//when
		productService.updateProduct(memberId, productId, updateProductDTO);

		//then
		then(commonProductService).should().getProduct(productId);
		assertEquals("newTitle", product.getTitle());
		assertEquals(product.getStatus(), ProductStatus.OPEN);

	}

	@Test
	@DisplayName("상품 삭제 성공")
	public void deleteProductSuccess() {
		//given
		Member member = aMember().build();
		member.setId(memberId);
		Product product = aProduct().setMemberId(memberId).build();
		given(commonProductService.getProduct(productId)).willReturn(product);

		//when
		productService.deleteProduct(memberId, productId);

		//then
		then(commonProductService.getProduct(productId));
		assertEquals(product.getStatus(), ProductStatus.CLOSED);
	}

	@Test
	@DisplayName("상품 삭제 실패")
	public void deleteProductFail() {
		//given
		Member member = aMember().build();
		member.setId(memberId);
		Product product = aProduct().build();
		Long wrongMemberId = 99L;
		given(commonProductService.getProduct(productId)).willReturn(product);

		//when
		assertThrows(CustomException.class, () -> productService.deleteProduct(wrongMemberId, productId));

		//then
		then(commonProductService).should().getProduct(productId);
		then(productRepository).shouldHaveNoMoreInteractions();
	}

}
