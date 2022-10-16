package com.works.services;

import com.works.entities.Basket;
import com.works.entities.User;
import com.works.entities.Orders;
import com.works.entities.Product;
import com.works.repositories.BasketRepository;
import com.works.repositories.UserRepository;
import com.works.repositories.OrdersRepository;
import com.works.repositories.ProductRepository;
import com.works.utils.REnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class OrdersService {

    final OrdersRepository ordersRepository;
    final ProductService productService;
    final BasketRepository basketRepository;
    final UserRepository userRepository;
    final HttpSession session;
    final ProductRepository productRepository;

    public OrdersService(OrdersRepository ordersRepository, ProductService productService, BasketRepository basketRepository, UserRepository userRepository, HttpSession session, ProductRepository productRepository) {
        this.ordersRepository = ordersRepository;
        this.productService = productService;
        this.basketRepository = basketRepository;
        this.userRepository = userRepository;
        this.session = session;
        this.productRepository = productRepository;
    }

    public ResponseEntity add() {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        Orders orders=new Orders();
        int sum=0;
        User user = (User) session.getAttribute("user");
        if(user !=null) {
            System.out.println("user mail"+ user.getEmail());
            List<Basket> baskets = basketRepository.findByCreatedByEqualsAndStatusFalse(user.getEmail());

            if (baskets.size() > 0) {
                orders.setUser(user);
                orders.setBaskets(baskets);
                for (Basket item : baskets) {
                    sum = sum + item.getProduct().getPrice() * item.getQuantity();
                    Optional<Basket> optionalBasket = basketRepository.findById(item.getId());
                    optionalBasket.get().setStatus(true);
                    basketRepository.saveAndFlush(optionalBasket.get());
                }
                orders.setTotalPrice(sum);
                ordersRepository.save(orders);
                hm.put(REnum.status, true);
                hm.put(REnum.result, orders);
                return new ResponseEntity<>(hm, HttpStatus.OK);
            } else {
                hm.put(REnum.status, false);
                hm.put(REnum.message, "Basket is empty");
                return new ResponseEntity<>(hm, HttpStatus.NOT_ACCEPTABLE);
            }
        }else {
            hm.put(REnum.status, false);
            hm.put(REnum.message, "No such a user ");
            return new ResponseEntity<>(hm, HttpStatus.NOT_ACCEPTABLE);
        }

    }



    public ResponseEntity delete(Long id) {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        try {
           Optional<Orders> optionalOrders=ordersRepository.findById(id);
           if(optionalOrders.isPresent()){
               Orders orders=optionalOrders.get();
               ordersRepository.deleteById(id);
               List<Basket> baskets=orders.getBaskets();
               for(Basket basket:baskets){
                   Integer quantity=basket.getQuantity();
                   Product product=basket.getProduct();
                   product.setStockQuantity(product.getStockQuantity()+quantity);
                   productRepository.save(product);
               }
                hm.put(REnum.status, true);
                return new ResponseEntity<>(hm, HttpStatus.OK);
           }else{
               hm.put(REnum.status, false);
               hm.put(REnum.message, "There is not such order id");
               return new ResponseEntity<>(hm, HttpStatus.BAD_REQUEST);
           }
        } catch (Exception ex) {
            hm.put(REnum.status, false);
            hm.put(REnum.error, ex.getMessage());
            return new ResponseEntity<>(hm, HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity getOrderByOrder_Id(Long id){
        Map<REnum, Object> hm = new LinkedHashMap<>();
        List<Orders> orders=ordersRepository.findByIdIs(id);
        hm.put(REnum.result,orders);
        return new ResponseEntity<>(hm,HttpStatus.OK);

    }

    public ResponseEntity list() {
        Map<REnum, Object> hm = new HashMap<>();
        List<Orders> ordersList = ordersRepository.findAll();
        hm.put(REnum.status, true);
        hm.put(REnum.result, ordersList);
        return new ResponseEntity<>(hm, HttpStatus.OK);
    }

    public ResponseEntity getOrderByUser() {
        Map<REnum, Object> hm = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            List<Orders> orders=ordersRepository.findByUser_Id(user.getId());
            hm.put(REnum.status, true);
            hm.put(REnum.result, orders);
            return new ResponseEntity<>(hm, HttpStatus.OK);
        }catch (Exception ex){
            hm.put(REnum.status, false);
            hm.put(REnum.error, ex.getMessage());
            return new ResponseEntity<>(hm, HttpStatus.BAD_REQUEST);
        }
    }

}
