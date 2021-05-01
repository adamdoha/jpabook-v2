## 기록

JPQL에서 fetch Join으로 1+N 문제를 해결하려고 할 때

1. DTO로 커팅해서 가져오는가

2. 엔티티를 가져와서 Application 단에서 데이터를 정리하는가

2가지의 상황이 있을 수 있는데, 각각의 Trade-off가 있다.

1의 경우 select절이 줄어들기 때문에 애플리케이션 네트워크 용량 최적화를 할 수 있다. 그러나 `재사용성` 에 문제가 있다.

2의 경우는 보여질 데이터가 아님에도 모든 데이터를 끌고 오기에 약간은 불필요한 감이 있지만, `재사용성` 은 1에 비해 높다.

<u>그렇지만, 네트워크 용량이 그렇게나 많이 차이날까?</u>

사실 성능을 먹는 것은 join에서 대부분 걸린다. 전체적으로 바라봤을 때는 select에 몇개 더 들어간다고 해서 성능 이슈는 거의 없다. 실제 성능을 좌지우지 하는 부분은 인덱스 쿼리이다. 그럼에도 고객 트래픽이 엄청난 API라면 1과 같이 최적화를 하는 것이 좋겠다.

**권장 순서**

1. 엔티티 조회방식으로 우선 접근
   1. 페치조인으로 쿼리 수를 최적화
   2. 컬렉션 최적화
      1. 페이징 필요O : hibernate.default_batch_fetch_size , @BatchSize 로 최적화 
      2. 페이징 필요X : 페치 조인 사용

2. 엔티티 조회 방식으로 해결이 안되면 DTO조회 방식 사용
3. DTO 조회 방식으로 해결이 안되면 NativeSQL or 스프링 JdbcTemplate



```java
private class OrderDto {
  private Long orderId;
  private String name;
  private LocalDateTime orderDate;
  private OrderStatus orderStatus;
  private Address address;
  private List<OrderItem> orderItems;
}
```

예를 들어서 Order 안에 OrderItem들이 있을 때, Order를 DTO로 만들겠다고, 해당 값들을 DTO로 복사한다. 그리고 그냥 orderDto.orderItems = order.getOrderItems() 를 하면, order에 있는 orderItem은 레이지로 발라져 있는 엔티티이기 때문에 dto.orderItems엔 null로 출력이 된다. 즉, 프록시 강제 초기화를 해줘야 한다.

근데 사실 ㅎㅎ 이렇게 엔티티를 DTO로 래핑하는 것도 좋지 않다. OrderItem 또한 DTO로 만들어줘야 한다.

완전히 Entity를 분리해줘야 한다.

```java
private class OrderDto {
  private Long orderId;
  private String name;
  private LocalDateTime orderDate;
  private OrderStatus orderStatus;
  private Address address;
  private List<OrderItemDto> orderItems;
}
```

