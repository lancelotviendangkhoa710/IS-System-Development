package com.bakery.model.dto.hethong;

import com.bakery.model.enums.SystemModule;

/**
 * Định nghĩa thông tin một module để hiển thị trên Menu.
 */
public record ModuleDef(SystemModule module, String label, String fxmlPath) {}
