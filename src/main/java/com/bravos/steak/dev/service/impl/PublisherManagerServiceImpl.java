package com.bravos.steak.dev.service.impl;

import com.bravos.steak.common.security.JwtTokenClaims;
import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.dev.entity.Publisher;
import com.bravos.steak.dev.entity.PublisherAccount;
import com.bravos.steak.dev.entity.PublisherRole;
import com.bravos.steak.dev.model.request.CreateCustomRoleRequest;
import com.bravos.steak.dev.model.request.CreatePublisherAccountRequest;
import com.bravos.steak.dev.model.response.PublisherAccountDetail;
import com.bravos.steak.dev.model.response.PublisherAccountListItem;
import com.bravos.steak.dev.model.response.RoleDetail;
import com.bravos.steak.dev.model.response.RoleListItem;
import com.bravos.steak.dev.repo.PublisherAccountRepository;
import com.bravos.steak.dev.repo.PublisherRoleRepository;
import com.bravos.steak.dev.service.PublisherManagerService;
import com.bravos.steak.exceptions.BadRequestException;
import com.bravos.steak.useraccount.model.enums.AccountStatus;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PublisherManagerServiceImpl implements PublisherManagerService {

    private final PublisherAccountRepository publisherAccountRepository;
    private final SessionService sessionService;
    private final SnowflakeGenerator snowflakeGenerator;
    private final PasswordEncoder passwordEncoder;
    private final PublisherRoleRepository publisherRoleRepository;
    private final RedisService redisService;
    private final PublisherRole masterRole;

    public PublisherManagerServiceImpl(PublisherAccountRepository publisherAccountRepository,
                                       SessionService sessionService, SnowflakeGenerator snowflakeGenerator,
                                       PasswordEncoder passwordEncoder,
                                       PublisherRoleRepository publisherRoleRepository,
                                       RedisService redisService) {
        this.publisherAccountRepository = publisherAccountRepository;
        this.sessionService = sessionService;
        this.snowflakeGenerator = snowflakeGenerator;
        this.passwordEncoder = passwordEncoder;
        this.publisherRoleRepository = publisherRoleRepository;
        this.redisService = redisService;
        this.masterRole = publisherRoleRepository.getMasterRole().orElseThrow(() ->
                new RuntimeException("Master role not found. Please create a master role first."));
    }

    @Override
    public Page<PublisherAccountListItem> getPublisherAccounts(int page, int size, String status) {
        if ("all".equalsIgnoreCase(status)) {
            return publisherAccountRepository.findAllz(PageRequest.of(page - 1, size));
        } else {
            try {
                return publisherAccountRepository.findAllByStatus(AccountStatus.valueOf(status),
                        PageRequest.of(page - 1, size));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid account status: " + status);
            }
        }
    }

    @Override
    public List<RoleListItem> getCustomRoleList() {
        JwtTokenClaims claims = (JwtTokenClaims) sessionService.getAuthentication().getDetails();
        long publisherId = (long) claims.getOtherClaims().get("publisherId");
        List<PublisherRole> myCustomRoles = publisherRoleRepository.findAllByPublisherId(publisherId);
        if (myCustomRoles != null && !myCustomRoles.isEmpty()) {
            return myCustomRoles.stream()
                    .map(role -> RoleListItem.builder()
                            .id(role.getId())
                            .name(role.getName())
                            .description(role.getDescription())
                            .active(role.getActive())
                            .build())
                    .toList();
        }
        return List.of();
    }

    @Override
    public RoleDetail getRoleDetail(Long roleId) {
        JwtTokenClaims claims = (JwtTokenClaims) sessionService.getAuthentication().getDetails();
        long publisherId = (long) claims.getOtherClaims().get("publisherId");
        PublisherRole role = publisherRoleRepository.findAvailableRoleByIdAndPublisherId(roleId, publisherId);
        if(role == null) {
            throw new BadRequestException("Role not found or not available for this publisher.");
        }
        List<PublisherAccountListItem> assignedAccounts =
                publisherRoleRepository.findAssignedAccountsByRoleIdAndPublisherId(roleId, publisherId);
        return RoleDetail.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .isActive(role.getActive())
                .assignedAccounts(assignedAccounts)
                .build();
    }

    @Override
    public PublisherAccountDetail myAccountDetail() {
        Long userId = (Long) sessionService.getAuthentication().getPrincipal();
        if (userId == null) {
            throw new BadRequestException("User ID is not available in the session.");
        }
        PublisherAccount myAccount = publisherAccountRepository.findById(userId).orElseThrow(() ->
                new BadRequestException("Publisher account not found for user ID: " + userId));

        return getPublisherAccountDetailById(myAccount);
    }

    @Override
    public PublisherAccountDetail getAccountDetail(Long accountId) {
        PublisherAccount account = publisherAccountRepository.findById(accountId)
                .orElseThrow(() -> new BadRequestException("Publisher account not found for ID: " + accountId));
        return getPublisherAccountDetailById(account);
    }

    private PublisherAccountDetail getPublisherAccountDetailById(PublisherAccount account) {
        List<PublisherAccountDetail.RoleAndId> roles = account.getRoles().stream()
                .map(role ->
                        new PublisherAccountDetail.RoleAndId(role.getName(), role.getId()))
                .toList();
        return PublisherAccountDetail.builder()
                .id(account.getId())
                .username(account.getUsername())
                .email(account.getEmail())
                .createdAt(account.getCreatedAt())
                .status(account.getStatus())
                .roles(roles)
                .build();
    }

    @Transactional
    @Override
    public PublisherAccountDetail createAccount(CreatePublisherAccountRequest request) {
        long newId = snowflakeGenerator.generateId();
        JwtTokenClaims claims = (JwtTokenClaims) sessionService.getAuthentication().getDetails();
        long publisherId = (long) claims.getOtherClaims().get("publisherId");

        if (publisherAccountRepository.existsByEmailOrUsername(request.getEmail(), request.getUsername())) {
            throw new BadRequestException("Email or username already exists.");
        }

        PublisherAccount account = PublisherAccount.builder()
                .id(newId)
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(AccountStatus.ACTIVE)
                .publisher(Publisher.builder().id(publisherId).build())
                .build();

        if (request.getAssignedRoles() == null || request.getAssignedRoles().isEmpty()) {
            try {
                account = publisherAccountRepository.save(account);
            } catch (Exception e) {
                log.error("Failed to create publisher account: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to create publisher account: " + e.getMessage(), e);
            }
            return getPublisherAccountDetailById(account);
        }

        Long countAvailableRoles = publisherRoleRepository
                .countRolesAvailableByPublisherId(publisherId, new HashSet<>(request.getAssignedRoles()));
        if (countAvailableRoles == null || countAvailableRoles < request.getAssignedRoles().size()) {
            throw new BadRequestException("Some roles are not available for this publisher.");
        }

        List<PublisherRole> roles = request.getAssignedRoles().stream()
                .map(roleId -> PublisherRole.builder().id(roleId).build())
                .toList();

        account.setRoles(new HashSet<>(roles));

        try {
            account = publisherAccountRepository.save(account);
        } catch (Exception e) {
            log.error("Failed to create publisher account: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create publisher account: " + e.getMessage(), e);
        }

        return getPublisherAccountDetailById(account);
    }

    @Override
    public RoleDetail createNewCustomRole(CreateCustomRoleRequest request) {
        return null;
    }

    @Override
    @Transactional
    public void changeRoleStatus(Long roleId, Boolean isActive) {
        PublisherRole role = publisherRoleRepository.findById(roleId)
                .orElseThrow(() -> new BadRequestException("Role not found for ID: " + roleId));
        JwtTokenClaims claims = (JwtTokenClaims) sessionService.getAuthentication().getDetails();
        long publisherId = (long) claims.getOtherClaims().get("publisherId");
        if(role.getPublisher().getId() == null) {
            throw new BadRequestException("You cannot change status default roles.");
        }
        if(!role.getPublisher().getId().equals(publisherId)) {
            throw new BadRequestException("You cannot change status of roles that are not created by you.");
        }
        if (isActive == null || isActive.equals(role.getActive())) {
            try {
                invalidatePublisherAccountToken(role.getAssignedAccounts().stream()
                        .map(PublisherAccount::getId)
                        .collect(Collectors.toList()));
            } catch (Exception e) {
                log.error("Failed to invalidate account tokens: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to invalidate account tokens: " + e.getMessage(), e);
            }
            role.setActive(isActive);
            try {
                publisherRoleRepository.saveAndFlush(role);
            } catch (Exception e) {
                log.error("Failed to change role status: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to change role status: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void removeAccountFromRole(Long roleId, Long accountId) {
        if(roleId.equals(masterRole.getId())) {
            throw new BadRequestException("You cannot remove account from master role.");
        }
        JwtTokenClaims claims = (JwtTokenClaims) sessionService.getAuthentication().getDetails();
        long publisherId = (long) claims.getOtherClaims().get("publisherId");
        long userId = claims.getId();
        PublisherAccount account = publisherAccountRepository.findById(accountId)
                .orElseThrow(() -> new BadRequestException("Publisher account not found for ID: " + accountId));

        if (!account.getPublisher().getId().equals(publisherId)) {
            throw new BadRequestException("You cannot remove roles from accounts that are not created by you.");
        }
        if (account.getId().equals(userId)) {
            throw new BadRequestException("You cannot remove roles from your own account. " +
                    "Please contact support for assistance.");
        }

        PublisherRole removeRole = account.getRoles().stream()
                .filter(role -> role.getId().equals(roleId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Role not found for ID: " + roleId));

        account.getRoles().remove(removeRole);

        try {
            invalidatePublisherAccountToken(accountId);
        } catch (Exception e) {
            log.error("Failed to invalidate account token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to invalidate account token: " + e.getMessage(), e);
        }
        try {
            publisherAccountRepository.saveAndFlush(account);
        } catch (Exception e) {
            log.error("Failed to remove account from role: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to remove account from role: " + e.getMessage(), e);
        }
    }

    @Override
    public void assignAccountToRole(Long roleId, Long accountId) {
        if(roleId.equals(masterRole.getId())) {
            JwtTokenClaims claims = (JwtTokenClaims) sessionService.getAuthentication().getDetails();
            if(!claims.getAuthorities().contains("PUBLISHER_MASTER")) {
                throw new BadRequestException("You do not have permission to assign accounts to the master role.");
            }
        }
        JwtTokenClaims claims = (JwtTokenClaims) sessionService.getAuthentication().getDetails();
        long publisherId = (long) claims.getOtherClaims().get("publisherId");
        PublisherAccount account = publisherAccountRepository.findById(accountId)
                .orElseThrow(() -> new BadRequestException("Publisher account not found for ID: " + accountId));
        if (!account.getPublisher().getId().equals(publisherId)) {
            throw new BadRequestException("You cannot assign roles to accounts that are not created by you.");
        }
        PublisherRole role = publisherRoleRepository
                .findAvailableRoleByIdAndPublisherId(roleId, account.getPublisher().getId());
        if (role == null) {
            throw new BadRequestException("Role not found or not available for this publisher.");
        }

        account.getRoles().add(role);

        try {
            invalidatePublisherAccountToken(accountId);
        } catch (Exception e) {
            log.error("Failed to invalidate account token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to invalidate account token: " + e.getMessage(), e);
        }
        try {
            publisherAccountRepository.saveAndFlush(account);
        } catch (Exception e) {
            log.error("Failed to assign account to role: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to assign account to role: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteAccount(Long accountId) {
        PublisherAccount account = publisherAccountRepository.findById(accountId)
                .orElseThrow(() -> new BadRequestException("Publisher account not found for ID: " + accountId));
        if (account.getRoles().stream().anyMatch(role -> role.getId().equals(masterRole.getId()))) {
            throw new BadRequestException("You cannot delete an account that has the master role assigned.");
        }

        JwtTokenClaims claims = (JwtTokenClaims) sessionService.getAuthentication().getDetails();
        long publisherId = (long) claims.getOtherClaims().get("publisherId");
        long userId = claims.getId();
        if (!account.getPublisher().getId().equals(publisherId)) {
            throw new BadRequestException("You cannot delete an account that is not created by you.");
        }
        if (account.getStatus() == AccountStatus.DELETED) {
            throw new BadRequestException("Account is already deleted.");
        }
        if (account.getId().equals(userId)) {
            throw new BadRequestException("You cannot delete your own account. Please contact support for assistance.");
        }

        try {
            invalidatePublisherAccountToken(accountId);
        } catch (Exception e) {
            log.error("Failed to invalidate account token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to invalidate account token: " + e.getMessage(), e);
        }

        account.setStatus(AccountStatus.DELETED);
        account.getRoles().clear();

        try {
            publisherAccountRepository.saveAndFlush(account);
        } catch (Exception e) {
            log.error("Failed to delete account: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete account: " + e.getMessage(), e);
        }
    }

    private void invalidatePublisherAccountToken(List<Long> accountIds) {
        Map<String,Long> invalidatedKeys = accountIds.stream()
                .collect(Collectors.toMap(
                        id -> "lock_by_change_role:" + id,
                        id -> DateTimeHelper.currentTimeMillis()
                ));
        invalidatedKeys.forEach((key, value) ->
                redisService.save(key, value, 30, TimeUnit.MINUTES));
    }

    private void invalidatePublisherAccountToken(Long accountId) {
        String key = "lock_by_change_role:" + accountId;
        Long lockTime = DateTimeHelper.currentTimeMillis();
        redisService.save(key, lockTime, 30, TimeUnit.MINUTES);
    }

}
