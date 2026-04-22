package handlers;

import aog.Renderer;
import aog.UserLeaderboard;
import db.UserManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderboardHandler implements Handler {

    private final UserManager user_manager;

    public LeaderboardHandler(UserManager user_manager) {
        this.user_manager = user_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        List<UserLeaderboard> leaderboard = user_manager.getLeaderboard();
        Map<String, Object> model = new HashMap<>();
        model.put("leaderboard", leaderboard);
        Renderer.render(context, "/templates/user/leaderboard.ftl", model);
    }
}
