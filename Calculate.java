import java.util.ArrayList;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.Arrays;
import java.io.FileWriter;
import java.io.IOException;


public class Calculate{
    private ArrayList<Town> allTowns;
    private Town[] allTownsFinal; //stores the original list of towns, final variable. Used only for finding closest town at the end with leftover towns.
    private District[] allDistricts;

    //pThreshold, a percentage of when to stop adding towns to district of totalpop/#districts. it should be less than 1.
    public Calculate(String filename, int districts, double pThreshold, String stateName){
        try {
            //Read from the file and put all info into allTowns
            allTowns = new ArrayList<Town>();
            double totalStatePop = 0;
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] values = data.split(",");
                allTowns.add(new Town(values[0],Integer.parseInt(values[1].replace(" ", "")),Double.parseDouble(values[2].replace(" ", "")),Double.parseDouble(values[3].replace(" ", ""))));
                totalStatePop += Integer.parseInt(values[1].replace(" ", ""));
            }
            myReader.close();
            allTownsFinal = new Town[allTowns.size()];
            for(int i =0 ;i<allTowns.size();i++){
                allTownsFinal[i] = allTowns.get(i);
            }
            System.out.println("Done reading File");
            System.out.println("Calculated Total Population: "+ totalStatePop);



            allDistricts = new District[districts];
            double threshold = pThreshold*totalStatePop/districts;
            System.out.println("Population threshold:"+threshold);
            System.out.println("Starting Algorithm\n");
            for (int i =0; i<allDistricts.length;i++){
                //create all the districts from the current most populous, then add until passed the threshold;
                //i+1 because indexing starts at zero, and it wouldn't make sense for a "0th district"
                allDistricts[i] = createDistrict(threshold,i+1);
            }



            //At the end of the district creation when there are leftover towns, assign them to nearest district.
            //this loops through all the towns, deleting them when they are assigned a district.
            while(allTowns.size()>0){
                int index = findClosestTownWithDistrict(allTowns.get(0));
                allDistricts[index-1].addTown(allTowns.get(0));
                allTowns.remove(0);
            }



            if (allTownsFinal.length<=100){
                System.out.println("RESULTS:"+Arrays.toString(allDistricts).replace("[","\n").replace("]","\n"));
            }else{
                System.out.println("RESULTS: \n\n 100+ lines of output\n");
            }
            try {
                FileWriter myWriter = new FileWriter("results.txt");
                String strForFile = stateName+"," +pThreshold+Arrays.toString(allDistricts).replace("[","");
                strForFile = strForFile.replace("]","");
                myWriter.write(strForFile);
                myWriter.close();
                System.out.println("\n **Successfully written to results.txt**");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }




        } catch (FileNotFoundException e) {




            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


    //Creates a district around the most populous town currently in allTowns, and deletes already taken towns along the way.
    //threshold should be about 90% of totalstatepop/#districts
    private District createDistrict(double threshold, int num){
        if(allTowns.size()<1) return new District(num);
        District d = new District(num);
        //first add the most populous town, and then remove from the allTowns list
        int mpIndex = findMostPopulous();
        d.addTown(allTowns.get(mpIndex));
        allTowns.remove(mpIndex);
        //then keep on adding the nearest town until passed the threshold;
        while(d.getTotalPop()<=threshold&&allTowns.size()>0){
            // System.out.println("TOTAL:   "+d.getTotalPop());
            // System.out.println(threshold);
            // System.out.println(allTowns.size());
            int index = findClosest(d);
            d.addTown(allTowns.get(index));
            //System.out.println("-+"+allTowns.get(index).getPopulation());
            allTowns.remove(index);
        }
        return d;
    }

    //searches the allTowns instance variable arraylist for the town closest to the center of population for some district
    //returns the index of the town in allTowns that is closest.
    private int findClosest(District district){
        int index = 0;
        double minDist = Double.POSITIVE_INFINITY;
        for (int i =0; i<allTowns.size();i++){
            if (district.distTo(allTowns.get(i))<minDist){
                index = i;
                minDist = district.distTo(allTowns.get(i));
            }
        }
        return index;
    }

    //find closest town that is already assigned a district to another town. Only use at end. 2nd Version of Algorithm. Returns the number DISTRICT of that closest town. NOT the index of the town or the index of the district (index of the district starts at 0)
    private int findClosestTownWithDistrict(Town town){
        int index = 1;
        double minDist = Double.POSITIVE_INFINITY;
        for (int i =0; i<allTownsFinal.length;i++){
            if (allTownsFinal[i].getDistrict()!=-1&&town.getLocation().distTo(allTownsFinal[i].getLocation())<minDist&&town!=allTownsFinal[i]){
                minDist = town.getLocation().distTo(allTownsFinal[i].getLocation());
                index =allTownsFinal[i].getDistrict();
            }
        }
        return index;
    }

    //finds closests DISTRICT given a town. Only use at the end.
    private int findClosestDistrict(Town town){
        int index = 0;
        double minDist = Double.POSITIVE_INFINITY;
        for (int i =0; i<allDistricts.length;i++){
            if (allDistricts[i].distTo(town)<minDist){
                index = i;
                minDist = allDistricts[i].distTo(town);
            }
        }
        return index;
    }

    //returns index of most populous town in the allTowns arraylist
    private int findMostPopulous(){
        int index = 0;
        double maxPop = 0;
        for (int i =0; i<allTowns.size();i++){
            if (allTowns.get(i).getPopulation()>maxPop) {
                index = i;
                maxPop = allTowns.get(i).getPopulation();
            }
        }
        return index;
    }

}
