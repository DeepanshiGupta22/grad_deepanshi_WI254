package MaintenanceApp.src;

public interface IMaintenanceService {
    double calculateCost(String category, int areaSqFt);
    String determineStatus(double total, double paid);
}