package com.bravos.steak.store.entity;

import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.useraccount.entity.UserAccount;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cart")
public class Cart {

    @Id
    private Long id;

    @ManyToOne(targetEntity = UserAccount.class)
    @JoinColumn(name = "user_account_id",unique = true)
    private UserAccount userAccount;

    @OneToMany(
            mappedBy = "cart",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<CartItem> cartItems;

    @Builder.Default
    private Long createdAt = DateTimeHelper.currentTimeMillis();

    @Builder.Default
    private Long updatedAt = DateTimeHelper.currentTimeMillis();

}
