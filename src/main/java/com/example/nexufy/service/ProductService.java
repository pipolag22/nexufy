package com.example.nexufy.service;


import com.example.nexufy.persistence.entities.Product;
import com.example.nexufy.persistence.entities.RatingComment;
import com.example.nexufy.persistence.repository.CustomerRepository;
import com.example.nexufy.persistence.repository.ProductRepository;
import com.example.nexufy.persistence.repository.RatingCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;



import java.util.*;
import java.util.stream.Collectors;

import java.time.LocalDateTime;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RatingCommentRepository ratingCommentRepository;

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getTopRatedProducts() {
        List<Product> allProducts = productRepository.findAll();
        List<RatingComment> allRatings = ratingCommentRepository.findAll();

        Map<String, Double> productRatings = new HashMap<>();
        for (RatingComment rating : allRatings) {
            productRatings.merge(rating.getProductId(), (double) rating.getRating(), Double::sum);
        }

        Map<String, Long> ratingCounts = allRatings.stream()
                .collect(Collectors.groupingBy(RatingComment::getProductId, Collectors.counting()));

        Map<String, Double> averageRatings = productRatings.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue() / ratingCounts.getOrDefault(entry.getKey(), 1L)
                ));

        return allProducts.stream()
                .sorted((p1, p2) -> Double.compare(averageRatings.getOrDefault(p2.getId(), 0.0), averageRatings.getOrDefault(p1.getId(), 0.0)))
                .limit(4)
                .collect(Collectors.toList());
    }

    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    // Modificación en el método addProduct para agregar la fecha de publicación
    public Product addProduct(Product product) {
        product.setPublicationDate(LocalDateTime.now());  // Asigna la fecha de publicación
        return productRepository.save(product);
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public Product updateProduct(String id, Product productDetails) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isPresent()) {
            Product existingProduct = optionalProduct.get();

            existingProduct.setName(productDetails.getName());
            existingProduct.setDescription(productDetails.getDescription());
            existingProduct.setPrice(productDetails.getPrice());
            existingProduct.setStock(productDetails.getStock());
            existingProduct.setCategory(productDetails.getCategory());
            existingProduct.setProvider(productDetails.getProvider());
            existingProduct.setSerialNumber(productDetails.getSerialNumber());
            existingProduct.setLength(productDetails.getLength());
            existingProduct.setWidth(productDetails.getWidth());
            existingProduct.setHeight(productDetails.getHeight());
            existingProduct.setWeight(productDetails.getWeight());
            existingProduct.setUrlImage(productDetails.getUrlImage());
            existingProduct.setState(productDetails.getState());
            existingProduct.setCustomer(productDetails.getCustomer());
            existingProduct.setSuspended(productDetails.isSuspended());
            existingProduct.setSuspendedUntil(productDetails.getSuspendedUntil());
            existingProduct.setSuspendedReason(productDetails.getSuspendedReason());

            return productRepository.save(existingProduct);
        } else {
            throw new RuntimeException("Producto no encontrado con id: " + id);
        }
    }

    public Map<String, Long> getProductsCountByMonth(List<Product> products) {
        return products.stream()
                .collect(Collectors.groupingBy(product -> product.getPublicationDate().getMonth().name(), Collectors.counting()));
    }
    public long countAllProducts() {
        return productRepository.count();
    }
}