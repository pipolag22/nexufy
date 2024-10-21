package com.example.nexufy.controller;

import com.example.nexufy.dtos.ProductDTO;
import com.example.nexufy.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Obtener todos los productos
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // Obtener un producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable String id) {
        Optional<ProductDTO> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener productos por cliente
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCustomerId(@PathVariable String customerId) {
        List<ProductDTO> products = productService.getProductsByCustomerId(customerId);
        return ResponseEntity.ok(products);
    }

    // Buscar productos por nombre
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String name) {
        List<ProductDTO> products = productService.searchProducts(name);
        return ResponseEntity.ok(products);
    }

    // Obtener los productos mejor valorados
    @GetMapping("/top-rated")
    public ResponseEntity<List<ProductDTO>> getTopRatedProducts() {
        List<ProductDTO> topRatedProducts = productService.getTopRatedProducts();
        return ResponseEntity.ok(topRatedProducts);
    }

    // Crear un producto y asignarlo a un cliente
    @PostMapping("/customer/{customerId}")
    public ResponseEntity<ProductDTO> addProductWithCustomer(
            @PathVariable String customerId,
            @RequestBody ProductDTO productDTO) {
        ProductDTO newProduct = productService.addProductWithCustomer(customerId, productDTO);
        return ResponseEntity.ok(newProduct);
    }

    // Actualizar un producto
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable String id,
            @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    // Eliminar un producto por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
