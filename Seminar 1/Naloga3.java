import java.util.Scanner;

public class Naloga3 { //VLAKI
     
 	protected static int maxTeza,stVagonov,trenutnoStVagonov; 
    protected static Vagon lokomotiva;
	protected static Vagon last;	
	 
	public static void odstraniLihe(){ //OK
		Vagon tmp = lokomotiva.next;
		int index = 0;
		while(tmp != null){
			if(index%2 != 0){	
			 if(tmp == last){
				last = tmp.previous; 
			 }
			  tmp.previous.next = tmp.next;
			  if(tmp.next != null){
			  tmp.next.previous = tmp.previous;	  
			  }
			  trenutnoStVagonov--;
			}
			index++;
			tmp = tmp.next;
		}
	}
	
	public static void odstraniHet(int n){ //OK
		Vagon tmp = lokomotiva.next;
		while(tmp != null){
		      if(tmp.stTovora >= n){
		    	  if(tmp == last){
						last = tmp.previous; 
					 }
			    tmp.previous.next = tmp.next;
			    if(tmp.next != null){
			     tmp.next.previous = tmp.previous;	  
			    }
			trenutnoStVagonov--;
		      }
		    tmp = tmp.next;
	    }
	}
	
	public static void odstraniZas(int P){ //OK
		Vagon tmp = lokomotiva.next;
		while(tmp != null){
			int cargo = tmp.skupnaTeza;
			if(100*cargo/tmp.maxT >= P){
				if(tmp == last){
					last = tmp.previous;
				}
				tmp.previous.next = tmp.next;
				if(tmp.next != null){
			    tmp.next.previous = tmp.previous;	  
				}
				trenutnoStVagonov--;
			}
			tmp = tmp.next;
		}
	}
	
	public static void obrni(){ //OK
		Vagon previous = lokomotiva.next;
		Vagon current = previous.next;
		Vagon naslednji = current.next;
		while(current != null){
			if(current == last){
				last = current.previous;
			}
			previous.next = naslednji;
			current.next = lokomotiva.next;
			current.previous = lokomotiva;
		    lokomotiva.next.previous = current;
		    lokomotiva.next = current;
			current = naslednji;
			if(naslednji != null){
			naslednji.previous = previous;
			naslednji = naslednji.next;
			}
		}
		 last = last.previous;
	}
	
	public static Vagon uredi(Vagon lkmtv){ //mergeSort algoritem O(n*log(n))
	      if(lkmtv == null || lkmtv.next == null){ // v primeru da je vlak prazen, ali da ima samo en vagon ne rabimo sortirat
	    	 return lkmtv; 
	      }
	      Vagon sredina = poisciSredino(lkmtv); //poisce sredinski vagon dane lkmtv
	      Vagon half = sredina.next; // naslednji element od sredinskega vzamemo kot zacetek nove liste 
	      sredina.next = null; // s tem ločimo linked list na 2 dela,kasneje bomo to združili nazaj
		  half.previous = null;
	      return merge(uredi(lkmtv),uredi(half));
	}
	public static Vagon merge(Vagon prvaPol,Vagon drugaPol){ // zdruzi manjsi kompoziciji vagonov a in b skupaj v eno
		Vagon tmp = null;
		Vagon zacetek = null;
		boolean prvi = true;
		  while(prvaPol != null && drugaPol != null){ // zdruzimo oba podVlaka
			  if(prvaPol.skupnaTeza <= drugaPol.skupnaTeza){ // <= velja zaradi stabilnega urejanje
				  if(prvi){
					tmp = prvaPol;
					zacetek = tmp;
					prvi = false;
				  }else{
					  tmp.next = prvaPol;
					  prvaPol.previous = tmp;
					  tmp = tmp.next;
				  }  
				  prvaPol = prvaPol.next;
				  
			  }else{
				 
				  if(prvi){
				  tmp = drugaPol;
				  zacetek = tmp;
				  prvi = false;
				  }else{
					  tmp.next = drugaPol;
					  drugaPol.previous = tmp; 
					  tmp = tmp.next;
				  } 
				  drugaPol = drugaPol.next;  
			  
			  }
			  
		  }
		  if(prvaPol == null){ // potrebno za dodajanje zadnjega elementa(največjega) iz obeh podVlakov
			 if(zacetek == null){
				zacetek = drugaPol;
			 }else{
				tmp.next = drugaPol;
				drugaPol.previous = tmp;
			 } 
			 
			 
		  }else{
			  if(zacetek == null){
				zacetek = prvaPol;  
			  }else{
				  tmp.next = prvaPol;
				  prvaPol.previous = tmp;
			  }
			  
		  }
		 return zacetek; 
	}
	
	public static Vagon poisciSredino(Vagon lkmtv){ //poisce sredinski vagon danega vlaka in ga vrne;
		Vagon sredina = lkmtv;
		Vagon skakac = lkmtv.next;
		     while(skakac != null && skakac.next != null){
		    	 sredina = sredina.next;
		    	 skakac = skakac.next.next;
		     }   
		
		return sredina;
	}
	
	public static void premakni(String tip,int from,int to){ // glede na indexa from in to izbere s katere strani vlaka bo pot krajsa
		if(from != to && 0 <= from && from < trenutnoStVagonov && 0 <= to && to < trenutnoStVagonov){
			  if(from <= trenutnoStVagonov/2 && to <= trenutnoStVagonov/2){
			 premik(tip,from,to,lokomotiva.next,1,0); 
		  }else{
			  if(from < to){
				  if(from > trenutnoStVagonov - to){
					 premik(tip,from,to,lokomotiva.next,1,0); 
				  }else{
					  premik(tip,from,to,last.next,0,trenutnoStVagonov-1);
				  }  
			  }else{
				  if(to > trenutnoStVagonov - from){
					  premik(tip,from,to,lokomotiva.next,1,0);
				  }else{
					  premik(tip,from,to,last.next,0,trenutnoStVagonov-1);
				  }
			  }
		  }  
	    }
	}
	
	public static void premik(String tip,int from,int to,Vagon start,int mode,int zacetek){
		
		seznam Premikajoci = new seznam();
        int index = zacetek;
        Vagon tmp = start;
		Vagon zacasni = null;
		boolean koncaj = false;
		  while(true){
		   if(index == from){
			 seznam komadi = tmp.seznam.next;				
			    while(komadi != null){
					if(komadi.tovor.equals(tip)){
						Premikajoci.tovor = komadi.tovor;
						Premikajoci.teza = komadi.teza;
						komadi.previous.next = komadi.next;
						if(komadi.next != null){
						komadi.next.previous = komadi.previous;
						}
						tmp.skupnaTeza -= komadi.teza;
						tmp.stTovora --;
						break;
					}
				 komadi = komadi.next;
				}
			  if(Premikajoci.tovor == null){ // komada ni. zaključimo
					break;
			  }
		      if(zacasni!= null){
				tmp = zacasni;
				index = to;
			  }
		   }
		   if(index == to){
		      if(Premikajoci.tovor == null){
				zacasni = tmp;
			  }else{
				seznam komadi = tmp.seznam.next; 
				  while(komadi != null){
					  if(komadi.tovor.equals(Premikajoci.tovor)){
						 komadi.teza += Premikajoci.teza;
					     tmp.skupnaTeza += Premikajoci.teza;
					     koncaj = true;
						 break;
					  }
					  if(komadi.next == null){
						 komadi.next = Premikajoci; 
					     tmp.skupnaTeza += Premikajoci.teza;
					     tmp.stTovora++;
					     koncaj = true;
					     break;
					  }
				   komadi = komadi.next;
				  }
			      if(koncaj == true){
				   break; 
			      }
			  }
		   }
		   if(mode == 1){
		   index++;
		   tmp = tmp.next;
		   }else{
			   index--;
		   tmp = tmp.previous; 
		   }
		   
		}  
	}
	
	public static void izpisi(){
		 System.out.println("Lokomotiva:(MaxTeza:"+ lokomotiva.maxT +")");
	        Vagon temp = lokomotiva.next;
	        for(int i=0;i<trenutnoStVagonov;i++){
	        	System.out.println(i+1+".vagon: (MAxTeza "+temp.maxT+")");
	        seznam ptr = temp.seznam.next;    
	           while(ptr != null){
	        	  System.out.println("TOVOR:"+ptr.tovor+" teza:"+ptr.teza);
                  ptr =ptr.next;	        	
	           }
	           temp = temp.next;    
	        }
	}
	public static void posodobiLast(){
		Vagon tmp = lokomotiva;
		Vagon prev = lokomotiva;
		while(tmp.next != null){
			prev = tmp;
			tmp = tmp.next;
			
		}
		last = prev;
	}
	
	public static void main(String[] args) {
		
	 Scanner scan = new Scanner(System.in);
        maxTeza = scan.nextInt();
        stVagonov = scan.nextInt();
        trenutnoStVagonov = stVagonov;
        lokomotiva = new Vagon(maxTeza);
        last = null;
        int maxV,stTipov;
        Vagon tmp = lokomotiva;
        for(int i=0;i<stVagonov;i++){
        	//System.out.println(last);
        	maxV = scan.nextInt();
        	stTipov = scan.nextInt();
        	seznam sz = new seznam();
        	Vagon Vagon = new Vagon(maxV,stTipov,sz);
        	seznam ptr = Vagon.seznam;
        	for(int j=0;j<stTipov;j++){
        		String tp = scan.next();
        		int tz = scan.nextInt();
        		seznam seznam = new seznam(tp,tz,ptr);
        		Vagon.skupnaTeza += seznam.teza;
                ptr.next = seznam;
                ptr = seznam;
        	}
        	   if(i == 0){
        	     last = tmp; 	
        	     tmp.next = Vagon;
        	     Vagon.previous = tmp;
        	     tmp = tmp.next;
        	   }else{
        	     tmp.next = Vagon;
        	     Vagon.previous = tmp;
        	     tmp = tmp.next;
        	     last = last.next;
        	   }
         }    
        String ukaz;
        while(scan.hasNext()){
        	ukaz = scan.next();  	
        	switch(ukaz){
      
        	case "ODSTRANI_LIHE":
        	    odstraniLihe();
        	   break;
        	case "ODSTRANI_HET":
        	    int N = scan.nextInt();
        	    odstraniHet(N);
        	   break;
        	case "ODSTRANI_ZAS":
        	    int P = scan.nextInt();
        	    odstraniZas(P);
        	   break;
        	case "OBRNI":
        	    obrni();	
        	   break;
        	case "UREDI":
        		lokomotiva.next = uredi(lokomotiva.next);
        		lokomotiva.next.previous = lokomotiva;
        		posodobiLast(); // posodobimo kazalec last
        	   break;   	 
        	case "PREMAKNI":
        		String tip = scan.next();
        		if(tip == null){
        		 break;	
        		}
        		int from = scan.nextInt();
        		int to =scan.nextInt();
        		premakni(tip,from,to);
        	   break;
        	}
        }
        int celotnaTeza = 0;
        String veljavno = "DA";
        Vagon trenutni = lokomotiva.next;
        System.out.println(maxTeza + " " + trenutnoStVagonov);
        while(trenutni != null){
        	System.out.println(trenutni.maxT + " " + trenutni.stTovora);
        	celotnaTeza += trenutni.skupnaTeza;
        	if(trenutni.maxT < trenutni.skupnaTeza){
        		veljavno = "NE";
        	}
        	seznam tovorNaVagonu = trenutni.seznam.next;
        	while(tovorNaVagonu != null){
        		System.out.println(tovorNaVagonu.tovor + " " + tovorNaVagonu.teza);
        	     tovorNaVagonu = tovorNaVagonu.next;
        	}
        	trenutni = trenutni.next;
        }
        if(celotnaTeza > maxTeza){
        	veljavno = "NE";
        }
        System.out.println(veljavno);
	    scan.close();
	}
}

class Vagon {
	int maxT;
	int stTovora;
	int skupnaTeza;
	seznam seznam;
    Vagon next,previous;
    Vagon(int mxt){ //ustvari lokomotivo
    	maxT = mxt;
    	previous = null;
    	next = null;
    }
    Vagon(int mxt,int stTp, seznam sz){ //ustvari vagon 
    	maxT = mxt;
    	seznam = sz;
    	stTovora = stTp; 
    }
}

class seznam {
	int teza;
	String tovor;
	seznam previous,next;
	seznam(){
	 previous = null;
	 next = null;
	}
	seznam(String Tov,int tz,seznam ptr){
		tovor = Tov;
		teza = tz;
		previous = ptr;
		next = null;
	}	
}
