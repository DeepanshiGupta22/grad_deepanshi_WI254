package MaintenanceApp.src;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LayoutDAO {
    private IMaintenanceService service = new MaintenanceServiceImpl();

    // --- AUTHENTICATION ---
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("role"), rs.getString("phone_number"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // --- OWNER: FETCH DATA (Returns List of Objects) ---
    public List<String> getSiteDetailsWithMaintenance(int ownerId) {
        List<String> report = new ArrayList<>();
        String sql = "SELECT s.site_id, s.dimension, s.category, m.year, m.total_amount, m.paid_amount, m.status " +
                     "FROM sites s " +
                     "LEFT JOIN maintenance m ON s.site_id = m.site_id " +
                     "WHERE s.owner_id = ? " +
                     "ORDER BY s.site_id, m.year DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ownerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String line = String.format("Site %d [%s] (%s) | Year: %d | Due: %.2f | Paid: %.2f | Status: %s",
                    rs.getInt("site_id"), rs.getString("dimension"), rs.getString("category"),
                    rs.getInt("year"), rs.getDouble("total_amount"), rs.getDouble("paid_amount"), rs.getString("status"));
                report.add(line);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return report;
    }

    // --- ACTIONS ---
    public boolean requestUpdate(int ownerId, int siteId, String newCategory) {
        try (Connection conn = DBConnection.getConnection()) {
            // Check Ownership
            PreparedStatement check = conn.prepareStatement("SELECT count(*) FROM sites WHERE site_id=? AND owner_id=?");
            check.setInt(1, siteId);
            check.setInt(2, ownerId);
            ResultSet rs = check.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) return false;

            // Insert Request
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO site_update_requests (site_id, owner_id, requested_category) VALUES (?, ?, ?)");
            pstmt.setInt(1, siteId);
            pstmt.setInt(2, ownerId);
            pstmt.setString(3, newCategory);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public String payMaintenance(int ownerId, int siteId, int year, double amount) {
        try (Connection conn = DBConnection.getConnection()) {
            // Check Ownership
            PreparedStatement check = conn.prepareStatement("SELECT count(*) FROM sites WHERE site_id=? AND owner_id=?");
            check.setInt(1, siteId);
            check.setInt(2, ownerId);
            ResultSet checkRs = check.executeQuery();
            checkRs.next();
            if (checkRs.getInt(1) == 0) return "Ownership Error";

            // Get Current Dues
            PreparedStatement getDue = conn.prepareStatement("SELECT total_amount, paid_amount FROM maintenance WHERE site_id=? AND year=?");
            getDue.setInt(1, siteId);
            getDue.setInt(2, year);
            ResultSet rs = getDue.executeQuery();

            if (rs.next()) {
                double total = rs.getDouble("total_amount");
                double currentPaid = rs.getDouble("paid_amount");
                double newPaid = currentPaid + amount;
                String newStatus = service.determineStatus(total, newPaid);

                PreparedStatement update = conn.prepareStatement("UPDATE maintenance SET paid_amount=?, status=? WHERE site_id=? AND year=?");
                update.setDouble(1, newPaid);
                update.setString(2, newStatus);
                update.setInt(3, siteId);
                update.setInt(4, year);
                update.executeUpdate();
                return "Payment Success. New Status: " + newStatus;
            }
            return "No bill found for Year " + year;
        } catch (SQLException e) { e.printStackTrace(); return "Database Error"; }
    }

    // --- ADMIN ---
    public int generateBills(int year) {
        String sql = "SELECT site_id, category, area_sqft FROM sites";
        int count = 0;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            PreparedStatement insert = conn.prepareStatement(
                "INSERT INTO maintenance (site_id, year, total_amount) VALUES (?, ?, ?) ON CONFLICT DO NOTHING"
            );

            while(rs.next()) {
                double cost = service.calculateCost(rs.getString("category"), rs.getInt("area_sqft"));
                insert.setInt(1, rs.getInt("site_id"));
                insert.setInt(2, year);
                insert.setDouble(3, cost);
                insert.addBatch();
                count++;
            }
            insert.executeBatch();
        } catch (SQLException e) { e.printStackTrace(); }
        return count;
    }

    public void processRequests() {
        String sql = "SELECT r.req_id, r.site_id, r.requested_category, u.username FROM site_update_requests r " +
                     "JOIN users u ON r.owner_id = u.user_id WHERE r.status = 'PENDING'";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql);
             Scanner sc = new Scanner(System.in)) {
             
            System.out.println("\n--- PENDING APPROVALS ---");
            boolean found = false;
            while(rs.next()) {
                found = true;
                System.out.printf("User %s wants Site %d -> %s. Approve? (y/n): ", 
                    rs.getString("username"), rs.getInt("site_id"), rs.getString("requested_category"));
                
                if (sc.next().equalsIgnoreCase("y")) {
                    conn.setAutoCommit(false); 
                    try {
                        Statement tStmt = conn.createStatement();
                        tStmt.executeUpdate("UPDATE sites SET category='" + rs.getString("requested_category") + "' WHERE site_id=" + rs.getInt("site_id"));
                        tStmt.executeUpdate("UPDATE site_update_requests SET status='APPROVED' WHERE req_id=" + rs.getInt("req_id"));
                        conn.commit();
                        System.out.println("Approved.");
                    } catch (Exception ex) {
                        conn.rollback();
                        ex.printStackTrace();
                    } finally {
                        conn.setAutoCommit(true);
                    }
                }
            }
            if(!found) System.out.println("No pending requests.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean registerUser(User user, String password) {
        String sql = "INSERT INTO users (username, password, phone_number, role) VALUES (?, ?, ?, ?)";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, password); // In real app, hash this!
            pstmt.setString(3, user.getPhone());
            pstmt.setString(4, user.getRole());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}