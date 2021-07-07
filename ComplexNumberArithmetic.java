public class ComplexNumberArithmetic {

    public static void main(String[] args) {


        // 1+2i
        // 2+3i
        //total 3+5i
        ComplexNumber complex1= new ComplexNumber(3,5);
        ComplexNumber complex2= new ComplexNumber(2,3);
        ComplexNumber complex3=complex1.addNumber(complex2);
        complex1.printMethod(complex3);

        ComplexNumber complexMulti1= new ComplexNumber(3,5);
        ComplexNumber complexMulti2= new ComplexNumber(2,3);
        ComplexNumber complexMulti3=complexMulti1.multiNumber(complexMulti2);
        complex1.printMethod(complexMulti3);
    }

}
class ComplexNumber{

    double realNo=0d;
    double imgNo =0d;

    ComplexNumber(double realNo, double imgNo){
        this.realNo= realNo;
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
        System.out.println(String.valueOf(complex.getRealNo())+"+"+String.valueOf(complex.getImgNo())+"i");
    }

    public ComplexNumber addNumber(ComplexNumber c) {
        double realAddResult =	c.getRealNo()+this.realNo;
        double imgResult = c.getImgNo()+this.imgNo;
        return new ComplexNumber(realAddResult,imgResult);
    }

    public ComplexNumber multiNumber(ComplexNumber c) {
        double real = this.realNo * c.realNo - this.imgNo * c.getImgNo();
        double img = this.realNo*c.getImgNo() + this.getImgNo()*c.realNo;
        return new ComplexNumber(real,img);

    }

}
