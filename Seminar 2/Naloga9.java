import java.io.*;
import java.util.*;

public class Naloga9 {
	public static int[][] Kardinalnosti;
 public static void main(String[] args) throws IOException{
	 
	 //System.out.println(args[0]);
	 //System.out.println(args[1]);
	 File IZ = new File(args[0]);
	 File V = new File(args[1]);
	 V.createNewFile();
	 FileReader in = new FileReader(IZ);
	 FileWriter out = new FileWriter(V);
	   
    Scanner sc = new Scanner(in);
    
    int stPrijateljev = sc.nextInt();
    int stOdvisnosti = sc.nextInt();
    sc.nextLine();
    int PrihodPrijatelja = 0;
    int OdvisenOdPrijatelja = 0;
    ArrayList<HashSet<Integer>> seznamOdvisnosti = new ArrayList<HashSet<Integer>>(); //seznam ki hrani HashSet za vsakega Prijatelja
    HashSet<Integer>  odvisnost = new HashSet<Integer>(); // tu so shranjene odvisnosti vsakega posameznika
    int[][] Odvisnosti = new int[stPrijateljev][stPrijateljev];  // v pomoc pri koncu za preverjanje ciklanja
    int trenutniPrijatelj = 1;
    Kardinalnosti = new int[stPrijateljev][2];
    boolean napaka = false;
    for(int i = 0;i < stOdvisnosti; i++){ // sprehodimo se cez vse odvisnosti, ki jih hranimo v hashSetu ki ga spremljamo z binarno tabelo ki nam hrani odvisnosti za posameznega Prijatelja
      String Odvisnost = sc.nextLine();
      String[] Podatki = Odvisnost.split(",");
      PrihodPrijatelja = Integer.parseInt(Podatki[0]);
      if(trenutniPrijatelj != PrihodPrijatelja){ // nov prijatelj
    	  seznamOdvisnosti.add(odvisnost);  
    	  odvisnost = new HashSet<Integer>();
    	  trenutniPrijatelj++;
    	while(trenutniPrijatelj < PrihodPrijatelja){ // dodajamo prijatelje ki niso odvisni od nobenega
    		seznamOdvisnosti.add(odvisnost);  
    	  odvisnost = new HashSet<Integer>();
    	  trenutniPrijatelj++;
        }
      }
      OdvisenOdPrijatelja = Integer.parseInt(Podatki[1]); // st. prijatelja od katerega je odvisen
      odvisnost.add(OdvisenOdPrijatelja);
      Odvisnosti[PrihodPrijatelja-1][OdvisenOdPrijatelja-1] = 1; // s tem se orientiramo po odvisnostih
    }
    seznamOdvisnosti.add(odvisnost);// za dodajanje zadnjega seta
      if(trenutniPrijatelj < stPrijateljev){
    	  while(trenutniPrijatelj < stPrijateljev){
    		  odvisnost = new HashSet<Integer>();
    		  seznamOdvisnosti.add(odvisnost); 
    		  trenutniPrijatelj++;
    	  }
      }
    for(int i = 0;i < stPrijateljev;i++){ // pregled ciklanja
    	odvisnost = seznamOdvisnosti.get(i);
    	if(!odvisnost.isEmpty()){
    	  for(int j = 0;j < stPrijateljev;j++){
    		if(i!=j){
    		  if(Odvisnosti[i][j]==1){
    	       HashSet<Integer> Vsebovan = seznamOdvisnosti.get(j);
    	         if(!Vsebovan.isEmpty()){
    		     odvisnost.addAll(Vsebovan); // naredi unijo
    	         }
    	       }	
    		}
    	  }
      }
       if(odvisnost.contains(i+1)){ // pregledamo ce je v uniji vseh el odvisnih od osebe i tudi oseba i. v tem primeru se zaciklamo
    	   napaka = true;
    	   break;
       }
       Kardinalnosti[i][0] = i+1; // index Prijatelja
       Kardinalnosti[i][1] = odvisnost.size(); // dobimo velikosti posameznih mnozic
    }
    if(napaka){ // prislo je do napake
    	//out.write("-1\n");          
    }else{ // napake ni vse skupaj uredimo in pripravimo izpis
      //uredi Kardinalnost narascajoce(nizja naprej)	
    	Arrays.sort(Kardinalnosti, new Primerjalnik()); // urejanje 2D array z Kardinalnostjo setov v povezavi z indexom posameznega Prijatelja
    	String Solution = null;
    	HashSet<Integer> Dodani = new HashSet<Integer>(); // hrani stevila ki smo jih ze dodali v koncno resitev
    	int stDodanih = 0; // hrani velikost HashSeta Dodani
    	int zacetniIndex = 0; // vsi elementi z indexom manjsim od tega stevila so ze porabljeni
    	int index = 0; // index elementa za dodajanje
		for(int i = 0;i < stPrijateljev;i++){ //izpis
			if(i < stPrijateljev-1){	
			    if(i == 0){ // dodajanje prvega
				Solution = Kardinalnosti[i][0]+",";
				Dodani.add(Kardinalnosti[i][0]);
				stDodanih++;
				zacetniIndex++;
				Kardinalnosti[i][0] = 0;
				}else{ // dodajanje vseh elementov med prvim in zadnjim
					index = DodajNaslednjega(seznamOdvisnosti,Dodani,zacetniIndex,stDodanih);
					if(index == zacetniIndex){
						zacetniIndex++;
					}else if(Kardinalnosti[zacetniIndex][0] == 0){
						zacetniIndex = posodobi(zacetniIndex);
					}
				Solution += Kardinalnosti[index][0] + ",";
				Dodani.add(Kardinalnosti[index][0]);
				stDodanih++;
				Kardinalnosti[index][0] = 0; // pobrisemo dodani element
				}
			}else{ // dodajanje zadnjega elementa
				 // dodamo zadnjega in zakljucimo
				index = posodobi(zacetniIndex);
				Solution += Kardinalnosti[index][0];
				Kardinalnosti[index][0] = 0;
			}
		}
		//System.out.println(Solution);
    	out.write(Solution+"\n");
    }
    sc.close();
	out.flush();
    out.close();
 }
     public static int DodajNaslednjega(ArrayList<HashSet<Integer>> seznam,HashSet<Integer> ZeIzbrani,int Index,int stDodanih){ // funkcija poisce naslednje stevilo(vrne njegov index), ki ga dodamo v koncno resitev. na mesto izbranega st. zapise 0(to stevilo je porabljeno)
         int IzbraniElement = 0;  
    	 int StIzbranegaElementa = 999999999;
    	 HashSet<Integer> trenutni = null;
    	 while(true){
    		 if(Index == Kardinalnosti.length){
    			 break;
    		 }
    		 if(Kardinalnosti[Index][0] != 0){// element je na voljo
    		      if(Kardinalnosti[Index][1] <= stDodanih){ // prisli smo do kandidata za Izbrani element preverimo ce je najmanjsi
    		    	     int i = Kardinalnosti[Index][0]-1;
    		             trenutni = seznam.get(i);
    		    	  if(ZeIzbrani.containsAll(trenutni)){ // preverimo ce so vsi elementi od katerih je odvisen ze dodani pred njim
    		    		     if(Kardinalnosti[Index][0] < StIzbranegaElementa){ // preverimo ce je manjsi od vseh kandidatov
    		    			  StIzbranegaElementa = Kardinalnosti[Index][0];
    		    			  IzbraniElement = Index;
    		    		     }   	
    		    	    }
    		       Index++;	
    		      }else break; // prisli smo do elementa ki je odvisen od vec elementov, kot jih imamo trenutno izbranih. prekinemo zanko in vrnemo index trenutnega najmanjsega
    	     }else Index++;
    	}
    	return IzbraniElement;
     }
     public static int posodobi(int index){
    	 while(Kardinalnosti[index][0] == 0){
    		 index++;
    	 }
    	 return index;
     }
}
class Primerjalnik implements Comparator<int[]> { // uredi dano tabelo po vrednostih dolocenega polja - implementira comparator
    public int compare(int[] parameter1,int[] parameter2) {   
        if (parameter1[1] > parameter2[1]){
            return 1;
		}else if(parameter1[1] == parameter2[1]){
			   if(parameter1[0] > parameter2[0]){
				   return 1;
			   }else{
				   return -1;
			   }
		}else{
            return -1;
		}
    }
}