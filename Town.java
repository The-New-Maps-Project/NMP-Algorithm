public class Town {
    private int district;
    private String name;
    private int population;
    private Location location;
    private int id;
    private double portion; //a decimal of how much percent of this Town's population is in this town object. (in the case of dividing towns. Otherwise, set to 1 (100%))

    public Town(String name, int population, double lat, double lng, double portion) {
        this.name = name;
        this.population = population;
        this.location = new Location(lat, lng);
        this.district = -1;
        this.id = -1; //to signify ID is not asssigned yet.
        this.portion = portion;
    }

    public Town(String name, int population, double lat, double lng,int id, double portion) {
        this.name = name;
        this.population = population;
        this.location = new Location(lat, lng);
        this.district = -1;
        this.id = id;
        this.portion = portion;
    }

    public Town(Town t){
        this.name = t.getName();
        this.population = t.getPopulation();
        this.location = t.getLocation();
        this.district = t.getDistrict();
        this.id = t.getId();
        this.portion = t.getPortion();
    }

    public String getName() {
        return name;
    }

    public int getDistrict() {
        return district;
    }

    public int getId(){
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public int getPopulation() {
        return population;
    }

    public String toString() {
        return name + ", District: " + district + ", Location:" + location + ", Population: " + population;
    }

    public void setDistrict(int district) {
        this.district = district;
    }

    public void setPopulation(int newPop){
        this.population = newPop;
    }

    public double getPortion(){
        return portion;
    }

    public void setPortion(double newPortion){
        this.portion = newPortion;
    }
}

