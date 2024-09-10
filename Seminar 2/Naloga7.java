import java.io.*;
import java.util.*;

public class Naloga7 {
	
	
 public static void main(String[] args) throws IOException{
	 
	 File IZ = new File(args[0]);
	 File V = new File(args[1]);
	 FileReader in = new FileReader(IZ);
	 FileWriter out = new FileWriter(V);
	   
    Scanner sc = new Scanner(in);
    String data = sc.nextLine();
    sc.close();
    String[] gradniki = data.split(" ");
    int[] poddrevo = new int[2];
    poddrevo[0] = 0; poddrevo[1] = 0;
    Node root = Drevo(gradniki,0,poddrevo,0); // zgradimo drevo ter vrnemo njegov root
    HashSet<String> setSpremenljivk = vrniSeznamSpremenljivk(root); // vrne seznam spremenljivk
    ArrayList<String> spremenljivke = new ArrayList<String>(setSpremenljivk);
    int Resitev = stVsehTrue(root,spremenljivke);  // ugotovi st vseh pravilnih vrednosti izraza glede na spremenljivke
    //System.out.println(Resitev);
    out.write(Resitev + System.getProperty("line.separator"));
    out.flush();
    out.close();
 }
///////////////////////////////////////////////////////////////////////// glavna funkcija za gradnjo drevesa 
 public static Node Drevo(String[] gradniki,int i,int[] poddrevo,int depth){ // depth nam kontrolira kako globoko je nase trenutno poddrevo drevesa glede na '(' ')'
	 Stack<Node> stackOP = new Stack<Node>(); // operandi
	 Stack<Node> stackFnc = new Stack<Node>(); // operatorji
	 boolean koncaj = false;
	 while(true){
		  if(i== gradniki.length){
			 break;
		  }
		    String stikalo = "Operand";
		   // String grad = gradniki[i];
		       if(gradniki[i].equals("NOT") ||gradniki[i].equals("AND")){
			   stikalo = "Operator";  
		       }
		       if(gradniki[i].equals("OR")){
			   stikalo = "OR"; 
		       }  
		 switch(stikalo){
		 case "Operator": // ce dobimo NOT ali AND ga lahko kar potisnemo na stack, saj je NOT vedno vezan na operand AND pa ima prednost pred OR
			Node novi = new Node(gradniki[i]);
			stackFnc.push(novi);
			i++;
		    break;
		 
		 case "OR": // ko dobimo OR moramo preveriti ce obstaja poddrevo (AND ima prednost). ce drevesa ni nadaljujemo normalno z gradnjo
			 if(poddrevo[0]==1 && poddrevo[1] == 0){ // prisli smo do konca nasega poddrevesa z OR
				 koncaj = true;
				  break;
		     }
			 Node novo = new Node(gradniki[i]);
			 int[] podd = obstajaPoddrevo(gradniki,i+1);
			 if(podd[0]==1){ // poddrevo obstaja. zgradimo ga
			 Node podDrevo = Drevo(gradniki,i+1,podd,depth); // v tem primeru se globina ne poveca
			 Node OP1 = stackOP.pop();
			 novo.leftChild = OP1;
			 novo.rightChild = podDrevo;
			 novo.index = podDrevo.index;
			 novo.depth = podDrevo.depth;
			 stackOP.push(novo);
			 i = podDrevo.index; // i sedaj kaze na naslednji OPERATOR !! vse prej je ze zgrajeno in caka na stackOP 
			    if(podDrevo.depth < depth){
					koncaj = true; 
				}
			 }else{
			 stackFnc.push(novo);
			 i++;
			 }
		 break;
		 default: // ko dobimo operand
			 if(gradniki[i].contains("(")){// zgradimo poddrevo
				   gradniki[i] = gradniki[i].substring(1); // odstranimo 1 '('
				   podd = new int[2];
				   podd[0] = 1; // gradimo poddrevo
				   podd[1] = -1; //trenutno poddrevo se bo zakljucilo z ')'
				   Node podDrevo = Drevo(gradniki,i,podd,depth+1); // rekurzija , globina se poveca
				   if(stackFnc.isEmpty()){ // to poddrevo je na levi strani nekega OPERATORJA. potisnemo ga na stackOP kot OP1
					stackOP.push(podDrevo);
				    i = podDrevo.index; // nadaljujemo z gradnjo od tam, kjer smo koncali poddrevo
				   }else{ // to poddrevo je na desni strani nekega OPERATORJA. izvedemo operacijo
					   Node nalozi = Operand(podDrevo,podDrevo.index,stackFnc,stackOP,podDrevo.depth);
					   stackOP.push(nalozi);
					   i = podDrevo.index;
				   }
			       if(podDrevo.depth < depth){
				   koncaj = true; 
			       }
			 }else if(gradniki[i].contains(")")){ // zakljucek poddrevesa
				 String gr = gradniki[i];
				 int decreaseDepth = depth;
				  while(gr.contains(")")){ // potrebno dolociti pravo globino umika nazaj
				   decreaseDepth--;
                  gr = gr.substring(0, gr.length()-1);
				  }
			   koncaj = true;
			   String gradnik = gradniki[i].replaceAll("[)]", "");
			   Node nov = new Node(gradnik);
			   Node nalozi = Operand(nov,i+1,stackFnc,stackOP,decreaseDepth);// obdelamo operand
			   stackOP.push(nalozi);
		     }else if(!stackFnc.isEmpty()){ // v tem primeru moramo izvrsiti operator na stacku operatorjev nad novim operandom in tistim na stacku operandov
			   Node nov = new Node(gradniki[i]);
		       Node nalozi = Operand(nov,i+1,stackFnc,stackOP,depth);// obdelamo operand
			   stackOP.push(nalozi);
			   i++;
			 }else{ // ce na stacku operatorjev nimamo nobenega, potem mora biti tudi stack operandov prazen. operand zato zgolj potisnemo nanj
			   Node nalozi = new Node(gradniki[i]);
			   stackOP.push(nalozi);
			   i++;
			 }
		 break;
		 } 
	  if(koncaj){
	  break;
	  }
	 }
	 return stackOP.pop(); // vrne poddrevo oz celotno drevo
 }
 ///////////////////////////////////////////////////////////////////////////// funkcija ki obdela operand
 public static Node Operand(Node nov,int i,Stack<Node> stackFnc,Stack<Node> stackOP,int depth){ 
	  boolean finNot = false;
	  while(stackFnc.peek().vrednost.equals("NOT")){ // v primeru da vrednost moramo negirati in  da je vec NOT zapovrstjo
	   Node tmp = stackFnc.pop();
	   tmp.leftChild = nov;
	   if(stackFnc.isEmpty()){ // v primeru da so bili na stackFnc sami NOT je stackOP prazen. v tem primeru nad novim operandom ne izvajamo nicesar ampak se premaknemo v ukazu naprej. naslednji gradnik je OPERATOR
		 tmp.index = i;
		 tmp.depth = depth;
		 stackOP.push(tmp);
		 finNot = true;
		 break;
	   }else{ // na stackFnc imamo OPERATOR
		 nov = tmp;
		 if(!stackFnc.peek().vrednost.equals("NOT")){ // ce na stackFnc nimamo NOT imamo OR ali ALI. v tem primeru je ravno obdelani operand 2 torej moramo izvesti operacijo. zanko zakljucimo
		 break;
		 }
	   }
	  }if(!finNot){ // ce ne bomo izvedli operacije zgolj nadaljujemo z gradnjo drevesa. naslednji gradnik je OPERATOR
         // v primeru da stackFnc ni prazen izvedemo operacijo
		  Node Operator = stackFnc.pop();
		  Node OP1 = stackOP.pop();
		  Operator.leftChild = OP1;
		  Operator.rightChild = nov;
		  Operator.index = i;
		  Operator.depth = depth;
		  stackOP.push(Operator);
	  }
	 
	 return stackOP.pop();
 }
 //////////////////////////////////////////////////////////////// funkcija se klice ko je trenutni operator OR
 public static int[] obstajaPoddrevo(String[] gradniki,int i){ 
	 int[] obstaja = new int[2];
	 int mode = 1;
	 int counter = 0;
	 while(true){
		 if(i == gradniki.length){ // konec  
			   obstaja[1] = 3;
			   break;
		 }
		 if(mode == 1){
		   if(gradniki[i].equals("AND")){ // poddrevo obstaja
	          obstaja[0] = 1;
		   }else if(gradniki[i].equals("OR")){ // poddrevo se zakljuci z OR ce obstaja
			  obstaja[1] = 0;
			  break;
		   }else if(gradniki[i].charAt(0)=='('){ // gremo v to podpoddrevo in cez
				  mode = 0;
			      counter++;
		   }else if(gradniki[i].charAt(gradniki[i].length()-1)==')'){
			   counter--;
			   if(counter < 0){
				obstaja[1] = -1;
			    break;  
			   }
		   }
		 }else{
			 if(gradniki[i].charAt(0)=='('){
				 String gradnik = gradniki[i];
				 while(gradnik.charAt(0)=='('){
				 counter++;
				 gradnik = gradnik.substring(1);
				 }
			 }
			 if(gradniki[i].charAt(gradniki[i].length()-1)==')'){
				 String gradnik = gradniki[i];
				 while(gradnik.charAt(gradnik.length()-1)==')'){
				 counter--;
				 gradnik = gradnik.substring(0, gradnik.length()-1);
				 }
			 }
			 if(counter == 0){
				mode = 1; 
			 }
			 if(counter < 0){
			 obstaja[1] = -1;
			 break;
			 }
		 }
		 i++;
	 }
	 return obstaja;
 }
 //////////////////7.naloga
 /////////////////////////////////////////////////////////////////// sprehodi se cez drevo in nam vrne HashSet vseh spremenljivk
 public static HashSet<String> vrniSeznamSpremenljivk(Node root){ 
	 
	  HashSet<String> spremenljivke = new HashSet<String>();
	  if(root != null){ // ce je prazen list vrnemo prazen HashSet
	     if(root.vrednost.equals("NOT") || root.vrednost.equals("AND") || root.vrednost.equals("OR")){ // v primeru da nismo v listu
		  HashSet<String> levoPoddrevo = vrniSeznamSpremenljivk(root.leftChild);
		  HashSet<String> desnoPoddrevo = vrniSeznamSpremenljivk(root.rightChild);
		  spremenljivke.addAll(levoPoddrevo);
		  spremenljivke.addAll(desnoPoddrevo);
		  
	     }else{ // smo v listu
	        if(!root.vrednost.equals("TRUE") && !root.vrednost.equals("FALSE") ){
	    	
	    	 spremenljivke.add(root.vrednost);
	        }	 
	     }
	  }
	  return spremenljivke;
 } 
 ////////////////////////////////////////////// za vse kombinacije vrednosti spremenljivk klice funkcijo VrednostIzraza
 public static int stVsehTrue(Node root,ArrayList<String> spremenljivke){ 
	 int stVsehTrue = 0;
	 char[] vrednostiSpremenljivk = new char[spremenljivke.size()]; // v tem arrayu bomo na mestu i hranili, ali ima spremenljivka na mestu i vrednost true(1) or false(0)
	 double spr = (double) spremenljivke.size();
	 int i = 0;
	 int stVsehMoznosti = (int) Math.pow(2, spr);
	 while(i < stVsehMoznosti){
		if(VrednostIzraza(root,spremenljivke,vrednostiSpremenljivk)){
		 stVsehTrue++;	
		}
		if(i == stVsehMoznosti-1){
		break;	
		}
		i++;
		vrednostiSpremenljivk = posodobi(i,vrednostiSpremenljivk);
	 }
	 return stVsehTrue;
 }
 //////////////////////////////////////////posodobi vrednosti Spremenljivk
 public static char[] posodobi(int i,char[] spremenljivke){
	 String binarno = Integer.toBinaryString(i);
	 int index = spremenljivke.length - binarno.length();
	 int j = 0;
	 while(index < spremenljivke.length){
		 spremenljivke[index] = binarno.charAt(j);
		 index++;
		 j++;
	 }
	 return spremenljivke;
 }
 ////////////////////////////////////////////////////////////// za doloceno konfiguracijo vrednosti spremenljivk preveri vrednost celotnega izraza
 public static boolean VrednostIzraza(Node root,ArrayList<String> spremenljivke,char[] vrednostiSpremenljivk){
	 boolean vrednost = false;
	 if(root.vrednost.equals("AND")){ // AND
		 boolean left = VrednostIzraza(root.leftChild,spremenljivke,vrednostiSpremenljivk);
		 boolean right = VrednostIzraza(root.rightChild,spremenljivke,vrednostiSpremenljivk);
		 vrednost = left & right;
	 }else if(root.vrednost.equals("OR")){ //OR
		 boolean left = VrednostIzraza(root.leftChild,spremenljivke,vrednostiSpremenljivk);
		 boolean right = VrednostIzraza(root.rightChild,spremenljivke,vrednostiSpremenljivk);
		 vrednost = left | right;
     }else if(root.vrednost.equals("NOT")){ // NOT
	  	 boolean Child= VrednostIzraza(root.leftChild,spremenljivke,vrednostiSpremenljivk);
	  	 vrednost = !Child;
	 }else{ // listi
		 if(root.vrednost.equals("TRUE")){ //TRUE
		  vrednost = true;	 
		 }else if(root.vrednost.equals("FALSE")){ //FALSE
			vrednost = false; 
		 }else{ // Spremenljivka
			 int index = spremenljivke.indexOf(root.vrednost);
		   if(Character.getNumericValue(vrednostiSpremenljivk[index]) == 1){
			  vrednost = true; 
		   }else{
			  vrednost = false; 
		   }
		 } 
	 }
	return vrednost;
 }
}
class Node {
	String vrednost;
	int index,depth;
	Node leftChild,rightChild;
	public Node(String value){
	 vrednost = value;
	 index = 0;
	 depth = 0;
	 leftChild = null;
	 rightChild = null;
	}
}