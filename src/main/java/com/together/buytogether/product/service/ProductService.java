package com.together.buytogether.product.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.together.buytogether.annotation.SingleFlightCacheEvict;
import com.together.buytogether.cache.CacheKey;
import com.together.buytogether.cache.event.RedisCacheEventPublisher;
import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import com.together.buytogether.common.service.CommonMemberService;
import com.together.buytogether.common.service.CommonProductService;
import com.together.buytogether.common.utils.ResponseDTO;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.product.domain.Product;
import com.together.buytogether.product.domain.ProductLike;
import com.together.buytogether.product.domain.ProductLikeRepository;
import com.together.buytogether.product.domain.ProductLikeStatus;
import com.together.buytogether.product.domain.ProductRepository;
import com.together.buytogether.product.domain.ProductStatus;
import com.together.buytogether.product.dto.request.RegisterProductDTO;
import com.together.buytogether.product.dto.request.UpdateProductDTO;
import com.together.buytogether.product.dto.response.ProductLikeResponseDTO;
import com.together.buytogether.product.dto.response.ProductResponseDTO;
import com.together.buytogether.product.dto.response.RegisterProductResponseDTO;
import com.together.buytogether.product.dto.response.UpdateProductResponseDTO;

@Service
public class ProductService {
	private final ProductRepository productRepository;
	private final CommonMemberService commonMemberService;
	private final CommonProductService commonProductService;
	private final ProductLikeRepository productLikeRepository;
	private final RedisCacheEventPublisher redisCacheEventPublisher;

	public ProductService(
		ProductRepository productRepository,
		CommonMemberService commonMemberService,
		CommonProductService commonProductService,
		ProductLikeRepository productLikeRepository,
		RedisCacheEventPublisher redisCacheEventPublisher) {
		this.productRepository = productRepository;
		this.commonMemberService = commonMemberService;
		this.commonProductService = commonProductService;
		this.productLikeRepository = productLikeRepository;
		this.redisCacheEventPublisher = redisCacheEventPublisher;
	}

	@Transactional
	public ResponseDTO<RegisterProductResponseDTO> registerProduct(Long memberId, RegisterProductDTO registerProductDTO) {
		Member member = commonMemberService.getMember(memberId);
		Product product = productRepository.save(registerProductDTO.toDomain(member));
		return ResponseDTO.successResult(RegisterProductResponseDTO.builder()
			.productId(product.getProductId())
			.name(product.getMember().getName())
			.title(product.getTitle())
			.content(product.getContent())
			.status(product.getStatus())
			.price(product.getPrice())
			.soldQuantity(product.getSoldQuantity())
			.maxQuantity(product.getMaxQuantity())
			.createdAt(LocalDateTime.now())
			.build());
	}

	@Transactional
	public ResponseDTO<ProductLikeResponseDTO> likeProduct(Long memberId, Long productId) {
		Member member = commonMemberService.getMember(memberId);
		Product product = commonProductService.getProduct(productId);
		if (product.getStatus() == ProductStatus.CLOSED) {
			throw new CustomException(ErrorCode.PRODUCT_CLOSED);
		}
		Optional<ProductLike> optionalProductLike = productLikeRepository.findByMemberIdAndProductId(memberId, productId);
		if (optionalProductLike.isPresent()) {
			ProductLike productLike = optionalProductLike.get();
			if (productLike.getProductLikeStatus() == ProductLikeStatus.OPEN) {
				productLike.delete();
				return ResponseDTO.successResult(ProductLikeResponseDTO.builder()
					.productId(productId)
					.memberId(memberId)
					.isDeleted(true)
					.build());
			}
			productLike.active();
		} else {
			ProductLike productLike = new ProductLike(member, product, ProductLikeStatus.OPEN);
			productLikeRepository.save(productLike);
		}
		return ResponseDTO.successResult(ProductLikeResponseDTO.builder()
			.productId(productId)
			.memberId(memberId)
			.isDeleted(false)
			.build());
	}

	@SingleFlightCacheEvict(cacheName = "SingleProduct", key = "#productId")
	@Transactional
	public ResponseDTO<UpdateProductResponseDTO> updateProduct(
		Long memberId,
		Long productId,
		UpdateProductDTO updateProductDTO) {
		Product product = commonProductService.getProduct(productId);
		product.checkOwner(memberId);
		product.update(
			updateProductDTO.title(),
			updateProductDTO.content(),
			updateProductDTO.status(),
			updateProductDTO.expiredAt(),
			updateProductDTO.price(),
			updateProductDTO.maxQuantity()
		);
		redisCacheEventPublisher.publishEvict(CacheKey.PRODUCTS, productId);
		return ResponseDTO.successResult(UpdateProductResponseDTO.builder()
			.productId(productId)
			.memberName(product.getMember().getName())
			.title(product.getTitle())
			.content(product.getContent())
			.status(product.getStatus())
			.price(product.getPrice())
			.maxQuantity(product.getMaxQuantity())
			.updatedAt(LocalDateTime.now())
			.build());
	}

	@CacheEvict(cacheNames = {CacheKey.PRODUCTS}, key = "#productId")
	@Transactional
	public ResponseDTO<String> deleteProduct(Long memberId, Long productId) {
		Product product = commonProductService.getProduct(productId);
		product.checkOwner(memberId);
		product.deleteProduct();
		redisCacheEventPublisher.publishEvict(CacheKey.PRODUCTS, productId);
		return ResponseDTO.successResult("성공적으로 상품을 삭제했습니다");
	}

	public ResponseDTO<ProductResponseDTO> getProduct(Long productId) {
		Product product = commonProductService.getProduct(productId);
		return ResponseDTO.successResult(ProductResponseDTO.from(product));
	}

	@Transactional(readOnly = true)
	public ResponseDTO<List<ProductResponseDTO>> getProducts() {
		List<Product> products = productRepository.findAll();
		List<ProductResponseDTO> allProducts = products.stream()
			.map(ProductResponseDTO::from)
			.toList();
		return ResponseDTO.successResult(allProducts);
	}
}
