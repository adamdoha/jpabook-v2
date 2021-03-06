package com.jpashop.dolphago.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jpashop.dolphago.domain.shop.Delivery;
import com.jpashop.dolphago.domain.shop.Item;
import com.jpashop.dolphago.domain.shop.Member;
import com.jpashop.dolphago.domain.shop.Order;
import com.jpashop.dolphago.domain.shop.OrderItem;
import com.jpashop.dolphago.repository.ItemRepository;
import com.jpashop.dolphago.repository.MemberRepository;
import com.jpashop.dolphago.repository.OrderRepository;
import com.jpashop.dolphago.repository.OrderSearch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    //주문
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        //엔티티 조회
        final Member member = memberRepository.findOne(memberId);
        final Item item = itemRepository.findOne(itemId);

        //배송 정보 생성
        final Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress()); //멤버의 주소로 배송할 때

        //주문상품 생성
        final OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        final Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장 -> Cascade 옵션이 있기 때문에 orderItem 과 delivery 도 같이 저장이 된다.
        orderRepository.save(order);
        return order.getId();
    }

    // 주문 취소
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 엔티티 조회
        final Order order = orderRepository.findOne(orderId);
        //주문 취소
        order.cancel();
    }

    //검색
    public List<Order> findOrders(OrderSearch orderSearch) {
//        return orderRepository.findAllByString(orderSearch);
        return orderRepository.findAll(orderSearch);
    }

}
