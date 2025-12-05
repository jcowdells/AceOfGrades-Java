package handlers;

import aog.Renderer;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class QuizHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        Renderer.render(context, "/templates/quiz.ftl", new HashMap<>());
    }
}
