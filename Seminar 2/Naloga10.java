import java.io.*;
import java.util.*;

public class Naloga10 {
	
 public static void main(String[] args) throws IOException{
	 File IZ = new File(args[0]);
	 File V = new File(args[1]);
	 FileReader in = new FileReader(IZ);
	 FileWriter out = new FileWriter(V);
	 
    Scanner sc = new Scanner(in);
    int stPovezav = sc.nextInt();
    sc.nextLine();
    String trenutniIz = null;
    vozlisce Novo = null;
    ArrayList<vozlisce>  vsaVozlisca = new ArrayList<vozlisce>();
    for(int i = 0;i < stPovezav;i++){
    	String povezava = sc.nextLine();
    	String[] gradniki = povezava.split(",");
    	if(trenutniIz == null){ // prvoVozlisce
    	 trenutniIz = gradniki[0];	
    	 Novo = new vozlisce(trenutniIz);
    	}else if(!trenutniIz.equals(gradniki[0])){ // novoVozlisce
    		trenutniIz = gradniki[0];
    		vsaVozlisca.add(Novo);// dodamo staro vozlisce
    		Novo = new vozlisce(trenutniIz);
    	}// dodamo povezavo v vozlisce z oznako trenutniIz	
    	povezava NovaPovezava = new povezava(gradniki);
    	Novo.dodajPovezavo(NovaPovezava);
    } 
    sc.close();
    vsaVozlisca.add(Novo); // dodamo zadnje vozlisce
    int stVozlisc = vsaVozlisca.size();
    int[] skupaj = new int[stVozlisc];
    for(int i = 0;i< stVozlisc;i++){
    	vozlisce trenutno = vsaVozlisca.get(i);
    	int stpovezav = trenutno.povezave.size();
    	for(int j = 0;j < stpovezav;j++){
    		povezava trenutna = trenutno.povezave.get(j);
    		int indexV = vrniVozlisce(trenutna.vhodnoVozlisce,vsaVozlisca,stVozlisc);
    		skupaj[i] += trenutna.velikostPovezave;
    		skupaj[indexV] -= trenutna.velikostPovezave;		
    	}
    }
    
    Arrays.sort(skupaj); // sortiranje po velikosti
    int[] index = sprehod(skupaj); // iskanje indexa za locitev arraya 
    int[] vhodni = Arrays.copyOfRange(skupaj, 0, index[0]); // >0
    int[] izhodni =Arrays.copyOfRange(skupaj, index[1], skupaj.length); // <0
    int minStPovezav = minimiziraj(vhodni,izhodni);
    //System.out.println(minStPovezav);
    out.write(minStPovezav + System.getProperty("line.separator"));
    out.close();
 }
  public static int vrniVozlisce(String oznaka,ArrayList<vozlisce> vozlisca,int stV){ // vrne vozlisce s podano oznako
	  int index = 0;
	  for(int i = 0;i< stV ; i++){
		vozlisce tmp = vozlisca.get(i);
		if(tmp.Label.equals(oznaka)){
	     index = i;
	     break;
		}
	  }
	  return index;
  }
/////////////////
  public static int[] sprehod(int[] polje){ // poisce indexa da lahko array locimo na >0 in <0. =0 pri tem izlocimo
	  int size = polje.length;
	  int[] index = new int[2];
	  index[0] = -1;
	  for(int i = 0; i < size;i++){
		  if(polje[i] == 0 && index[0] == -1){
			index[0] = i;
		  }else if(polje[i] > 0){
			 index[1] = i;
			 if(index[0]==-1){
			 index[0] = i;	 
			 }
			 break;
		  }
	  }
	  return index;
  }
  public static int minimiziraj(int[] vhodni,int[] izhodni){
	  Queue vrsta = new Queue();
	  int stpovezav = 0;
	  stanje stanje = new stanje(vhodni,izhodni,stpovezav);
	  vrsta.enqueue(stanje);
	  int s1 = izhodni.length;
	  int s2 = vhodni.length;
	  int ix = 0;
	  while(!vrsta.empty()){
		QueueElement Element = (QueueElement) vrsta.front();
		stanje trenutnoStanje = (stanje) Element.element; 
		vrsta.dequeue();
		vhodni = trenutnoStanje.vhodni;
		izhodni = trenutnoStanje.izhodni;
		stpovezav = trenutnoStanje.stPovezav;
		if(koncaj(vhodni,izhodni)){
		   break; 
		}
		for(int i = (s1-1);i >= 0;i--){
			if(izhodni[i]!= 0){
			ix = i;
			break;
			}
		}
		for(int i = 0;i< s2;i++){
			if(vhodni[i]!= 0){
			int tmp = vhodni[i] + izhodni[ix];
			int[] klonV = vhodni.clone();
			int[] klonIZ = izhodni.clone();
			if(tmp > 0){
				klonIZ[ix] = tmp;
				klonV[i] = 0;
			}else if(tmp < 0){
				klonIZ[ix] = 0;
				klonV[i] = tmp;	
			}else{
			klonIZ[ix] = 0;
			klonV[i] = 0;
			}
			stanje Novo = new stanje(klonV,klonIZ,stpovezav+1);
			vrsta.enqueue(Novo);
			}
		}
	  }
	  return stpovezav;
  }
  public static boolean koncaj(int[] v,int[] iz){
	  int s1 = v.length;
	  int s2 = iz.length;
	  for(int i= 0;i<s1;i++){
		 if(v[i] != 0){
		 return false;	 
		 } 
	  }
	  for(int i= 0;i<s2;i++){
		  if(iz[i] != 0){
		  return false;	 
		  } 
	  }
	  return true;
  }

}
class vozlisce{
	
	String Label;
	ArrayList<povezava> povezave = new ArrayList<povezava>();
	 public vozlisce(String name){
	 Label = name;	
	 }
     public void dodajPovezavo(povezava povezava){
     povezave.add(povezava);
     }
}
class povezava{
	
	String izhodnoVozlisce,vhodnoVozlisce;
	int velikostPovezave; 
	 public povezava(String[] podatki){
		izhodnoVozlisce = podatki[0];
		vhodnoVozlisce = podatki[1];
		velikostPovezave = Integer.parseInt(podatki[2]);
	 }  
}
class stanje {
	int[] vhodni,izhodni;
	int stPovezav;
	public stanje(int[] v,int[] iz,int sp){
	  vhodni = v;
	  izhodni = iz;
	  stPovezav = sp;
	}
}
//////////////////////////////////////////////////////////////////////////////
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