package com.grok.hunt.Controllers;

import com.grok.hunt.Models.Product;
import com.grok.hunt.Models.Users;
import com.grok.hunt.Repository.ProductRepository;
import com.grok.hunt.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepo userRepository;


    // Create a product
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();  // or get from JWT subject

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        product.setPostedBy(user);
        product.setCreatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    // Get all products
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get product by ID
    @GetMapping("/{id}")
    public Optional<Product> getProductById(@PathVariable int id) {
        return productRepository.findById(id);
    }

    // Delete product by ID
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable int id) {
        productRepository.deleteById(id);
    }

    // Update product (basic example)
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable int id, @RequestBody Product updatedProduct) {
        return productRepository.findById(id).map(product -> {
            product.setName(updatedProduct.getName());
            product.setTagline(updatedProduct.getTagline());
            product.setDescription(updatedProduct.getDescription());
            product.setWebsiteUrl(updatedProduct.getWebsiteUrl());
            product.setThumbnailUrl(updatedProduct.getThumbnailUrl());
            return productRepository.save(product);
        }).orElseThrow(() -> new RuntimeException("Product not found with id " + id));
    }

    // Upvote a product
    @PostMapping("/{id}/upvote")
    public Product upvoteProduct(@PathVariable int id) {
        return productRepository.findById(id).map(product -> {
            product.setUpvotes(product.getUpvotes() + 1);
            return productRepository.save(product);
        }).orElseThrow(() -> new RuntimeException("Product not found with id " + id));
    }
}
