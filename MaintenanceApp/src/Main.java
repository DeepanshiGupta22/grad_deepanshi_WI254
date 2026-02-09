package MaintenanceApp.src;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LayoutDAO dao = new LayoutDAO();
        User currentUser = null;

        System.out.println("=== Layout Maintenance System ===");
        
        // LOGIN
        while (currentUser == null) {
            System.out.print("Username: ");
            String u = sc.next();
            System.out.print("Password: ");
            String p = sc.next();
            currentUser = dao.login(u, p);
            if (currentUser == null) System.out.println("Invalid credentials.");
        }

        System.out.println("Welcome, " + currentUser.getUsername() + " [" + currentUser.getRole() + "]");

        // ROLE SWITCH
        switch (currentUser.getRole()) {
            case "OWNER":
                boolean ownerLoop = true;
                while (ownerLoop) {
                    System.out.println("\n--- OWNER MENU ---");
                    System.out.println("1. View My Sites");
                    System.out.println("2. Pay Maintenance");
                    System.out.println("3. Request Update");
                    System.out.println("4. Exit");
                    System.out.print("Choice: ");
                    
                    int ch = sc.nextInt();
                    switch (ch) {
                        case 1:
                            List<String> report = dao.getSiteDetailsWithMaintenance(currentUser.getId());
                            for(String line : report) System.out.println(line);
                            break;
                        case 2:
                            System.out.print("Site ID: "); int sid = sc.nextInt();
                            System.out.print("Year: "); int yr = sc.nextInt();
                            System.out.print("Amount: "); double amt = sc.nextDouble();
                            System.out.println(dao.payMaintenance(currentUser.getId(), sid, yr, amt));
                            break;
                        case 3:
                            System.out.print("Site ID: "); int reqSid = sc.nextInt();
                            System.out.print("New Type (OPEN/CLOSED): "); String type = sc.next();
                            boolean sent = dao.requestUpdate(currentUser.getId(), reqSid, type);
                            System.out.println(sent ? "Request Sent." : "Failed (Check Site ID).");
                            break;
                        case 4: ownerLoop = false; break;
                    }
                }
                break;

            case "ADMIN":
                boolean adminLoop = true;
                while (adminLoop) {
                    System.out.println("\n--- ADMIN MENU ---");
                    System.out.println("1. Generate Yearly Bills");
                    System.out.println("2. Process Requests");
                    System.out.println("3. Register New Owner");
                    System.out.println("4. Exit");
                    System.out.print("Choice: ");

                    int ch = sc.nextInt();
                    switch (ch) {
                        case 1:
                            System.out.print("Year: ");
                            int count = dao.generateBills(sc.nextInt());
                            System.out.println("Bills generated for " + count + " sites.");
                            break;
                        case 2:
                            dao.processRequests();
                            break;
                        case 3:
                            System.out.print("Username: "); String newU = sc.next();
                            System.out.print("Password: "); String newP = sc.next();
                            String phone = "";
                            while(true) {
                                System.out.print("Phone (10 digits): ");
                                phone = sc.next();
                                if (User.isValidPhone(phone)) break; // Using User class static method
                                System.out.println("Invalid Phone.");
                            }
                            // Create temporary User object to pass to DAO
                            User newUser = new User(0, newU, "OWNER", phone);
                            if(dao.registerUser(newUser, newP)) System.out.println("Registered!");
                            else System.out.println("Error: Username likely taken.");
                            break;
                        case 4: adminLoop = false; break;
                    }
                }
                break;
        }
        sc.close();
    }
}