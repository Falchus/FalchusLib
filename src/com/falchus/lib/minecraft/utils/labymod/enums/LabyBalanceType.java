package com.falchus.lib.minecraft.utils.labymod.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LabyBalanceType {
	CASH("cash"),
	BANK("bank");
	
	private final String key;
}
