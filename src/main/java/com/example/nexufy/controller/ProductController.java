package com.example.nexufy.controller;

import com.example.nexufy.persistence.entities.Product;
import com.example.nexufy.service.AzureBlobService;
import com.example.nexufy.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private AzureBlobService azureBlobService;

    // Obtener productos destacados
    @GetMapping
    public List<Product> getHomeProducts() {
        return productService.getTopRatedProducts();
    }

    // Obtener todos los productos
    @GetMapping("/all")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // Buscar productos por nombre
    @GetMapping("/search")
    public List<Product> search(@RequestParam String name){
        return productService.searchProducts(name);
    }

    // Obtener producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear producto con imagen (la imagen es obligatoria)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> createProduct(
            @RequestPart("product") Product product,
            @RequestPart("image") MultipartFile imageFile
    ) {
        try {
            // Primero, guarda el producto sin la URL de la imagen para obtener el productId
            Product savedProduct = productService.addProduct(product);

            // Sube la imagen a Azure Blob Storage usando el productId
            String imageUrl = azureBlobService.uploadFile(imageFile, savedProduct.getId());

            // Actualiza el producto con la URL de la imagen
            savedProduct.setUrlImage(imageUrl);

            // Guarda nuevamente el producto actualizado
            productService.updateProduct(savedProduct.getId(), savedProduct);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Actualizar producto con imagen (la imagen es obligatoria)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> updateProduct(
            @PathVariable String id,
            @RequestPart("product") Product productDetails,
            @RequestPart("image") MultipartFile imageFile
    ) {
        try {
            // Sube la nueva imagen a Azure Blob Storage
            String imageUrl = azureBlobService.uploadFile(imageFile, id);
            productDetails.setUrlImage(imageUrl);

            // Actualizar el producto en la base de datos
            Product updatedProduct = productService.updateProduct(id, productDetails);

            return ResponseEntity.ok(updatedProduct);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        if (productService.getProductById(id).isPresent()) {
            productService.deleteProduct(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
