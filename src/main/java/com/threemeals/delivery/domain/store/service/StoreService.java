package com.threemeals.delivery.domain.store.service;

import com.threemeals.delivery.config.error.ErrorCode;
import com.threemeals.delivery.domain.common.exception.AccessDeniedException;
import com.threemeals.delivery.domain.store.dto.request.SaveStoreRequestDto;
import com.threemeals.delivery.domain.store.dto.response.StoreResponseDto;
import com.threemeals.delivery.domain.store.entity.Store;
import com.threemeals.delivery.domain.store.repository.StoreRepository;
import com.threemeals.delivery.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;

    public StoreResponseDto saveStore(SaveStoreRequestDto requestDto, Long userId) {

        // 사장님이 소유한 가게 수 확인
        long storeCount = storeRepository.countByOwnerIdAndIsClosedFalse(userId);
        if (storeCount >= 3) {
            throw new IllegalArgumentException("사장님은 최대 3개의 가게만 소유할 수 있습니다.");
        }

        // User 객체 생성 (소유자 정보 설정)
        User owner = new User(userId);

        // Store 엔티티 생성
        Store store = requestDto.toEntity(owner);

        // 저장 후 DTO 변환
        Store savedStore = storeRepository.save(store);
        return StoreResponseDto.toDto(savedStore);
    }

    // 다건 조회
    public List<StoreResponseDto> findStoresByName(String name) {
        List<Store> stores = storeRepository.findByStoreNameContainingAndIsClosedFalse(name); // 가게명 검색
        return stores.stream()
                .map(StoreResponseDto::toDto) // 엔티티를 DTO로 변환
                .collect(Collectors.toList());
    }

    public StoreResponseDto updateStore(Long storeId, @Valid SaveStoreRequestDto requestDto, Long userId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게를 찾을 수 없습니다."));

        // 소유권 확인
        if (!store.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException(ErrorCode.ACCESS_DENIED);
        }

        // 필드 업데이트
        store.update(
                requestDto.storeName(),
                requestDto.storeProfileImgUrl(),
                requestDto.address(),
                requestDto.openingTime(),
                requestDto.closingTime(),
                requestDto.deliveryTip(),
                requestDto.minOrderPrice()
        );

        // 저장 및 DTO 변환
        Store updatedStore = storeRepository.save(store);
        return StoreResponseDto.toDto(updatedStore);
    }

    public void deleteStore(Long storeId, Long userId) {

        // 삭제할 가게 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게를 찾을수 없습니다."));

        // 소유권 확인
        if (!store.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException(ErrorCode.ACCESS_DENIED);
        }

        // 소프트 삭제
        store.storeClosed();
        storeRepository.save(store);
    }
}
