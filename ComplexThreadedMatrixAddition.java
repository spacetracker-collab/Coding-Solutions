import java.util.Arrays;

class ComplexNumberNd {

    double realNo = 0d;
    double imgNo = 0d;

    ComplexNumberNd(double realNo, double imgNo) {
        this.realNo = realNo;
        this.imgNo = imgNo;
    }

    public double getRealNo() {
        return realNo;
    }

    public void setRealNo(double realNo) {
        this.realNo = realNo;
    }

    public double getImgNo() {
        return imgNo;
    }

    public void setImgNo(double imgNo) {
        this.imgNo = imgNo;
    }

    // 3+2i


    public ComplexNumberNd addNumber(ComplexNumberNd c) {
        double realAddResult = c.getRealNo() + this.realNo;
        double imgResult = c.getImgNo() + this.imgNo;
        return new ComplexNumberNd(realAddResult, imgResult);
    }

    public ComplexNumberNd multiNumber(ComplexNumberNd c) {
        double real = this.realNo * c.realNo - this.imgNo * c.getImgNo();
        double img = this.realNo * c.getImgNo() + this.getImgNo() * c.realNo;
        return new ComplexNumberNd(real, img);

    }

}

class T1 implements Runnable
{
    public ComplexNumberNd[] compRowMatrixA;
    public ComplexNumberNd[] compRowMatrixB;
    public ComplexNumberNd[] compRowMatrixSum;

    @Override
    public void run() {
        this.compRowMatrixSum=ComplexThreadedMatrixAddition.addRowOfMatrix(compRowMatrixA, compRowMatrixB);
    }


}

public class ComplexThreadedMatrixAddition {

    public static void printMethod(ComplexNumberNd complex) {
        System.out.print(String.valueOf(complex.getRealNo()) + "+" + String.valueOf(complex.getImgNo()) + "i ");
    }

    public static void printMatrix(ComplexNumberNd[][] complexMatrix)
    {
        for (int i = 0; i < complexMatrix.length; i++) {
            ComplexNumberNd complexsum=null;
            for (int j = 0; j < complexMatrix[0].length; j++) {
                complexsum = complexMatrix[i][j];
                printMethod(complexsum);
            }
            System.out.println();

        }


    }
    public static int i = 0;
    public static ComplexNumberNd[] addRowOfMatrix(ComplexNumberNd[] compRowMatrixA,ComplexNumberNd[] compRowMatrixB)
    {
        System.out.println(i);
        i++;

        ComplexNumberNd[] compRowMatrixSum= new ComplexNumberNd[compRowMatrixA.length];
        for (int i = 0; i < compRowMatrixA.length; i++) {
            compRowMatrixSum[i]=compRowMatrixA[i].addNumber(compRowMatrixB[i]);
            printMethod(compRowMatrixSum[i]);
        }
        return compRowMatrixSum;
    }

    public static void main(String[] args) throws InterruptedException {
        ComplexNumberNd complexMulti1 = new ComplexNumberNd(1, 2);
        ComplexNumberNd complexMulti2 = new ComplexNumberNd(2, 3);
        ComplexNumberNd complexMulti3 = new ComplexNumberNd(3, 4);
        ComplexNumberNd[][] summatrix = new ComplexNumberNd[3][3];

        ComplexNumberNd[][] complexMatrixA = { { complexMulti1, complexMulti2,complexMulti2 } ,
                { complexMulti1, complexMulti2,complexMulti3 },{ complexMulti1, complexMulti2,complexMulti3 }};

        //printMatrix(complexMatrixA);
       // System.out.println();

        //row 1 : 5+7i 7+9i 8+10i
        //row 2 : 5+7i 7+9i 9+11i
        //row 3: 5+7i 7+9i 9+11i

        ComplexNumberNd complexMulti4 = new ComplexNumberNd(4, 5);
        ComplexNumberNd complexMulti5 = new ComplexNumberNd(5, 6);
        ComplexNumberNd complexMulti6 = new ComplexNumberNd(6, 7);

        ComplexNumberNd[][] complexMatrixB= {{complexMulti4,complexMulti5,complexMulti6},
                {complexMulti4,complexMulti5,complexMulti6},{complexMulti4,complexMulti5,complexMulti6}};

        //printMatrix(complexMatrixB);
        //System.out.println();



  /*  ComplexNumberNd[] compRowMatrixSum=addRowOfMatrix(complexMatrixA[0], complexMatrixB[0]);
    summatrix[0]=compRowMatrixSum;
    compRowMatrixSum=addRowOfMatrix(complexMatrixA[1], complexMatrixB[1]);
    summatrix[1]=compRowMatrixSum;
    compRowMatrixSum=addRowOfMatrix(complexMatrixA[2], complexMatrixB[2]);
    summatrix[2]=compRowMatrixSum;
    printMatrix(summatrix);*/


        for (int i = 0; i < complexMatrixA.length; i++) {
            T1 t1=new T1();
            t1.compRowMatrixA=complexMatrixA[i];
            t1.compRowMatrixB=complexMatrixB[i];
            Thread tr1=new Thread(t1);
            tr1.start();
            tr1.join();
            summatrix[i]=t1.compRowMatrixSum;
            System.out.println();
        }
        //printMatrix(summatrix);


    }
}
