/**
 * Created by Tirthmehta on 11/26/16.
 */
import java.io.*;
import java.util.HashMap;

/**
 * Created by Tirthmehta on 11/11/16.
 */
import java.util.*;

public class homework {

    public static int counterInfinite = 0;

    public static HashMap<String, ArrayList<String>> storekbhmap = new HashMap<String, ArrayList<String>>();

    public static String negate(String query)
    {
        if(query.contains("~"))
            return query.substring(1);
        else
            return "~"+query;
    }

    public static boolean dfs_function(Stack<String> querystack,int counterInfinite)
    {
      //  System.out.println("The first stack is "+querystack);
        while(!querystack.isEmpty())
        {
           /* if(querystack.isEmpty())
            {
                return true;
            }*/

            String firstel=querystack.pop();//not eats
           // System.out.println("the popped element is "+firstel);
            String querytp=negate(firstel);// eats

            String pred="";
            int index=-1;

            //GET PREDICATE STARTS-------------
            for(int i=0;i<querytp.length();i++)
            {
                while(querytp.charAt(i)!='(')
                {
                    pred+=querytp.charAt(i);
                    i++;
                }
                index=i;
                break;
            }
           // System.out.println(pred);
            // GET PREDICATE ENDS--------------


            //STACK ARGUMENTS
            String arguments1[]=querytp.substring(index+1,querytp.length()-1).split(",");


            //STACK ARGUMENT ENDS


            //FOR SEARCHING IN KB
            /*
            if(pred.contains("~"))
                pred=pred.substring(1);
            else
                pred="~"+pred;
            */

            //GOT SEARCHING PREDICATE KEY


            if(storekbhmap.containsKey(pred))
            {
                //GETS VALUES FOR A GIVEN PREDICATE KEY
                ArrayList<String> values=storekbhmap.get(pred);

                //This is first time we get predicate values.
                for(int i=0;i<values.size();i++)
                {

                    if(counterInfinite > 800)
                    {
                        //System.out.println("The counter is ****************************************************f"+counterInfinite);
                        return false;
                    }
                    String iter=values.get(i);
                    //System.out.println("iter "+iter);
                    ArrayList<String> ored=new ArrayList<String>();
                    String splitter[]=iter.split("\\|");

                    String match="";
                    for(String x:splitter) {
                        ored.add(x);
                        if(x.contains(pred))
                            match=x;
                        //System.out.print(x+" ");
                    }
                    String arguments2string="";
                    //System.out.println("match "+match);
                    for(int j=0;j<match.length();j++)
                    {
                        if(match.charAt(j)=='(')
                        {
                            j++;
                            while(match.charAt(j)!=')')
                            {
                                arguments2string+=match.charAt(j);
                                j++;
                            }
                            break;
                        }
                    }
                   // System.out.println("arg2 "+arguments2string);
                    //KB ARGUMENT LIST
                    String arguments2[]=arguments2string.split(",");



                    //Here we do unification with stack arg and matched predicate arguments
                    boolean result=unification(arguments1,arguments2);
               //     System.out.println("result is "+result);

                    //RESOLVING PART If unification result is true
                    if(result==true)
                    {
                        HashMap<String,String> hmapunify=new HashMap<String,String>();
                        for(int h=0;h<arguments1.length;h++)
                        {
                            String stackargs=arguments1[h];
                            String kbargs=arguments2[h];
                            if(!hmapunify.containsKey(kbargs))
                                hmapunify.put(kbargs,stackargs);

                        }
                      //  System.out.println("hmapunify "+hmapunify);
                        String newrule="";

                        //Copying querystack to copystack
                        Stack<String> copystack=new Stack<String>();
                        String stackarray[]= querystack.toArray(new String[querystack.size()]);
                        ArrayList<String> stackarraylist=new ArrayList<String>();
                        for(String x:stackarray)
                            stackarraylist.add(x);

                        for(int si=0;si<querystack.size();si++)
                        {
                            copystack.push(stackarray[si]);
                            // System.out.println("copystack ="+copystack);
                        }

                        for(int m=0;m<ored.size();m++)
                        {
                            String currentkbelement=ored.get(m);
                            // System.out.println("current "+currentkbelement);
                            Iterator it = hmapunify.entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry pair = (Map.Entry)it.next();
                                if(currentkbelement.contains((String)pair.getKey()))
                                    currentkbelement=currentkbelement.replace((String)pair.getKey(),(String)pair.getValue());
                                //it.remove(); // avoids a ConcurrentModificationException
                            }
                            //  System.out.println("after substitution: "+currentkbelement);
                            //   System.out.println("pred is: "+pred);


                          /* if(!currentkbelement.contains(pred)) {
                                System.out.println("i am inside if loop");
                               copystack.push(currentkbelement);
                                newrule+=currentkbelement+"|";

                            }*/

                            //to avoid adding resolved predicates in stack eg.A(Alice)
                            String checking="";
                            for(int f=0;f<currentkbelement.length();f++)
                            {

                                while(currentkbelement.charAt(f)!='(')
                                {
                                    checking+=currentkbelement.charAt(f)+"";
                                    f++;
                                }
                                break;
                            }
                        //    System.out.println("checking "+checking);
                        //    System.out.println("pred is "+pred);

                            if(!checking.equals(pred))  //Whtever remain after resolution
                            {
                               // System.out.println("i am inside the if loop");



                                String original=currentkbelement;
                                String temp="";
                                if(original.contains("~"))
                                    temp=original.substring(1);
                                else
                                    temp="~"+original;
                                int count=0;


                                for (Iterator<String> iterator = stackarraylist.iterator(); iterator.hasNext();) {
                                    String string = iterator.next();
                                    if (string.equals(temp)) {
                                        // Remove the current element from the iterator and the list.
                                        iterator.remove();
                                        count=1;
                                    }
                                }
                                if(count!=1)
                                    stackarraylist.add(original);




                                // copystack.push(currentkbelement);
                            }




                        }
                      //  System.out.println("Im innide dfs with query stack "+querystack);
                        Stack<String> finalstack=new Stack<String>();
                        for(String z:stackarraylist)
                            finalstack.push(z);
                      //  System.out.println("counter is "+counterInfinite);
                        boolean printing=dfs_function(finalstack,++counterInfinite);
                       // System.out.println("print dfs="+printing);
                        if(printing==true)
                        {
                            return true;
                        }
                    }
                }
                return false;
            }
            else
            {
                return false;
            }


        }
      //  System.out.println("exiting while loop");

        return true;//check this
    }

    public static void kbmaker(ArrayList<String> kblist2)
    {
        for(int k=0;k<kblist2.size();k++) {
            String w = kblist2.get(k);

            for (int i = 0; i < w.length(); i++) {

                if (w.charAt(i) >= 'A' && w.charAt(i) <= 'Z') {
                    if (i - 1 != -1) {
                        if (w.charAt(i - 1) == '~') {
                            String pred = "~";
                            int checkconstant = 0;
                            while (w.charAt(i) != '(') {
                                if (w.charAt(i) == ','|| w.charAt(i)==')') {
                                    checkconstant = 1;
                                    break;
                                }
                                pred += w.charAt(i) + "";
                                i++;
                            }
                            if (checkconstant == 0) {
                                if(storekbhmap.containsKey(pred))
                                {
                                    ArrayList<String> temper=storekbhmap.get(pred);
                                    temper.add(w);
                                    storekbhmap.remove(pred);
                                    storekbhmap.put(pred,temper);
                                }
                                else {
                                    ArrayList<String> temp = new ArrayList<String>();
                                    temp.add(w);
                                    storekbhmap.put(pred, temp);
                                }
                            }
                        } else {
                            //  System.out.println("hi im here");
                            String pred = "";
                            int checkconstant = 0;
                            while (w.charAt(i) != '(') {
                                if (w.charAt(i) == ',' || w.charAt(i)==')') {
                                    checkconstant = 1;
                                    break;
                                }
                                pred += w.charAt(i) + "";
                                i++;
                            }
                            if (checkconstant == 0) {
                                if(storekbhmap.containsKey(pred))
                                {
                                    ArrayList<String> temper=storekbhmap.get(pred);
                                    temper.add(w);
                                    storekbhmap.remove(pred);
                                    storekbhmap.put(pred,temper);
                                }
                                else {
                                    ArrayList<String> temp = new ArrayList<String>();
                                    temp.add(w);
                                    storekbhmap.put(pred, temp);
                                }
                            }
                        }
                    } else {
                        String pred = "";
                        int checkconstant = 0;
                        while (w.charAt(i) != '(') {
                            if (w.charAt(i) == ',') {
                                checkconstant = 1;
                                break;
                            }
                            pred += w.charAt(i) + "";
                            i++;
                        }
                        if (checkconstant == 0) {
                            if(storekbhmap.containsKey(pred))
                            {
                                ArrayList<String> temper=storekbhmap.get(pred);
                                temper.add(w);
                                storekbhmap.remove(pred);
                                storekbhmap.put(pred,temper);
                            }
                            else {
                                ArrayList<String> temp = new ArrayList<String>();
                                temp.add(w);
                                storekbhmap.put(pred, temp);
                            }
                        }
                    }
                }


            }
        }
    }

    public static boolean unification(String a[],String b[])
    {

        int counter=0;
        for(int i=0;i<a.length;i++)
        {
            String x=a[i]; //stack
            String y=b[i];   //kbstring

            if(x.charAt(0)>='a'&& x.charAt(0)<='z' && y.charAt(0)>='a' && y.charAt(0)<='z')
            {
                counter++;

            }
            else if(x.charAt(0)>='a'&& x.charAt(0)<='z' && y.charAt(0)>='A' && y.charAt(0)<='Z')
                counter++;
            else if(x.charAt(0)>='A'&& x.charAt(0)<='Z' && y.charAt(0)>='a' && y.charAt(0)<='z')
                counter++;
            else if(x.equals(y))
                counter++;


        }
        if(counter==a.length)
            return true;
        else
            return false;
    }

    public static String[] reader() throws IOException
    {

        FileReader fr=new FileReader("input.txt");
        BufferedReader br=new BufferedReader(fr);
        String c;
        int lines=0;
        while((c=br.readLine())!=null)
            lines++;
        String arr[]=new String[lines];
        int i=0;
        br.close();
        FileReader fr2=new FileReader("input.txt");
        BufferedReader br2=new BufferedReader(fr2);
        while((c=br2.readLine())!=null) {
            arr[i]=c;
            i++;
        }
        br.close();
        return arr;
    }
    public static void writeans(ArrayList<String> ans) throws IOException
    {
        FileWriter fw = new FileWriter("output.txt");
        PrintWriter pw = new PrintWriter(fw);

        for(int i=0;i<ans.size();i++)
        {
            pw.println(ans.get(i));
        }

        fw.close();
    }

    public static String distributivity(String a)
    {
        Stack<String> s=new Stack<String>();

        for(int i=a.length()-1;i>=0;i--)
        {
            // System.out.println(s);
            if(a.charAt(i)=='p')
            {
                String pred=a.substring(i,i+4);
                s.push(pred);
            }
            if(a.charAt(i)=='&')
            {
                String first=s.pop();
                String second=s.pop();
                String third=first+a.charAt(i)+second;
                // System.out.println(third);
                s.push(third);
            }
            if(a.charAt(i)=='~')
            {
                String newfirst="";
                String first=s.pop();
                newfirst="~"+first;

                s.push(newfirst);
            }
            if(a.charAt(i)=='|')
            {
                String first=s.pop();
                //    System.out.println("first "+first);
                String second=s.pop();
                //    System.out.println("second "+second);
                String third="";
                ArrayList<String> one=new ArrayList<String>();
                ArrayList<String> two=new ArrayList<String>();
                if(first.contains("&"))
                {

                    for(String d:first.split("&"))
                        one.add(d);
                }
                if(second.contains("&"))
                {
                    for(String d:second.split("&"))
                        two.add(d);
                }
                //    System.out.println("one "+one);
                //      System.out.println("two "+two);

                if(one.isEmpty() && two.isEmpty())
                {
                    third+=first+"|"+second;
                }
                if(one.isEmpty() && !two.isEmpty())
                {
                    for(String ee:two)
                    {
                        third+=first+"|"+ee;
                        third+="&";
                    }
                }
                if(!one.isEmpty() && two.isEmpty())
                {
                    for(String ee:one)
                    {
                        third+=second+"|"+ee;
                        third+="&";
                    }
                }
                if(!one.isEmpty() && !two.isEmpty())
                {
                    for(String ee:one)
                    {
                        for(String ee2:two)
                        {
                            third+=ee+"|"+ee2;
                            third+="&";
                        }
                    }
                }
                //  System.out.println("third in or"+third);
                if(third.charAt(third.length()-1)=='&')
                    third=third.substring(0,third.length()-1);
                s.push(third);
            }


        }
        return s.pop();
    }

    public static String removenegation(String a)
    {
        // System.out.println("this is what comes to remove negation "+a);
        Stack<String> s=new Stack<String>();
        for(int i=a.length()-1;i>=0;i--)
        {
            if(a.charAt(i)=='p')
            {
                String pred=a.substring(i,i+4);
                s.push(pred);
            }
            if(a.charAt(i)=='&'||a.charAt(i)=='|')
            {
                String first=s.pop();
                String second=s.pop();
                String third=first+a.charAt(i)+second;
                // System.out.println("& part "+third);
                s.push(third);
            }
            if(a.charAt(i)=='~')
            {
                String newfirst="";
                String first=s.pop();
                for(int j=0;j<first.length();j++)
                {
                    if(j==0 && first.charAt(j)=='p')
                    {
                        newfirst+="~";
                        newfirst+=first.substring(j,j+4);
                    }
                    else if(first.charAt(j)=='|')
                    {
                        newfirst+="&";
                    }
                    else if(first.charAt(j)=='&')
                    {
                        newfirst+="|";
                    }

                    else if(first.charAt(j)=='p')
                    {
                        if(first.charAt(j-1)=='~')
                        {
                            newfirst+=first.substring(j,j+4);
                        }
                        else
                        {
                            newfirst+="~"+first.substring(j,j+4);
                        }

                    }

                }
                if(newfirst.contains("&"))
                {
                    // System.out.println("yes it contains &");
                    ArrayList<String> a1=new ArrayList<String>();
                    for(String a111:newfirst.split("&"))
                    {
                        a111="("+a111+")";
                        a1.add(a111);
                    }
                    String tempnewfirst="";
                    for(String temper:a1)
                    {
                        tempnewfirst+=temper+"&";
                    }
                    tempnewfirst=tempnewfirst.substring(0,tempnewfirst.length()-1);
                    tempnewfirst="("+tempnewfirst+")";
                    newfirst=tempnewfirst;

                }

                s.push(newfirst);
            }

        }
        return s.pop();
    }

    public static String removeimplication(String a)
    {
        //  System.out.println("what is passed to remove implication "+a);
        Stack<String> s=new Stack<String>();
        for(int i=a.length()-1;i>=0;i--)
        {
            if(a.charAt(i)=='p')
            {
                String pred=a.substring(i,i+4);
                s.push(pred);
            }
            if(a.charAt(i)=='&'||a.charAt(i)=='|')
            {
                String first=s.pop();
                String second=s.pop();
                String third=first+a.charAt(i)+second;
                s.push(third);
            }
            if(a.charAt(i)=='~')
            {
                String newfirst="";
                String first=s.pop();
                first="~("+first+")";
                s.push(first);
            }
            if(a.charAt(i)=='>')
            {
                String first="~("+s.pop()+")";
                String second=s.pop();
                String third=first+"|"+second;
                s.push(third);
            }

        }
        // System.out.println("after implication removal "+s.peek());
        return s.pop();

    }

    public static int checkprec(char a)
    {
        if(a=='~')
            return 4;
        if(a=='&')
            return 3;
        if(a=='|')
            return 2;
        if(a=='>')
            return 1;
        if(a=='(')
            return 0;
        if(a==')')
            return 0;
        return 0;

    }

    public static String prefix(String x)
    {
        Stack<Character> s=new Stack<Character>();
        String prefixexp="";
        for(int i=x.length()-1;i>=0;i--)
        {
            if(x.charAt(i)==')')
            {
                s.push(x.charAt(i));
            }
            if(x.charAt(i)=='p')
            {
                StringBuilder x1=new StringBuilder(x.substring(i,i+4));
                x1.reverse();

                prefixexp+=x1.toString();
            }
            if(x.charAt(i)=='&'||x.charAt(i)=='|'||x.charAt(i)=='>'||x.charAt(i)=='~')
            {

                if(s.isEmpty())
                    s.push(x.charAt(i));
                else
                {
                    int prec1=checkprec(x.charAt(i));
                    int prec2=checkprec(s.peek());
                    if(prec1>prec2)
                    {
                        s.push(x.charAt(i));
                    }
                    else
                    {
                        while(prec1<prec2 && (!(s.isEmpty())))
                        {
                            prefixexp+=s.pop();
                            if(!s.isEmpty())
                            {prec2=checkprec(s.peek());}

                        }
                        s.push(x.charAt(i));

                    }

                }
            }
            if(x.charAt(i)=='(')
            {
                // System.out.println(prefixexp);
                //  System.out.println(s);
                while(s.peek()!=')')
                {
                    // System.out.println("hi");
                    prefixexp+=s.pop();
                }
                s.pop();
            }

        }
        while(!s.isEmpty())
            prefixexp+=s.pop()+"";
        StringBuilder x2=new StringBuilder(prefixexp);
        x2.reverse();
        return x2.toString();
    }




    public static void main(String[] args) throws IOException {

        String list[]=reader();
        int querysize=Integer.parseInt(list[0]);
        ArrayList<String> query=new ArrayList<String>();
        for(int i=0;i<querysize;i++)
        {
            query.add(list[i+1]);
        }
        int kbsize=Integer.parseInt(list[querysize+1]);
        ArrayList<String> givenkb=new ArrayList<String>();
        for(int i=0;i<kbsize;i++)
        {
            givenkb.add(list[i+querysize+2]);
        }

        /*
        System.out.println("querysize"+querysize);
        System.out.println("query"+query);
        System.out.println("kbsize "+kbsize);
        System.out.println("given kb "+givenkb);
*/



        // String a="B(x,y) & C(x,y) => A(x)";
        //hashmaps

        HashMap<String, String> hmap1 = new HashMap<String, String>();
        HashMap<String, String> hmap2 = new HashMap<String, String>();
        int hmapcount = 0;


        //anslist
        ArrayList<String> kblist=new ArrayList<String>();

        for(int k=0;k<givenkb.size();k++) {
            String a = givenkb.get(k);
            String temparr[] = a.split(" ");
            String b = "";
            for (int i = 0; i < temparr.length; i++)
                b += temparr[i];
            // System.out.println(b);
            b = b.replace("=>", ">");
          //  System.out.println("after removing spaces" +b);


            String c = "";
            for (int i = 0; i < b.length(); i++) {
                if (b.charAt(i) >= 'A' && b.charAt(i) <= 'Z') {
                    String pred = b.charAt(i) + "";
                    i++;
                    while (b.charAt(i) != ')') {
                        pred += b.charAt(i) + "";
                        i++;
                    }
                    i--;
                    hmapcount++;
                    if (hmapcount > 0 && hmapcount <= 9) {
                        String ppart = "p00" + hmapcount;
                        pred += ")";
                        hmap1.put(ppart, pred);
                        hmap2.put(pred, ppart);
                        c += ppart;
                    } else if (hmapcount >= 10 && hmapcount <= 99) {
                        String ppart = "p0" + hmapcount;
                        pred += ")";
                        hmap1.put(ppart, pred);
                        hmap2.put(pred, ppart);
                        c += ppart;
                    } else if (hmapcount >= 100 && hmapcount <= 999) {
                        String ppart = "p" + hmapcount;
                        pred += ")";
                        hmap1.put(ppart, pred);
                        hmap2.put(pred, ppart);
                        c += ppart;
                    }
                    i++;


                } else {
                    c += b.charAt(i) + "";
                }
            }
          //  System.out.println("hmap1 "+hmap1);


            // System.out.println("prefix form with p's" + c);
            //Prefix
            //    System.out.println("c is "+c);

            String prefixconvert1 = prefix(c);
          //  System.out.println("prefix 1 "+prefixconvert1);

            String prefixremoveimplication="";
            //Remove implication
            if(prefixconvert1.contains(">"))
            {
                prefixremoveimplication = removeimplication(prefixconvert1);
               // System.out.println("prefix remove implication "+prefixremoveimplication);
                prefixremoveimplication=prefix(prefixremoveimplication);
             //   System.out.println("after remove iml prefix "+prefixremoveimplication);
            }
            else
            {
                prefixremoveimplication=prefixconvert1;
            }

            String after_inwardnegation="";
            if(prefixremoveimplication.contains("~"))
            {
                after_inwardnegation = removenegation(prefixremoveimplication);
             //   System.out.println(" remove negation "+after_inwardnegation);
                after_inwardnegation=prefix(after_inwardnegation);
            //    System.out.println(" remove negation prefix "+after_inwardnegation);
            }
            else
            {
                after_inwardnegation=prefixremoveimplication;
            }

            //Prefix preparation for negation inward
            // System.out.println("remove implication ans "+prefixremoveimplication);

            // prefixremoveimplication="~(~p001|p002)|p003";

            //Negation inward


            //  System.out.println("negation inward "+after_inwardnegation);
            //Distributivity
            //part1 prefix again

            //    System.out.println("prefix  before distributivity "+startingpartdistr_prefixing);

            //part2 handling distributivity

            String secondpartdistributivity = distributivity(after_inwardnegation);
         //   System.out.println("distributivity ans "+secondpartdistributivity);


            ArrayList<String> andsplitter = new ArrayList<String>();
            for (String putter : secondpartdistributivity.split("&"))
                andsplitter.add(putter);


            // System.out.println(hmap1);


            for (String x : andsplitter) {
                String ans = "";
                for (int i = 0; i < x.length(); i++) {

                    if (x.charAt(i) == 'p') {
                        String temp = x.substring(i, i + 4);
                        if (hmap1.containsKey(temp)) {
                            // System.out.println("hi i matched the key");
                            String newtemp = hmap1.get(temp);
                            ans += newtemp;
                            i += 3;
                        }
                    } else {
                        ans += x.charAt(i) + "";
                    }
                }
                kblist.add(ans);
            }
        }
      //  System.out.println("first kblist "+kblist);










       /* System.out.println(prefixconvert1);
        System.out.println(prefixremoveimplication);
        System.out.println(prefix_afterremovalimplies);
        System.out.println(after_inwardnegation);
        System.out.println(startingpartdistr_prefixing);

        System.out.println(secondpartdistributivity);

        System.out.println(andsplitter);


        System.out.println("kb list:"+kblist);

*/


        //Storing KB
        //Step1 standardize the variables:

        ArrayList<String> kblist2 = new ArrayList<String>();

        int hmaptrycount = 0;
        String tobeputinkblist2="";
        for(int k=0;k<kblist.size();k++) {

            String trying = kblist.get(k);
            String tryingreplacer = "";
            HashMap<String, String> hmaptry = new HashMap<String, String>();

            for (int i = 0; i < trying.length(); i++) {

                if (trying.charAt(i) == '(') {
                    // tryingreplacer+=trying.charAt(i);

                    String parameters = "";
                    // hmaptrycount++;
                    i++;
                    while (trying.charAt(i) != ')') {
                        parameters += trying.charAt(i) + "";

                        i++;
                    }
                    //  System.out.println("my pars are "+parameters);
                    // i++;
                    if (parameters.contains(",")) {
                        //  System.out.println("i came into ,");
                        String parlist[] = parameters.split(",");


                        for (String par1 : parlist) {
                            if (par1.charAt(0) >= 'a' && par1.charAt(0) <= 'z') {
                                if (!hmaptry.containsKey(par1)) {
                                    hmaptrycount++;
                                    String pping = "";
                                    if (hmaptrycount > 0 && hmaptrycount <= 9) {
                                        pping = "p00" + hmaptrycount;
                                    } else if (hmaptrycount > 9 && hmaptrycount <= 99) {
                                        pping = "p0" + hmaptrycount;
                                    } else if (hmaptrycount > 99 && hmaptrycount <= 999) {
                                        pping = "p" + hmaptrycount;
                                    }
                                    // tryingreplacer+=pping+",";
                                    hmaptry.put(par1, pping);
                                }
                                //tryingreplacer=tryingreplacer.substring(0,tryingreplacer.length()-1);
                            }
                        }

                    } else {
                        // System.out.println("i didnt come into ,");
                        //  System.out.println(parameters);
                        if (parameters.charAt(0) >= 'a' && parameters.charAt(0) <= 'z') {
                            if (!hmaptry.containsKey(parameters)) {
                                hmaptrycount++;
                                String pping = "";
                                if (hmaptrycount > 0 && hmaptrycount <= 9) {
                                    pping = "p00" + hmaptrycount;
                                } else if (hmaptrycount > 9 && hmaptrycount <= 99) {
                                    pping = "p0" + hmaptrycount;
                                } else if (hmaptrycount > 99 && hmaptrycount <= 999) {
                                    pping = "p" + hmaptrycount;
                                }
                                //   tryingreplacer+=pping;

                                hmaptry.put(parameters, pping);
                            }
                        }
                    }


                }


            }
          //  System.out.println("hmaptry is "+hmaptry);
            // System.out.println("trying replace " + tryingreplacer);

            Iterator it = hmaptry.entrySet().iterator();
            String newtrying = trying;

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                //System.out.println(pair.getKey() + " " + pair.getValue());
                String pre="";
                for(int d=0;d<newtrying.length();d++)
                {
                    if(newtrying.charAt(d)=='(')
                    {
                        pre+=newtrying.charAt(d);
                        d++;
                        String toreplace="";
                        while(newtrying.charAt(d)!=')')
                        {
                            toreplace+=newtrying.charAt(d);
                            d++;
                        }
                        pre+=toreplace.replace(pair.getKey().toString(), pair.getValue().toString());
                        pre+=')';

                    }
                    else {
                        pre += newtrying.charAt(d);
                    }





                }

                // newtrying = newtrying.replace(pair.getKey().toString(), pair.getValue().toString());
                // System.out.println(newtrying);
                tobeputinkblist2=pre;
                newtrying=pre;
                it.remove(); // avoids a ConcurrentModificationException
            }
            if(!tobeputinkblist2.equals(""))
                kblist2.add(tobeputinkblist2);
            else
                kblist2.add(newtrying);
            tobeputinkblist2="";

        }
       // System.out.println("kblist2 is "+kblist2);


        //end standardizing variables


        //KB STORAGE STARTS----------------


        //start storing the kb
        //HASHMAP FOR KB-STORAGE
        kbmaker(kblist2);


/*
        for(int k=0;k<kblist2.size();k++) {
            String w = kblist2.get(k);

            for (int i = 0; i < w.length(); i++) {

                if (w.charAt(i) >= 'A' && w.charAt(i) <= 'Z') {
                    if (i - 1 != -1) {
                        if (w.charAt(i - 1) == '~') {
                            String pred = "~";
                            int checkconstant = 0;
                            while (w.charAt(i) != '(') {
                                if (w.charAt(i) == ','|| w.charAt(i)==')') {
                                    checkconstant = 1;
                                    break;
                                }
                                pred += w.charAt(i) + "";
                                i++;
                            }
                            if (checkconstant == 0) {
                                if(storekbhmap.containsKey(pred))
                                {
                                    ArrayList<String> temper=storekbhmap.get(pred);
                                    temper.add(w);
                                    storekbhmap.remove(pred);
                                    storekbhmap.put(pred,temper);
                                }
                                else {
                                    ArrayList<String> temp = new ArrayList<String>();
                                    temp.add(w);
                                    storekbhmap.put(pred, temp);
                                }
                            }
                        } else {
                            System.out.println("hi im here");
                            String pred = "";
                            int checkconstant = 0;
                            while (w.charAt(i) != '(') {
                                if (w.charAt(i) == ',' || w.charAt(i)==')') {
                                    checkconstant = 1;
                                    break;
                                }
                                pred += w.charAt(i) + "";
                                i++;
                            }
                            if (checkconstant == 0) {
                                if(storekbhmap.containsKey(pred))
                                {
                                    ArrayList<String> temper=storekbhmap.get(pred);
                                    temper.add(w);
                                    storekbhmap.remove(pred);
                                    storekbhmap.put(pred,temper);
                                }
                                else {
                                    ArrayList<String> temp = new ArrayList<String>();
                                    temp.add(w);
                                    storekbhmap.put(pred, temp);
                                }
                            }
                        }
                    } else {
                        String pred = "";
                        int checkconstant = 0;
                        while (w.charAt(i) != '(') {
                            if (w.charAt(i) == ',') {
                                checkconstant = 1;
                                break;
                            }
                            pred += w.charAt(i) + "";
                            i++;
                        }
                        if (checkconstant == 0) {
                            if(storekbhmap.containsKey(pred))
                            {
                                ArrayList<String> temper=storekbhmap.get(pred);
                                temper.add(w);
                                storekbhmap.remove(pred);
                                storekbhmap.put(pred,temper);
                            }
                            else {
                                ArrayList<String> temp = new ArrayList<String>();
                                temp.add(w);
                                storekbhmap.put(pred, temp);
                            }
                        }
                    }
                }


            }
        }
        */
      //  System.out.println("kb hashmap "+storekbhmap);



        //KB STORAGE ENDS------------------------------

        //trying it out on a basic example
        //query1 after negation




        //small fish example implementing

/*
        Stack<String> querystack=new Stack<String>();
        counterInfinite=0;
        String querytp="~Child(Scrooge)";
        querytp=querytp.replace(" ","");
        if(querytp.contains("~"))
            querytp=querytp.substring(1);
        else
            querytp="~"+querytp;
        querystack.push(querytp);
        boolean res=dfs_function(querystack,counterInfinite);

        if (res == true)
        {
            //answers.add("TRUE");
            System.out.println("the answer is TRUE");

        }
        else
        {
           // answers.add("FALSE");
            System.out.println("the answer is FALSE");
        }
        */


        //DFS FUNCTION STARTS

        ArrayList<String> answers=new ArrayList<String>();
        for(int an=0;an<querysize;an++)
        {
            counterInfinite=0;
            Stack<String> querystack=new Stack<String>();
            String querytp=query.get(an);
            querytp=querytp.replace(" ","");
            if(querytp.contains("~"))
                querytp=querytp.substring(1);
            else
                querytp="~"+querytp;

            querystack.push(querytp);

            boolean res=dfs_function(querystack,counterInfinite);

            if (res == true)
            {
                answers.add("TRUE");
               // System.out.println("the answer is TRUE");

            }
            else
            {
                answers.add("FALSE");
                //System.out.println("the answer is FALSE");
            }
        }
       // System.out.println("list of ans are "+answers);





        writeans(answers);



    }
}

