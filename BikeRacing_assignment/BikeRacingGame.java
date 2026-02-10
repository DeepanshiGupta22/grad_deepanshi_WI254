package BikeRacing_assignment;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

// Class to hold the final results for the dashboard
class RaceResult {
    String bikerName;
    long startTimeMillis;
    long endTimeMillis;
    long timeTakenMillis;

    public RaceResult(String bikerName, long startTimeMillis, long endTimeMillis) {
        this.bikerName = bikerName;
        this.startTimeMillis = startTimeMillis;
        this.endTimeMillis = endTimeMillis;
        this.timeTakenMillis = endTimeMillis - startTimeMillis;
    }
}

// The Biker Task representing the logic for a single racer
class BikerTask implements Callable<RaceResult> {
    private final String name;
    private final int totalDistanceMeters;
    private final CountDownLatch startLatch; // To ensure everyone starts at the exact same moment

    public BikerTask(String name, int totalDistanceMeters, CountDownLatch startLatch) {
        this.name = name;
        this.totalDistanceMeters = totalDistanceMeters;
        this.startLatch = startLatch;
    }

    @Override
    public RaceResult call() throws Exception {
        // Latch release
        startLatch.await();
        
        long startTime = System.currentTimeMillis();
        int currentDistance = 0;

        // Loop until the total distance is covered
        while (currentDistance < totalDistanceMeters) {
            // Determine the step (move 100m or whatever is left)
            int step = 100;
            if (currentDistance + step > totalDistanceMeters) {
                step = totalDistanceMeters - currentDistance;
            }
            // This makes the speed vary dynamically throughout the process
            int speedFactor = ThreadLocalRandom.current().nextInt(50, 150); 
            Thread.sleep(speedFactor);
            currentDistance += step;
            // Display coverage every 100 meters (or final stretch)
            // Synchronized block to prevent text from jumbling on console
            synchronized (System.out) {
                System.out.println(name + " covered " + currentDistance + "m");
            }
        }
        long endTime = System.currentTimeMillis();
        return new RaceResult(name, startTime, endTime);
    }
}

public class BikeRacingGame {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("     WELCOME TO THE BIKE RACING ARENA   ");
        System.out.println("=========================================");

        // 1. Enter 10 Biker names
        List<String> bikers = new ArrayList<>();
        System.out.println("Please enter the names of the 10 Bikers:");
        for (int i = 1; i <= 10; i++) {
            System.out.print("Enter name for Biker " + i + ": ");
            String name = scanner.nextLine();
            // Default name if user leaves it blank
            if(name.trim().isEmpty()) name = "Racer-" + i;
            bikers.add(name);
        }

        // 2. Enter Distance
        System.out.print("\nEnter total race distance in KM: ");
        double distanceKm = scanner.nextDouble();
        int distanceMeters = (int) (distanceKm * 1000); // Convert to meters

        // Setup Executor Service
        // We use a FixedThreadPool of 10 so all bikers run exactly at the same time
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        // Latch to hold all threads until countdown finishes
        CountDownLatch startSignal = new CountDownLatch(1);

        // Prepare the tasks
        List<Callable<RaceResult>> callables = new ArrayList<>();
        for (String bikerName : bikers) {
            callables.add(new BikerTask(bikerName, distanceMeters, startSignal));
        }

        try {
            // Submit all tasks to the executor but they will wait on the latch until the countdown is done
            List<Future<RaceResult>> futures = new ArrayList<>();
            for(Callable<RaceResult> c : callables) {
                futures.add(executor.submit(c));
            }

            //Countdown
            System.out.println("\nRacers are on the starting line...");
            Thread.sleep(1000);
            
            System.out.println("\n*** COUNTDOWN ***");
            for (int i = 10; i > 0; i--) {
                System.out.print(i + "...");
                Thread.sleep(800); // slight pause
            }
            System.out.println("\n\nGO! LETS PLAYYYY! \n");

            // Releasing the latch to start all threads at the same time
            startSignal.countDown(); 
        
            // Collect results
            List<RaceResult> results = new ArrayList<>();
            for (Future<RaceResult> future : futures) {
                results.add(future.get()); // This waits for the specific thread to finish
            }

            // Printing Dashboard
            printDashboard(results);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
            scanner.close();
        }
    }

    private static void printDashboard(List<RaceResult> results) {
        // Sort by time taken (ASC) to determine Rank
        results.sort(Comparator.comparingLong(r -> r.timeTakenMillis));

        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss.SSS");

        System.out.println("\n\n======================================================================================");
        System.out.println("                                 FINAL RACE DASHBOARD                                 ");
        System.out.println("======================================================================================");
        System.out.printf("%-5s | %-15s | %-15s | %-15s | %-15s%n", 
                "RANK", "BIKER NAME", "START TIME", "END TIME", "TIME TAKEN (sec)");
        System.out.println("--------------------------------------------------------------------------------------");

        int rank = 1;
        for (RaceResult r : results) {
            String startStr = timeFormatter.format(new Date(r.startTimeMillis));
            String endStr = timeFormatter.format(new Date(r.endTimeMillis));
            double durationSec = r.timeTakenMillis / 1000.0;

            System.out.printf("%-5d | %-15s | %-15s | %-15s | %-15.3f s%n", 
                    rank++, r.bikerName, startStr, endStr, durationSec);
        }
        System.out.println("======================================================================================");
    }
}