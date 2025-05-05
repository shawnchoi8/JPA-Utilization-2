package jpabook.jpashop.Service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.Repository.OrderRepository;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void orderTest() throws Exception {
        //given
        Member member = createMember();
        Item book = createBook("JPA book", 10000, 10);

        int orderCount = 2;
        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("When placing an order, the status must be ORDER", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("The number of ordered product types must be accurate", 1, getOrder.getOrderItems().size());
        assertEquals("The order price is calculated by multiplying the price and quantity", 10000 * orderCount, getOrder.getTotalPrice());
        assertEquals("The stock must decrease by the ordered quantity", 8, book.getStockQuantity());
    }

    @Test(expected = NotEnoughStockException.class)
    public void Order_ExceedingOrderQuantity() throws Exception {
        //given
        Member member = createMember();
        Item item = createBook("JPA book", 10000, 10);

        int orderCount = 11; //Exception

        //when
        orderService.order(member.getId(), item.getId(), orderCount);

        //then
        fail("A NotEnoughStockException must be thrown");
    }

    @Test
    public void cancelOrder() throws Exception {
        //given
        Member member = createMember();
        Item item = createBook("JPA book", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("When an order is canceled, the status must be CANCEL", OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("When an order is canceled, the stock must increase by the canceled quantity", 10, item.getStockQuantity());

    }

    private Member createMember() {
        Member member = new Member();
        member.setName("member1");
        member.setAddress(new Address("Seoul", "street", "11111"));
        em.persist(member);
        return member;
    }

    private Item createBook(String name, int price, int stockQuantity) {
        Item book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }
}