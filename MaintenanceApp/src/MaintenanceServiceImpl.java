package MaintenanceApp.src;

public class MaintenanceServiceImpl implements IMaintenanceService {
    
    @Override
    public double calculateCost(String category, int areaSqFt) {
        if ("CLOSED".equalsIgnoreCase(category)) {
            return areaSqFt * 9.0; // Villa/Apartment Rate
        }
        return areaSqFt * 6.0; // Open Site Rate
    }

    @Override
    public String determineStatus(double total, double paid) {
        if (paid >= total) return "PAID";
        if (paid > 0) return "PENDING";
        return "UNPAID";
    }
}
