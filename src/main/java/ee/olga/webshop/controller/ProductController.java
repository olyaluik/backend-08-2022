package ee.olga.webshop.controller;

import ee.olga.webshop.cache.ProductCache;
import ee.olga.webshop.entity.Product;
import ee.olga.webshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

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
        return new ResponseEntity<>(productRepository.findAll(), HttpStatus.OK);
    }

    @PutMapping("edit-product/{index}") //PUT localhost:8080/edit-product
    public ResponseEntity<List<Product>> editProduct(@RequestBody Product product, @PathVariable int index) {
        //products.set(index, product);
        if (!productRepository.existsById(product.getId())) {
            productRepository.save(product);
            productCache.emptyCache();
        }

        return new ResponseEntity<>(productRepository.findAll(), HttpStatus.OK);
    }

    @DeleteMapping("delete-product/{index}")
    public ResponseEntity<List<Product>> deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        productCache.emptyCache();
        return new ResponseEntity<>(productRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("get-product/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) throws ExecutionException {
//kas null või {id:1, name: "toode"}
        return new ResponseEntity<>(productCache.getProduct(id), HttpStatus.OK);
    }

    //GET- võtmiseks POST - lisamiseks DELETE - kustutamiseks
    //PUT - tervikuna asendamiseks PATCH - mingi ühe omaduse asendamine
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
            originalProduct.setStock(originalProduct.getStock()-1);
            productRepository.save(product);
        }
        return new ResponseEntity<>(productRepository.findAll(), HttpStatus.OK);
    }



    //lisame igale tootele andmebaasi ka koguse
    //API otspunkti kaudu saab kogusele +1 j a-1 panna
    //-1 kaudu ei lase miinusesse

    //võiks teha eraldi API otspunktid aktiivsete ja kogustega toodete jaoks

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

    //Pagination ->võtmine lehekülje kaupa

    //Productis kontrollid, et ei saa ilma nime ja hinnata sisestada

    /*võtta get
    lisada post
    muuta tervikuna put
    muuta ühte ainsat kindlat osa patch
    kustutada delete
     */

}
