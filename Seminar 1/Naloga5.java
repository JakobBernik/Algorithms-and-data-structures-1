
import java.util.Scanner;

public class Naloga5 { //LABIRINT
    
	public static int[][] labirint;
	public static int[] Pozicija;
	public static int Orientacija,dolzina,sirina,stKorakov;
	
	public static boolean forward(){ // premik v smeri Orientacija, ce je to mozno
		if(Orientacija == 0){ //sever
			int vrstica = dolzina - Pozicija[0]-2;
			int stolpec = Pozicija[1];
		    if(labirint[vrstica][stolpec] == 0){
		       Pozicija[0] ++;
		       return true;
		      }
		    
		    return false;
		}else if(Orientacija == 1){ // vhod
			int vrstica = dolzina -1-Pozicija[0];
			int stolpec = Pozicija[1]+1;
			if(labirint[vrstica][stolpec] == 0){
			     Pozicija[1] ++;
			return true;
			  }
			return false;
		}else if(Orientacija == 2){ // jug
			int vrstica = dolzina - Pozicija[0];
			int stolpec = Pozicija[1];
			if(labirint[vrstica][stolpec] == 0){
		 	     Pozicija[0] --;
		    return true;
			  }
			return false;
		}else{ // zahod
			int vrstica = dolzina - Pozicija[0] -1;
			int stolpec = Pozicija[1]-1;
			if(labirint[vrstica][stolpec] == 0){
			     Pozicija[1] --;
			return true;
			  }
			return false;
		}		
	}
	
	public static void RGT(){ // zasuk za 90° CW
	Orientacija	= (Orientacija+1)%4;
	}
	
	public static void LFT(){ // zasuk za 90° CCW
	 if(Orientacija == 0){
		Orientacija = 3;
	 }else{	
	 Orientacija = (Orientacija-1)%4;	
	 }
	}
	
	public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);
		
		int stFunkcij,stUkazovVFunkciji;
        dolzina = scan.nextInt();
        sirina = scan.nextInt();
		labirint = new int[dolzina][sirina];
           for(int i = 0;i < dolzina;i++){
        	  for(int j = 0;j < sirina;j++){
        		 labirint[i][j] = scan.nextInt(); 
        	  } 
           }
           stFunkcij = scan.nextInt();
           SeznamFunkcij Funkcije = new SeznamFunkcij(stFunkcij);
           for(int i = 0; i < stFunkcij;i++){ // branje funkcij
             stUkazovVFunkciji = scan.nextInt();
             String[] ukazi = new String[stUkazovVFunkciji];
             int[] FunIndex = new int[stUkazovVFunkciji];
             for(int j = 0; j < stUkazovVFunkciji;j++){
        	    ukazi[j] = scan.next();
        	    if(ukazi[j].equals("FUN")){
        	    FunIndex[j] = scan.nextInt();
        	    }
            }
             CodeBlock Fun = new CodeBlock(ukazi,FunIndex);
             Funkcije.funkcija[i]= Fun;
           }
           Pozicija = new int[2];
           Pozicija[0] = scan.nextInt(); Pozicija[1]= scan.nextInt(); Orientacija = scan.nextInt(); // preberemo zacetno pozicijo in orientacijo robota
            
           stKorakov = scan.nextInt();
           scan.close(); // zapremo scanner, ga ne potrebujemo vec
           //izpisi(Funkcije);
           Stack CodeStack = new Stack(); // naredimo nov CodeStack ki bo skrbel za izvajanjeProgramov
           Queue Izvajanje = new Queue(); // naredimo novO vrsto , ki bo hranila stanja izvajanja programa
           CodeBlock trenutnaFunkcija = Funkcije.funkcija[0]; // zacnemo z FUN 1   
              for(int i = 0;i < trenutnaFunkcija.ukazi.length;i++){
            	   if(stKorakov == 0){
            	    break;
                   }
        	   String ukaz = trenutnaFunkcija.ukazi[i];
        	   boolean uspesno = true;
        	   switch(ukaz){
        	   case "FWD": // treba paziti na uspesnost!
        		   uspesno = forward(); // premaknemo se naprej
        			stKorakov--;   
        		   break;
        	   case "RGT": 
        		   RGT(); // Rotacija CW
        		   stKorakov--;
        		   break;
        	   case "LFT":
        		   LFT(); // Rotacija CCW
        		   stKorakov--;
        		   break;
        	   case "FUN":
        		   int index = trenutnaFunkcija.FunIndex[i];
        		   if(i!= trenutnaFunkcija.ukazi.length -1){ // v primeru da funkcija se vsebuje ukaze, ki se niso izvedli.ce je to zadnji ukaz, lahko funkcijo preprosto zakljucimo
         		   NaStack Pushni = new NaStack(trenutnaFunkcija,i); // na stack potisnemo funkcijo,ki se je zaradi klicanja nove prekinila, i nam kaze od kje naprej bo laufala ko jo bomo potegnili z stacka
        		   CodeStack.push(Pushni);
        		   }
        		   trenutnaFunkcija = Funkcije.funkcija[index-1];
        		    i = -1; // nastavimo i na zacetek (zaradi i++ na -1 in ne na 0)
        		    continue;
        	   case "SETJMP":
        		   Stanje stanje = new Stanje(CodeStack,trenutnaFunkcija,i); // naredimo novo stanje (i kaze na ukaz SETJMP - ko ga bomo vrnili bo for samodejno skocil na naslednji ukaz);
        		   Izvajanje.enqueue(stanje); // shranimo stanje izvajanja v vrsto stanj
        		   break;
        	   case "JMP":
        		   if(Izvajanje.empty()){ // ce je vrsta prazna
        			uspesno = false;   
        		   }else{ // preberemo stanje iz vrste
        			QueueElement el = (QueueElement) Izvajanje.front();
        			Izvajanje.dequeue();
        			Stanje PrebranoStanje = (Stanje) el.element;
        			CodeStack = PrebranoStanje.CodeStack;
        			trenutnaFunkcija = PrebranoStanje.funkcija;
        			i = PrebranoStanje.SledeciUkaz;
        		   }
        		   break;
        	   
        		   
        	   }
        	   if(!uspesno){// v primeru da je bil ukaz neuspesen (FWD ALI JMP) se funkcija prekine in v CodeStacku se premaknemo eno funkcijo nazaj;
        		 if(CodeStack.empty()){
        		 break;	 
        		 }else{
        		 StackElement el = (StackElement) CodeStack.top();
        		 CodeStack.pop();
        		 NaStack Popni = (NaStack) el.element;
        		 trenutnaFunkcija = Popni.fun;
        		 i = Popni.NaslednjiUkaz;
        	    }
        	   }
        	   if(i==trenutnaFunkcija.ukazi.length-1){ //izvrsil se je zadnji ukaz v tej funkciji preverimo ali lahko zakljucimo(ki ni bil klic nove funkcije) 
        		   if(CodeStack.empty()){ // if true zakljucimo
        			 break;
        		   }else{
        			   StackElement el = (StackElement) CodeStack.top();
              		 CodeStack.pop();
              		 NaStack Popni = (NaStack) el.element;
              		 trenutnaFunkcija = Popni.fun;
              		 i = Popni.NaslednjiUkaz;
        		   }
        	   }
              }
             
           System.out.println(Pozicija[0] +" "+ Pozicija[1] +" "+ Orientacija);   
	}
	
	public static void izpisi(SeznamFunkcij funkcije){
		for(int i = 0; i < funkcije.funkcija.length;i++){
		    CodeBlock fun = funkcije.funkcija[i];
		    System.out.println("Funkcija "+(i+1)+":");
		    for(int j = 0; j < fun.ukazi.length;j++){
			    if(fun.ukazi[j].equals("FUN")){
			    	 System.out.println(fun.ukazi[j]+" "+fun.FunIndex[j]); 
			    }else{
			    	 System.out.println(fun.ukazi[j]); 
			    }          	    
		   }
		   System.out.println(); 
		  } 
		}
}
class Stanje{ // to bo element, ki ga bomo dajali v queue , in nam bo hranil stanje izvajanja programa
	Stack CodeStack;
	CodeBlock funkcija;
	int SledeciUkaz;
	Stanje(Stack code,CodeBlock fun,int i){
		CodeStack = code;
		funkcija = fun;
		SledeciUkaz = i;
	}
}

class CodeBlock{ // ogrodje za posamezno funkcijo
	String[] ukazi;
	int[] FunIndex;
	CodeBlock(String[] Ukazi,int[] funInd){
	 ukazi = Ukazi;
	 FunIndex = funInd;
	}
}

class SeznamFunkcij{ //v seznamu shranjene funkcije (npr. FUN 1 je shranjena na naslovu funkcija[0] itd.)
	CodeBlock[] funkcija;
	SeznamFunkcij(int i){
	funkcija = new CodeBlock[i];	
	}
}
class NaStack{ // ogrodje shranjevanja na Stack - drzi funkcijo in index od katerega naprej se bo dana funkcija izvajala
	CodeBlock fun;
	int NaslednjiUkaz;
	NaStack(CodeBlock func,int index){
		fun = func;
		NaslednjiUkaz = index;
	}
}
//////////////////////////////////////////////////////////////////////////////////	QUEUE /////////////////////////////////////////////////////////////
// ADT Queue iz vaj
class QueueElement{
	Object element;
	QueueElement next;

	QueueElement()
	{
		element = null;
		next = null;
	}
}
class Queue{
	//QueueElement -> QueueElement -> QueueElement -> ... -> QueueElement
	//     ^                                                       ^
	//     |                                                       |  
	//    front                                                   rear
	//
	// nove elemente dodajamo na konec vrste (kazalec rear)
	// elemente brisemo na zacetku vrste (kazalec front)
	private QueueElement front;
	private QueueElement rear;
	
	public Queue(){
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
	   QueueElement queueE = new QueueElement();
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
////////////////////////////////////////////////////////////////////////////////// STACK //////////////////////////////////////////////////////////////
//ADT Stack iz vaj
class StackElement{
	Object element;
	StackElement next;

	StackElement(){
		element = null;
		next = null;
	}
}
class Stack{
	//StackElement -> StackElement -> StackElement -> ... -> StackElement
	//     ^
	//     |
	//    top                                                   
	//
	// elemente vedno dodajamo in brisemo na zacetku seznama (kazalec top)
	private StackElement top;
	public Stack(){
		makenull();
	}
	public void makenull(){
		top = null;
	}
	public boolean empty(){
		return (top == null);
	}
	public Object top(){
		// Funkcija vrne vrhnji element sklada (nanj kaze kazalec top).
		// Elementa NE ODSTRANIMO z vrha sklada!
		
		return this.top;
	}
	public void push(Object obj){
		// Funkcija vstavi nov element na vrh sklada (oznacuje ga kazalec top)
	    StackElement stackE = new StackElement();
	    stackE.element = obj;
	    stackE.next = top;
	    top = stackE;
	}
	public void pop(){
		// Funkcija odstrani element z vrha sklada (oznacuje ga kazalec top)
	    top = top.next; 
	}
}