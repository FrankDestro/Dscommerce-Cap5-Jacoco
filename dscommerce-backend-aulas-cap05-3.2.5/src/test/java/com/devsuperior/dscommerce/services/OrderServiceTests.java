package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.OrderDTO;
import com.devsuperior.dscommerce.entities.*;
import com.devsuperior.dscommerce.factory.OrderFactory;
import com.devsuperior.dscommerce.factory.ProductFactory;
import com.devsuperior.dscommerce.factory.UserFactory;
import com.devsuperior.dscommerce.repositories.OrderItemRepository;
import com.devsuperior.dscommerce.repositories.OrderRepository;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.ForbiddenException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class OrderServiceTests {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    private Order order;
    private OrderDTO orderDTO;
    private Product product;

    private long existingOrderId;
    private long nonExistingOrderId;

    private User adminClient;
    private User selfClient;
    private User otherClient;
    private long existingProductId;
    private long nonExistingProductId;

    @BeforeEach
    void Setup() throws Exception{

        existingOrderId = 1L;
        nonExistingOrderId = 2L;

        existingProductId = 1L;
        nonExistingProductId = 2L;

        adminClient = UserFactory.createAdminUser();
        selfClient = UserFactory.createCustomClientUser(1L, "Bob Blue");
        otherClient = UserFactory.createCustomClientUser(2L, "Ana Yellow");

        order = OrderFactory.createOrder(selfClient);
        orderDTO = new OrderDTO(order);

        product = ProductFactory.createProduct();

        when(orderRepository.findById(existingOrderId)).thenReturn(Optional.of(order));
        when(orderRepository.findById(nonExistingOrderId)).thenReturn(Optional.empty());

        when(productRepository.getReferenceById(existingProductId)).thenReturn(product);
        when(productRepository.getReferenceById(nonExistingProductId)).thenThrow(EntityNotFoundException.class);


        when(orderRepository.save(any())).thenReturn(order);

        when(orderItemRepository.saveAll(any())).thenReturn(new ArrayList<>(order.getItems()));
    }

    @Test
    public void findByIdShouldReturnOrderDTOWhenIdExistsAndAdminLogged() {

        doNothing().when(authService).validateSelfOrAdmin(any());

        OrderDTO result = orderService.findById(existingOrderId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingOrderId);
    }

    @Test
    public void findByIdShouldReturnOrderDTOWhenIdExistsSelfClientLogged() {

        doNothing().when(authService).validateSelfOrAdmin(any());

        OrderDTO result = orderService.findById(existingOrderId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingOrderId);
    }

    @Test
    public void findByIdShouldThrowForbiddenExceptionWhenIdExistsAndOtherLogged() {
        doThrow(ForbiddenException.class).when(authService).validateSelfOrAdmin(any());

        Assertions.assertThrows(ForbiddenException.class, () -> {
            OrderDTO result = orderService.findById(existingOrderId);
        });
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenOrderIdDoesNotExists() {

        doNothing().when(authService).validateSelfOrAdmin(any());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
           orderService.findById(nonExistingOrderId);
        });
    }

    @Test
    public void insertShouldReturnOrderDTOWhenAndAdminLogged() {
        when(userService.authenticated()).thenReturn(adminClient);

        OrderDTO result = orderService.insert(orderDTO);
        Assertions.assertNotNull(result);
    }

    @Test
    public void insertShouldReturnOrderDTOWhenAndClientLogged() {
        when(userService.authenticated()).thenReturn(selfClient);

        OrderDTO result = orderService.insert(orderDTO);
        Assertions.assertNotNull(result);
        assertEquals(result.getClient().getId(), selfClient.getId());
    }

    @Test
    public void insertShouldThrowsUserNotFoundExceptionWhenUserNotLogged() {

      doThrow(UsernameNotFoundException.class).when(userService).authenticated();

      order.setClient(new User());
      orderDTO = new OrderDTO(order);

      Assertions.assertThrows(UsernameNotFoundException.class, () -> {
          orderService.insert(orderDTO);
      });
    }

    @Test
    public void insertShouldThrowsEntityNotFoundExceptionWhenProductIdDoesNotExists() {
        when(userService.authenticated()).thenReturn(selfClient);

        product.setId(nonExistingOrderId);
        OrderItem orderItem = new OrderItem(order, product, 2,10.0);
        order.getItems().add(orderItem);
        orderDTO = new OrderDTO(order);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            orderService.insert(orderDTO);
        });
    }
}
