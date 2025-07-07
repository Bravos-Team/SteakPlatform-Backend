package com.bravos.steak.store.model.enums;

public enum VersionStatus {

    STABLE, // Phiên bản ổn định, đã sẵn sàng cho người dùng
    DOWNLOADABLE, // Phiên bản có thể tải về
    DRAFT, // Phiên bản đang trong quá trình phát triển, chưa phát hành
    DELETED, // Phiên bản đã bị xoá, không còn khả dụng
    BANNED, // Phiên bản bị cấm, không thể sử dụng

}
