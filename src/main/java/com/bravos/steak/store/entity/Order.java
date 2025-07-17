package com.bravos.steak.store.entity;

import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.store.model.enums.OrderStatus;
import com.bravos.steak.useraccount.entity.UserAccount;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Order {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = UserAccount.class)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

    @Enumerated(EnumType.ORDINAL)
    @Builder.Default
    private OrderStatus status = OrderStatus.UNPAID;

    @Builder.Default
    private Long createdAt = DateTimeHelper.currentTimeMillis();

    @Builder.Default
    private Long updatedAt = DateTimeHelper.currentTimeMillis();

    private String message;

}
