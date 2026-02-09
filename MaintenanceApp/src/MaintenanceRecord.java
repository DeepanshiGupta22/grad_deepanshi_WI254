package MaintenanceApp.src;

public class MaintenanceRecord {
    private int id;
    private int siteId;
    private int year;
    private double totalAmount;
    private double paidAmount;
    private String status;

    public MaintenanceRecord(int id, int siteId, int year, double totalAmount, double paidAmount, String status) {
        this.id = id;
        this.siteId = siteId;
        this.year = year;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.status = status;
    }

    public int getSiteId() { return siteId; }
    public int getYear() { return year; }
    public double getTotalAmount() { return totalAmount; }
    public double getPaidAmount() { return paidAmount; }
    public String getStatus() { return status; }
    
    @Override
    public String toString() {
        return String.format("Year: %d | Due: %.2f | Paid: %.2f | Status: %s", year, totalAmount, paidAmount, status);
    }
}