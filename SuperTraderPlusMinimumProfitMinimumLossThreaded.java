import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class SuperTraderPlusMinimumProfitMinimumLossThreaded{

    public static void main(String[] args) {

        int arraySize = 8;
        double prices[] = { 10.0, 1.0, 30.0, 40.0, 50.0, 100.0, 5.0,1.0};
        int qty = 1;
        double brokeragePercent = 0;

        double startTime = System.nanoTime();
        for (int i = 0; i <1000; i ++) {
            printMinProfit(arraySize, prices, qty, brokeragePercent);
            System.out.println("=====================================================");
            printMinLoss(arraySize, prices, qty, brokeragePercent);
        }

        double endTime = System.nanoTime();


        System.out.println("It took "+ (endTime - startTime) + " nanoseconds");
    }

    public static void printAllProfitableTradesAndAverageProfit(int arraySize, double[] prices, int qty, double brokeragePercent) {

        double profitSum = 0;
        int dayCount = 0;

        boolean profitDayFlag = false;
        if (prices.length > 1) {
            for (int i = 0; i < prices.length; i++) {

                for (int j = (i + 1); j < prices.length; j++) {
                    if (prices[i] < prices[j]) {
                        profitDayFlag = true;

                        int presentday = i + 1;
                        int nextDay = j + 1;
                        double priceOfPresentDay = prices[i] * qty;
                        double brokerageAmount = (brokeragePercent / 100) * priceOfPresentDay;
                        double costPriceTotal = priceOfPresentDay + brokerageAmount;

                        double sellingPrice = prices[j] * qty;
                        double brokerageSP = (brokeragePercent / 100) * sellingPrice;
                        double sellingPriceTotal = sellingPrice - brokerageSP;
                        if (sellingPriceTotal > costPriceTotal) {
                            System.out.println("ProfitableCombination:" + "(Day" + presentday + "," + " Day" + nextDay + " Profit:" + (sellingPriceTotal - costPriceTotal) + ")");
                            System.out.println("Profit on Days:" + (++dayCount));
                            profitSum += (sellingPriceTotal - costPriceTotal);
                        }
                    }

                }
                if (profitDayFlag == true) {
                    // break;
                }
            }
        } else {
            profitDayFlag = false;
        }
        System.out.println("ProfitSum:" + profitSum);
        System.out.println("TotalDays:" + dayCount);
        System.out.println("AverageProfit:" + profitSum / dayCount);

    }

    public static boolean areThereProfitableDaysWithBrokerage(int arraySize, double[] prices, int qty, double brokeragePercent) {

        boolean profitDayFlag = false;

        if (prices.length > 1) {
            for (int i = 0; i < prices.length; i++) {

                for (int j = (i + 1); j < prices.length; j++) {
                    if (prices[i] < prices[j]) {
                        profitDayFlag = true;

                        int presentday = i + 1;
                        int nextDay = j + 1;
                        double priceOfPresentDay = prices[i] * qty;
                        double brokerageAmount = (brokeragePercent / 100) * priceOfPresentDay;
                        double costPriceTotal = priceOfPresentDay + brokerageAmount;

                        double sellingPrice = prices[j] * qty;
                        double brokerageSP = (brokeragePercent / 100) * sellingPrice;
                        double sellingPriceTotal = sellingPrice - brokerageSP;
                        if (sellingPriceTotal > costPriceTotal) {
                            System.out.println("Stock at day   " + presentday);
                            System.out.println("Cost Price per Stock " + prices[i]);
                            System.out.println("Stock Qty " + qty);
                            System.out.println("Cost Price without Brokerage " + priceOfPresentDay);
                            System.out.println("Brokerage Percent  " + brokeragePercent);
                            System.out.println("Price with brokerages is " + costPriceTotal);
                            System.out.println("Bokerage Amount Cost Price :" + brokerageAmount);
                            System.out.println("Next day " + nextDay);
                            System.out.println("Selling Price per Stock " + prices[j]);
                            System.out.println("Selling Price without Brokerage " + sellingPrice);
                            System.out.println("Stock price with brokerages is " + sellingPriceTotal);
                            System.out.println("Total Profit " + (sellingPriceTotal - costPriceTotal));
                            System.out.println("Brokerage AmountSelling Price :" + brokerageSP);
                            // break;
                        }
                    }

                }
                if (profitDayFlag == true) {
                    // break;
                }
            }
        } else {
            profitDayFlag = false;
        }

        return profitDayFlag;

    }

    public static boolean areThereProfitableDays(int arraySize, double[] prices) {

        boolean profitDayFlag = false;

        if (prices.length > 1) {
            for (int i = 0; i < prices.length; i++) {

                for (int j = 1; j < prices.length; j++) {
                    if (prices[i] < prices[j]) {
                        profitDayFlag = true;

                        int presentday = i + 1;
                        int nextDay = j + 1;
                        System.out.println("Stock at day   " + presentday + " and price is " + prices[i] + " next day   " + nextDay + " stock price is " + prices[j]);
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

    public static void printMinProfit(int arraySize, double[] prices, int qty, double brokeragePercent) {

        Map<Double, String> dayWiseProfit = new HashMap<>();

        if (prices.length > 1) {
            for (int i = 0; i < prices.length - 1; i++) {
                // int j = i + 1;
                for (int j = (i + 1); j < prices.length; j++) {
                    if (prices[i] < prices[j]) {

                        int presentday = i + 1;
                        int nextDay = j + 1;
                        double priceOfPresentDay = prices[i] * qty;
                        double brokerageAmount = (brokeragePercent / 100) * priceOfPresentDay;
                        double costPriceTotal = priceOfPresentDay + brokerageAmount;

                        double sellingPrice = prices[j] * qty;
                        double brokerageSP = (brokeragePercent / 100) * sellingPrice;
                        double sellingPriceTotal = sellingPrice - brokerageSP;
                        if (sellingPriceTotal > costPriceTotal) {
                            double profitAmount = (sellingPriceTotal - costPriceTotal);
                            String profitableDays = "Day" + presentday + "," + " Day" + nextDay;
                            if (dayWiseProfit.containsKey(profitAmount)) {
                                String detail = dayWiseProfit.get(profitAmount);
                                detail = detail + " || " + profitableDays;
                                dayWiseProfit.remove(profitAmount);
                                dayWiseProfit.put(profitAmount, detail);
                            } else {
                                dayWiseProfit.put(profitAmount, profitableDays);
                            }
                            //System.out.println("Profit Details => " + "(" + profitableDays + " Profit:" + profitAmount + ")");
                        }
                    }
                }
            }
        } else {
            System.out.print("There is just one entry present in the list");
        }

        // TreeMap to store values of HashMap
        TreeMap<Double, String> sorted = new TreeMap<>();

        // Copy all data from hashMap into TreeMap
        sorted.putAll(dayWiseProfit);

        //System.out.println();

        // Display the TreeMap which is naturally sorted
        for (Map.Entry<Double, String> entry : sorted.entrySet()) {
            System.out.println("Profit Amount = " + entry.getKey() + ", Profitable Days are = " + entry.getValue());
            break;
        }

       /* // Alternative way
        Map<Double, String> result2 = new LinkedHashMap<>();
        dayWiseProfit.entrySet().stream().sorted(Map.Entry.<Double, String>comparingByValue()).forEachOrdered(x -> result2.put(x.getKey(), x.getValue()));
        System.out.println();
        System.out.println(result2);*/
    }

    public static void printMinLoss(int arraySize, double[] prices, int qty, double brokeragePercent) {

        Map<Double, String> dayWiseProfit = new HashMap<>();

        if (prices.length > 1) {
            for (int i = 0; i < prices.length - 1; i++) {
                // int j = i + 1;
                for (int j = (i + 1); j < prices.length; j++) {
                    if (prices[i] > prices[j]) {

                        int presentday = i + 1;
                        int nextDay = j + 1;
                        double priceOfPresentDay = prices[i] * qty;
                        double brokerageAmount = (brokeragePercent / 100) * priceOfPresentDay;
                        double costPriceTotal = priceOfPresentDay + brokerageAmount;

                        double sellingPrice = prices[j] * qty;
                        double brokerageSP = (brokeragePercent / 100) * sellingPrice;
                        double sellingPriceTotal = sellingPrice - brokerageSP;
                        if (costPriceTotal > sellingPriceTotal) {
                            double lossAmount = (costPriceTotal - sellingPriceTotal);
                            String lossableDays = "Day" + presentday + "," + " Day" + nextDay;
                            if (dayWiseProfit.containsKey(lossAmount)) {
                                String detail = dayWiseProfit.get(lossAmount);
                                detail = detail + " || " + lossableDays;
                                dayWiseProfit.remove(lossAmount);
                                dayWiseProfit.put(lossAmount, detail);
                            } else {
                                dayWiseProfit.put(lossAmount, lossableDays);
                            }
                            //System.out.println("Loss Details => " + "(" + lossableDays + " Loss:" + lossAmount + ")");
                        }
                    }
                }
            }
        } else {
            System.out.print("There is just one entry present in the list");
        }

        // TreeMap to store values of HashMap
        // Use reverseOrder() method in the constructor
        TreeMap<Double, String> sorted = new TreeMap<>(); //Collections.reverseOrder()

        // Copy all data from hashMap into TreeMap
        sorted.putAll(dayWiseProfit);

        System.out.println();

        // Display the TreeMap which is naturally sorted
        for (Map.Entry<Double, String> entry : sorted.entrySet()) {
            System.out.println("Loss Amount = " + entry.getKey() + ", Lossable Days are = " + entry.getValue());
            break;
        }

       /* // Alternative way
        Map<Double, String> reverseMap = new LinkedHashMap<>();
        dayWiseProfit.entrySet().stream().sorted(Map.Entry.<Double, String>comparingByValue().reversed()).forEachOrdered(x -> reverseMap.put(x.getKey(), x.getValue()));
        System.out.println();
        System.out.println(reverseMap);*/
    }
}
