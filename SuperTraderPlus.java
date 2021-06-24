public class SuperTraderPlus {

    public static void main(String[] args) {

        int arraySize = 6;
        double prices[] = { 3.0, 20.0, 2.0, 2.0, 2.0, 2.0 };
        int qty=1;
        double brokeragePercent=20;
        // boolean profitabledays = areThereProfitableDays(arraySize, prices);

        boolean profitabledaysWithBrokerages=  areThereProfitableDaysWithBrokerage(arraySize, prices,qty,brokeragePercent);

//        if (profitabledays == true) {
//            System.out.println("Profit Got");
//        } else {
//            System.out.println("Got loss");
//        }
//
        if (profitabledaysWithBrokerages == true) {
            System.out.println("Profit with brokerages Got");
        } else {
            System.out.println("Got loss with brokerages");
        }


    }
    public static boolean areThereProfitableDaysWithBrokerage(int arraySize, double[] prices, int qty, double brokeragePercent) {

        boolean profitDayFlag = false;

        if (prices.length > 1) {
            for (int i = 0; i < prices.length; i++) {

                for (int j = 1; j < prices.length; j++) {
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
                            break;
                        }
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
