package io.github.skippi.hodmc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class StatBoard {
    private List<String> lines = new ArrayList<>();
    private String title = "";

    private StatBoard() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public Scoreboard toScoreboard() {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("game", "dummy", title);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        for (int i = 0; i < lines.size(); ++i) {
            obj.getScore(lines.get(i)).setScore(lines.size() - 1 - i);
        }
        return board;
    }

    public static class Builder {
        private List<String> lines = new ArrayList<>();
        private String title = "";

        public StatBoard build() {
            StatBoard board = new StatBoard();
            board.lines = lines;
            board.title = title;
            return board;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withLine(String line) {
            lines.add(line);
            return this;
        }

        public Builder withSeparator() {
            lines.add("-------------");
            return this;
        }
    }
}
