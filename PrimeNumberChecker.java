public class PrimeNumberChecker {

    public static void main(String[] args) {
        int[] testNumbers = {2, 3, 4, 5, 10, 13, 17, 0, 1, -7};
        for (int number : testNumbers) {
            System.out.println(number + " is prime: " + isPrime(number));
        }
    }

    /**
     * Checks whether the given integer is a prime number.
     *
     * A prime number is a natural number greater than 1 that has no positive
     * divisors other than 1 and itself.
     *
     * @param number the integer to check
     * @return true if the number is prime, false otherwise
     */
    public static boolean isPrime(int number) {
        if (number <= 1) {
            return false;
        }
        if (number <= 3) {
            return true;
        }
        if (number % 2 == 0 || number % 3 == 0) {
            return false;
        }
        for (int i = 5; i * i <= number; i += 6) {
            if (number % i == 0 || number % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }
}
