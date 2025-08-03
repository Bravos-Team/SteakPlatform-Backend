package com.bravos.steak.store.repo.injection;

import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class LibraryInfo {

   Long gameId;

   Long ownedDate;

   Long lastPlayedAt;

}
