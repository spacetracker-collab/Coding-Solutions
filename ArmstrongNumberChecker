public class ArmstrongNumberChecker{

    public static void main(String []args){

        System.out.println(ArmstrongNumberChecker.checkArmtrongNumber("153"));

    }

    public static double checkIntPerformance(double number1, double number2, int times)
    {
        double begin = System.nanoTime();

        for(int i =0; i <times; i++)
        {
            if(number1 == number2)
            {

            }
        }

        double end = System.nanoTime();
        double total = end - begin;
        return total;


    }

    public static double checkStringPerformance(String number1, String number2, int times)
    {

        double begin = System.nanoTime();

        for(int i =0; i <times; i++)
        {
            if(number2.equals(number1))
            {

            }

        }

        double end = System.nanoTime();
        double total = end - begin;
        return total;


    }


    public static boolean checkArmtrongNumber(String number) {

        int armstrongNumber  = 0;
        if ("0".equals(number) )
            return true;
        if (Integer.parseInt(number) < 0)
            return false;

        for(int i =0 ; i< number.length(); i++){
            System.out.println(number.charAt(i)) ;

            char c = number.charAt(i);
            char[]c1 = new char[1];
            c1[0] = c;
            String s = new String(c1);
            Integer a = Integer.parseInt(s);

            armstrongNumber = armstrongNumber + (a * a * a);
        }


        boolean result = false;
        if(Integer.parseInt(number) == armstrongNumber){
            result = true;
        } else {
            result = false;
        }

        System.out.println("Running tests");

        double number2 = Integer.parseInt(number);
        double int_time = ArmstrongNumberChecker.checkIntPerformance(armstrongNumber, number2, 1000);


        String num2 = String.valueOf(armstrongNumber);
        double string_time = ArmstrongNumberChecker.checkStringPerformance(num2,number,1000);

        System.out.println("1000 execution int time in nanoseconds:"+ int_time);
        System.out.println("1000 execution string time in nanonseconds"+ string_time);

        return result;

    }
}
