COP4520 - Spring 2023 - PA1
Author: Yohan Hmaiti

# To compile and run the code use the following:
1. Open the terminal, and navigate using the cd command to the location of the project, and check with the ls command that the file primes.java is there.
2. Execute the followig commands:
```
    a. To compile the program: $javac primes.java
    b. To run the program: $java primes
```
3. Expected Output:
    a. a file will be generated with details about the execution time on your machine, sum of primes found and their total count, 
    also the top ten primes found will be printed from low to high. The file will be called primes.txt and will be generated in the same directory as the primes.java
    b. a couple summary statements will be printed to the console too that serve as debug logs as the program runs.

# Summary of The Approach and Proof of Correctness and Efficiency of My Design Along with Experimental Evaluation:
->(Note: this strategy was also covered in the code files as comments)

we implement a version of the Sieve of Eratosthenes (segmented sieve) algorithm to find all the primes up to 10^8, 
we mark the multiples of each prime as non prime, and we start from 2, and we go up to the sqrt of the
threshhold, which we update to avoid overlap between threads and their work, and we mark all the multiples of each prime as non prime, 
and we do this for all the primes, leading to an exclusion of all the composites that are multiples of the primes found.

In this work, we rely on the usage of 8 threads, and we use a threshhold to avoid overlap between threads. The main criteria
is that the threshhold is update by t *= 10, and we start from 10, and for each thread we increment the thredhhold by *10, we also 
for each thread run the sieve of erathosthenes from 2 up to the sqrt of the threshhold specific to that thread, and then we mark all the multiples of the 
primes out, our algorithm can be shown here: 
```
for (int i = 2; i <= Math.sqrt(t); i++) 
               if (primeNumbers[i]) 
                   for (int j = i * i; j <= t; j += i) 
                       primeNumbers[j] = false;
```
Thus, the algorithm technically takes the numbers that are multiples of the current prime number (x) in the itertation that are greater than or equal to x^2, and markes them as non-prime. 

Based on the looping process that we have, we tend to mark an amount of numbers that is equal to (n / current starting prime point), for example:
if my loop starts at 2, then I need to mark n / 2 elements, and so on, leading to n/2 + n/3 + n/5 + ....
Which is simply n * (1/2 + 1/3 + .....), which results in n * log(log(n)) based on a harmonic progression, and that matches the expected runtime, considering that 
The implemented version of this Sieve takes n * log(log(n)), the runtime we achieved is way better than the redundant and brute forced approach that can be used through direct looping and without threading.

The prior confirms that our approach is in fact correct, however for better assessment of the success and performance of my approach, I have added additional testing as I did run the algorithm
up to the maximum expected from the assignment, and checked the values output which were correct, and I also did run other tests with different max sizes, inclusing 50, 100, 1000, 10^4, 10^6 and so on. All the reflected results were correct and within a time range that was optimized compared to running a regular prime checker without a concurrent approach (just with the main thread, instead of 8 different threads) all the results recorded when using 8 threads resulted in a reduction of the runtime, such that for the biggest test case, with 8threads we reached an average runtime of around 1.4s


