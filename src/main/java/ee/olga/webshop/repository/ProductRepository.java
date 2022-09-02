package ee.olga.webshop.repository;

import ee.olga.webshop.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    //tagasta kõik tooted kellel kogus on suurem kui
    //      ja aktiivsus on võrdne
    //              sorteeri kasvavas järjekorras
    List<Product> findAllByStockGreaterThanAndActiveEqualsOrderByIdAsc(int stock, boolean active, Pageable pageable);

    List<Product> findAllByStockGreaterThanAndActiveEqualsOrderByIdAsc(int stock, boolean active);
}
