package fr.univlyon1.m1if.m1if13.users.services.http;

public enum HttpMessage {
    USER_NOT_FOUND("User not found"),
    USER_FOUND("User found"),
    USER_CREATED_SUCCESSFULLY("User created successfully"),
    USER_ALREADY_EXISTS("User already exists"),
    PASSWORD_UPDATED_SUCCESSFULLY("Password updated successfully"),
    USER_DELETED_SUCCESSFULLY("User deleted successfully"),
    USERS_FOUND("Users found"),
    NO_USERS_FOUND("No users found"),
    WRONG_PASSWORD("Wrong password"),
    LOGIN_SUCCESS("Login successful"),
    USER_DISCONNECTED("User disconnected"),
    JWT_MISSING("JWT token missing"),
    ORIGIN_MISSING("Origin header missing"),
    AUTHENTICATED("Authentication successful");

    private final String message;

    HttpMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
