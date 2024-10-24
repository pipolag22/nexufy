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

    // Generar el reporte de clientes
    public byte[] generateCustomerReport(List<Customer> customers) throws JRException {
        String jrxmlFile = "src/main/resources/customer_report.jrxml"; //
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile);

        // Seleccionar los últimos 5 clientes, con o sin fecha de registro
        List<Customer> last5Customers = customers.stream()
                .sorted((c1, c2) -> {
                    if (c1.getRegistrationDate() == null && c2.getRegistrationDate() == null) return 0;
                    if (c1.getRegistrationDate() == null) return 1;
                    if (c2.getRegistrationDate() == null) return -1;
                    return c2.getRegistrationDate().compareTo(c1.getRegistrationDate());
                })
                .limit(5)
                .collect(Collectors.toList());


        JRBeanCollectionDataSource customerDataSource = new JRBeanCollectionDataSource(last5Customers);

        // Obtener el total de clientes
        long totalCustomers = customerRepository.count();

        // Agregar los parámetros al mapa
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ReportTitle", "Reporte de Usuarios");
        parameters.put("totalCustomers", totalCustomers);

        // Llenar el reporte con los parámetros y la fuente de datos de los clientes
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, customerDataSource);

        // Exportar a PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    // Generar el reporte de productos
    public byte[] generateProductReport(List<Product> products) throws JRException {
        String jrxmlFile = "src/main/resources/product_report.jrxml";  // Ruta al archivo de diseño del reporte
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile);

        // Verificar si la lista de productos está vacía
        boolean isProductListEmpty = products.isEmpty();
        String noProductsMessage = isProductListEmpty ? "No hay productos disponibles." : "";

        // Crear un data source para los productos
        JRBeanCollectionDataSource productDataSource = new JRBeanCollectionDataSource(products);

        // Parámetros del reporte
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("NoProductsMessage", noProductsMessage);

        // Llenar el reporte con los parámetros y el data source
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, productDataSource);

        // Exportar el reporte a PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }



    // Métodos de estadística
    public Map<String, Long> getCustomerRegistrationsByMonth() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .filter(customer -> customer.getRegistrationDate() != null)
                .collect(Collectors.groupingBy(
                        customer -> customer.getRegistrationDate().getMonth().toString(),
                        Collectors.counting()
                ));
    }

    public Map<String, Long> getProductsByMonth() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .filter(product -> product.getPublicationDate() != null)
                .collect(Collectors.groupingBy(
                        product -> product.getPublicationDate().getMonth().toString(),
                        Collectors.counting()
                ));
    }

    public long getTotalProducts() {
        return productRepository.count();
    }

    public long getTotalCustomers() {
        return customerRepository.count();
    }
}
