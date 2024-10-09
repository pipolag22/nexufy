package com.example.nexufy.service;

import com.example.nexufy.persistence.entities.Customer;
import com.example.nexufy.persistence.entities.Product;
import com.example.nexufy.persistence.entities.RatingComment;
import com.example.nexufy.persistence.repository.CustomerRepository;
import com.example.nexufy.persistence.repository.ProductRepository;
import com.example.nexufy.persistence.repository.RatingCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;
    private RatingCommentRepository ratingCommentRepository;


    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List <Product> getTopRatedProducts(){
        List<Product> allProducts = productRepository.findAll();
        List<RatingComment> allRatings = ratingCommentRepository.findAll();

        // Calcular el rating promedio para cada producto
        Map<String, Double> productRatings = new HashMap<>();
        for (RatingComment rating : allRatings) {
            productRatings.merge(rating.getProductId(), (double) rating.getRating(), Double::sum);
        }

        // Obtener el número de ratings por producto para calcular el promedio
        Map<String, Long> ratingCounts = allRatings.stream()
                .collect(Collectors.groupingBy(RatingComment::getProductId, Collectors.counting()));

        // Calcular el promedio de rating para cada producto
        Map<String, Double> averageRatings = productRatings.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue() / ratingCounts.getOrDefault(entry.getKey(), 1L)
                ));

        // Ordenar los productos por el promedio de rating

        return allProducts.stream()
                .sorted((p1, p2) -> Double.compare(averageRatings.getOrDefault(p2.getId(), 0.0), averageRatings.getOrDefault(p1.getId(), 0.0)))
                .limit(4)
                .collect(Collectors.toList());
    }

    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    public Product addProduct(Product product) {
        // Verifica si el cliente está presente
        if (product.getCustomer() != null && product.getCustomer().getId() != null) {
            Optional<Customer> customerOpt = customerRepository.findById(product.getCustomer().getId());
            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();
                // Añade el producto a la lista de productos del cliente
                List<Product> products = customer.getProducts();
                if (products == null) {
                    products = new ArrayList<>();
                }
                products.add(product);
                customer.setProducts(products);
                customerRepository.save(customer); // Guarda el cliente actualizado
            }
        }

        // Guarda el producto
        return productRepository.save(product);
    }

    public void deleteProduct(String id) {productRepository.deleteById(id);}

    public List<Product> searchProducts(String name){
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public Product updateProduct(String id, Product product) {
        product.setId(id);
        return productRepository.save(product);
    }
}