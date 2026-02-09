package MaintenanceApp.src;


public class Site {
    private int siteId;
    private String dimension;
    private int areaSqFt;
    private String category;
    private int ownerId;

    public Site(int siteId, String dimension, int areaSqFt, String category, int ownerId) {
        this.siteId = siteId;
        this.dimension = dimension;
        this.areaSqFt = areaSqFt;
        this.category = category;
        this.ownerId = ownerId;
    }

    public int getSiteId() { return siteId; }
    public String getDimension() { return dimension; }
    public int getAreaSqFt() { return areaSqFt; }
    public String getCategory() { return category; }
    public int getOwnerId() { return ownerId; }
    
    
    @Override
    public String toString() {
        return String.format("Site ID: %d | %s (%d sqft) | Type: %s", siteId, dimension, areaSqFt, category);
    }
}