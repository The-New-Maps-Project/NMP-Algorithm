import java.util.ArrayList;
import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

// import jdk.internal.jshell.tool.resources.l10n;

import java.util.Arrays;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Calculate {
    private ArrayList<Town> allTowns;
    private Town[] allTownsFinal; // stores the original list of towns, final variable. Used only for finding
                                  // closest town at the end with leftover towns.
    private District[] allDistricts;
    private Location center; //the center of population for the whole state.
    private boolean apd;

    // pThreshold, a percentage of when to stop adding towns to district of
    // totalpop/#districts. it should be less than 1.
    public Calculate(String filename, int districts, double pThreshold, String stateName, boolean apdParam) {
        try {
            apd = apdParam;
            //Step 1: Read from the file and put all info into allTowns
            allTowns = new ArrayList<Town>();
            double totalStatePop = 0;
            File myObj = new File(filename);
            Scanner sc = new Scanner(myObj);
            int idCount = 1;
            while (sc.hasNextLine()) {
                String data = sc.nextLine();
                String[] values = data.split(",");
                allTowns.add(new Town(values[0], Integer.parseInt(values[1].replace(" ", "")),
                        Double.parseDouble(values[2].replace(" ", "")),
                        Double.parseDouble(values[3].replace(" ", "")),idCount,1.0));
                totalStatePop += Integer.parseInt(values[1].replace(" ", ""));
                idCount++;
            }
            sc.close();
            System.out.println("Done reading File");

            //Step 2: Find total population by creating the allTownsFinal array.
            allTownsFinal = new Town[allTowns.size()];
            for (int i = 0; i < allTowns.size(); i++) {
                allTownsFinal[i] = new Town(allTowns.get(i)); //MUST create a clone, because if using partial towns, when the population changes in the arraylist, the total pop in the array should remain the same
            }
            System.out.println("Calculated Total Population: " + totalStatePop);

            //Step 3: find the center of population
            int countPop = 0;
            double totalLat = 0;
            double totalLng = 0;
            center = new Location(0.0,0.0);
            for(Town t: allTowns){
                totalLat += t.getPopulation()*t.getLocation().getLat();
                totalLng += t.getPopulation()*t.getLocation().getLng();
            }
            center.setLat(totalLat/totalStatePop);
            center.setLng(totalLng/totalStatePop);
            System.out.println("CENTER: "+center+"\n");

            //Step 4: Create the districts, one by one.
            allDistricts = new District[districts];
            double threshold = pThreshold * totalStatePop / districts;
            System.out.println("Population threshold:" + threshold);
            System.out.println("Starting Algorithm\n");
            for (int i = 0; i < allDistricts.length; i++) {
                // create all the districts from the current most populous, then add until
                // passed the threshold;
                // i+1 because indexing starts at zero, and it wouldn't make sense for a "0th
                // district"
                allDistricts[i] = createDistrict(threshold, i + 1);
            }

            // At the end of the district creation when there are leftover towns, assign
            // them to nearest district.
            // this loops through all the towns, deleting them when they are assigned a
            // district.
            while (allTowns.size() > 0) {
                int index = findClosestTownWithDistrict(allTowns.get(0));
                allDistricts[index - 1].addTown(allTowns.get(0));
                allTowns.remove(0);
            }

            if (allTownsFinal.length <= 100) {
                System.out.println("RESULTS:" + Arrays.toString(allDistricts).replace("[", "\n").replace("]", "\n"));
            } else {
                System.out.println("RESULTS: \n\n " + allTownsFinal.length + " lines of output\n");
            }
            try {
                Date date = new Date();
                String uniqueFileName = System.getProperty("user.dir") + File.separator + "results" + File.separator
                        + stateName + "-" + pThreshold + "---"
                        + date.toString().replaceAll("\\s+", "-").replaceAll(":", "--") + ".txt";
                File file = new File(uniqueFileName);
                if (!file.exists()) {
                    System.out.println("Created New File in \"results\" folder: " + file.createNewFile());
                }
                FileWriter myWriter = new FileWriter(uniqueFileName);
                String strForFile = stateName + "," + pThreshold + Arrays.toString(allDistricts).replace("[", "");
                strForFile = strForFile.replace("]", "");
                myWriter.write(strForFile);
                myWriter.close();
                System.out.println("\n **Successfully written to results folder**\n File Name: " + uniqueFileName
                        .replace(System.getProperty("user.dir") + File.separator + "results" + File.separator, ""));
            } catch (IOException e) {
                System.out.println("***FAILED TO WRITE RESULTS***\nAn error occurred.");
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {

            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    // Creates a district around the most populous town currently in allTowns, and
    // deletes already taken towns along the way.
    // threshold should be about 90% of totalstatepop/#districts
    private District createDistrict(double threshold, int num) {
        if (allTowns.size() < 1)
            return new District(num);
        District d = new District(num);
        // first add the most populous town, and then remove from the allTowns list
        int mpIndex = apd?findExtremeLocation():findMostPopulous();        
        d.addTown(allTowns.get(mpIndex));
        allTowns.remove(mpIndex);
        // then keep on adding the nearest town until passed the threshold;
        while (d.getTotalPop() <= threshold && allTowns.size() > 0) {
            // System.out.println("TOTAL: "+d.getTotalPop());
            // System.out.println(threshold);
            // System.out.println(allTowns.size());
            int index = findClosest(d);
            Town closestTown = allTowns.get(index);
            //Do a special check to see if you need to split a town
            if(apd&&d.getTotalPop()+closestTown.getPopulation()>threshold){
                
                int portion =  (int)Math.floor(threshold)-d.getTotalPop(); //exactly how many people you need to get to the threshold.
                int remaining = closestTown.getPopulation() - portion;
                int totalPop = (findTownTotalPop(closestTown.getId()));
                //NOTE: portionPercent and remainingPercent may not add to 100%, because a really large city can be divided between 3 or more districts.
                double portionPercent = (double)portion/(double)totalPop;
                double remainingPercent = (double)remaining/(double)totalPop;
                //must create a new instance, because it is like dividing the town into two.
                d.addTown(new Town(closestTown.getName(),portion,closestTown.getLocation().getLat(),closestTown.getLocation().getLng(),closestTown.getId(),portionPercent));
                closestTown.setPopulation(remaining); //the remaining population can be in another district, don't delete
                closestTown.setPortion(remainingPercent);
                break; //break because this can cause an infinite loop due to rounding of the threshold.
            }else{ //otherwise, normally add the town.
                d.addTown(closestTown);
                allTowns.remove(index);
            }
            
            // System.out.println("-+"+allTowns.get(index).getPopulation());
            
        }
        return d;
    }

    // searches the allTowns instance variable arraylist for the town closest to the
    // center of population for some district
    // returns the index of the town in allTowns that is closest.
    private int findClosest(District district) {
        int index = 0;
        double minDist = Double.POSITIVE_INFINITY;
        for (int i = 0; i < allTowns.size(); i++) {
            if (district.distTo(allTowns.get(i)) < minDist) {
                index = i;
                minDist = district.distTo(allTowns.get(i));
            }
        }
        return index;
    }

    // find closest town that is already assigned a district to another town. Only
    // use at end. 2nd Version of Algorithm. Returns the number DISTRICT of that
    // closest town. NOT the index of the town or the index of the district (index
    // of the district starts at 0)
    private int findClosestTownWithDistrict(Town town) {
        int index = 1;
        double minDist = Double.POSITIVE_INFINITY;
        for (int i = 0; i < allTownsFinal.length; i++) {
            if (allTownsFinal[i].getDistrict() != -1
                    && town.getLocation().distTo(allTownsFinal[i].getLocation()) < minDist
                    && town != allTownsFinal[i]) {
                minDist = town.getLocation().distTo(allTownsFinal[i].getLocation());
                index = allTownsFinal[i].getDistrict();
            }
        }
        return index;
    }

    // finds closests DISTRICT given a town. Only use at the end.
    private int findClosestDistrict(Town town) {
        int index = 0;
        double minDist = Double.POSITIVE_INFINITY;
        for (int i = 0; i < allDistricts.length; i++) {
            if (allDistricts[i].distTo(town) < minDist) {
                index = i;
                minDist = allDistricts[i].distTo(town);
            }
        }
        return index;
    }

    // returns index of most populous town in the allTowns arraylist
    private int findMostPopulous() {
        int index = 0;
        double maxPop = 0;
        for (int i = 0; i < allTowns.size(); i++) {
            if (allTowns.get(i).getPopulation() > maxPop) {
                index = i;
                maxPop = allTowns.get(i).getPopulation();
            }
        }
        return index;
    }

    // returns index of the most extreme location of a town int he allTowns arraylist
    private int findExtremeLocation(){
        int index = 0;
        double maxDist = 0.0;
        for(int i = 0;i<allTowns.size();i++){
            double dist = allTowns.get(i).getLocation().distTo(center);
            if (dist > maxDist) {
                index = i;
                maxDist = dist;
            }
        }
        return index;
    }


    // returns total population of the town at the start
    // used for dividing partial towns
    private int findTownTotalPop(int id){
        for(Town t: allTownsFinal){
            if(t.getId()==id) return t.getPopulation();
        }
        return 0;
    }

}
