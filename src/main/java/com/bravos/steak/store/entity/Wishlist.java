package com.bravos.steak.store.entity;

import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.useraccount.entity.UserAccount;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
@Entity
public class Wishlist {

    @Id
    private Long id;

    @ManyToOne(targetEntity = UserAccount.class)
    private UserAccount userAccount;

    @ManyToOne(targetEntity = Game.class)
    private Game game;

    @Builder.Default
    private Long addedAt = DateTimeHelper.currentTimeMillis();

}
