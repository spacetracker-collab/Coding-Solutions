package dee;

public class ComplexNumberArithmeticMatrixMultiplication {

    public static void main(String[] args) {

        // 1+2i
        // 2+3i
        // total 3+5i
        ComplexNumber complex1 = new ComplexNumber(3, 5);
        ComplexNumber complex2 = new ComplexNumber(2, 3);
        ComplexNumber complex3 = complex1.addNumber(complex2);
        complex1.printMethod(complex3);

        ComplexNumber complexMulti1 = new ComplexNumber(1, 2);
        ComplexNumber complexMulti2 = new ComplexNumber(3, 2);
        ComplexNumber complexMulti3 = complexMulti1.multiNumber(complexMulti2);
        complex1.printMethod(complexMulti3);

        ComplexNumber[][] complexMatrixA = { { complexMulti1, complexMulti1} ,
                { complexMulti1, complexMulti1}};
       
        ComplexNumber[][] complexMatrixB = { { complexMulti2, complexMulti2 }, 
        		{ complexMulti2, complexMulti2 }};
        

        ComplexNumber sum[][];
        ComplexNumber multi[][];
        try {
            sum = addComplexMatrix(complexMatrixA, complexMatrixA.length, complexMatrixA[0].length, complexMatrixB,
                    complexMatrixB.length, complexMatrixB[0].length);
            
          
            //verify : https://adrianstoll.com/linear-algebra/matrix-product.html
            multi = multiplyComplexMatrix(complexMatrixA, complexMatrixA.length, complexMatrixA[0].length, complexMatrixB,
                    complexMatrixB.length, complexMatrixB[0].length);
            


            // Displaying the result
            System.out.println("Sum of two matrices is: ");
            for (int i = 0; i < sum.length; i++) {
                for (int j = 0; j < sum[0].length; j++) {
                    ComplexNumber complexsum = sum[i][j];
                    complexsum.printMethod(complexsum);
                    // System.out.print(" ");
                }
                System.out.println();
            }
            
            // Displaying the result
            System.out.println("Multiplication of two matrices is: ");
            for (int i = 0; i < multi.length; i++) {	
                for (int j = 0; j < multi[0].length; j++) {
                    ComplexNumber complexsum1 = multi[i][j];
                    complexsum1.printMethod(complexsum1);
                    // System.out.print(" ");
                }
                System.out.println();
            }
            

        } catch (MatrixDimensionsError e) {
            System.out.println("Dimensions not same");
        }

    }
    
    public static ComplexNumber[][] multiplyComplexMatrix(ComplexNumber[][] complexMatrixA, int complexMatrixA_rows,
            int complexMatrixA_cols, ComplexNumber[][] complexMatrixB, int complexMatrixB_rows, int complexMatrixB_col) throws MatrixDimensionsError
    {
    	
    	double re = 0.0;
        double im = 0.0;
        ComplexNumber mul1 = new ComplexNumber(re, im);
         
    	   ComplexNumber mul[][] = new ComplexNumber[complexMatrixA.length][complexMatrixA[0].length];
			
        int count = 1;
		if (complexMatrixA.length == complexMatrixB.length && complexMatrixA[0].length == complexMatrixB[0].length) {
			for (int i = 0; i < complexMatrixA.length; i++) {
				for (int j = 0; j < complexMatrixA.length; j++) {
					for (int k = 0; k < complexMatrixB.length; k++) {
						ComplexNumber complexi = complexMatrixA[i][k];
						ComplexNumber complexj = complexMatrixB[k][j];

						if (count == 1) {
							mul[i][j] = mul1;
							count++;
						}
						mul[i][j] = mul[i][j].addNumber(complexi.multiNumber(complexj));

					}
					count = 1;
				}
			}
		} else {
               throw new MatrixDimensionsError("Rows and Col not equal");
           }
          
		return mul;
    	
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

}
