package com.example.nexufy.service;

import com.example.nexufy.persistence.entities.Customer;
import com.example.nexufy.persistence.entities.Product;
import com.example.nexufy.persistence.repository.CustomerRepository;
import com.example.nexufy.persistence.repository.ProductRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    public byte[] generateCustomerReport(List<Customer> customers) throws JRException {
        String jrxmlFile = "src/main/resources/customer_report.jrxml";

        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(customers);


        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ReportTitle", "Reporte de Usuarios Registrados");


        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);


        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    // Método para obtener la cantidad de clientes registrados por mes
    public Map<String, Long> getCustomerRegistrationsByMonth() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .filter(customer -> customer.getRegistrationDate() != null)  // Corregido aquí
                .collect(Collectors.groupingBy(
                        customer -> customer.getRegistrationDate().getMonth().toString(),  // Corregido aquí
                        Collectors.counting()
                ));
    }

    // Método para obtener la cantidad de productos publicados por mes
    public Map<String, Long> getProductsByMonth() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .filter(product -> product.getPublicationDate() != null)
                .collect(Collectors.groupingBy(
                        product -> product.getPublicationDate().getMonth().toString(),
                        Collectors.counting()
                ));
    }

    public long getTotalCustomers() {
        return customerRepository.count();
    }
}

