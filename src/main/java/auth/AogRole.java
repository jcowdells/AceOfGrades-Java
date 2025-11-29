package auth;

import io.javalin.security.RouteRole;

public enum AogRoles implements RouteRole {
    ANYONE,
    USER,
    ADMIN
}
