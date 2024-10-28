package com.example.nexufy.controller;

import com.example.nexufy.dtos.CustomerDTO;
import com.example.nexufy.dtos.ProductDTO;
import com.example.nexufy.dtos.CustomerContactDto;
import com.example.nexufy.persistence.entities.Customer;
import com.example.nexufy.persistence.entities.EnumRoles;
import com.example.nexufy.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/customer")
@Tag(name = "Clientes", description = "Operaciones relacionadas a los customers registrados")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Operation(summary = "Obtener un cliente por ID", description = "Devuelve los detalles del cliente según su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado exitosamente."),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado.")
    })
    @GetMapping("/{id}")
    public CustomerDTO getCustomerById(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable String id) {
        return customerService.getCustomerById(id);
    }

    @Operation(summary = "Promover un cliente a un nuevo rol", description = "Actualiza el rol del cliente al rol especificado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rol del cliente actualizado exitosamente."),
            @ApiResponse(responseCode = "400", description = "Error al actualizar el rol del cliente.")
    })
    @PutMapping("/promote")
    public ResponseEntity<String> promoteCustomerToRole(
            @Parameter(description = "ID del cliente", required = true)
            @RequestParam String customerId,
            @Parameter(description = "Nuevo rol a asignar", required = true)
            @RequestParam EnumRoles role) {
        customerService.updateCustomerRole(customerId, role);
        return ResponseEntity.ok("Customer role updated successfully to " + role);
    }

    @Operation(summary = "Obtener productos por ID de cliente", description = "Devuelve los productos asociados al cliente especificado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente."),
            @ApiResponse(responseCode = "400", description = "Error al obtener los productos.")
    })
    @GetMapping("/{id}/products")
    public ResponseEntity<List<ProductDTO>> getProductsByCustomerId(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable String id) {
        try {
            List<ProductDTO> products = customerService.getProductsByCustomerId(id);
            return ResponseEntity.ok(products);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @Operation(summary = "Obtener todos los clientes", description = "Devuelve una lista de todos los clientes registrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente.")
    })
    @GetMapping("/all")
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @Operation(summary = "Eliminar un cliente", description = "Elimina el cliente especificado por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente eliminado exitosamente."),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable String id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Customer deleted successfully");
    }

    @Operation(summary = "Obtener información de contacto del cliente", description = "Devuelve los detalles de contacto del cliente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Información de contacto obtenida exitosamente."),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado.")
    })
    @GetMapping("/{id}/contact")
    public CustomerContactDto getCustomerContact(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable String id) {
        return customerService.getCustomerContactById(id);
    }

    @Operation(summary = "Actualizar un cliente", description = "Actualiza los detalles del cliente especificado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente."),
            @ApiResponse(responseCode = "400", description = "Error al actualizar el cliente.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<String> updateCustomer(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable String id,
            @Parameter(description = "Detalles actualizados del cliente", required = true)
            @RequestBody CustomerDTO customerDTO) {
        try {
            Customer updatedCustomer = customerService.updateCustomer(id, customerDTO);
            return ResponseEntity.ok("Customer updated successfully: " + updatedCustomer.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Buscar clientes por nombre de usuario", description = "Devuelve una lista de clientes que coinciden con el nombre de usuario proporcionado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clientes encontrados exitosamente.")
    })
    @GetMapping("/search")
    public List<CustomerDTO> search(
            @Parameter(description = "Nombre de usuario a buscar", required = true)
            @RequestParam String username) {
        return customerService.searchCustomers(username);
    }
}
