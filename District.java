import java.util.ArrayList;

class District {
    private int districtNo;
    private int totalPop;
    private Location centerOfPop;
    private ArrayList<Town> towns;

    public District(int num) {
        this.districtNo = num;
        this.totalPop = 0;
        this.centerOfPop = null;
        this.towns = new ArrayList<Town>();
    }

    public void addTown(Town town) {
        town.setDistrict(districtNo);
        if (centerOfPop == null) this.centerOfPop = town.getLocation();
        else {
            double newLat = (this.centerOfPop.getLat() * totalPop + town.getLocation().getLat() * town.getPopulation()) / (totalPop + town.getPopulation());
            double newLng = (this.centerOfPop.getLng() * totalPop + town.getLocation().getLng() * town.getPopulation()) / (totalPop + town.getPopulation());
            centerOfPop.setLat(newLat);
            centerOfPop.setLng(newLng);
        }
        totalPop += town.getPopulation();
        towns.add(town);
    }

    public int getDistrictNo() {
        return districtNo;
    }

    public int getTotalPop() {
        return totalPop;
    }

    public Location getCenterOfPop() {
        return centerOfPop;
    }

    public String toString(){
        // String s = "\nDistrict " + districtNo + ". Total Population: " + totalPop + "\n   ALL TOWNS:";
        // for(Town t:towns){
        //   s += "\n    -" + t.getName() + "| Population: " + t.getPopulation();
        // }
        String s = "";
        for(Town t:towns){
            String portion = t.getPortion()!=1.0? " ("+(Math.floor(t.getPortion() * 100))+"%) ":"";
            s += "\n" + t.getName() + portion+"," + districtNo+","+t.getLocation().getLat()+","+t.getLocation().getLng()+","+t.getPopulation();
        }
        return s;
    }

    public double distTo(Town town){
        //uses the Location.distTo(Location) method in Location.java
        return centerOfPop.distTo(town.getLocation());
    }
}
