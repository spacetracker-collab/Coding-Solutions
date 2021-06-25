public class SuperTraderPlusAverageProfit {

    public static void main(String[] args) {

        int arraySize = 6;
        double prices[] = { 10.0, 20.0, 30.0, 40.0, 50.0, 100.0 };
        int qty=100;
        double brokeragePercent=1;

        printAllProfitableTradesAndAverageProfit(arraySize, prices,qty,brokeragePercent);
    }
    public static void printAllProfitableTradesAndAverageProfit(int arraySize, double[] prices, int qty, double brokeragePercent) {

        double profitSum =0;
        int dayCount=0;

        boolean profitDayFlag =false;
        if (prices.length > 1) {
            for (int i = 0; i < prices.length; i++) {

                for (int j = (i+1); j < prices.length; j++) {
                    if (prices[i] < prices[j]) {
                        profitDayFlag  = true;

                        int presentday = i + 1;
                        int nextDay = j + 1;
                        double priceOfPresentDay = prices[i]*qty;
                        double brokerageAmount = (brokeragePercent/100)*priceOfPresentDay;
                        double  costPriceTotal = priceOfPresentDay+brokerageAmount;

                        double sellingPrice= prices[j]*qty;
                        double brokerageSP = (brokeragePercent/100)*sellingPrice;
                        double sellingPriceTotal = sellingPrice - brokerageSP;
                        if(sellingPriceTotal> costPriceTotal){
                            System.out.println("ProfitableCombination:" +"(Day" +presentday+","+" Day"+nextDay +" Profit:"+(sellingPriceTotal-costPriceTotal)+")");
                            System.out.println("Profit on Days:"+(++dayCount));
                            profitSum+=(sellingPriceTotal-costPriceTotal);
                        }
                    }

                }
                if (profitDayFlag == true) {
                    //break;
                }
            }
        } else {
            profitDayFlag = false;
        }
        System.out.println("ProfitSum:"+profitSum);
        System.out.println("TotalDays:"+dayCount);
        System.out.println("AverageProfit:"+profitSum/dayCount);


    }
    public static boolean areThereProfitableDaysWithBrokerage(int arraySize, double[] prices, int qty, double brokeragePercent) {

        boolean profitDayFlag = false;

        if (prices.length > 1) {
            for (int i = 0; i < prices.length; i++) {

                for (int j = (i+1); j < prices.length; j++) {
                    if (prices[i] < prices[j]) {
                        profitDayFlag = true;

                        int presentday = i + 1;
                        int nextDay = j + 1;
                        double priceOfPresentDay = prices[i]*qty;
                        double brokerageAmount = (brokeragePercent/100)*priceOfPresentDay;
                        double  costPriceTotal = priceOfPresentDay+brokerageAmount;

                        double sellingPrice= prices[j]*qty;
                        double brokerageSP = (brokeragePercent/100)*sellingPrice;
                        double sellingPriceTotal = sellingPrice - brokerageSP;
                        if(sellingPriceTotal> costPriceTotal){
                            System.out.println("Stock at day   " + presentday);
                            System.out.println("Cost Price per Stock "+prices[i]);
                            System.out.println("Stock Qty "+ qty);
                            System.out.println("Cost Price without Brokerage "+priceOfPresentDay);
                            System.out.println("Brokerage Percent  " + brokeragePercent);
                            System.out.println("Price with brokerages is " + costPriceTotal);
                            System.out.println("Bokerage Amount Cost Price :"+brokerageAmount);
                            System.out.println("Next day " + nextDay);
                            System.out.println("Selling Price per Stock "+prices[j]);
                            System.out.println("Selling Price without Brokerage "+sellingPrice);
                            System.out.println("Stock price with brokerages is " + sellingPriceTotal);
                            System.out.println("Total Profit " +(sellingPriceTotal-costPriceTotal) );
                            System.out.println("Brokerage AmountSelling Price :"+brokerageSP);
                            //break;
                        }
                    }

                }
                if (profitDayFlag == true) {
                    //break;
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
