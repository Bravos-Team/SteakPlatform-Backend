package com.bravos.steak.administration.service.impl;

import com.bravos.steak.administration.entity.review.From;
import com.bravos.steak.administration.entity.review.ReviewReply;
import com.bravos.steak.administration.model.request.ReviewerReviewReplyRequest;
import com.bravos.steak.administration.repo.ReviewReplyRepository;
import com.bravos.steak.administration.service.GameReviewService;
import com.bravos.steak.common.security.JwtAuthentication;
import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.dev.entity.Publisher;
import com.bravos.steak.dev.entity.gamesubmission.GameSubmission;
import com.bravos.steak.dev.entity.gamesubmission.GameSubmissionStatus;
import com.bravos.steak.dev.model.GameSubmissionListItem;
import com.bravos.steak.dev.model.enums.PublisherStatus;
import com.bravos.steak.dev.repo.GameSubmissionRepository;
import com.bravos.steak.dev.repo.PublisherRepository;
import com.bravos.steak.dev.service.GameSubmissionService;
import com.bravos.steak.exceptions.BadRequestException;
import com.bravos.steak.exceptions.ResourceNotFoundException;
import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.entity.GameVersion;
import com.bravos.steak.store.entity.details.GameDetails;
import com.bravos.steak.store.model.enums.GameStatus;
import com.bravos.steak.store.model.enums.VersionStatus;
import com.bravos.steak.store.repo.GameDetailsRepository;
import com.bravos.steak.store.repo.GameRepository;
import com.bravos.steak.store.repo.GameVersionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class GameReviewServiceImpl implements GameReviewService {

    private final GameSubmissionRepository gameSubmissionRepository;
    private final ReviewReplyRepository reviewReplyRepository;
    private final SessionService sessionService;
    private final PublisherRepository publisherRepository;
    private final GameRepository gameRepository;
    private final SnowflakeGenerator snowflakeGenerator;
    private final GameVersionRepository gameVersionRepository;
    private final GameDetailsRepository gameDetailsRepository;
    private final GameSubmissionService gameSubmissionService;

    public GameReviewServiceImpl(GameSubmissionRepository gameSubmissionRepository, ReviewReplyRepository reviewReplyRepository,
                                 SessionService sessionService, PublisherRepository publisherRepository, GameRepository gameRepository,
                                 SnowflakeGenerator snowflakeGenerator, GameVersionRepository gameVersionRepository,
                                 GameDetailsRepository gameDetailsRepository, GameSubmissionService gameSubmissionService) {
        this.gameSubmissionRepository = gameSubmissionRepository;
        this.reviewReplyRepository = reviewReplyRepository;
        this.sessionService = sessionService;
        this.publisherRepository = publisherRepository;
        this.gameRepository = gameRepository;
        this.snowflakeGenerator = snowflakeGenerator;
        this.gameVersionRepository = gameVersionRepository;
        this.gameDetailsRepository = gameDetailsRepository;
        this.gameSubmissionService = gameSubmissionService;
    }

    @Override
    public Page<GameSubmissionListItem> getNeedReviewGames(GameSubmissionStatus status, String keyword,
                                                           Long publisherId, int page, int size, Sort sort) {
        return gameSubmissionRepository.getGameSubmissionListDisplay(
                publisherId, status, keyword,
                page, size, sort
        );
    }

    @Override
    public GameSubmission getGameSubmissionById(Long submissionId) {
        return gameSubmissionRepository.findById(submissionId).orElse(null);
    }

    @Override
    @Transactional
    public void approveGameSubmission(Long submissionId) {
        GameSubmission submission = gameSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Game submission not found with ID: " + submissionId));

        if(submission.getStatus() != GameSubmissionStatus.PENDING_REVIEW) {
            throw new BadRequestException("Game submission is not in pending review status");
        }

        Publisher publisher = publisherRepository.findById(submission.getPublisherId()).orElse(null);

        if (publisher == null) {
            throw new ResourceNotFoundException("Publisher not found with ID: " + submission.getPublisherId());
        }

        if(publisher.getStatus() == PublisherStatus.BANNED) {
            throw new BadRequestException("Publisher is banned and cannot submit games");
        }

        LocalDateTime now = LocalDateTime.now();

        Game game = Game.builder()
                .id(submission.getId())
                .name(submission.getName())
                .publisher(publisher)
                .status(GameStatus.OPENING)
                .price(BigDecimal.valueOf(submission.getPrice()))
                .createdAt(now)
                .updatedAt(now)
                .releaseDate(submission.getEstimatedReleaseDate()
                        .toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build();

        try {
            game = gameRepository.save(game);
        } catch (Exception e) {
            log.error("Failed to save game to database: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save game to database");
        }

        GameVersion gameVersion = GameVersion.builder()
                .id(snowflakeGenerator.generateId())
                .game(game)
                .changeLog("First version of the game")
                .name(submission.getBuildInfo().getVersionName())
                .execPath(submission.getBuildInfo().getExecPath())
                .downloadUrl(submission.getBuildInfo().getDownloadUrl())
                .createdAt(now)
                .releaseDate(game.getReleaseDate())
                .status(VersionStatus.STABLE)
                .build();

        try {
            gameVersion = gameVersionRepository.save(gameVersion);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save game version to database" );
        }

        GameDetails gameDetails = GameDetails.builder()
                .id(game.getId())
                .developersTeam(submission.getDeveloperTeam())
                .region(submission.getRegion())
                .thumbnail(submission.getThumbnail())
                .media(submission.getMedia())
                .shortDescription(submission.getShortDescription())
                .longDescription(submission.getLongDescription())
                .platforms(submission.getPlatform())
                .systemRequirements(submission.getSystemRequirements())
                .internetConnection(submission.getInternetConnection())
                .languageSupported(submission.getLanguageSupported())
                .updatedAt(new Date())
                .build();

        try {
            gameDetailsRepository.save(gameDetails);
        } catch (Exception e) {
            gameVersionRepository.deleteById(gameVersion.getId());
            gameRepository.deleteById(game.getId());
            log.error("Failed to save game details to database: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save game details to database");
        }

        gameSubmissionService.updateGameSubmissionStatus(submissionId, GameSubmissionStatus.ACCEPTED);

    }

    @Override
    public void requireUpdateGameSubmission(Long submissionId) {
        gameSubmissionService.updateGameSubmissionStatus(submissionId, GameSubmissionStatus.NEED_UPDATE);
    }

    @Override
    public void rejectGameSubmission(Long submissionId) {
        gameSubmissionService.updateGameSubmissionStatus(submissionId, GameSubmissionStatus.REJECTED);
    }

    @Override
    public List<ReviewReply> getReviewRepliesBySubmissionId(Long submissionId) {
        return reviewReplyRepository.findByGameSubmissionId(submissionId);
    }

    @Override
    @Transactional
    public ReviewReply createReviewReply(ReviewerReviewReplyRequest request) {

        JwtAuthentication auth = sessionService.getAuthentication();
        Long reviewerId = (Long) auth.getPrincipal();

        if(!gameSubmissionRepository.existsById(request.getSubmissionId())) {
            throw new ResourceNotFoundException("Game submission not found with ID: " + request.getSubmissionId());
        }

        ReviewReply reply = ReviewReply.builder()
                .id(snowflakeGenerator.generateId())
                .gameSubmissionId(request.getSubmissionId())
                .from(new From("reviewer",reviewerId))
                .content(request.getContent())
                .attachments(request.getAttachments())
                .repliedAt(new Date())
                .build();

        try {
            reply = reviewReplyRepository.save(reply);
        } catch (Exception e) {
            log.error("Failed to create review reply: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create review reply: " + e.getMessage());
        }

        try {
            if(request.getStatus() == GameSubmissionStatus.ACCEPTED) {
                approveGameSubmission(request.getSubmissionId());
            } else if (request.getStatus() == GameSubmissionStatus.REJECTED) {
                rejectGameSubmission(request.getSubmissionId());
            } else if (request.getStatus() == GameSubmissionStatus.NEED_UPDATE) {
                requireUpdateGameSubmission(request.getSubmissionId());
            }
        } catch (Exception e) {
            final ReviewReply finalReply = reply;
            Thread.startVirtualThread(() -> reviewReplyRepository.deleteById(finalReply.getId()));
            log.error("Failed to update game submission status: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update game submission status: " + e.getMessage());
        }

        return reply;

    }



}
