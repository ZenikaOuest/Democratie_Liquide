package com.zenika.liquid.democracy.api.category.persistence;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.zenika.liquid.democracy.model.Category;

public interface CategoryRepository extends MongoRepository<Category, Long> {

	Optional<Category> findCategoryByUuid(String categoryUuid);

    Optional<Category> findCategoryByTitle(String title);
}
