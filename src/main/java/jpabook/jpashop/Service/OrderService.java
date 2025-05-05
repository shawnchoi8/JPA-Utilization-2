package jpabook.jpashop.Service;

import jpabook.jpashop.Repository.ItemRepository;
import jpabook.jpashop.Repository.MemberRepository;
import jpabook.jpashop.Repository.OrderRepository;
import jpabook.jpashop.Repository.OrderSearch;
import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * Order
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        //Entity 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //Create Delivery Into
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress()); //Just to make it simple

        //Create OrderItem
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //Create Order
        Order order = Order.createOrder(member, delivery, orderItem);//order가 생성되었다.

        //Save order
        orderRepository.save(order);

        return order.getId(); //order의 식별자값 return
    }

    /**
     * Cancel Order
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        //Order Entity 조회
        Order order = orderRepository.findOne(orderId);
        //Cancel Order
        order.cancel(); //Order 클래스에 가서 cancel method 봐봐
    }

    /**
     * 주문 검색
     */
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAllByString(orderSearch);
    }
}
