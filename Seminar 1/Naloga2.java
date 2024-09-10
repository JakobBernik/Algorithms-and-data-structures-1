import java.util.Scanner;
import java.io.*;
public class Naloga2 { //OPTIMIZACIJA SKLADISCA
	public static int stTrakov,dolzinaTraku;
	
	 static void premakniGor(int trenutniTrak,String[][] skladisce){
		   if(dolzinaTraku == 1){
		    skladisce[trenutniTrak][0]=null;	   
		   }else{
		     int i = dolzinaTraku-1;
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
	    	if(dolzinaTraku == 1){
	    	 skladisce[trenutniTrak][0] = null;	
	    	}else{
	    	 int i = 0;
	  	     while(true){
	  	      skladisce[trenutniTrak][i] = skladisce[trenutniTrak][i+1];
	  	      i++;
	  	      if(i == dolzinaTraku-1){
	  	       skladisce[trenutniTrak][i] = null;
	  	       break;
	  	      }
	  	     }
	    	}
	   }
	    
	    
	public static void PreberiKonfiguracijo(String[][] skladisce,int stTrakov,int dolzinaTraku,Scanner scan){
		  for(int i =1;i<stTrakov+1;i++){
	           String vrstica = scan.nextLine();
	           vrstica = vrstica.substring(2);
	           String[] vrs = vrstica.split(",");
	           for(int j = 0;j<dolzinaTraku;j++){
	        	   if(j<vrs.length){
	        		  if(vrs[j].equals("")){
	        			skladisce[i][j] = null;  
	        		  }else{
	        			 skladisce[i][j] = vrs[j]; 
	        		  } 
	        	   }else{
	        		   skladisce[i][j] = null;
	        	   }
	           }
	        }
	}
	
	public static boolean Primerjaj(String[][] s1,String[][] s2){
		boolean veljavno = true;
		for(int i=1;i<stTrakov+1;i++){
			for(int j=0;j<dolzinaTraku;j++){
				if(s1[i][j] != null && s2[i][j] != null){
					if(!s1[i][j].equals(s2[i][j])){
						veljavno = false;
					    break;
					}
				}else if(s1[i][j]==null && s2[i][j] == null){
				}else{
				  veljavno = false;
				  break;
				}
			}
			if(!veljavno){
			 break;
			}
		}
		
		return veljavno;
	}
	
	public static String[][] novaInstanca(String[][] skladisce){
		String[][] sk = new String[stTrakov+1][dolzinaTraku];
		for(int i=1;i<stTrakov+1;i++){
		System.arraycopy(skladisce[i], 0, sk[i], 0, dolzinaTraku);	
		}
		return sk;
	}
	
	public static void Komadi(String[][] skladisce){
		
	}
	
	public static boolean koncnaFormacija(String k,zbirka koma){
		while(koma!=null){
			if(k.equals(koma.komad)){
			return true;	
			}
			koma = koma.next;
		}
		return false;
	}
	public static zbirka nastavi(String[][] skl){ // na zacetku v zbirko zapisemo elemente,ki so v koncnem stanju prisotni
		zbirka z = new zbirka();
		zbirka root = z;
		for(int i = 1;i < stTrakov+1;i++){
			for(int j = 0;j < dolzinaTraku;j++){
				if(skl[i][j]!= null){
					if(z.komad == null){
			         z.komad = skl[i][j];
					}else{
						zbirka tmp = root;
						 while(true){
						 if(skl[i][j].equals(tmp.komad)){
							break;
						 }
						 z = tmp;
						 tmp = tmp.next;
						  if(tmp == null){
							zbirka nz = new zbirka();
							nz.komad = skl[i][j];
							z.next = nz;
							break;
						  }
						}
					}
				}
			}
		}
		return root;
	}
	
	public static int[] kolTr(String[][] skl){
		int[] k = new int[stTrakov+1];
		     for(int i = 0;i < stTrakov+1;i++){
		    	 int index = 1;
		    	 for(int j = 0;j < dolzinaTraku;j++){
		    		 if(skl[i][j]!= null){
		    			index = 0;
		    			break;
		    		 }
		    	 }
		    	 k[i] = index;
		     }
		return k;
	}
	
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		stTrakov = scan.nextInt();
		dolzinaTraku = scan.nextInt();
        String[][] SkladisceTrenutno = new String[stTrakov+1][dolzinaTraku];
        String[][] SkladisceNovo = new String[stTrakov+1][dolzinaTraku];
        scan.nextLine();
        PreberiKonfiguracijo(SkladisceTrenutno,stTrakov,dolzinaTraku,scan);
        PreberiKonfiguracijo(SkladisceNovo,stTrakov,dolzinaTraku,scan);
        scan.close();
        zbirka koncni = nastavi(SkladisceNovo);
        int[] prazni = kolTr(SkladisceTrenutno);
        Queue2 vrsta = new Queue2();
        state TrenutnoStanje = new state("",SkladisceTrenutno,0,"Prazen",0);
        vrsta.enqueue(TrenutnoStanje);
       while(!vrsta.empty()){
    	   QueueElement2 el = (QueueElement2) vrsta.front();
    	   TrenutnoStanje = (state) el.element;
    	   vrsta.dequeue();
    	   SkladisceTrenutno = TrenutnoStanje.Skladisce;
    	   int trTrak = TrenutnoStanje.trTrak;
    	   int PrUkaz = TrenutnoStanje.prejsnjiUkaz;
    	   String voz = TrenutnoStanje.vozicek;
    	   String ukaz = TrenutnoStanje.ukazi;
    	   if(Primerjaj(SkladisceTrenutno,SkladisceNovo)){// zakljucek
    		 break;  
    	   }else{
    		if(trTrak == 0){ // premik z traku nic na vse ostale trakove
    		    for(int i = 1;i<stTrakov+1;i++){
    		    	if(prazni[i] == 0){
    		    	String[][] Nov = novaInstanca(SkladisceTrenutno);
    		    	state Stanje = new state(ukaz+"PREMIK "+i+",",Nov,i,"Prazen",0);
    		        vrsta.enqueue(Stanje);
    		    	}
    		    }
    		}
    		else{
    			//za premike
    			if(PrUkaz!= 0){ // ko prejsni ukaz ni premik
    			  for(int i = 1;i<stTrakov+1;i++){ 
    				if(i!=trTrak){    // premik na vse trakove, razen trenutnega
     		    	 String[][] Nov = novaInstanca(SkladisceTrenutno);
     		    	 state Stanje = new state(ukaz+"PREMIK "+i+",",Nov,i,voz,0);
     		         vrsta.enqueue(Stanje);
    				}
     		     }
    			}
    			// za gor
    			String[][] Nov1 = novaInstanca(SkladisceTrenutno);  
    			if(Nov1[trTrak][dolzinaTraku-1] == null && PrUkaz!= 2){ // ne izbrisemo nobenega elementa,dol v naslednji izvedbi je zato neuporaben
    			  premakniGor(trTrak,Nov1);
    			  state Stanje1 = new state(ukaz+"GOR,",Nov1,trTrak,voz,1);
    			  vrsta.enqueue(Stanje1);
    			}else if(Nov1[trTrak][dolzinaTraku-1] != null){ // premaknemo gor, a v tem primeru element zbrisemo. move ni useless
    				if(!koncnaFormacija(Nov1[trTrak][dolzinaTraku-1],koncni)){
    			     premakniGor(trTrak,Nov1);
    			     state Stanje1 = new state(ukaz+"GOR,",Nov1,trTrak,voz,5);
    			     vrsta.enqueue(Stanje1);
    				}
    			}
    			// za dol
    			String[][] Nov2 = novaInstanca(SkladisceTrenutno);
    			if(Nov2[trTrak][0] == null && PrUkaz!= 1){ // ne izbrisemo nobenega elementa,gor v naslednji izvedbi je zato neuporaben
      			  premakniDol(trTrak,Nov2);
      			  state Stanje2 = new state(ukaz+"DOL,",Nov2,trTrak,voz,2);
      			  vrsta.enqueue(Stanje2);
      			}else if(Nov2[trTrak][0] != null){ // premaknemo dol, a v tem primeru element zbrisemo. move ni useless
      				if(!koncnaFormacija(Nov2[trTrak][0],koncni)){
      				 premakniDol(trTrak,Nov2);
      			     state Stanje2 = new state(ukaz+"DOL,",Nov2,trTrak,voz,5);
      			     vrsta.enqueue(Stanje2);
      				}
      			}
    			// za nalozi
    			String[][] Nov3 = novaInstanca(SkladisceTrenutno);
    			if(voz.equals("Prazen") && Nov3[trTrak][0] != null && PrUkaz!=4 ){
    				if(koncnaFormacija(Nov3[trTrak][0],koncni)){
    			     String vozicek = Nov3[trTrak][0];
    			     Nov3[trTrak][0] = null;
    			     state Stanje3 = new state (ukaz+"NALOZI,",Nov3,trTrak,vozicek,3);
    			     vrsta.enqueue(Stanje3);
    			    }
    			}
    			// za odlozi
    			String[][] Nov4 = novaInstanca(SkladisceTrenutno);
    			if(!voz.equals("Prazen") && Nov3[trTrak][0] == null && PrUkaz!=3){
    			  String vozicek = voz;
      			  Nov4[trTrak][0] = vozicek;
      			  vozicek = "Prazen";
      			  state Stanje4 = new state (ukaz+"ODLOZI,",Nov4,trTrak,vozicek,4);
      			  vrsta.enqueue(Stanje4);
      			}	
    		}
    	   }
       }
       String[] Ukazi = TrenutnoStanje.ukazi.split(",");
       for(int i = 0;i< Ukazi.length;i++){
    	   System.out.println(Ukazi[i]);
       }
	}
	 public static void Izpisi(String[][] Skladisce){  
		  for(int i = 1;i<stTrakov+1;i++){
			System.out.print("trak"+i+": ");
			for(int j =0;j<dolzinaTraku;j++){
			if(Skladisce[i][j] == null){
			System.out.print(" ");	
			}else{
			System.out.print(Skladisce[i][j]);
			}
			}
			System.out.println();
			
		 }
     }
}
class zbirka{
	String komad;
	zbirka next;
	zbirka(){
	komad = null;
	next = null;
	}
}

 
class state{
	String ukazi = "";
	String[][] Skladisce;
	int trTrak;
	int prejsnjiUkaz;
	String vozicek;
	 state(String u,String[][] s,int tT,String v,int p){
	 ukazi += u;
	 Skladisce = s;
	 trTrak = tT;
	 vozicek = v;
	 prejsnjiUkaz = p;
	 }
}
//////////////////////////////////////////////////////////////////////////////////QUEUE /////////////////////////////////////////////////////////////
  class QueueElement2{
    Object element;
    QueueElement2 next;

      QueueElement2()
      {
       element = null;
       next = null;
      }
  }
  class Queue2{
  //QueueElement -> QueueElement -> QueueElement -> ... -> QueueElement
  //     ^                                                       ^
  //     |                                                       |  
  //    front                                                   rear
  //
  // nove elemente dodajamo na konec vrste (kazalec rear)
  // elemente brisemo na zacetku vrste (kazalec front)
   private QueueElement2 front;
   private QueueElement2 rear;

    public Queue2(){
     makenull();
    }
    public void makenull(){
     front = null;
     rear = null;
    }
    public boolean empty(){
     return (front == null);
    }
    public Object front(){
     // funkcija vrne zacetni element vrste (nanj kaze kazalec front).
     // Elementa NE ODSTRANIMO iz vrste!

     return this.front;
    }  
    public void enqueue(Object obj){
    // funkcija doda element na konec vrste (nanj kaze kazalec rear)
      QueueElement2 queueE = new QueueElement2();
      queueE.element = obj;
       if(rear== null){
        rear = queueE; 
       }else{
        rear.next = queueE;
        rear = queueE;
       }
       if(front == null){
        front = queueE;   
       }
    }
    public void dequeue(){
      // funkcija odstrani zacetni element vrste (nanj kaze kazalec front)
     front = front.next;
    }
  }

