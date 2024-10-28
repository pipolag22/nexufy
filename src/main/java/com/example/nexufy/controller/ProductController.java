package com.example.nexufy.controller;

import com.example.nexufy.dtos.ProductDTO;
import com.example.nexufy.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Productos", description = "Operaciones relacionadas con productos")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "Obtener todos los productos", description = "Devuelve una lista completa de productos disponibles.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente.")
    })
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Obtener producto por ID", description = "Devuelve los detalles de un producto específico mediante su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado exitosamente."),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "ID del producto", required = true)
            @PathVariable String id) {
        Optional<ProductDTO> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener productos por cliente", description = "Devuelve una lista de productos asociados a un cliente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente.")
    })
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCustomerId(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable String customerId) {
        List<ProductDTO> products = productService.getProductsByCustomerId(customerId);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Buscar productos por nombre", description = "Devuelve una lista de productos que coinciden con el nombre proporcionado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Productos encontrados exitosamente.")
    })
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(
            @Parameter(description = "Nombre del producto a buscar", required = true)
            @RequestParam String name) {
        List<ProductDTO> products = productService.searchProducts(name);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Obtener productos mejor valorados", description = "Devuelve una lista de los productos con mejores valoraciones.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de productos mejor valorados obtenida exitosamente.")
    })
    @GetMapping("/top-rated")
    public ResponseEntity<List<ProductDTO>> getTopRatedProducts() {
        List<ProductDTO> topRatedProducts = productService.getTopRatedProducts();
        return ResponseEntity.ok(topRatedProducts);
    }

    @Operation(summary = "Crear un producto y asignarlo a un cliente", description = "Crea un nuevo producto y lo asigna al cliente especificado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto creado y asignado exitosamente."),
            @ApiResponse(responseCode = "400", description = "Error al crear el producto.")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPERADMIN')")
    @PostMapping("/customer/{customerId}")
    public ResponseEntity<ProductDTO> addProductWithCustomer(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable String customerId,
            @Valid @RequestBody ProductDTO productDTO) {
        ProductDTO newProduct = productService.addProductWithCustomer(customerId, productDTO);
        return ResponseEntity.ok(newProduct);
    }

    @Operation(summary = "Actualizar un producto", description = "Actualiza los detalles de un producto específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente."),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado.")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPERADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @Parameter(description = "ID del producto", required = true)
            @PathVariable String id,
            @Valid @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(summary = "Eliminar un producto por ID", description = "Elimina un producto existente mediante su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente."),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado.")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPERADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID del producto", required = true)
            @PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener el conteo total de productos", description = "Devuelve el número total de productos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conteo total de productos obtenido exitosamente.")
    })
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalProducts() {
        long totalProducts = productService.countAllProducts();
        return ResponseEntity.ok(totalProducts);
    }

    @Operation(summary = "Obtener conteo de productos por categoría", description = "Devuelve el número de productos agrupados por categoría.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conteo de productos por categoría obtenido exitosamente.")
    })
    @GetMapping("/categories/counts")
    public ResponseEntity<Map<String, Long>> getProductCountsByCategory() {
        Map<String, Long> counts = productService.getProductCountsByCategory();
        return ResponseEntity.ok(counts);
    }
}
