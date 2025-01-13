package com.threemeals.delivery.domain.menu.repository;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.util.ReflectionTestUtils.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.threemeals.delivery.domain.menu.entity.Category;
import com.threemeals.delivery.domain.menu.entity.Menu;
import com.threemeals.delivery.domain.menu.entity.MenuOption;
import com.threemeals.delivery.domain.store.entity.Store;
import com.threemeals.delivery.domain.user.entity.Role;
import com.threemeals.delivery.domain.user.entity.User;

import jakarta.persistence.EntityManager;

// @ActiveProfiles("test")
@DataJpaTest
@Transactional
class MenuRepositoryTest {

	@Autowired
	MenuRepository menuRepository;

	@Autowired
	EntityManager entityManager;

	@Test
	void 메뉴를_삭제했을_때_하위_메뉴_옵션이_정상적으로_삭제된다() {
		// given
		User mockOwner = makeMockOwner();
		Store mockStore = makeMockStore(mockOwner);
		Menu mockMenu = makeMockMenu();
		mockMenu.setStore(mockStore);

		entityManager.persist(mockOwner);
		entityManager.persist(mockStore);
		entityManager.persist(mockMenu);

		List<MenuOption> mockMenuOptionList = makeMockMenuOptionList(mockMenu);

		// when
		menuRepository.deleteAllMenuOptionsByMenuId(mockMenu.getId());
		entityManager.flush();
		entityManager.clear();

		List<MenuOption> updatedMenuOptions = entityManager.createQuery(
				"SELECT mo FROM MenuOption mo WHERE mo.menu.id = :menuId", MenuOption.class)
			.setParameter("menuId", mockMenu.getId())
			.getResultList();

		// then
		assertThat(updatedMenuOptions)
			.extracting("isDeleted")
			.containsOnly(true);
	}

	private Menu makeMockMenu() {
		Menu mockMenu = Menu.builder()
			.menuName("메뉴 이름")
			.category(Category.of("KOREAN"))
			.menuImgUrl("https://")
			.price(12000)
			.description("description")
			.build();
		setField(mockMenu, "createdAt", LocalDateTime.now());
		setField(mockMenu, "updatedAt", LocalDateTime.now());

		return mockMenu;
	}

	private User makeMockOwner() {
		User mockOwner = User.builder()
			.username("user")
			.email("email@gmail.com")
			.address("address")
			.password("123")
			.role(Role.STORE_OWNER)
			.profileImgUrl("https://")
			.build();
		setField(mockOwner, "createdAt", LocalDateTime.now());
		setField(mockOwner, "updatedAt", LocalDateTime.now());

		return mockOwner;
	}

	private Store makeMockStore(User owner) {
		Store mockStore = Store.builder()
			.owner(owner)
			.storeName("storeName")
			.storeProfileImgUrl("https://")
			.openingTime(LocalTime.now())
			.closingTime(LocalTime.now())
			.address("뉴욕특별시")
			.deliveryTip(3000)
			.minOrderPrice(15000)
			.build();
		setField(mockStore, "createdAt", LocalDateTime.now());
		setField(mockStore, "updatedAt", LocalDateTime.now());

		return mockStore;
	}

	private List<MenuOption> makeMockMenuOptionList(Menu mockMenu) {
		List<MenuOption> menuOptionList = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			MenuOption menuOption = MenuOption.builder()
				.menuOptionName("메뉴 옵션 이름_" + i)
				.menuOptionPrice(13000)
				.menuOptionImgUrl("https://_" + i)
				.description("description_" + i)
				.build();
			setField(menuOption, "createdAt", LocalDateTime.now());
			setField(menuOption, "updatedAt", LocalDateTime.now());

			menuOption.setMenu(mockMenu);
			menuOptionList.add(menuOption);

			entityManager.persist(menuOption);
		}
		return menuOptionList;
	}

}