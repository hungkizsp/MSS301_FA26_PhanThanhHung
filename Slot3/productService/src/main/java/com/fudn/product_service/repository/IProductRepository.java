package com.fudn.product_service.repository;

import com.fudn.product_service.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository interface for Product CRUD operations.
 *
 * By extending MongoRepository<Product, String>, Spring Data MongoDB
 * automatically provides:
 *   - save(product)      → insert or update
 *   - findAll()          → get all products
 *   - findById(id)       → find by ObjectId
 *   - deleteAll()        → remove all (used in tests)
 *   ... and many more built-in query methods.
 *
 * Custom query methods can be added here using Spring Data naming conventions,
 * e.g.: List<Product> findByName(String name);
 */
public interface IProductRepository extends MongoRepository<Product, String> {
}
