import static org.junit.Assert.*;
import org.junit.Test;

public class PrimeNumberCheckerTest {

    @Test
    public void testSmallPrimes() {
        assertTrue(PrimeNumberChecker.isPrime(2));
        assertTrue(PrimeNumberChecker.isPrime(3));
        assertTrue(PrimeNumberChecker.isPrime(5));
        assertTrue(PrimeNumberChecker.isPrime(7));
        assertTrue(PrimeNumberChecker.isPrime(11));
        assertTrue(PrimeNumberChecker.isPrime(13));
    }

    @Test
    public void testLargerPrimes() {
        assertTrue(PrimeNumberChecker.isPrime(97));
        assertTrue(PrimeNumberChecker.isPrime(101));
        assertTrue(PrimeNumberChecker.isPrime(7919));
    }

    @Test
    public void testNonPrimes() {
        assertFalse(PrimeNumberChecker.isPrime(4));
        assertFalse(PrimeNumberChecker.isPrime(6));
        assertFalse(PrimeNumberChecker.isPrime(8));
        assertFalse(PrimeNumberChecker.isPrime(9));
        assertFalse(PrimeNumberChecker.isPrime(15));
        assertFalse(PrimeNumberChecker.isPrime(100));
    }

    @Test
    public void testEdgeCases() {
        assertFalse(PrimeNumberChecker.isPrime(0));
        assertFalse(PrimeNumberChecker.isPrime(1));
        assertFalse(PrimeNumberChecker.isPrime(-1));
        assertFalse(PrimeNumberChecker.isPrime(-7));
        assertFalse(PrimeNumberChecker.isPrime(Integer.MIN_VALUE));
    }

    @Test
    public void testTwo() {
        assertTrue(PrimeNumberChecker.isPrime(2));
    }

    @Test
    public void testSquareOfPrime() {
        assertFalse(PrimeNumberChecker.isPrime(25));  // 5*5
        assertFalse(PrimeNumberChecker.isPrime(49));  // 7*7
        assertFalse(PrimeNumberChecker.isPrime(121)); // 11*11
    }

    @Test
    public void testLargePrime() {
        assertTrue(PrimeNumberChecker.isPrime(104729));
    }

    @Test
    public void testEvenNumbers() {
        assertFalse(PrimeNumberChecker.isPrime(4));
        assertFalse(PrimeNumberChecker.isPrime(100));
        assertFalse(PrimeNumberChecker.isPrime(1000));
    }
}
