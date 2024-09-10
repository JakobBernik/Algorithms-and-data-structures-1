
import java.util.Scanner;

public class Naloga1 {  //SKLADISCE 
	
   static int stTrakov,dolTraku,velikostPaketa;
   
   static void premakniGor(int trenutniTrak,String[][] skladisce){
	 //System.out.println("AND ANY TIME YOU FEEL THE PAIN");
	   if(dolTraku == 1){
	    skladisce[trenutniTrak][0]=null;	   
	   }else{
	     int i = dolTraku-1;
	     while(true){
	      skladisce[trenutniTrak][i] = skladisce[trenutniTrak][i-1];
	      i--;
	      if(i==0){
	       skladisce[trenutniTrak][i] = null;
	       break;
	      }
	    }
	   }
   }
   
    static void premakniDol(int trenutniTrak,String[][] skladisce){
    	//System.out.println("DONT CARRY THE WORLD UPON YOUR SHOULDER");
    	if(dolTraku == 1){
    	 skladisce[trenutniTrak][0] = null;	
    	}else{
    	 int i = 0;
  	     while(true){
  	      skladisce[trenutniTrak][i] = skladisce[trenutniTrak][i+1];
  	      i++;
  	      if(i == dolTraku-1){
  	       skladisce[trenutniTrak][i] = null;
  	       break;
  	      }
  	     }
    	}
   }
    
    static String[] uredi(String[][] skladisce){
    	   String[] urejen = new String[stTrakov];
    	   int currentLast;
    	   int prazniVsi,prazniVmes;
    	        for(int i = 1;i < stTrakov+1;i++){
    		     currentLast = -1;
    		     prazniVsi = 0;
    		     prazniVmes = 0;
    		     for(int j=0;j < dolTraku;j++ ){
    			   if(skladisce[i][j] == null){
    				  skladisce[i][j] = "";
    				  prazniVmes++;
    			   }else{
    				currentLast = j;
    				prazniVsi += prazniVmes;
    				prazniVmes = 0;
    			   }
    		     }   
                   if(currentLast == -1){
                	//System.out.println("FOR WELL YOU KNOW THAT IT'S A FOOL");
            	    urejen[i-1] = "";
                   }else if(currentLast == dolTraku-1){
                	//System.out.println("WHO PLAYS IT COOL");
                	urejen[i-1] = String.join(",", skladisce[i]); 
                   }
                   else{
                	//System.out.println("BY MAKING HIS WORLD A LITTLE COLDER"); 
                	urejen[i-1] = String.join(",", skladisce[i]).substring(0,(currentLast-prazniVsi)*velikostPaketa + velikostPaketa + currentLast);
                   }
                }
     return urejen;
    }
	
 public static void main(String[] args) {
		
     Scanner scan = new Scanner(System.in);
     int seznamPolozaj = 0,trenutniTrak = 0;
     stTrakov = scan.nextInt();
     dolTraku = scan.nextInt();
     String[][] skladisce = new String[stTrakov+1][dolTraku]; // +1 je za zacetno polje
     scan.nextLine(); // zaradi /n
     String paketi = scan.nextLine();
     String[] seznamPaketov = paketi.split(","); // prebrani string paketov shranimo v array
     velikostPaketa = seznamPaketov[0].length();
     //System.out.println(velikostPaketa);
     String vozicek = "PRAZEN";
     /* int i = SeznamPaketov.length;
     while(i > 0){
    	 System.out.println(SeznamPaketov[SeznamPolozaj]);
         SeznamPolozaj++;
         i--;
     } */
     while(scan.hasNext()){
    	 String ukaz = scan.next();
    	 switch(ukaz){
    	   case "PREMIK":
    		//System.out.println("HEY JUDE,DONT BE AFRAID");
    		int premik = scan.nextInt(); // preberemo se index zelenega traku
    		trenutniTrak = premik; //premaknemo se do dolocenega traku 
    		break; 
    	   case "NALOZI":
    		//System.out.println("YOU WERE MADE TO GO OUT AND GET HER");
    		if(vozicek=="PRAZEN"){ //ce je vozicek prazen
    		  if(trenutniTrak == 0){ //ce nalagamo iz osnovnega panoja
    	         if(seznamPolozaj < seznamPaketov.length){
        		 vozicek = seznamPaketov[seznamPolozaj];
                 seznamPolozaj++;
    	         }  
    	      }else if(skladisce[trenutniTrak][0] != null){ // ce je na mestu prevzema sploh paket
    	        vozicek = skladisce[trenutniTrak][0];
    	        skladisce[trenutniTrak][0] = null;
    	      }
    		}
    		break; 
    	   case "ODLOZI":
    		//System.out.println("THE MINUTE YOU LET HER UNDER YOUR SKIN");
    		if(vozicek != "PRAZEN" && trenutniTrak != 0){ //ce sploh kaj lahko odlozimo, in nismo na zacetku
    		  if(skladisce[trenutniTrak][0] == null){ // ce je mesto odlaganja prosto
    		     skladisce[trenutniTrak][0] = vozicek;
    		     vozicek = "PRAZEN";
    		  }  
    		}   
    		break;
    	   case "GOR":
    		//System.out.println("THEN YOU BEGIN TO MAKE IT BETTER"); 
    		premakniGor(trenutniTrak,skladisce); //premik traku gor
    		break;
    	   case "DOL":
    		//System.out.println("HEY JUDE, REFRAIN");
    	 	premakniDol(trenutniTrak,skladisce); //premik traku dol
            break;
           }   	
     }
     scan.close();
     String[] urejeni = uredi(skladisce); 
     for(int i = 1;i < stTrakov+1;i++){
    	   //System.out.println("NA NA NA NAA-NAA"); 
           System.out.println(i+":"+ urejeni[i-1]);   	   
     }
 }
}