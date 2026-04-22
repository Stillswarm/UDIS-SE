package com.udis.model;

import java.time.LocalDateTime;

public class AuditEntry {
    private int id;
    private String username;
    private String action;
    private String entity;
    private LocalDateTime at;

    public AuditEntry() { }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getEntity() { return entity; }
    public void setEntity(String entity) { this.entity = entity; }
    public LocalDateTime getAt() { return at; }
    public void setAt(LocalDateTime at) { this.at = at; }
}
