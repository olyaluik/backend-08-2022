package ee.olga.webshop.repository;

import ee.olga.webshop.controller.model.CartProduct;
import ee.olga.webshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

    List<CartProduct> findAllByProductOrderByIdAsc(Product product);
}
