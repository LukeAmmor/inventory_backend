package com.inventory_backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory_backend.entities.Product;
import com.inventory_backend.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() throws IOException {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) throws IOException {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping(value = "/upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> uploadProductImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        return ResponseEntity.ok(productService.uploadProductImage(id, file));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> addProduct(
            @RequestParam("productData") String productJson,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        // Convert JSON string to Product object
        ObjectMapper mapper = new ObjectMapper();
        Product product = mapper.readValue(productJson, Product.class);

        // If file is provided, convert it to Base64 and set as imageData
        if (file != null && !file.isEmpty()) {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            product.setImageData(base64Image);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addProduct(product));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) throws IOException {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) throws IOException {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }


}