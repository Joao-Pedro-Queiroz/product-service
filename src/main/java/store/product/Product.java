package store.product;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Builder @Data @Accessors(fluent = true, chain = true)
public class Product {
    String id;
    String name;
    Double price;
    String unit;
}
