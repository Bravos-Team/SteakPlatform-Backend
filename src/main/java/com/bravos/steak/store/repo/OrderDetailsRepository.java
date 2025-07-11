package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
  List<OrderDetails> findByOrder_Id(Long orderId);

    List<OrderDetails> findByOrderId(Long orderId);
}