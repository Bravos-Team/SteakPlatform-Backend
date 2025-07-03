package com.bravos.steak.administration.repo;

import com.bravos.steak.administration.entity.review.ReviewReply;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewReplyRepository extends MongoRepository<ReviewReply, Long> {

    List<ReviewReply> findByGameSubmissionId(Long gameSubmissionId);
}
