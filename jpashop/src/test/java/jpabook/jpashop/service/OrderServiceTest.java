package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class OrderServiceTest {

    @PersistenceContext EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;
    
    @Test
    public void 상품주문() throws Exception {

        //given
        Member member = createMember();
        Book book = createBook("시골 JPA", 10000, 10);

        //when
        int count = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), count);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, getOrder.getOrderStatus());
        assertEquals(1, getOrder.getOrderItems().size());
        assertEquals(10000*count, getOrder.getTotalPrice());
        assertEquals(8, book.getStockQuantity());

    }

    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember();
        Book item = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);
        assertEquals(OrderStatus.CANCEL, getOrder.getOrderStatus());
        assertEquals(10, item.getStockQuantity());
    }

    @Test
    public void 재고_수량_초과() throws Exception {
        //given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);

        int orderCount = 11;

        //when
        orderService.order(member.getId(), item.getId(), orderCount);

        //then
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);

        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);

        return member;
    }
}