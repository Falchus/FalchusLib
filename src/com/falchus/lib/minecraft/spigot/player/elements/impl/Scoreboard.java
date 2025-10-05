package com.falchus.lib.minecraft.spigot.player.elements.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import com.falchus.lib.minecraft.spigot.player.elements.PlayerElement;

import lombok.NonNull;

/**
 * Represents a per-player sidebar scoreboard.
 */
public class Scoreboard extends PlayerElement {

	private final org.bukkit.scoreboard.Scoreboard scoreboard;
	private final Objective objective;
	private final Map<Integer, String> lastScores = new HashMap<>();
    private final Map<UUID, Integer> animationFrames = new HashMap<>();
	
    /**
     * Constructs a Scoreboard.
     */
	private Scoreboard(@NonNull Player player) {
		super(player);
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("FalchusLib", "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);
	}
	
	/**
	 * Updates the scoreboard immediately.
	 */
	public void send(@NonNull String title, @NonNull String titleColor, String titleSecondColor, @NonNull List<String> lines) {
		update(title, titleColor, titleSecondColor, lines);
	}

	/**
	 * Updates the scoreboard periodically with dynamic content.
	 */
	public void sendUpdating(
			@NonNull Long intervalTicks, 
			@NonNull Supplier<String> titleSupplier, 
			@NonNull Supplier<String> titleColorSupplier, 
			Supplier<String> titleSecondColorSupplier, 
			@NonNull Supplier<List<String>> linesSupplier
	) {
		super.sendUpdating(intervalTicks, () -> {
			String title = titleSupplier.get();
			String titleColor = titleColorSupplier.get();
			String titleSecondColor = titleSecondColorSupplier.get();
			List<String> lines = linesSupplier.get();
			update(title, titleColor, titleSecondColor, lines);
		});
	}
	
	public void remove() {
		super.remove();
		
		player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
	}
	
	private void update(@NonNull String title, @NonNull String titleColor, String titleSecondColor, @NonNull List<String> lines) {
		title = getTitle(title, titleColor, titleSecondColor);

	    objective.setDisplayName(title);

	    int scoreValue = lines.size();
	    Map<Integer, String> newLastScores = new HashMap<>();

	    for (int i = 0; i < lines.size(); i++) {
	        String newText = lines.get(i);
	        if (newText.isEmpty()) {
	            StringBuilder sb = new StringBuilder("§r");
	            for (int j = 0; j < scoreValue; j++) {
	                sb.append("§r");
	            }
	            newText = sb.toString();
	        } else if (newText.length() > 40) {
	            newText = newText.substring(0, 40);
	        }

	        String oldText = lastScores.get(scoreValue);
	        if (oldText != null && !oldText.equals(newText)) {
	            scoreboard.resetScores(oldText);
	        }

	        Score score = objective.getScore(newText);
	        score.setScore(scoreValue);
	        newLastScores.put(scoreValue, newText);
	        scoreValue--;
	    }

	    lastScores.clear();
	    lastScores.putAll(newLastScores);
	}
	
    private String getTitle(@NonNull String title, @NonNull String titleColor, String titleSecondColor) {
    	title = ChatColor.stripColor(title);
    	
        if (titleSecondColor == null) return titleColor + title;

        int frame = animationFrames.getOrDefault(player.getUniqueId(), 0);
        int length = title.length();
        int cycleLength = length * 2;
        int pos = frame % length;
        
        StringBuilder sb = new StringBuilder();

        if (frame < length) {
            sb.append(titleSecondColor);
            sb.append(title.substring(0, pos + 1));
            sb.append(titleColor);
            sb.append(title.substring(pos + 1));
        } else {
            sb.append(titleColor);
            sb.append(title.substring(0, pos + 1));
            sb.append(titleSecondColor);
            sb.append(title.substring(pos + 1));
        }

        frame = (frame + 1) % cycleLength;
        animationFrames.put(player.getUniqueId(), frame);

        return sb.toString();
    }
}
