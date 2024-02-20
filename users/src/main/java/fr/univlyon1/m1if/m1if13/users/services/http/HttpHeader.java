package fr.univlyon1.m1if.m1if13.users.services.http;

public enum HttpHeader {
    AUTHENTICATION("Authentication"),
    ORIGIN("Origin");

    private final String header;

    HttpHeader(String header) {
        this.header = header;
    }

    @Override
    public String toString() {
        return header;
    }
}
