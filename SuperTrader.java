/*https://practice.geeksforgeeks.org/problems/stock-buy-and-sell-1587115621/1
The cost of stock on each day is given in an array A[] of size N. Find all the days
on which you buy and sell the stock so that in between those days your profit is maximum.
Note: There may be multiple possible solutions. Return any one of them. Any correct solution
will result in a output of 1, whereas wrong solutions will result in an output of 0.*/
public class SuperTrader {

    public static void main(String[] args) {

        int arraySize = 6;
        double prices[] = { 0.5, 1.0, 1.0, 1.0, 1.0, 1.0 };

        boolean profitabledays = areThereProfitableDays(arraySize, prices);
        if (profitabledays == true) {
            System.out.println("Profit Got");
        } else {
            System.out.println("Got loss");
        }

    }

    public static boolean areThereProfitableDays(int arraySize, double[] prices) {

        boolean profitDayFlag = false;

        if (prices.length > 1) {
            for (int i = 0; i < prices.length; i++) {

                for (int j = (i+1); j < prices.length; j++) {
                    if (prices[i] < prices[j]) {
                        profitDayFlag = true;

                        int presentday = i + 1;
                        int nextDay = j + 1;
                        System.out.println("Stock at day   " + presentday + " and price is " + prices[i]
                                + " next day   " + nextDay + " stock price is " + prices[j]);
                        break;
                    }

                }
                if (profitDayFlag == true) {
                    break;
                }
            }
        } else {
            profitDayFlag = false;
        }

        return profitDayFlag;

    }

}
