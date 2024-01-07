package com.example.restap.controller;

import com.example.restap.dto.ProductDto;
import com.example.restap.exception.ProductExceptionHandler;
import com.example.restap.exception.ProductNotFoundException;
import com.example.restap.model.Products;
import com.example.restap.response.ResponseHandler;
import com.example.restap.services.ProductServiceImpl;
import com.example.restap.services.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
public class BlogController {
    private ProductServiceImpl productServiceImp;
    private UserServiceImpl userServiceImpl;

    @Autowired
    public BlogController (ProductServiceImpl productServiceImp, UserServiceImpl userServiceImpl){
        this.productServiceImp = productServiceImp;
        this.userServiceImpl =userServiceImpl;

    }
    @PostMapping("/add-design")
    public ResponseEntity<Object> registerProduct (@RequestBody ProductDto productDto){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Products products= productServiceImp.saveProduct.apply(new Products(productDto));
        products.setCreator(userServiceImpl.loadUserByUsername(userDetails.getUsername()));
        ProductDto productDto1 = new ObjectMapper().convertValue(products, ProductDto.class);

        return ResponseHandler.responseBuilder("you have successfully added a design ", HttpStatus.CREATED, productDto1);
    }

    @GetMapping("/get-product/{id}")
    public ResponseEntity<Object> getProduct (@PathVariable Long id){
        Products product = productServiceImp.findProduct.apply(id);
        if (product!= null) {
            ProductDto productDto = new ObjectMapper().convertValue(product, ProductDto.class);
            return ResponseHandler.responseBuilder("the product you asked for is given", HttpStatus.OK, productDto);
        }
        return ResponseHandler.responseBuilder("product not found", HttpStatus.NOT_FOUND,null);
    }

    @PutMapping("/edit-product/{id}")
    public ResponseEntity<Object> editProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        Products existingproducts = productServiceImp.findProduct.apply(id);
        if (existingproducts != null) {
            existingproducts.setName(productDto.getName());
            existingproducts.setCategory(productDto.getCategory());
            existingproducts.setColour(productDto.getColour());
            existingproducts.setImageURL(productDto.getImageURL());
            Products updatedProduct = productServiceImp.saveProduct.apply(existingproducts);
            ProductDto productDto1 = new ObjectMapper().convertValue(updatedProduct, ProductDto.class);
            return ResponseHandler.responseBuilder("Product Successfully Updated ", HttpStatus.CREATED, productDto1);
        }
        return new ProductExceptionHandler().handleProductNotFoundException(new ProductNotFoundException("Product not found"));
    }
    @GetMapping("/all-Pro")
    public ResponseEntity<Object> viewAllProduct(){
        List<Products> productsList = productServiceImp.findAllProduct.get();
        return ResponseHandler.responseBuilder("the products available are:",HttpStatus.OK,productsList);
    }
}
