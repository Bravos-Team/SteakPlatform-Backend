package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {"orderDetails, userAccount"})
    @Query("SELECT o FROM Order o WHERE o.id = :id ")
    Order getFullOrderDetailsById(Long id);

}
