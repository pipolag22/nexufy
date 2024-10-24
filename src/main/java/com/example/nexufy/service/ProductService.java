package com.example.nexufy.service;

import com.example.nexufy.dtos.ProductDTO;
import com.example.nexufy.persistence.entities.Customer;
import com.example.nexufy.persistence.entities.Product;
import com.example.nexufy.persistence.entities.RatingComment;
import com.example.nexufy.persistence.repository.CustomerRepository;
import com.example.nexufy.persistence.repository.ProductRepository;
import com.example.nexufy.persistence.repository.RatingCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RatingCommentRepository ratingCommentRepository;

    // Conversión de Product a ProductDTO
    private ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getUrlImage(),
                product.getState(),
                product.getCustomerId(),
                product.getPublicationDate()
        );
    }

    // Conversión de ProductDTO a Product
    private Product convertToEntity(ProductDTO dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        product.setUrlImage(dto.getUrlImage());
        product.setState(dto.getState());
        product.setCustomerId(dto.getCustomerId()); // Asignamos el ID del cliente
        return product;
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ProductDTO> getProductById(String id) {
        return productRepository.findById(id).map(this::convertToDTO);
    }

    public ProductDTO addProductWithCustomer(String customerId, ProductDTO productDTO) {

        Product product = convertToEntity(productDTO);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + customerId));

        product.setCustomerId(customerId);

        product.setPublicationDate(LocalDateTime.now());

        Product savedProduct = productRepository.save(product);

        customer.getProducts().add(savedProduct);
        customerRepository.save(customer);

        ProductDTO savedProductDTO = convertToDTO(savedProduct);

        return savedProductDTO;
    }

    public void deleteProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
        productRepository.deleteById(id);
    }

    public List<ProductDTO> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public List<ProductDTO> getTopRatedProducts() {
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
                .sorted((p1, p2) -> Double.compare(
                        averageRatings.getOrDefault(p2.getId(), 0.0),
                        averageRatings.getOrDefault(p1.getId(), 0.0)))
                .limit(4)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public ProductDTO updateProduct(String id, ProductDTO productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setCategory(productDetails.getCategory());
        product.setUrlImage(productDetails.getUrlImage());
        product.setState(productDetails.getState());

        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    public List<ProductDTO> getProductsByCustomerId(String customerId) {
        return productRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public Map<String, Long> getProductsCountByMonth(List<Product> products) {
        return products.stream()
                .collect(Collectors.groupingBy(product -> product.getPublicationDate().getMonth().name(), Collectors.counting()));
    }
    public long countAllProducts() {
        return productRepository.count();
    }
}