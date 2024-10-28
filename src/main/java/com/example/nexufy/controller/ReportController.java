package com.example.nexufy.controller;

import com.example.nexufy.persistence.entities.Customer;
import com.example.nexufy.persistence.entities.Product;
import com.example.nexufy.persistence.repository.CustomerRepository;
import com.example.nexufy.persistence.repository.ProductRepository;
import com.example.nexufy.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reportes", description = "Operaciones relacionadas con la generación de reportes")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Operation(summary = "Obtener estadísticas generales de clientes", description = "Devuelve el número total de clientes registrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente")
    })
    @GetMapping("/customers/stats/general")
    public Map<String, Object> getCustomerStats() {
        Map<String, Object> stats = new HashMap<>();
        long totalCustomers = reportService.getTotalCustomers();
        stats.put("totalCustomers", totalCustomers);
        return stats;
    }

    @Operation(summary = "Descargar reporte de clientes en PDF", description = "Genera un reporte PDF de los clientes y lo envía como archivo descargable.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno al generar el reporte")
    })
    @GetMapping("/customers/download")
    public void generateCustomerReport(HttpServletResponse response) {
        try {
            List<Customer> customers = customerRepository.findAll();
            byte[] pdfBytes = reportService.generateCustomerReport(customers);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=customer_report.pdf");

            OutputStream out = response.getOutputStream();
            out.write(pdfBytes);
            out.flush();
        } catch (JRException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Obtener estadísticas detalladas", description = "Devuelve estadísticas de clientes y productos por mes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente")
    })
    @GetMapping("/customers/stats/details")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        long totalCustomers = reportService.getTotalCustomers();
        long totalProducts = reportService.getTotalProducts();

        Map<String, Long> customerRegistrationsByMonth = reportService.getCustomerRegistrationsByMonth();
        Map<String, Long> productsByMonth = reportService.getProductsByMonth();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCustomers", totalCustomers);
        stats.put("totalProducts", totalProducts);
        stats.put("customerRegistrationsByMonth", customerRegistrationsByMonth);
        stats.put("productsByMonth", productsByMonth);

        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Generar reporte de productos en PDF", description = "Devuelve un reporte PDF con información de los productos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno al generar el reporte")
    })
    @GetMapping("/product-report")
    public ResponseEntity<byte[]> generateProductReport() throws JRException {
        List<Product> products = productRepository.findAll();
        byte[] pdfReport = reportService.generateProductReport(products);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "product_report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfReport);
    }

    @Operation(summary = "Descargar reporte de productos en PDF", description = "Genera y descarga un reporte PDF de productos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reporte descargado exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno al generar el reporte")
    })
    @GetMapping("/products/download")
    public ResponseEntity<byte[]> downloadProductReport(HttpServletResponse response) {
        try {
            List<Product> products = productRepository.findAll();
            byte[] pdfBytes = reportService.generateProductReport(products);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("product_report.pdf")
                    .build());

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (JRException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
