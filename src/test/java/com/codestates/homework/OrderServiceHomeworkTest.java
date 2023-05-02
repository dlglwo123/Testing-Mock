package com.codestates.homework;

import com.codestates.exception.BusinessLogicException;
import com.codestates.order.entity.Order;
import com.codestates.order.repository.OrderRepository;
import com.codestates.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class OrderServiceHomeworkTest {

    @Mock //해당 필드 객체를 Mock 객체로 생성
    private OrderRepository orderRepository;

    @InjectMocks // OrderService의 객체는 주입받은 OrderRepository Mock 객체 포함
    private OrderService orderService;


    @Test
    public void cancelOrderTest() {
        // TODO OrderService의 cancelOrder() 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^
        long orderId = 1L;
        Order order = new Order();
        order.setOrderStatus(Order.OrderStatus.ORDER_CONFIRM);

        //stubbing Mocking
        given(orderRepository.findById(Mockito.anyLong())).willReturn(Optional.of(order));

        Executable executable = () -> orderService.cancelOrder(orderId);

        assertThrows(BusinessLogicException.class, executable);

    }
}
