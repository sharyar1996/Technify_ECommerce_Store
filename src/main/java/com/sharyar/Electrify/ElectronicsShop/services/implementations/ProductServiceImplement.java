package com.sharyar.Electrify.ElectronicsShop.services.implementations;

import com.sharyar.Electrify.ElectronicsShop.config.GetPrincipal;
import com.sharyar.Electrify.ElectronicsShop.dto.PageableResponse;
import com.sharyar.Electrify.ElectronicsShop.dto.ProductRequestDto;
import com.sharyar.Electrify.ElectronicsShop.dto.ProductResponseDto;
import com.sharyar.Electrify.ElectronicsShop.entities.*;
import com.sharyar.Electrify.ElectronicsShop.exceptions.BadRequestApiException;
import com.sharyar.Electrify.ElectronicsShop.exceptions.ResourceNotFoundException;
import com.sharyar.Electrify.ElectronicsShop.helpers.Helper;
import com.sharyar.Electrify.ElectronicsShop.repositories.OrderItemRepository;
import com.sharyar.Electrify.ElectronicsShop.repositories.OrderRepository;
import com.sharyar.Electrify.ElectronicsShop.repositories.ProductRepository;
import com.sharyar.Electrify.ElectronicsShop.services.CategoryService;
import com.sharyar.Electrify.ElectronicsShop.services.FileService;
import com.sharyar.Electrify.ElectronicsShop.services.OrderService;
import com.sharyar.Electrify.ElectronicsShop.services.ProductService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ProductServiceImplement implements ProductService {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    FileService fileService;
    @Autowired
    GetPrincipal getPrincipal;
    @Autowired
    UserDetailsService userDetailsService;
    @Value("${product.profile.image.path}")
    private String productImagePath;

    Logger logger = LoggerFactory.getLogger(ProductServiceImplement.class);

    @Override
    public ProductResponseDto createProduct(ProductRequestDto productRequestDto) throws IOException {

        Product product = modelMapper.map(productRequestDto, Product.class);
        Category category = categoryService.checkCategoryExists(productRequestDto.getCategory());
        if( category != null)
        {
            product.setCategory(category);
            Set<Product> productSet = new HashSet<>();
            productSet.add(product);
            category.setProductList(productSet);
        }
        product.setProductId( UUID.randomUUID().toString() );
        product.setAddedDate(LocalDate.now());
        Product savedProduct = productRepository.save(product);
        ProductResponseDto productResponseDto = null;
        try{
            productResponseDto =
                    modelMapper.map(savedProduct, ProductResponseDto.class);
            logger.info("No problems in modelmapper so far");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return productResponseDto;
    }

    public ProductResponseDto uploadProductImagesToServer(String productId , List<MultipartFile> multipartFiles) throws IOException {

        Product product = checkProductExists(productId);
        if(product.getProductImages() != null)
        {
            deleteImagesFromServer(product);
        }
        String pathName = productImagePath + product.getProductId() + "\\" ;
        List<String> paths = fileService.uploadFile(pathName, multipartFiles);
        if(paths.isEmpty())
        {
            throw new RuntimeException("Images could not be uploaded to the server");
        }
        product.setProductImages(paths);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct , ProductResponseDto.class);

    }

    @Override
    public void rateProduct( String productId , double productRating)
    {
        Product product = checkProductExists(productId);
        Principal principal = getPrincipal.getPrincipal();
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        ProductRating newRating = new ProductRating(user, productRating , product);

        Set<ProductRating> usersWhoRatedThisProduct = product.getProductRatings();
        if(usersWhoRatedThisProduct.stream().anyMatch(existingProductRating -> {
            return existingProductRating.equals(newRating);
        }))
        {
           throw new BadRequestApiException("You have already rated this product before.");
        }
        boolean productBoughtByUserBefore = false;
        List<Order> ordersList = user.getOrderHistory();
        int size = 0;
        if(ordersList != null)
        {
            size = ordersList.size();
            Order[] ordersArr = new Order[size];
            user.getOrderHistory().toArray(ordersArr);
            for(int i = 0 ; i < size ; i++)
            {
                int orderItemArrSize = ordersArr[i].getOrderItems().size();
                OrderItem[] orderItemsArr = new OrderItem[orderItemArrSize];
                ordersArr[i].getOrderItems().toArray( orderItemsArr);

                for (int j = 0 ; j < orderItemArrSize ; j++)
                {
                    Product checkingProduct = orderItemsArr[j].getProduct();
                    if(checkingProduct != null)
                    {
                        if(checkingProduct == product)
                        {
                            productBoughtByUserBefore = true;
                            break;
                        }
                    }
                }
                if(productBoughtByUserBefore)
                {
                    break;
                }
            }
            if(productBoughtByUserBefore)
            {
                product.setAvgRating(productRating);
                usersWhoRatedThisProduct.add(newRating);
                productRepository.save(product);
                return;
            }
        }
            throw new BadRequestApiException("You CAN NOT " +
                    "rate this product because you have not purchased this product yet.");

    }

    @Override
    public ProductResponseDto getProductById(String productId) {

        Product product = checkProductExists(productId);

        return modelMapper.map(product , ProductResponseDto.class);
    }
    @Override
    public ProductResponseDto updateProduct(String productId ,
                                            ProductRequestDto productRequestDto)
    {
        Product product = checkProductExists(productId);
        product.setQuantityAvailable(productRequestDto.getQuantityAvailable());
//     product.setCategory(productDto.getCategory());
//     we would check 1st if this category exists
        product.setBrand(productRequestDto.getBrand());
        product.setDescription(productRequestDto.getDescription());
        product.setDiscountedPrice(productRequestDto.getDiscountedPrice());
        product.setPrice(productRequestDto.getDiscountedPrice());
        product.setName(productRequestDto.getName());
        product.setWarranty(productRequestDto.getWarranty());
        productRepository.save(product);

        return modelMapper.map(product , ProductResponseDto.class);
    }

    @Override
    public void updateProduct(Product product)
    {
        productRepository.save(product);
    }

    public void addProductToCategory(String productId , String categoryId)
    {
        Product product = checkProductExists(productId);
        Category category = categoryService.checkCategoryExists(categoryId);
        if(product.getCategory() != null)
        {
            product.getCategory().getProductList().remove(product);
        }
        product.setCategory(category);
        logger.info("category.getTitle = {}" , category.getTitle());
        category.getProductList().add(product);
        Product savedProduct = productRepository.save(product);
        logger.info("product saved to category in service : {}", savedProduct.getCategory().getProductList().size() );
    }

    @Override
    public PageableResponse<ProductResponseDto> getAllProducts(int pageNumber , int pageSize ,
                                                               String sortBy,
                                                               String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc"))?
                (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());

        Pageable productPageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> productPage = productRepository.findAll(productPageable);

        return Helper.getPageableResponse(productPage, ProductResponseDto.class);
    }

    @Override
    public PageableResponse<ProductResponseDto> searchByName(String name , int pageNumber
                                                   , int pageSize, String sortBy,
                                                             String sortDir ) {
        logger.info("productService method searchByName ran");
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ?
                (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        Pageable productPageable = PageRequest.of(pageNumber , pageSize , sort );
        Page<Product> productsPage = productRepository.findByNameContaining(name ,productPageable );
        logger.info("productsPage = {}" , productsPage);
        PageableResponse<ProductResponseDto> pageableResponse = Helper.getPageableResponse(productsPage , ProductResponseDto.class);

        return pageableResponse;
    }

    @Override
    public PageableResponse<ProductResponseDto> searchByMaxPrice(double price , int pageNumber
            , int pageSize , String sortDir) {
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        Page<Product> productsPage = productRepository.findByPriceLessThanEqual(price,pageable);

        return Helper.getPageableResponse( productsPage , ProductResponseDto.class );
    }

    @Override
    public PageableResponse<ProductResponseDto> searchByCategoryAndProductBrand(String categoryName
            , String brand, int pageNumber, int pageSize, String sortBy , String sortDir   ) {
        logger.info("categoryId from controller : {}" , categoryName);
        Category category = categoryService.checkCategoryExists(categoryName);
        Pageable pageable = Helper.getPageable(pageNumber , pageSize , sortBy , sortDir);
        Page<Product> productPage = productRepository.findByCategoryAndBrand(category,brand, pageable);

        return Helper.getPageableResponse(productPage , ProductResponseDto.class);
    }

    @Override
    public PageableResponse<ProductResponseDto> searchByCategory(String categoryName, int pageNumber,
                                                                 int pageSize, String sortBy , String sortDir) {
         Category category = categoryService.checkCategoryExists(categoryName);
         Pageable pageable = Helper.getPageable(pageNumber , pageSize ,sortBy , sortDir);
         Page<Product> productsPage = productRepository.findByCategory(category , pageable);

         return Helper.getPageableResponse(productsPage , ProductResponseDto.class);
    }



    @Override
    public PageableResponse<ProductResponseDto> searchByBrand(String brandName, int pageNumber,
                                                              int pageSize, String sortBy
                                                     , String sortDir)
    {
         Pageable pageable = Helper.getPageable(pageNumber,pageSize,sortBy,sortDir);
         Page<Product> productPage = productRepository.findByBrandContaining(brandName , pageable);

         return Helper.getPageableResponse(productPage , ProductResponseDto.class);
    }

    @Override
    public PageableResponse<ProductResponseDto> searchByCategoryInStockProducts(String categoryName, int pageNumber,
                                                                                int pageSize, String sortBy
                                                              , String sortDir) {
        Category category = categoryService.checkCategoryExists(categoryName);
        Pageable pageable = Helper.getPageable(pageNumber , pageSize , sortBy , sortDir);
        Page<Product> productPage = productRepository.findByCategoryAndInStockTrue(category , pageable);

        return Helper.getPageableResponse(productPage , ProductResponseDto.class);
    }

    @Override
    public PageableResponse<ProductResponseDto> searchByProductsOnDiscount(int pageNumber,
                                                                           int pageSize, String sortBy
            , String sortDir)
    {
        Pageable pageable = Helper.getPageable(pageNumber,pageSize,sortBy,sortDir);
        Page<Product> productPage = productRepository.findByDiscountedPriceIsNotNull(pageable);

        return Helper.getPageableResponse(productPage , ProductResponseDto.class);
    }

    @Override
    public void deleteProduct(String productId) {
        Product product = checkProductExists(productId);
        deleteProduct(product);
    }

    @Override
    public  void deleteProduct(Product product)
    {
        Category productCategory = product.getCategory();
        if(productCategory != null)
        {
            productCategory.removeAProduct(product);
            product.removeCategory();
            categoryService.updateCategory(productCategory);
        }
        this.deleteImagesFromServer(product);
        List<OrderItem> orderItemsList =  orderItemRepository.findByProduct(product);
        int orderItemsLen = orderItemsList.size();
        OrderItem[] orderItems = new OrderItem[orderItemsList.size()];
        if(orderItemsLen > 0)
        {
           orderItemsList.toArray(orderItems);
        }
        for(int i =0; i < orderItemsLen ; i++)
        {
             orderItems[i].setProduct(null);
             orderRepository.save(orderItems[i].getOrder());
        }

        productRepository.delete(product);
    }

    private boolean deleteImagesFromServer(Product product)
    {
        List<String> pathList = product.getProductImages();
        if (pathList == null)
        {
            return  true;
        }
        int numOfImages = pathList.size();
        String[] photos = new String[numOfImages];
        pathList.toArray(photos);
        try
        {
            for (int i = 0; i < numOfImages; i++)
            {
                Path path = Path.of(photos[i]);

                boolean deleted = Files.deleteIfExists(path);
                if (deleted) {
                    logger.info("Product " + product.getName() + "\\'s " + i + 1 + "st   image " +
                            "have been deleted");
                } else {
                    logger.info("Product " + product.getName() + "\\'s  images " +
                            " CANNOT BE DELETED!");
                }
            }
            //NOW DELETING THE EMPTY FOLDER
           Path emptyFolderPath = Path.of(productImagePath + product.getProductId());
            try(DirectoryStream<Path> stream = Files.newDirectoryStream(emptyFolderPath);)
            {
                if(!stream.iterator().hasNext())
                {
                    logger.info("emptyFolderPath is empty!");
                    Files.delete(emptyFolderPath);
                }
                else
                {
                    logger.info("emptyFolderPath is not empty!");
                }
            }
            catch (DirectoryNotEmptyException e)
            {
                throw new RuntimeException(e.getMessage());
            }

        }
        catch (IOException e) {
                logger.info("IOException during deleting product files from server");
                throw new RuntimeException(e.getMessage());
            }

       return true;
    }

    public Product checkProductExists(String id)
    {
        Product product = productRepository.findById(id).orElse(null);
        if(product == null)
        {
            throw new ResourceNotFoundException("No product exists with this productId." +
                    "Can't upload product images!");
        }
        return  product;
    }

}
