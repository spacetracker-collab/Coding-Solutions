public class ComplexNumberAddition {
	
	public static void main(String[] args) {
		
		
		// 1+2i
		// 2+3i
		//total 3+5i
		ComplexNumber complex1= new ComplexNumber(1,2);
		ComplexNumber complex2= new ComplexNumber(2,3);
		ComplexNumber complex3=complex1.addNumber(complex2);
		complex1.printMethod(complex3);
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
}
