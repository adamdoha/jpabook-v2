package com.jpashop.dolphago.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.jpashop.dolphago.domain.shop.Item;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em;

    public void save(Item item) {
        //데이터를 처음에 저장할 때는 아이디가 없으므로.
        //아이디가 없다는 것은 완전히 새로 생성한 객체라는 뜻
        if (item.getId() == null) {
            em.persist(item);
        } else {
            em.merge(item); //업데이트와 비슷한 개념
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class).getResultList();
    }

}
