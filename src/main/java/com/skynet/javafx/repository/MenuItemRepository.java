package com.skynet.javafx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.skynet.javafx.model.MenuItem;
import java.util.List;


@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
	List<MenuItem> findByParent(Long parent);
}
