package com.sharyar.Electrify.ElectronicsShop.services.implementations;

import com.sharyar.Electrify.ElectronicsShop.config.GetPrincipal;
import com.sharyar.Electrify.ElectronicsShop.dto.CartDto;
import com.sharyar.Electrify.ElectronicsShop.dto.CartItemRequest;
import com.sharyar.Electrify.ElectronicsShop.dto.UserResponseDto;
import com.sharyar.Electrify.ElectronicsShop.entities.Cart;
import com.sharyar.Electrify.ElectronicsShop.entities.CartItem;
import com.sharyar.Electrify.ElectronicsShop.entities.Product;
import com.sharyar.Electrify.ElectronicsShop.entities.User;
import com.sharyar.Electrify.ElectronicsShop.exceptions.BadRequestApiException;
import com.sharyar.Electrify.ElectronicsShop.exceptions.ResourceNotFoundException;
import com.sharyar.Electrify.ElectronicsShop.repositories.CartItemRepository;
import com.sharyar.Electrify.ElectronicsShop.repositories.CartRepository;
import com.sharyar.Electrify.ElectronicsShop.services.CartService;
import com.sharyar.Electrify.ElectronicsShop.services.ProductService;
import com.sharyar.Electrify.ElectronicsShop.services.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Service
public class CartServiceImplementation implements CartService {

    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    GetPrincipal getPrincipal;
    private Logger logger = LoggerFactory.getLogger(CartServiceImplementation.class);
   // private static Map<Integer , Product> productAvailableQuantitiesBeforeAddingToCart = new HashMap<>();

    @Override
    public CartDto addToCart(CartItemRequest itemRequest)
    {
        Principal principal = getPrincipal.getPrincipal();
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
       Cart userCart = user.getMyCart();
       if( userCart == null)
       {
           logger.info("userCart is null");
           userCart = new Cart(LocalDate.now() , user);
           user.setMyCart(userCart);
       }
       Set<CartItem> cartItems = userCart.getCartItemSet();

       Product product = productService.checkProductExists(itemRequest.getProductId());
       int productAvailableQuantity = product.getQuantityAvailable();
       int requestedQuantity = itemRequest.getCount();
       logger.info("productAvailable :{}" , productAvailableQuantity );
       logger.info("requested: {}" , requestedQuantity);
       if(productAvailableQuantity < requestedQuantity || !product.getInStock())
       {
           throw new ResourceNotFoundException("We only have " + productAvailableQuantity
              + " pieces of this product while you requested " + requestedQuantity);
       }
       Iterator<CartItem> it = cartItems.iterator();
       boolean itemAlreadyExistsInCart = false;
       while(it.hasNext())
       {
           CartItem item = it.next();
           if(item.getProduct() == product)
           {
               logger.info("This product already exists in the cartItem");
               if(productAvailableQuantity > item.getCount() + requestedQuantity)
               {
                   item.setCount(item.getCount() + requestedQuantity);
               }
               else {
                   throw  new BadRequestApiException("We dont have enough of this product in the inventory.");
               }
               userCart.setTotalBill();
               itemAlreadyExistsInCart = true;
           }
       }
       if (!itemAlreadyExistsInCart)
       {
           logger.info("This product didnt exist before in your CartItems. we will create it now");
           CartItem newCartItem = new CartItem();
           newCartItem.setProduct(product);
           newCartItem.setCount(requestedQuantity);
           newCartItem.setCart(userCart);
           cartItems.add(newCartItem);
           userCart.setTotalBill();
       }
       cartRepository.save(userCart);
       logger.info("product updated successfully and usercart along with its " +
                "associated entities saved!");
       CartDto cartDto = modelMapper.map(userCart, CartDto.class);
       cartDto.setUserResponseDto( modelMapper.map(user , UserResponseDto.class));

       return cartDto;
    }

    @Override
    public CartDto deleteFromCart(int cartItemId)
    {
        Principal principal = getPrincipal.getPrincipal();
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Cart userCart =  user.getMyCart();
        if(userCart == null || userCart.getCartItemSet().isEmpty())
        {
            throw new BadRequestApiException("this user does not have any item in his cart");
        }
        Set<CartItem> userCartItems = userCart.getCartItemSet();
        Iterator<CartItem> it = userCartItems.iterator();
        while (it.hasNext())
        {
            CartItem currentCartItem = it.next();
            if(currentCartItem.getCartItemId() == cartItemId)
            {
               userCartItems.remove(currentCartItem);
               it.remove();
            }
        }
        userCart.setTotalBill();
        Cart updatedUserCart = cartRepository.save(userCart);
        CartDto cartDto = modelMapper.map(updatedUserCart, CartDto.class);
        cartDto.setUserResponseDto( modelMapper.map(updatedUserCart , UserResponseDto.class) );

        return cartDto;
    }

    @Override
    public void clearCart()
    {
        Principal principal = getPrincipal.getPrincipal();
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        clearCart(user, user.getMyCart());
    }

    private void clearCart(User user , Cart myCart)
    {
        if(myCart != null)
        {
            myCart.setTotalBill(0);
            myCart.getCartItemSet().clear();
            cartRepository.save(myCart);
        }
    }

    @Override
    public CartDto getCart()
    {
        Principal principal = getPrincipal.getPrincipal();
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Cart cart = cartRepository.findByUser(user);
        if(cart == null)
        {
            throw new ResourceNotFoundException("Your cart doesnt exist. Add some items " +
                    "to create your cart");
        }

        return  modelMapper.map(cart , CartDto.class);
    }



}
