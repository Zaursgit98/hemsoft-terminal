package az.hemsoft.terminaljx.business.restaurant.model;

public class User {
    private int id;
    private String username;
    private String pin;
    private String name;
    private String role;
    private boolean isActive;
    
    public User() {
    }
    
    public User(int id, String username, String pin, String name, String role, boolean isActive) {
        this.id = id;
        this.username = username;
        this.pin = pin;
        this.name = name;
        this.role = role;
        this.isActive = isActive;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPin() {
        return pin;
    }
    
    public void setPin(String pin) {
        this.pin = pin;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
}

