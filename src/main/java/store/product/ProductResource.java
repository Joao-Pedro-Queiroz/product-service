package store.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class ProductResource implements ProductController {

    @Autowired
    private ProductService productService;

    @Override
    public ResponseEntity<ProductOut> create(ProductIn in) {
        Product product = ProductParser.to(in);

        Product saved = productService.create(product);

        return ResponseEntity.created(
            ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id())
                .toUri()
        ).body(ProductParser.to(saved));
    }

    @Override
    public ResponseEntity<ProductOut> findById(String id) {
        Product product = productService.findById(id);
        return ResponseEntity
        .ok()
        .body(ProductParser.to(product));
    }

    @Override
    public ResponseEntity<List<ProductOut>> findAll() {
        return ResponseEntity
            .ok()
            .body(ProductParser.to(productService.findAll()));
    }

    @Override
    public ResponseEntity<Void> delete(String id) {
        productService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
    
}