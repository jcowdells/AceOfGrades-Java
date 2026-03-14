package api_handlers;

import aog.Renderer;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class PacksSelectApiHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        System.out.println(context.contentType());
        Renderer.renderHXError(context, "Not Implemented!");
    }
}
