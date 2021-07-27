package org.matsim.prepare;

public class test {

    public static void main(String[] args) {


        int totalVisitors=17000;
        double percentageSafariOwnCar = 0.4;
        double percentageVisitorsOwnCar = 0.9;
        String[] parkingLots = {"Wasserlandparkplatz", "Serengeti-Parkplatz", "Eickeloh-Parkplatz"}; //


        int ownCarVisitorsVehicles = (int) ( (percentageVisitorsOwnCar*totalVisitors) / 3.4);
        int serengetiParkVehicles = (int) ((percentageSafariOwnCar * totalVisitors) / 3.4);
        int carparkVehicles = ownCarVisitorsVehicles - serengetiParkVehicles;
        int serengetiCarparkVehicles = (int) (carparkVehicles/parkingLots.length);
        int wasserlandCarparkVehicles = (int) (carparkVehicles/parkingLots.length);
        int eickelohCarparkVehicles = 0;

        if (parkingLots.length == 3) {
            eickelohCarparkVehicles = (int) (carparkVehicles/parkingLots.length);
        }


        System.out.println("Safari: " + serengetiParkVehicles);
        System.out.println("each parking lot: " + serengetiCarparkVehicles);
        System.out.println("number wasserland visitors: " + wasserlandCarparkVehicles);
        System.out.println("number eickeloh visitors: " + eickelohCarparkVehicles);

    }

}
