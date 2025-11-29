package auth;

import io.javalin.security.RouteRole;

public enum AogRole implements RouteRole {
    ANYONE,
    USER,
    ADMIN
}
