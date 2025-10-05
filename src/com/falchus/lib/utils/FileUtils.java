package com.falchus.lib.utils;

import java.io.File;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileUtils {

	public static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					deleteFolder(file);
				} else {
					file.delete();
				}
			}
		}
		folder.delete();
	}
}
