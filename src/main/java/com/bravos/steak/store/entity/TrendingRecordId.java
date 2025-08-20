package com.bravos.steak.store.entity;

import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Objects;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
@Embeddable
public class TrendingRecordId implements Serializable {

    Integer year;

    Integer month;

    Integer rank;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TrendingRecordId that = (TrendingRecordId) o;
        return Objects.equals(year, that.year) && Objects.equals(month, that.month) && Objects.equals(rank, that.rank);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month, rank);
    }

}
