public class HelloWorld{

     public static void main(String []args){
        int matrix[][]={{1,2,3},{4,5,6},{7,8,9},{10,11,12}};
     int row=4;
     int column=3;
     snakePrint(matrix,row,column);
     }
     
     public static void snakePrint(int matrix[][],int row,int column)
     {
         
         for(int i=0;i<matrix.length;i++)
         {
             
             if(i%2==0)
             {
                 for(int j=0;j<matrix[0].length;j++)
                 {
                     System.out.println(matrix[i][j] + " ");
                 }
             }
             else
             {
                 
                 for(int j=matrix[0].length-1;j>=0;j--)
                 {
                     System.out.println(matrix[i][j] + " ");
                 }
             }
         }
     }
     
}
