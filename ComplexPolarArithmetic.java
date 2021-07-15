import java.util.Arrays;

public class ComplexPolarArithmetic {

    public static void main(String[] args) {

        Complex_Number complexRectangularA = new Complex_Number(3,4);
        Complex_Number complexRectangularB = new Complex_Number(10,11);

        double[] polarMultiply = complexRectangularA.polarMultiply(complexRectangularB);
        double[] polarDivide = complexRectangularA.polarDivide(complexRectangularB);

        System.out.println("Multiplication: "+Arrays.toString(polarMultiply));

        System.out.println("Division: "+Arrays.toString(polarDivide));

    }

    public static Complex_Number[][] addComplexMatrix(Complex_Number[][] complexMatrixA, int complexMatrixA_rows,
                                                      int complexMatrixA_cols, Complex_Number[][] complexMatrixB, int complexMatrixB_rows, int complexMatrixB_col)
            {

        Complex_Number sum[][] = new Complex_Number[complexMatrixA.length][complexMatrixA[0].length];
        if (complexMatrixA.length == complexMatrixB.length && complexMatrixA[0].length == complexMatrixB[0].length) {
            for (int i = 0; i < complexMatrixA.length; i++) {
                for (int j = 0; j < complexMatrixA[0].length; j++) {
                    Complex_Number complexi = complexMatrixA[i][j];
                    Complex_Number complexj = complexMatrixB[i][j];
                    sum[i][j] = complexi.addNumber(complexj);
                }
            }
        } else {
            //throw new MatrixDimensionsError("Rows and Col not equal");
        }
        return sum;

    }

}


class Complex_Number {

    double realNo = 0d;
    double imgNo = 0d;

    Complex_Number(double real)
    {
        this(real, 0);
    }

    Complex_Number(double realNo, double imgNo) {
        this.realNo = realNo;
        this.imgNo = imgNo;
    }

    public double[] toPolar(Complex_Number c) {
        double realAddResult = c.getRealNo();
        double imgResult = c.getImgNo();
        double square = (realAddResult*realAddResult) +  (imgResult*imgResult);
        double[] polarCoardinates = {Math.sqrt(square), Math.atan(imgResult/realAddResult)};

        return polarCoardinates;
    }

    public double[] convertToPolar(Complex_Number c) {

        if (c.imgNo==0) {
            double[] y1 = {Double.valueOf(c.realNo)};
            return y1;
        }

        double realAddResult = c.getRealNo();
        double imgResult = c.getImgNo();
        double square = (realAddResult*realAddResult) +  (imgResult*imgResult);
        double[] polarCoardinates = {Math.sqrt(square), Math.atan2(imgResult,realAddResult)};

        return polarCoardinates;
    }



    public double[] polarDivide(Complex_Number b) {
        // TODO Auto-generated method stub
        double c = b.realNo;
        double d = b.imgNo;

        // check for Real,Imaginary(z) == 0 to avoid +/-infinity in result
        double zreal2 = 0.0;
        if (c != 0.0)
        {
            zreal2 = StrictMath.pow(c, 2.);
        }
        double zimag2 = 0.0;
        if (d != 0.0)
        {
            zimag2 = StrictMath.pow(d, 2.);
        }

        double ac = this.realNo*c;
        double bd = this.imgNo*d;
        double bc = this.imgNo*c;
        double ad = this.realNo*d;
        //return toPolar(new Complex_Number((ac+bd)/(zreal2+zimag2),(bc-ad)/(zreal2+zimag2)));
        return convertToPolar(new Complex_Number((ac+bd)/(zreal2+zimag2),(bc-ad)/(zreal2+zimag2)));
    }

    public double[] polarMultiply(Complex_Number b) {

        // if both Im(this) and Im(z) are zero use double arithmetic
        double real = this.realNo * b.realNo - this.imgNo * b.imgNo;
        double img = this.realNo * b.imgNo + this.imgNo * b.realNo;
        //return toPolar(new Complex_Number(real, img));
        return convertToPolar(new Complex_Number(real, img));
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
    public void printMethod(Complex_Number complex) {
        System.out.println(String.valueOf(complex.getRealNo()) + "+" + String.valueOf(complex.getImgNo()) + "i");
    }

    public Complex_Number addNumber(Complex_Number c) {
        double realAddResult = c.getRealNo() + this.realNo;
        double imgResult = c.getImgNo() + this.imgNo;
        return new Complex_Number(realAddResult, imgResult);
    }

	    /*public double[] invert(Complex_Number c) {
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
	    }*/


}


