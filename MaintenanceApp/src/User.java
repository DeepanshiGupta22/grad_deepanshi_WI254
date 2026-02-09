package MaintenanceApp.src;



public class User {
    private int id;
    private String username;
    private String role;
    private String phone;

    public User(int id, String username, String role, String phone) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.phone = phone;
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^\\d{10}$");
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getPhone() { return phone; }
}