package com.bravos.steak.store.model.request;

import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class FilterQuery {

    Long cursor;

    String keyword;

    Long[] genreIds;

    Long[] tagIds;

    Double minPrice;

    Double maxPrice;

    String sortBy;

    @Builder.Default
    Boolean cursorDirection = true; // true for ascending, false for descending

    @Min(1)
    @Builder.Default
    Integer page = 1;

    @Min(1)
    @Builder.Default
    Integer pageSize = 10;

    @Override
    public int hashCode() {
        if(genreIds != null && genreIds.length > 0) {
            Arrays.sort(genreIds);
        }
        if(tagIds != null && tagIds.length > 0) {
            Arrays.sort(tagIds);
        }
        return Arrays.hashCode(new Object[]{
                cursor, keyword, Arrays.hashCode(genreIds), Arrays.hashCode(tagIds),
                minPrice, maxPrice, sortBy, cursorDirection, page, pageSize
        });
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FilterQuery that = (FilterQuery) obj;
        if(this.getGenreIds() != null) {
            if (that.getGenreIds() == null || this.getGenreIds().length != that.getGenreIds().length) {
                return false;
            }
            Arrays.sort(this.genreIds);
            Arrays.sort(that.genreIds);
        }
        if(this.getTagIds() != null) {
            if (that.getTagIds() == null || this.getTagIds().length != that.getTagIds().length) {
                return false;
            }
            Arrays.sort(this.tagIds);
            Arrays.sort(that.tagIds);
        }
        return cursor.equals(that.cursor) &&
                keyword.equals(that.keyword) &&
                Arrays.equals(genreIds, that.genreIds) &&
                Arrays.equals(tagIds, that.tagIds) &&
                minPrice.equals(that.minPrice) &&
                maxPrice.equals(that.maxPrice) &&
                sortBy.equals(that.sortBy) &&
                cursorDirection.equals(that.cursorDirection) &&
                page.equals(that.page) &&
                pageSize.equals(that.pageSize);
    }

}
