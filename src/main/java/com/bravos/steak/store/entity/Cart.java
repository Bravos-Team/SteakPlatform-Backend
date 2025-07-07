package com.bravos.steak.store.entity;

import com.bravos.steak.useraccount.entity.UserAccount;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "cart")
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

    private Long createdAt;

    private Long updatedAt;

}
