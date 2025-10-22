package store.product;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.cache.annotation.Cacheable;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @CacheEvict(cacheNames = { "products-list" }, key = "'all'")
    public Product create(Product product) {
        if (null == product.name()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Name is mandatory!"
            );
        }
        if (null == product.price()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Price is mandatory!"
            );
        }

        if (productRepository.findByName(product.name()) != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Name already have been registered!"
            );
        return productRepository.save(
            new ProductModel(product)
        ).to();
    }

    @Cacheable(cacheNames = "products-list", key = "'all'")
    public List<Product> findAll() {
        return StreamSupport.stream(
            productRepository.findAll().spliterator(), false)
            .map(ProductModel::to)
            .toList();
    }    

    @Cacheable(cacheNames = "product-by-id", key = "#id")
    public Product findById(String id) {
        return productRepository.findById(id)
            .map(ProductModel::to)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Product not found"
            ));
    }

    @CacheEvict(cacheNames = { "product-by-id" }, key = "#id")
    public void delete(String id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Product not found"
            );
        }
        productRepository.deleteById(id);
    }
}