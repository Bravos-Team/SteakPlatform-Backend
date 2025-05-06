package com.bravos.steak.common.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public abstract class CustomMongoRepository {

    private final MongoTemplate mongoTemplate;

    public CustomMongoRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public final <T> T getProjectionById(Object id, Class<T> clazz) {
        Query query = Query.query(Criteria.where("_id").is(id));
        return getProjectionByQuery(query,clazz);
    }

    public final <T> T getProjectionByQuery(Query query, Class<T> clazz) {
        this.injectQueryMapping(query,clazz);
        return mongoTemplate.findOne(query,clazz, this.collectionName());
    }

    public final <T> Collection<T> getProjectionsByQuery(Query query, Class<T> clazz) {
        this.injectQueryMapping(query,clazz);
        return mongoTemplate.find(query,clazz);
    }

    public final <T> Page<T> getPageProjectionsByQuery(Query query, Class<T> clazz, Pageable pageable) {
        long count = mongoTemplate.count(query,collectionName());
        query.with(pageable);
        this.injectQueryMapping(query,clazz);
        List<T> list = mongoTemplate.find(query,clazz,collectionName());
        return new PageImpl<>(list,pageable,count);
    }

    private <T> void injectQueryMapping(Query query, Class<T> clazz) {
        query.fields().include(Arrays.stream(clazz.getDeclaredFields()).map(Field::getName).toList());
    }

    protected abstract String collectionName();

}
