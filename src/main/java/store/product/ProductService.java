package store.product;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Caching(
        evict = {
            @CacheEvict(cacheNames = "products-list", allEntries = true)
        },
        put = {
            @CachePut(cacheNames = "product-by-id", key = "#result.id", unless = "#result == null")
        }
    )
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

    @Cacheable(cacheNames = "products-list", unless = "#result == null || #result.isEmpty()")
    public List<Product> findAll() {
        return StreamSupport.stream(
            productRepository.findAll().spliterator(), false)
            .map(ProductModel::to)
            .toList();
    }    

    @Cacheable(cacheNames = "product-by-id", key = "#id", unless = "#result == null")
    public Product findById(String id) {
        return productRepository.findById(id)
            .map(ProductModel::to)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Product not found"
            ));
    }

    @Caching(
        evict = {
            @CacheEvict(cacheNames = "product-by-id", key = "#id"),
            @CacheEvict(cacheNames = "products-list", allEntries = true)
        }
    )
    public void delete(String id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Product not found"
            );
        }
        productRepository.deleteById(id);
    }
}