package com.sharyar.Electrify.ElectronicsShop.services.implementations;

import com.sharyar.Electrify.ElectronicsShop.config.GetPrincipal;
import com.sharyar.Electrify.ElectronicsShop.dto.OrderDto;
import com.sharyar.Electrify.ElectronicsShop.entities.*;
import com.sharyar.Electrify.ElectronicsShop.exceptions.BadRequestApiException;
import com.sharyar.Electrify.ElectronicsShop.exceptions.ResourceNotFoundException;
import com.sharyar.Electrify.ElectronicsShop.repositories.OrderRepository;
import com.sharyar.Electrify.ElectronicsShop.repositories.ProductRepository;
import com.sharyar.Electrify.ElectronicsShop.services.CartService;
import com.sharyar.Electrify.ElectronicsShop.services.OrderService;
import com.sharyar.Electrify.ElectronicsShop.services.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImplementation implements OrderService {
    @Autowired
    private UserService userService;
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private GetPrincipal getPrincipal;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ModelMapper modelMapper;

    Logger logger = LoggerFactory.getLogger(OrderServiceImplementation.class);

    @Override
    public OrderDto proceedToCheckout() {
        Principal principal = getPrincipal.getPrincipal();
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        List<Order> orders =  user.getOrderHistory();
        for(Order order : orders)
        {
            if(!order.isPaymentStatus())
            {
                throw new BadRequestApiException("You already have 1 order pending. Please clear that order first" +
                        " before creating a new order");
            }
        }
        Cart userCart = user.getMyCart();
        if(userCart == null)
        {
            throw new BadRequestApiException("Your cart is empty.");
        }
        Set<CartItem> cartItems = userCart.getCartItemSet();
        Order order = new Order();
        order.setUser(user);
        order.setOrderedDate(LocalDate.now());
        order.setOrderStatus("pending");
        order.setPaymentStatus(false);
        order.setOrderBill(userCart.getTotalBill());
        if(user.getBillingAddress() == null)
        {
            throw new ResourceNotFoundException("Please fill the Billing address first" +
                    " before creating your order");
        }
        order.setBillingAddress(user.getBillingAddress());
        Set<OrderItem> orderItems= order.getOrderItems();
        Iterator<CartItem> it = cartItems.iterator();
        while(it.hasNext())
        {
            CartItem cartItem = it.next();
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setItemBill(cartItem.getItemBill());
            orderItem.setCount(cartItem.getCount());
            orderItem.setProduct(cartItem.getProduct());
            orderItems.add(orderItem);
            logger.info("orderItem in this order : {}" , orderItem.getId());
        }
        orders.add(order);
        logger.info("New Order Id = {}" , order.getOrderId());
        Order savedOrder = orderRepository.save(order);

        return modelMapper.map(savedOrder , OrderDto.class );
    }

    @Override
    public void confirmAndShipToAddress( ) {

           Principal principal = getPrincipal.getPrincipal();
           User user = (User) userDetailsService.loadUserByUsername(principal.getName());
           List<Order> userOrderHistory = user.getOrderHistory();
           int size = userOrderHistory.size();
           if(size < 1)
           {
               throw new BadRequestApiException("You dont have any order created yet. Grab some items in your" +
                       " cart first , create the order and then place the order");
           }
           Order order = null;
           for( Order tempOrder : userOrderHistory)
           {
               logger.info("orderId is {} " , tempOrder.getOrderId());
               if(!tempOrder.isPaymentStatus())
               {
                   order = tempOrder;
               }
           }
           if(order == null)
           {
               throw new BadRequestApiException("You have not created any new order. Grab some items in your" +
                       " cart first , create the order and then place the order");
           }
           Map<Integer , Product> productsBought = new HashMap<>();
           order.getOrderItems().stream().forEach(orderItem -> {
               productsBought.put(orderItem.getCount(), orderItem.getProduct());
           });
           int productsBoughtSize = productsBought.size();
           for( Map.Entry<Integer , Product> entry : productsBought.entrySet())
           {
                int quantityBought = entry.getKey();
                Product product = entry.getValue();
                product.setQuantityAvailable(product.getQuantityAvailable() - quantityBought);
               if(product.getQuantityAvailable() < 1)
               {
                   product.setInStock(false);
                   deleteThisOrder(order);
                   productRepository.save(product);
                   throw new BadRequestApiException("When you added this product " +
                           "in your cart , this product was inStock but " +
                           "now this product seems to be out of stock. We will remove this order.\n" +
                           "Please create a new order. " +
                           "Next time try confirming the order as soon as you create the order because this product " +
                           "is quite in demand because the stock " +
                           "depletes very quickly.");
               }
           }
           order.setPaymentStatus(true);
           order.setOrderStatus("dispatched");
           logger.info("order dispatched is {} " , order.getOrderId());
           cartService.clearCart();
           orderRepository.save(order);

    }

    public List<OrderDto> getAllOrdersByAUser(String userId)
    {
        User user = userService.checkUserExists(userId);
        List <OrderDto> list = user.getOrderHistory().stream().map(
                 order->{
                    return modelMapper.map(order, OrderDto.class);
                 }
         ).toList();

        return  list;
    }

    @Override
    public List<OrderDto> getMyOrderHistory()
    {
        Principal principal = getPrincipal.getPrincipal();
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        List <OrderDto> list = user.getOrderHistory().stream().map(
                order->{
                    return modelMapper.map(order, OrderDto.class);
                }
        ).toList();

        return  list;
    }

    private void deleteThisOrder(Order order)
    {
        orderRepository.delete(order);
    }

    public void deleteMyOrder()
    {
      Principal principal =  getPrincipal.getPrincipal();
      User user = (User) userDetailsService.loadUserByUsername(principal.getName());

     Order order = user.getOrderHistory().stream().filter(newUnConfirmedOrder ->
             !newUnConfirmedOrder.isPaymentStatus()).findFirst().orElse(null);
     if(order == null)
     {
         throw new BadRequestApiException("Can't delete! No order exits! You dont have any newly created order.");
     }
     deleteThisOrder(order);
    }

}
