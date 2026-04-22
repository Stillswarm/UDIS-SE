package com.udis.model;

public class User {
    private int id;
    private String username;
    private String role;
    private String fullName;

    public User(int id, String username, String role, String fullName) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.fullName = fullName;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getFullName() { return fullName; }

    public boolean isSecretary() { return "SECRETARY".equals(role); }
    public boolean isHod()       { return "HOD".equals(role); }
    public boolean isFaculty()   { return "FACULTY".equals(role); }
    public boolean isAdmin()     { return "ADMIN".equals(role); }
}
