package com.example.nexufy.controller;

import com.example.nexufy.persistence.entities.Customer;
import com.example.nexufy.persistence.entities.Product;
import com.example.nexufy.persistence.repository.CustomerRepository;
import com.example.nexufy.persistence.repository.ProductRepository;
import com.example.nexufy.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;



    //obtener estadísticas clientes
    @GetMapping("/customers/stats/general")
    public Map<String, Object> getCustomerStats() {
        Map<String, Object> stats = new HashMap<>();
        long totalCustomers = reportService.getTotalCustomers();
        stats.put("totalCustomers", totalCustomers);
        return stats;
    }

    //descargar el reporte en PDF
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
    @GetMapping("/products/download")
    public ResponseEntity<byte[]> downloadProductReport(HttpServletResponse response) {
        try {
            // Obtener los productos de la base de datos
            List<Product> products = productRepository.findAll();

            // Generar el reporte PDF
            byte[] pdfBytes = reportService.generateProductReport(products);

            // Configurar los headers y enviar el PDF como respuesta
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

