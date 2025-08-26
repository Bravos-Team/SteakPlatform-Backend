package com.bravos.steak.store.repo;

import com.bravos.steak.dev.model.response.GameStatisticItem;
import com.bravos.steak.store.entity.OrderDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {

    List<OrderDetails> findByOrder_Id(Long orderId);

    List<OrderDetails> findByOrderId(Long orderId);

    @Query("SELECT new com.bravos.steak.dev.model.response.GameStatisticItem(od.game.id, od.game.name, '', COUNT(od.id), SUM(od.price)) " +
            "FROM OrderDetails od " +
            "WHERE od.game.publisher.id = :publisherId " +
            "GROUP BY od.game.id, od.game.name " +
            "ORDER BY SUM(od.price) DESC")
    Page<GameStatisticItem> getGameStatisticsRevenue(Long publisherId, Pageable pageable);

    @Query("SELECT new com.bravos.steak.dev.model.response.GameStatisticItem(od.game.id, od.game.name, '', COUNT(od.id), SUM(od.price)) " +
            "FROM OrderDetails od " +
            "WHERE od.order.createdAt BETWEEN :from AND :to AND od.game.publisher.id = :publisherId " +
            "GROUP BY od.game.id, od.game.name " +
            "ORDER BY SUM(od.price) DESC")
    Page<GameStatisticItem> getGameStatisticsRevenue(Long publisherId, Long from, Long to, Pageable pageable);

}