package ee.olga.webshop.controller;

import ee.olga.webshop.cache.ProductCache;
import ee.olga.webshop.entity.Product;
import ee.olga.webshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Product> addProduct(@RequestBody Product product) {
       // products.add(product);
        productRepository.save(product);
        return productRepository.findAll();
    }

    @PutMapping("edit-product/{index}") //PUT localhost:8080/edit-product
    public List<Product> editProduct(@RequestBody Product product, @PathVariable int index) {
        //products.set(index, product);
        if (!productRepository.existsById(product.getId())) {
            productRepository.save(product);
            productCache.emptyCache();
        }

        return productRepository.findAll();
    }

    @DeleteMapping("delete-product/{index}")
    public List<Product> deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        productCache.emptyCache();
        return productRepository.findAll();
    }

    @GetMapping("get-product/{id}")
    public Product getProduct(@PathVariable Long id) throws ExecutionException {
//kas null või {id:1, name: "toode"}
        return productCache.getProduct(id);
    }

    /*võtta get
    lisada post
    muuta tervikuna put
    muuta ühte ainsat kindlat osa patch
    kustutada delete
     */

}
