// COP4520 - Assignment 1 - Yohan Hmaiti
// This problem finds all the primes up to 10^8 with an optimized runtime
// and finds the top max primes found and prints the output to a file in a specific format
// using 8 threads and a version of the Sieve of Eratosthenes algorithm "Segmented Sieve".

// Strategy:
// we implement a version of the Sieve of Eratosthenes algorithm (segmented sieve) to find all the primes up to 10^8, 
// we mark the multiples of each prime as non prime, and we start from 2, and we go up to the sqrt of the
// threshhold, which we update to avoid overlap between threads and their work, and we mark all the multiples of each prime as non prime, 
// and we do this for all the primes, leading to an exclusion of all the comosites that are multiples of the primes found.
// In this work, we rely on the usage of 8 threads, and we use a threshhold to avoid overlap between threads. The main criteria
// is that the threshhold is update by t *= 10, and we start from 10, and for each thread we increment the thredhhold by *10, we also 
// for each thread run the sieve of erathosthenes from 2 up to the sqrt of the threshhold specific to that thread, and then we mark all the multiples of the 
// primes out, our algorithm can be shown here: 
/*
 *          // we can also do i^2 <= t
 *           for (int i = 2; i <= Math.sqrt(t) && i*i <= t; i++) {
 *               if (primeNumbers[i]) {
 *                   for (int j = i * i; j <= t; j += i) {
 *                       primeNumbers[j] = false;
 *                   }
 *               }
 *           }
 */

 /*
  * resulting sum: 279209790387276
  * resulting count of primes: 5761455
  * resulting time: (found in primes.txt)
  * top 10 primes (low to high): (found in primes.txt)
  * additional details, evaluation/experiemntation/runtime and additional discussion are provided in the readMe.txt file
  */

// pre-processor directives
import java.util.*;
import java.lang.*;
import java.io.*;
import java.util.concurrent.*;

// class that implements the prime checker using a version of the Sieve of Eratosthenes algorithm
// for an optimized runtime and better processing 
public class primes extends Thread{

    // we set the max number of umbers to be processed as constant 
    public static final double n = Math.pow(10, 8);

    // we declare the number of threads to be used
    public static final int threadCount = 8;

    // set an array of booleans for the primes
    public static boolean[] primeNumbers = new boolean[(int) n + 1];

    // driver method
    public static void main(String[] args) throws IOException{
        
        // declare a variable to hold the count of primes found
        // a variable to store the sum, and a variables to store the time of execution
        int countOfPrimes = 0;
        long totalSum = 0;
        long duration = 0;

        // variables to log the start and end time
        long start = 0, finish = 0;

        // threshold for the thread start position to avoid overlap later between threads on the numbers 
        // to be processed, we will increment this threshhold by 10 each time 
        int t = 10;

        // go over the array of the primes and set all to true 
        // we set the 0 and 1 to not be primes 
        Arrays.fill(primeNumbers, true);
        primeNumbers[0] = primeNumbers[1] = false;

        // declare an array of threads 
        Thread[] threads = new Thread[threadCount];

        // log the start time 
        start = System.currentTimeMillis();

        // we start the simulation
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(primeChecker(t));
            threads[i].start();

            // increment the threshhold to avoid overlap between the running threads
            t *= 10;
        }

        // we join the threads at the end of the simulation
        for (int i = 0; i < threadCount; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                System.out.println("Thread number: " + i + " failed!!");
            }
        }

        // record the end time and then get the duration, count and sum
        finish = System.currentTimeMillis();
        System.out.println("start: " + start + " finish: " + finish);
        duration = finish - start;
        System.out.println("duration: " + duration + " ms");
        countOfPrimes = getCount();
        totalSum = getSum();

        // we record the top 10 primes found from the array of primes
        int[] topTen = new int[10];

        int countHelper = 0;
        for (int i = primeNumbers.length - 1; i > 0; i--) {
            if (countHelper == 10) {
                break;
            }
            if (primeNumbers[i]) {
                topTen[countHelper] = i;
                System.out.println("top ten now: " + topTen[countHelper]);
                countHelper++;
            }
        }

        // print the output to primes.txt
        printOutput(countOfPrimes, duration, totalSum, topTen);

        // end of the processing 
        System.out.print("end of the processing!");


    }

    // method that implements the Sieve of Eratosthenes algorithm to get all the prime 
    // numbers that are less than a stated maximum, for the fuction it runs using our set threshhold
    // yet after all threads execute we will get all the primes up to 10^8
    // Approach:
    // we already excluded 0 and 1 in main, so we start at 2, and as we find non prime numbers we switch the 
    // value of the arrray at that position to false.
    // when looping from 2 up to the sqrt of the threshhold unclusive, we check if the prime is true, 
    // if it is, we begin from i^2 and increment by the i to get to the multiples of i, then we mark them as false
    // resource wikepedia: https://en.wikipedia.org/wiki/Sieve_of_Eratosthenes
    public static Runnable primeChecker(int t) {
        return () -> {
            // here we can also do i^2 <= t
            for (int i = 2; i <= Math.sqrt(t) && i * i <= t; i++) {
                if (primeNumbers[i]) {
                    for (int j = i * i; j <= t; j += i) {
                        primeNumbers[j] = false;
                    }
                }
            }
        };
    }

    // method that counts the number of primes found
    public static int getCount() {
        int count = 0;
        for (int i = 0; i < n + 1; i++) {
            if (primeNumbers[i]) {
                count++;
            }
        }
        return count;
    }

    // method that gets the sum of all the primes found
    public static long getSum() {
        long sum = 0;
        for (int i = 0; i < n + 1; i++) {
            if (primeNumbers[i]) {
                sum += i;
            }
        }
        return sum;
    }

    // method that prints to primes.txt the execution time, total number of primes found and the 
    // sum of all the primes found, and the top ten prime numbers found in order of low to high
    public static void printOutput(int countOfPrimes, long duration, long totalSum, int[] tenMax) {

        try {
            PrintWriter writer = new PrintWriter("primes.txt");
            writer.println("Execution time: " + duration + " ms" + " -> " + duration / 1000.0 + "s");
            writer.println("Total number of primes found: " + countOfPrimes);
            writer.println("Sum of all primes found: " + totalSum);

            // we print the top 10 primes found from low to high
            Arrays.sort(tenMax);
            writer.println("Top ten maximum primes, listed in order from lowest to highest: ");
            for (int i = 0; i < tenMax.length; i++) {
                writer.print(tenMax[i] + " ");
            }

            writer.close();
        } catch (IOException e) {
            System.out.println("Could not write the output to primes.txt");
        }
    }

}
