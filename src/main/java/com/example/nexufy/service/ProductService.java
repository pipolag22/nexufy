package com.example.nexufy.service;

import com.example.nexufy.persistence.entities.Customer;
import com.example.nexufy.persistence.entities.Product;
import com.example.nexufy.persistence.repository.CustomerRepository;
import com.example.nexufy.persistence.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;


    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
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


    public Product updateProduct(String id, Product product) {
        product.setId(id);
        return productRepository.save(product);
    }
}