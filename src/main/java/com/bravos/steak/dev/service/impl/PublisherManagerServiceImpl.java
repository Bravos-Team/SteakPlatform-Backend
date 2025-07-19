package com.bravos.steak.dev.service.impl;

import com.bravos.steak.common.security.JwtTokenClaims;
import com.bravos.steak.common.service.auth.SessionService;
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

@Slf4j
@Service
public class PublisherManagerServiceImpl implements PublisherManagerService {

    private final PublisherAccountRepository publisherAccountRepository;
    private final SessionService sessionService;
    private final SnowflakeGenerator snowflakeGenerator;
    private final PasswordEncoder passwordEncoder;
    private final PublisherRoleRepository publisherRoleRepository;

    public PublisherManagerServiceImpl(PublisherAccountRepository publisherAccountRepository,
                                       SessionService sessionService, SnowflakeGenerator snowflakeGenerator,
                                       PasswordEncoder passwordEncoder,
                                       PublisherRoleRepository publisherRoleRepository) {
        this.publisherAccountRepository = publisherAccountRepository;
        this.sessionService = sessionService;
        this.snowflakeGenerator = snowflakeGenerator;
        this.passwordEncoder = passwordEncoder;
        this.publisherRoleRepository = publisherRoleRepository;
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
    public void changeRoleStatus(Long roleId, Boolean isActive) {

    }

    @Override
    public void removeAccountFromRole(Long roleId, Long accountId) {

    }

    @Override
    public void assignAccountToRole(Long roleId, Long accountId) {

    }

}
