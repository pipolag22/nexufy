package com.example.nexufy.service;

import com.example.nexufy.persistence.entities.Customer;
import com.example.nexufy.persistence.entities.Product;
import com.example.nexufy.persistence.entities.RatingComment;
import com.example.nexufy.persistence.repository.CustomerRepository;
import com.example.nexufy.persistence.repository.ProductRepository;
import com.example.nexufy.persistence.repository.RatingCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private AzureBlobService azureBlobService;


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
        return productRepository.save(product);
    }


    public void deleteProduct(String id) {productRepository.deleteById(id);}

    public List<Product> searchProducts(String name){
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public Product updateProduct(String id, Product productDetails) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isPresent()) {
            Product existingProduct = optionalProduct.get();

            // Actualizar los campos necesarios
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

            // Guarda el producto actualizado
            return productRepository.save(existingProduct);
        } else {
            throw new RuntimeException("Producto no encontrado con id: " + id);
        }
    }


}