
import java.util.Scanner;

public class Naloga4 { //KOLIKO VAGONOV
    public static int stTovora,maxTezaNaVagonu;
	
    
    public static int poisciCeno(Object[][] tovori,nosilec vagon,int index,int trenutnaCena,int mode){
    	if(konec(tovori)){ // robni pogoj , ves tovor smo porabili
        // zadnji vagon se ni bil vstet v ceno // (sem pridemo lahko iz stanja 1 ali 2)
    		if(vagon.stTovora == 0){ // ??? useless
    		return trenutnaCena;	
    		}else{
    		return trenutnaCena + 1 + vagon.stTovora;	
    		}
    	}
    	if(mode == 1){ // vagon je prazen - dodamo element, ki je trenutno na vrsti na vagon,in klicemo funkcijo v mode 2(ali mode 3 ce teza == maxTeza);
    	  //if(index == tovori.length-1){ // prisli smo do konca, dani element lahko spravimo zgolj na svoj vagon
    		//return trenutnaCena +2; 
    	  //}else{
    		if((int)tovori[index][2] > 0){
    			vagon.seznam.tovor = (String) tovori[index][0];
    			vagon.seznam.teza = (int) tovori[index][1];
    		 tovori = pobrisi(tovori,index); // zmanjsa kvantiteto dodanega tovora
    		 if((int)tovori[index][2] == 0){ // ce imamo 0 primerkov nekega komada,ga odstranimo 
    			tovori[index][0] = null; 
    		 }
    		 vagon.stTovora++;
    		 if(vagon.seznam.teza == maxTezaNaVagonu){
     	    	/* nosilec Pass = UstvariNovega(vagon); // naredimo novo instanco nosilca (da se nam ne pokrivajo stvari)
     	    	 Object[][] PassingInto = NovObj(tovori); // nov seznam tovora za razlozitev
     	    	 // starega zapisemo na nov naslov, tako se nam pri vracanju stari ne bo zmedel
    			 return poisciCeno(PassingInto,Pass,index,trenutnaCena,3);*/
    			 return poisciCeno(tovori,vagon,index,trenutnaCena,3);
    		 }else{
    			/* nosilec Pass1 = UstvariNovega(vagon); // dajemo na nov naslov (prekrivanje ?)
     	    	 nosilec Pass2 = UstvariNovega(vagon); // -||-
     	    	 Object[][] PassingInto1 = NovObj(tovori);
     	    	 Object[][] PassingInto2 = NovObj(tovori); 
    			 int cena1 = poisciCeno(PassingInto1,Pass1,index,trenutnaCena,2);
    			 int cena2 = poisciCeno(PassingInto2,Pass2,index,trenutnaCena,3); */
    			 if(vagon.seznam.teza < maxTezaNaVagonu/2){
    			 return poisciCeno(tovori,vagon,index,trenutnaCena,2);
    			 }else{
    			 int cena1 = poisciCeno(tovori,vagon,index,trenutnaCena,2);
    			 int cena2 = poisciCeno(tovori,vagon,index,trenutnaCena,3);
    			 return Math.min(cena1,cena2);
    			 }
    		}
    	 }else{ // tega elementa ni vec, premaknemo se naprej
    		return poisciCeno(tovori,vagon,index+1,trenutnaCena,1); 
    	 }
    	//  }
    	}else if(mode == 2){ // vagon vsebuje tovor,vendar ni poln
    		int cena = 69;
    		for(int i = index;i < stTovora;i++){
              	if(tovori[i][0] != null){
            		int tmpT = (int) tovori[i][1];
            		String tmpI = (String) tovori[i][0];
            		int curCarg = dobiTovor(vagon.seznam);
            	    if(curCarg + tmpT < maxTezaNaVagonu){
            	    	nosilec Pass1 = UstvariNovega(vagon); // dajemo na nov naslov (prekrivanje ?)
            	    	nosilec Pass2 = UstvariNovega(vagon); // -||-
            	    	dodajElement(Pass1,tmpI,tmpT);
            	    	dodajElement(Pass2,tmpI,tmpT);
            	    	Object[][] PassingInto1 = NovObj(tovori);
            	    	Object[][] PassingInto2 = NovObj(tovori);
                        PassingInto1 = pobrisi(PassingInto1,i);
                        PassingInto2 = pobrisi(PassingInto2,i);
            	    	if((int)PassingInto1[i][2] == 0){ // ce smo dodali zadnji komad tovora danega tipa ga pobrisemo
            	    		PassingInto1[i][0] = null;
            	    		PassingInto2[i][0] = null;
            	    	}
            	    	
            	    	int cena1 = Math.min(poisciCeno(PassingInto1,Pass1,index,trenutnaCena,2),poisciCeno(PassingInto2,Pass2,index,trenutnaCena,3));  
            	    	cena = Math.min(cena,cena1);
            	    }else if(curCarg+tmpT == maxTezaNaVagonu){
            	    	nosilec Pass = UstvariNovega(vagon);
            	    	dodajElement(Pass,tmpI,tmpT);
            	    	Object[][] PassingInto = NovObj(tovori);
            	    	PassingInto = pobrisi(PassingInto,i);
            	    	if((int)PassingInto[i][2] == 0){ // ce smo dodali zadnji komad tovora danega tipa ga pobrisemo
            	    		PassingInto[i][0] = null;
            	    	}
            	    	
            	    	int cena2 = poisciCeno(PassingInto,Pass,index,trenutnaCena,3);
            	    	cena = Math.min(cena,cena2);
            	    }
            	}
              	
            }
    		if(cena == 69){ // noben komad ni sel na ta vagon
    			 nosilec Pass = UstvariNovega(vagon); 
    			 Object[][] PassingInto = NovObj(tovori);
          		return poisciCeno(PassingInto,Pass,index,trenutnaCena,3);
          	}else{
    		return cena;
          	}
    	}else{ //zakljuceno stanje, vagon je lahko poln ali pa ne, tu zakljucimo,obracunamo trenutno ceno in naredimo nov vagon ter gremo v mode 1
    	trenutnaCena += 1 + vagon.stTovora;
    	nosilec Pass = new nosilec();
    	return poisciCeno(tovori,Pass,index,trenutnaCena,1); 
    	}
    }
    
    public static nosilec UstvariNovega(nosilec vagon){
    	    nosilec Pass = new nosilec(); // nov naslov vagona
    	    Pass.stTovora = vagon.stTovora;
    	    cargo ptr = Pass.seznam;// za premikanje po seznamu
    	    cargo prev = null; // nastavlja .next
    	    cargo root = null; // drzi vrednost prvega elementa na seznamu Pass
    	    cargo rootV = vagon.seznam; // drzi vrednost prvega elementa na seznamu vagon
    	    int i = Pass.stTovora; // sprehod cez ves tovor
    	    while(i > 0){
    	    	cargo Seznam = new cargo(vagon.seznam.teza,vagon.seznam.tovor); // nov naslov tovora na njem
    	    	if(prev == null){
    	    	 prev = Seznam;	
    	    	}
    	    	ptr = Seznam;
    	    	if(i == Pass.stTovora){
    	    	root = ptr;	
    	    	}
    	    	else{
    	    	 prev.next = Seznam;
    	    	 prev = prev.next;
    	    	}
    	    	ptr = ptr.next;
    	        vagon.seznam = vagon.seznam.next;
    	        i--;
    	    }
    	    vagon.seznam = rootV;
    	    Pass.seznam = root;
    	return Pass;
    }
    
    public static Object[][] NovObj(Object[][] tovor){
    	Object[][] PassingInto = new Object[stTovora][3];
    	for(int i = 0; i < stTovora;i++){
    	int tz = (int) tovor[i][1];
    	int kz = (int) tovor[i][2];
    	String ime = (String) tovor[i][0];
    	PassingInto[i][0] = ime;
    	PassingInto[i][1] = tz;
    	PassingInto[i][2] = kz;
    	}
    	return PassingInto;
    }
    
    public static boolean konec(Object[][] tovor){ // preveri ali smo porabili vse elemente
    	for(int i = 0;i < stTovora;i++){
    		if(tovor[i][0] != null){
    			return false;
    		}
    	}
    	return true;
    }
    
    public static void dodajElement(nosilec vagon,String ime,int teza){ // doda element na dani vagon
    	cargo tmp = vagon.seznam;
    	while(tmp != null){
    		if(tmp.tovor.equals(ime)){
    			tmp.teza += teza;
    			break;
    		}
    		if(tmp.next == null){
    		 cargo novi = new cargo();
    		 tmp.next = novi;
    		 novi.tovor = ime;
    		 novi.teza = teza;
    		 vagon.stTovora++;
    		 break;
    		}
    		tmp = tmp.next;
    	}
    }
    public static int dobiTovor(cargo cargo){ // pridobi trenutni tovor na danem vagonu
    	int curCarg = 0;
    	 while(cargo!= null){
    		curCarg += cargo.teza;
    		cargo = cargo.next;
    	 }
    	return curCarg;
    }
    
    public static Object[][] dodaj(Object[][] tovor,String Ime,int kilaza){ //dodajanje in grupiranje novega tovora
    	for(int i = 0;i<tovor.length;i++){
    	    if(tovor[i][0] == null){
    	      tovor[i][0] = Ime;	
    	      tovor[i][1] = kilaza;
    	      tovor[i][2] = 1; 
    	      break;
    	    }else{
    	      if(tovor[i][0].equals(Ime) && (int)tovor[i][1] == kilaza){
    	    	  int temp = (int) tovor[i][2];
    	    	  temp++;
    	    	  tovor[i][2] = temp;
    	    	  break;
    	      }	
    	    }
    	}
    	return tovor;
    }
    
    public static void izpisi(Object[][] tovor){ // izpis prebranega tovora
    	int i = 0;
    	while(i < tovor.length){
    		if(tovor[i][0] == null){
    			break;
    		}
    		System.out.println("Ime:"+tovor[i][0]+" teza:"+tovor[i][1]+" Kilaza:"+tovor[i][2]);
    	    i++;                      
    	}
    }
    public static Object[][] zapolni(Object[][] tovori){ // na zacetku zapolni kvantiteto in tezo praznih polj z 0 (zaradi null)
    	for(int i=0;i< tovori.length;i++){
    		if(tovori[i][0] == null){
    			tovori[i][2] = 0;
    			tovori[i][1] = 0;
    		}
    	}
    	return tovori;
    }
    public static Object[][] pobrisi(Object[][] tovori,int i){ //funkcija odstrani en element iz kvantitete danega komada v tovori
        int tmp = (int) tovori[i][2];
        tmp--;
        tovori[i][2] = tmp;
    	return tovori;
    }
    
	public static void main(String[] args) {
		
		Scanner scan = new Scanner(System.in);
		
		int cena = 0;
		int kilaza;
		String Ime;
        stTovora = scan.nextInt();
        maxTezaNaVagonu = scan.nextInt();
         Object[][] tovori = new Object[stTovora][3];
           for(int i = 0;i<stTovora;i++){
        	     Ime = scan.next();
        	     kilaza = scan.nextInt();		 
        	     tovori = dodaj(tovori,Ime,kilaza);
                 }
                 tovori = zapolni(tovori);
           izpisi(tovori);
           nosilec vagon = new nosilec();
           cena = poisciCeno(tovori,vagon,0,0,1);
		   scan.close();
		   System.out.println(cena);
	}

}

class nosilec {
	int stTovora;
	cargo seznam;
    nosilec(){ //ustvari vagon  
    stTovora = 0;
    seznam = new cargo();
    }
}

class cargo {
	int teza;
	String tovor;
	cargo next;
	cargo(){ // inventar na vagonu
		teza = 0;
		next = null;
	}
	cargo(int tz,String ime){
	 teza = tz;
	 tovor = ime;
	}
}