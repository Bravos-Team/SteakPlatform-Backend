package com.bravos.steak.store.model.enums;

public enum GameStatus {

    OPENING, // Mở bình thường
    CLOSED, // Game đóng tạm thời do NPH update, vv
    BANNED, // Game bị cấm
    RETIRED, // Game bị ngừng kinh doanh, ko thể mua nữa, người đã mua vẫn chơi được

}
