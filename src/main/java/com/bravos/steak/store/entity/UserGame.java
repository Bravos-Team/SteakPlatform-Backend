package com.bravos.steak.store.entity;

import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.useraccount.entity.UserAccount;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_game")
public class UserGame {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccount user;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Builder.Default
    private Long ownedAt = DateTimeHelper.currentTimeMillis();

    @Builder.Default
    private Long playSeconds = 0L;

    private Long playRecentDate;

}
