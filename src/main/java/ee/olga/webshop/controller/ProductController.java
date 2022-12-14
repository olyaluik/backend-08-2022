package ee.olga.webshop.controller;

import ee.olga.webshop.cache.ProductCache;
import ee.olga.webshop.controller.exceptions.CategoryInUseException;
import ee.olga.webshop.controller.exceptions.ProductInUseException;
import ee.olga.webshop.entity.Category;
import ee.olga.webshop.entity.Order;
import ee.olga.webshop.entity.Product;
import ee.olga.webshop.repository.CategoryRepository;
import ee.olga.webshop.repository.ProductRepository;
import ee.olga.webshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController

public class ProductController {
    List<Product> products = new ArrayList<>();

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductCache productCache;

    @GetMapping("products")
    public List<Product> getProducts() {

        return productRepository.findAll();
    }

    //@GetMapping("products?id={id}&name={name}&price={price}&image={image}&active={active}")
    /*@PostMapping("add-product2")
    public List<Product> addProduct2(
            @PathParam("id") Long id,
            @PathParam("name") String name,
            @PathParam("price") double price,
            @PathParam("image") String image,
            @PathParam("active") boolean active) {
        Product product = new Product(id, name, price, image, active);
        products.add(product);
        return products;
    }*/

    @PostMapping("add-product")
    public ResponseEntity<List<Product>> addProduct(@RequestBody Product product) {
       // products.add(product);
        productRepository.save(product);
        return new ResponseEntity<>(productRepository.findAllByOrderById(), HttpStatus.OK);
    }

    @PutMapping("edit-product/{index}") //PUT localhost:8080/edit-product
    public ResponseEntity<List<Product>> editProduct(@RequestBody Product product, @PathVariable int index) {
        //products.set(index, product);
        if (productRepository.existsById(product.getId())) {
            productRepository.save(product);
            productCache.emptyCache();
        }

        return new ResponseEntity<>(productRepository.findAllByOrderById(), HttpStatus.OK);
    }

    @DeleteMapping("delete-product/{id}")
    public ResponseEntity<List<Product>> deleteProduct(@PathVariable Long id) throws ProductInUseException {
//        productRepository.deleteById(id);
  //      productCache.emptyCache();

        try {
            productRepository.deleteById(id);
            productCache.emptyCache();
        } catch (DataIntegrityViolationException e) {
            throw new ProductInUseException();
        }

        return new ResponseEntity<>(productRepository.findAllByOrderById(), HttpStatus.OK);
    }

    @GetMapping("get-product/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) throws ExecutionException {
//kas null v??i {id:1, name: "toode"}
        return new ResponseEntity<>(productCache.getProduct(id), HttpStatus.OK);
    }

    //GET- v??tmiseks POST - lisamiseks DELETE - kustutamiseks
    //PUT - tervikuna asendamiseks PATCH - mingi ??he omaduse asendamine
    @PatchMapping("add-stock")
    public ResponseEntity<List<Product>> addStock(@RequestBody Product product) {
        Product originalProduct = productRepository.findById(product.getId()).get();
        originalProduct.setStock(originalProduct.getStock()+1);
        productRepository.save(originalProduct);
        return new ResponseEntity<>(productRepository.findAll(), HttpStatus.OK);
    }

    @PatchMapping("decrease-stock")
    public ResponseEntity<List<Product>> decreaseStock(@RequestBody Product product) {
        Product originalProduct = productRepository.findById(product.getId()).get();
        if (originalProduct.getStock() > 0) {
            originalProduct.setStock(originalProduct.getStock() - 1);
            productRepository.save(product);
        }
        return new ResponseEntity<>(productRepository.findAllByOrderById(), HttpStatus.OK);
    }



    //lisame igale tootele andmebaasi ka koguse
    //API otspunkti kaudu saab kogusele +1 j a-1 panna
    //-1 kaudu ei lase miinusesse

    //v??iks teha eraldi API otspunktid aktiivsete ja kogustega toodete jaoks

    @GetMapping("active-products")
    public ResponseEntity<List<Product>> getAllActiveProducts() {
       return new ResponseEntity<>(
               productRepository.findAllByStockGreaterThanAndActiveEqualsOrderByIdAsc(0, true),
               HttpStatus.OK);
    }
//
//    @GetMapping("active-products/{pagenr}")
//    public ResponseEntity<List<Product>> getAllActiveProducts() {
//        return new ResponseEntity<>(
//                productRepository.findAllByStockGreaterThanAndActiveEqualsOrderByIdAsc(0, true),
//                HttpStatus.OK);
//    }

    @GetMapping("products-per-page/{pagenr}")
    public Page<Product> getProductsPerPage(@PathVariable int pagenr) {
        Pageable pageable = PageRequest.of(pagenr, 2);
        return productRepository.findAll(pageable);
    }

    @Autowired
    CategoryRepository categoryRepository;

    @GetMapping("products-by-category/{categoryId}")
    public List<Long> getProductsByCategory(@PathVariable Long categoryId) {
        Category category = categoryRepository.findById(categoryId).get();
        List<Long> ids = productRepository.findAllByCategoryOrderByIdAsc(category).stream()
                .map(Product::getId)
                .collect(Collectors.toList());
        return ids;
    }

    //Pagination ->v??tmine lehek??lje kaupa

    //Productis kontrollid, et ei saa ilma nime ja hinnata sisestada

    /*v??tta get
    lisada post
    muuta tervikuna put
    muuta ??hte ainsat kindlat osa patch
    kustutada delete
     */


    @Autowired
    OrderService orderService;

    @GetMapping(path="cart-products/{ids}", produces = {"application/json"})
    public List<Product> getOriginalProducts(@PathVariable List<Long> ids) {
        return orderService.findOriginalProducts(ids);
    }

}
