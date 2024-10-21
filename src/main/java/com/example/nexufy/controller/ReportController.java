package com.example.nexufy.controller;

import com.example.nexufy.persistence.entities.Customer;
import com.example.nexufy.persistence.repository.CustomerRepository;
import com.example.nexufy.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public void downloadCustomerReport(HttpServletResponse response) {
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

    // obtener estadísticas detalladas de clientes y productos
    @GetMapping("/customers/stats/details")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        long totalCustomers = reportService.getTotalCustomers();
        Map<String, Long> customerRegistrationsByMonth = reportService.getCustomerRegistrationsByMonth();
        Map<String, Long> productsByMonth = reportService.getProductsByMonth();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCustomers", totalCustomers);
        stats.put("customerRegistrationsByMonth", customerRegistrationsByMonth);
        stats.put("productsByMonth", productsByMonth);

        return ResponseEntity.ok(stats);
    }
}
