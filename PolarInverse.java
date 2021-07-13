import java.util.Arrays;

public class PolarInverse {

    public static void main(String[] args) {

        //Real part>0 imaginaryPart > 0
        ComplexNumber complexRectangular = new ComplexNumber(20,50);


        double[] polarCoardinates = complexRectangular.invert(complexRectangular);

        System.out.println(Arrays.toString(polarCoardinates));

    }
    public static ComplexNumber[][] addComplexMatrix(ComplexNumber[][] complexMatrixA, int complexMatrixA_rows,
                                                     int complexMatrixA_cols, ComplexNumber[][] complexMatrixB, int complexMatrixB_rows, int complexMatrixB_col)
            throws MatrixDimensionsError {

        ComplexNumber sum[][] = new ComplexNumber[complexMatrixA.length][complexMatrixA[0].length];
        if (complexMatrixA.length == complexMatrixB.length && complexMatrixA[0].length == complexMatrixB[0].length) {
            for (int i = 0; i < complexMatrixA.length; i++) {
                for (int j = 0; j < complexMatrixA[0].length; j++) {
                    ComplexNumber complexi = complexMatrixA[i][j];
                    ComplexNumber complexj = complexMatrixB[i][j];
                    sum[i][j] = complexi.addNumber(complexj);
                }
            }
        } else {
            throw new MatrixDimensionsError("Rows and Col not equal");
        }
        return sum;

    }

}

class MatrixDimensionsError extends Exception {
    public MatrixDimensionsError(String s) {
        // Call constructor of parent Exception
        super(s);
    }
}

class ComplexNumber {

    double realNo = 0d;
    double imgNo = 0d;

    ComplexNumber(double realNo, double imgNo) {
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
    public void printMethod(ComplexNumber complex) {
        System.out.println(String.valueOf(complex.getRealNo()) + "+" + String.valueOf(complex.getImgNo()) + "i");
    }

    public ComplexNumber addNumber(ComplexNumber c) {
        double realAddResult = c.getRealNo() + this.realNo;
        double imgResult = c.getImgNo() + this.imgNo;
        return new ComplexNumber(realAddResult, imgResult);
    }

    public ComplexNumber multiNumber(ComplexNumber c) {
        double real = this.realNo * c.realNo - this.imgNo * c.getImgNo();
        double img = this.realNo * c.getImgNo() + this.getImgNo() * c.realNo;
        return new ComplexNumber(real, img);

    }

    public double[] invert(ComplexNumber c) {
        double realAddResult = c.getRealNo();
        double imgResult = c.getImgNo();
        double square = (realAddResult*realAddResult) +  (imgResult*imgResult);


        double[] polarCoardinates = {Math.sqrt(square), Math.atan(imgResult/realAddResult)};
        double a[] = new double[2];
        double polarCoardinates1 =  realAddResult/square;

        double polarCoardinates2 = -imgResult/square;
        a[0] = polarCoardinates1;
        a[1] = polarCoardinates2;
        return a;
    }
}
