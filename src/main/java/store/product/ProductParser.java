package store.product;

import java.util.List;

public class ProductParser {

    public static Product to(ProductIn in) {
        return in == null ? null :
            Product.builder()
                .name(in.name())
                .price(in.price())
                .unit(in.unit())
                .build();
    }

    public static ProductOut to(Product a) {
        return a == null ? null :
            ProductOut.builder()
                .id(a.id())
                .name(a.name())
                .price(a.price())
                .unit(a.unit())
                .build();
    }

    public static List<ProductOut> to(List<Product> as) {
        return as == null ? null :
            as.stream().map(ProductParser::to).toList();
    }
    
}