package com.example.springbootsampleprojectdeployonexternalserver.controllers;
import com.example.springbootsampleprojectdeployonexternalserver.exception.UserNotFoundException;
import com.example.springbootsampleprojectdeployonexternalserver.models.Product;
import com.example.springbootsampleprojectdeployonexternalserver.models.ProductDto;
import com.example.springbootsampleprojectdeployonexternalserver.services.ProductsRepository;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/products")
public class ProductsController {
    @Autowired
    private ProductsRepository repo;
//https://www.youtube.com/watch?v=o1HsGbTZObQ&t=299s  for dotenv video

    @GetMapping({"", "/"})
    public String showProductList(Model model) {
//        Dotenv dotenv = Dotenv.load();
//        System.out.println("Sweet");
//        System.out.println(dotenv.get("DB_USERNAME"));
        List<Product> products = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("products", products);
        return "products/index";
    }

    @GetMapping("/create") //Getmapping to show the form
    public String showCreatePage(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "products/CreateProduct";
    }

    @PostMapping("/create") //Post the form data and save to database
    public String createProduct(
            @Valid @ModelAttribute ProductDto productDto, BindingResult result
    ) {
        if (productDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("productDto", "imageFile", "The image file is required"));
        }
        if (result.hasErrors()) {
            return "products/CreateProduct";
        }
        //save image file
        MultipartFile image = productDto.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCreatedAt(createdAt);
        product.setImageFileName(storageFileName);

        repo.save(product);

        return "redirect:/products";
    }

    @GetMapping("/edit")
    public String showEditPage(
            Model model, @RequestParam int id
    ) {

        try {
            Product product = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
            model.addAttribute("product", product);
            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());

            model.addAttribute("productDto", productDto);
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/products";
        }
        return "products/EditProduct.html";
    }

    //update the data
    @PostMapping("/edit")
    public String updateProduct(
            Model model, @RequestParam int id,
            @Valid @ModelAttribute ProductDto productDto,
            BindingResult result
    ) {
        String storageFileName;
        try {
            Product product = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
            model.addAttribute("product", product);
            //product will populate in the form (IN) and productDto will be submitted from the form (OUT)
            if (result.hasErrors()) {
                return "products/EditProduct";
            }
            if (!productDto.getImageFile().isEmpty()) { //true if productDto has new image
                //delete old image

                String uploadDir = "public/images/";
                Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());
                try {
                    Files.delete(oldImagePath);
                } catch (Exception ex) {
                    System.out.println("Exception: " + ex.getMessage());
                }

                //save new image file
                Date createdAt = new Date();
                MultipartFile image = productDto.getImageFile();
                storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
                try (InputStream inputStream = image.getInputStream()) {
                    uploadDir = "public/images/";
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                            StandardCopyOption.REPLACE_EXISTING);
                    product.setName(productDto.getName());
                    product.setBrand(productDto.getBrand());
                    product.setCategory(productDto.getCategory());
                    product.setPrice(productDto.getPrice());
                    product.setDescription(productDto.getDescription());
                    product.setCreatedAt(createdAt);
                    product.setImageFileName(storageFileName);
                    repo.save(product);

                }
            } else {

                try {
                    product.setName(productDto.getName());
                    product.setBrand(productDto.getBrand());
                    product.setCategory(productDto.getCategory());
                    product.setPrice(productDto.getPrice());
                    product.setDescription(productDto.getDescription());
                    product.setImageFileName(product.getImageFileName());
                    repo.save(product);

                } catch (Exception ex) {
                    System.out.println("Exception: " + ex.getMessage());
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }


        return "redirect:/products";
    }

    @GetMapping("/delete")
    public String deleteProduct(
            @RequestParam int id
    ) {

        try {
            Product product = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
            //delete image
            String uploadDir = "public/images/";
            Path imagePath = Paths.get(uploadDir + product.getImageFileName());
            try {
                Files.delete(imagePath);
            } catch (Exception ex) {
                System.out.println("Exception: " + ex.getMessage());
            }
            repo.delete(product);
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }
        return "redirect:/products";
    }

}
