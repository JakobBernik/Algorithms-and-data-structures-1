import java.io.*;
import java.util.*;

public class Naloga6 {
	 	
	 public static String Solution;
 public static void main(String[] args) throws IOException{
	 File IZ = new File(args[0]);
	 File V = new File(args[1]);
	 FileReader in = new FileReader(IZ);
	 FileWriter out = new FileWriter(V);
	   
    Scanner sc = new Scanner(in);
    Solution = null;
    String data = sc.nextLine();
    sc.close();
    String[] gradniki = data.split(" ");
    int[] poddrevo = new int[2];
    poddrevo[0] = 0;
    poddrevo[1] = 0;
    ExpNode root = Drevo(gradniki,0,poddrevo,0); // zgradimo drevo ter vrnemo njegov root
    int globina = preorder(root,0); // v Solution shranimo koncno resitev, poleg tega pa vrnemo najvecjo globino drevesa, Solution se konca z vejico
    Solution = Solution.substring(0, Solution.length()-1);// odstranimo zadnjo ','
    //System.out.println(Solution + "\n" + globina);
    out.write(Solution + System.getProperty( "line.separator" ) + globina + System.getProperty( "line.separator" ));
    out.flush();
    out.close();
 }
///////////////////////////////////////////////////////////////////////// 
 public static ExpNode Drevo(String[] gradniki,int i,int[] poddrevo,int depth){ // depth nam kontrolira kako globoko je nase trenutno poddrevo drevesa glede na '(' ')'
	 Stack<ExpNode> stackOP = new Stack<ExpNode>(); // operandi
	 Stack<ExpNode> stackFnc = new Stack<ExpNode>(); // operatorji
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
			ExpNode novi = new ExpNode(gradniki[i]);
			stackFnc.push(novi);
			i++;
		    break;
		 case "OR": // ko dobimo OR moramo preveriti ce obstaja poddrevo (AND ima prednost). ce drevesa ni nadaljujemo normalno z gradnjo
			 if(poddrevo[0]==1 && poddrevo[1] == 0){ // prisli smo do konca nasega poddrevesa z OR
				 koncaj = true;
				  break;
		     }
			  ExpNode novo = new ExpNode(gradniki[i]);
			 int[] podd = obstajaPoddrevo(gradniki,i+1);
			 if(podd[0]==1){ // poddrevo obstaja. zgradimo ga
			  ExpNode podDrevo = Drevo(gradniki,i+1,podd,depth); // v tem primeru se globina ne poveca
			  ExpNode OP1 = stackOP.pop();
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
				   ExpNode podDrevo = Drevo(gradniki,i,podd,depth+1); // rekurzija , globina se poveca
				   if(stackFnc.isEmpty()){ // to poddrevo je na levi strani nekega OPERATORJA. potisnemo ga na stackOP kot OP1
					stackOP.push(podDrevo);
				    i = podDrevo.index; // nadaljujemo z gradnjo od tam, kjer smo koncali poddrevo
				   }else{ // to poddrevo je na desni strani nekega OPERATORJA. izvedemo operacijo
					   ExpNode nalozi = Operand(podDrevo,podDrevo.index,stackFnc,stackOP,podDrevo.depth);
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
			   ExpNode nov = new ExpNode(gradnik);
			   ExpNode nalozi = Operand(nov,i+1,stackFnc,stackOP,decreaseDepth);// obdelamo operand
			   stackOP.push(nalozi);
		     }else if(!stackFnc.isEmpty()){ // v tem primeru moramo izvrsiti operator na stacku operatorjev nad novim operandom in tistim na stacku operandov
			   ExpNode nov = new ExpNode(gradniki[i]);
		       ExpNode nalozi = Operand(nov,i+1,stackFnc,stackOP,depth);// obdelamo operand
			   stackOP.push(nalozi);
			   i++;
			 }else{ // ce na stacku operatorjev nimamo nobenega, potem mora biti tudi stack operandov prazen. operand zato zgolj potisnemo nanj
			   ExpNode nalozi = new ExpNode(gradniki[i]);
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
 ///////////////////////////////////////////////////////////////////////////
 public static ExpNode Operand(ExpNode nov,int i,Stack<ExpNode> stackFnc,Stack<ExpNode> stackOP,int depth){ // funkcija ki obdela operand
	  boolean finNot = false;
	  while(stackFnc.peek().vrednost.equals("NOT")){ // v primeru da vrednost moramo negirati in  da je vec NOT zapovrstjo
	   ExpNode tmp = stackFnc.pop();
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
		  ExpNode Operator = stackFnc.pop();
		  ExpNode OP1 = stackOP.pop();
		  Operator.leftChild = OP1;
		  Operator.rightChild = nov;
		  Operator.index = i;
		  Operator.depth = depth;
		  stackOP.push(Operator);
	  }
	 return stackOP.pop();
 }
 public static int[] obstajaPoddrevo(String[] gradniki,int i){ // funkcija se klice ko je trenutni operator OR
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
 public static int preorder(ExpNode root,int globina){
	 if(Solution == null){	 
	 Solution = root.vrednost + ",";
	 globina++;
	 int left = preorder(root.leftChild,globina);
	 int right= preorder(root.rightChild,globina);
	 return Math.max(left, right);
	 }else{
		 if(root != null){
	      Solution += root.vrednost+",";
	      globina++;
	      int left = preorder(root.leftChild,globina);
	 	  int right= preorder(root.rightChild,globina);
	 	  return Math.max(left, right);
		 }else{
		  return globina;	 
		 }
	 }
 }
}
class ExpNode {
	String vrednost;
	int index,depth; // zgolj za pomoc pri gradnji drevesa (zaradi rekurzije)
	ExpNode leftChild,rightChild;
	public ExpNode(String value){
	 vrednost = value;
	 index = 0;
	 depth = 0;
	 leftChild = null;
	 rightChild = null;
	}
}
//------------------------------------------------------------------------------------------------------------------------------------------