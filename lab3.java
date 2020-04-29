import java.util.Hashtable;
import java.util.stream.Stream;
import java.util.List;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.ArrayList;
import java.lang.String;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class lab2 {
    public static void main(String[] args) 
    {

        //REDO: Hashtable<String, Object> from name -> object of instruction
        //Each instruction has: Name, Format, function code, opcode
        //empty initially: fill in registers / immediate / address / shamt

        //and, or, add, addi, sll, sub, slt, beq, bne, lw, sw, j, jr, and jal
        List<List<String>> lineList = new ArrayList<List<String>>();

        Hashtable<String, instructionObject> lineObjectTable = new Hashtable<String, instructionObject>();
        lineObjectTable.put("and", new instructionObject("and", "R", "100100", "000000"));
        lineObjectTable.put("or", new instructionObject("or", "R", "100101", "000000"));
        lineObjectTable.put("add", new instructionObject("add", "R", "100000", "000000"));
        lineObjectTable.put("sub", new instructionObject("sub", "R", "100010", "000000"));
        lineObjectTable.put("addi", new instructionObject("addi", "I", "", "001000"));
        lineObjectTable.put("sll", new instructionObject("sll", "RS", "000000", "000000"));
        lineObjectTable.put("slt", new instructionObject("slt", "R", "101010", "000000"));
        lineObjectTable.put("beq", new instructionObject("beq", "I", "", "000100"));
        lineObjectTable.put("bne", new instructionObject("bne", "I", "", "000101"));
        lineObjectTable.put("lw", new instructionObject("lw", "IS", "", "100011"));
        lineObjectTable.put("sw", new instructionObject("sw", "IS", "", "101011"));
        lineObjectTable.put("j", new instructionObject("j", "J", "", "000010"));
        lineObjectTable.put("jr", new instructionObject("jr", "RJ", "001000", "000000"));
        lineObjectTable.put("jal", new instructionObject("jal", "J", "", "000011"));

        Hashtable<String, String> registerTable = new Hashtable<String, String>();
        registerTable.put("zero","00000");
        registerTable.put("0","00000");
        registerTable.put("v0","00010");
        registerTable.put("v1","00011");
        registerTable.put("a0","00100");
        registerTable.put("a1","00101");
        registerTable.put("a2","00110");
        registerTable.put("a3","00111");
        registerTable.put("t0","01000");
        registerTable.put("t1","01001");
        registerTable.put("t2","01010");
        registerTable.put("t3","01011");
        registerTable.put("t4","01100");
        registerTable.put("t5","01101");
        registerTable.put("t6","01110");
        registerTable.put("t7","01111");
        registerTable.put("s0","10000");
        registerTable.put("s1","10001");
        registerTable.put("s2","10010");
        registerTable.put("s3","10011");
        registerTable.put("s4","10100");
        registerTable.put("s5","10101");
        registerTable.put("s6","10110");
        registerTable.put("s7","10111");
        registerTable.put("t8","11000");
        registerTable.put("t9","11001");
        registerTable.put("sp","11101");
        registerTable.put("ra","11111");

        Hashtable<String, Integer> labels = new Hashtable<String, Integer>();

        //String x = instructionTable.get("jal");
        //System.out.println(x);
        //Read in .asm file
        
        //instructionObject i = new instructionObject("1", "1", "1", "1");
        //System.out.println(i.name);
        //System.out.println(i.register1);

	    File asmFile = new File(args[0]);
        int address = 0;

	    try(Stream<String> instructions = Files.lines(asmFile.toPath())){
            List<String> instList = instructions.map(String::trim)
                                    .filter(line -> line.length() > 0)
                                    .collect(Collectors.toList());

             for(int j=0;j<instList.size();j++){
                String line = instList.get(j);
                if(line.charAt(0) == '#'){
                    continue;
                }
                else{
                    int size = line.length() -1;
                    //System.out.print("line: ");
                    //System.out.println(line);
                    String[] noComment = line.split("#");
                    String justInst = noComment[0];
                    String[] seperate = justInst.split("\\$|\\(|\\)|\\s|\\,");
                    ArrayList<String> list = Arrays.stream(seperate)
                             .filter(t->!t.isEmpty())
                             .map(String::trim)
                             .collect(Collectors.toCollection(ArrayList::new));
                    
                    //System.out.print("As list: ");
                    //System.out.println(list);
                    if(list.get(0).contains(":")){
                        //System.out.println(list.get(0));
                        //make sure to check after colon for instruction
                        int colon = list.get(0).indexOf(':',0);
                        int length = list.get(0).length();
                        String noColon = list.get(0).substring(0, colon);
                        if(list.get(0).charAt(length-1) != ':'){
                            list.set(0,list.get(0).substring(colon+1)); 
                        }
                        labels.put(noColon, address);
                    }
                    if(list.size() > 1){
                        if(list.get(0).contains(":")) {
                            list.remove(0);
                            //System.out.println(list);
                        }
                        lineList.add(list);
                    }
                    //conditional to check label: contains ':'
                    //System.out.println(address);
                    //System.out.println();
                    address += (list.size()<2) ? 0:1;
                }
            }
        //System.out.println(labels);
        //System.out.println(lineList);

        //hash the command name
        //load in list[1] and list[2]
        //depending on format load list[3] as imm/reg/addr
        //for jump, hash label for true destination address
        instructionObject temp = new instructionObject("1", "1", "1", "1");
        instructionObject invalid = new instructionObject("1", "1", "1", "1");
        
        
        for(int i = 0; i < lineList.size(); i++) {
            address = i;
            temp = lineObjectTable.getOrDefault(lineList.get(i).get(0), invalid);
            int tempInt;
            String immed;
            switch (temp.format) {
                case "R":
                    //System.out.println("Register Format");
                    //insert arguments to the operations
                    System.out.print(temp.opcode + " ");
                    System.out.print(registerTable.getOrDefault(lineList.get(i).get(2), "Invalid") + " ");
                    System.out.print(registerTable.getOrDefault(lineList.get(i).get(3), "Invalid") + " ");
                    System.out.print(registerTable.getOrDefault(lineList.get(i).get(1), "Invalid") + " ");
                    //System.out.print(temp.shamt.equals("") ? "00000" + " ":temp.shamt + " ");
                    System.out.print("00000" + " ");
                    System.out.println(temp.functioncode);
                    break;

                case "RS":
                    //System.out.println("Register Shift Format");
                    System.out.print(temp.opcode + " ");
                    System.out.print("00000" + " ");
                    System.out.print(registerTable.getOrDefault(lineList.get(i).get(2), "Invalid") + " ");
                    System.out.print(registerTable.getOrDefault(lineList.get(i).get(1), "Invalid") + " ");
                    //System.out.print(temp.shamt.equals("") ? "00000" + " ":temp.shamt + " ");
                    tempInt = Integer.parseInt(lineList.get(i).get(3));
                    System.out.print((String.format("%5s", Integer.toBinaryString(tempInt)).replace(" ", "0")) + " ");
                    System.out.println(temp.functioncode);
                    break;

                case "I":
                    //may have labels
                    //System.out.println("Immediate Format");
                    System.out.print(temp.opcode + " ");
                    System.out.print(registerTable.getOrDefault(lineList.get(i).get(1), "Invalid") + " ");
                    System.out.print(registerTable.getOrDefault(lineList.get(i).get(2), "Invalid") + " ");
                    //System.out.print(temp.shamt.equals("") ? "00000" + " ":temp.shamt + " ");
                    //need to check if label
                
                    if((lineList.get(i).get(3).matches("-?([0-9]+)?[0-9]+")) || (lineList.get(i).get(3).matches("-"))){
                        tempInt = Integer.parseInt(lineList.get(i).get(3));
                    }
                    else{
                        tempInt = labels.get(lineList.get(i).get(3))-address-1; 
                    }
                    immed = String.format("%16s", Integer.toBinaryString(tempInt)).replace(" ", "0" );
                    System.out.println(immed.substring(immed.length() -16) + " ");
                    break;

                case "IS":
                    //System.out.println("Immediate Load/Store Format");
                    System.out.print(temp.opcode + " ");
                    System.out.print(registerTable.getOrDefault(lineList.get(i).get(3), "Invalid") + " ");
                    System.out.print(registerTable.getOrDefault(lineList.get(i).get(1), "Invalid") + " ");
                    //System.out.print(temp.shamt.equals("") ? "00000" + " ":temp.shamt + " ");
                    //need to check if label
                    tempInt = Integer.parseInt(lineList.get(i).get(2));
                    immed = String.format("%16s", Integer.toBinaryString(tempInt)).replace(" ", "0" );
                    System.out.println(immed.substring(immed.length()-16) + " ");
                    break;

                case "J":
                    //has labels
                    //System.out.println("Jump Format");
                    System.out.print(temp.opcode + " ");
                    if(lineList.get(i).get(1).matches("-?([0-9]+)?[0-9]+")){
                        tempInt = Integer.parseInt(lineList.get(i).get(1));
                    }
                    else{
                        tempInt = labels.get(lineList.get(i).get(1));
                    }
                    immed = String.format("%26s", Integer.toBinaryString(tempInt)).replace(" ", "0" );
                    System.out.println(immed.substring(immed.length() -26) + " ");
                    break;

                case "RJ":
                    //System.out.println("Jump Register Format");
                    System.out.print(temp.opcode);
                    System.out.print(registerTable.getOrDefault(lineList.get(i).get(1), "Invalid") + " ");
                    System.out.print("000000000000000 ");
                    System.out.println(temp.functioncode);
                    break;

                default:
                    System.out.print("invalid instruction: ");
                    System.out.println(lineList.get(i).get(0) + "\n");
                    return;
            }
        }


        //first loop is for labels:
        //auto-discard whitespace (string.trim())
        //wrap -> array or direct -> array
        //first instruction is at address 0, increment by 1
        //hash table from label -> address

        //second pass:
        //have to subtract ht.get(label) from (current + 1) for jumps
        //funcTable only for register format
        //?make private class for lines read in? -> line object array
        //?make an array of lines?

        //from objects -> hash various object variables based on format
        //and concatenate to a string


        }
        catch(IOException ex){

        }

    }

}

class instructionObject {
    public String name;
    public String format;
    public String functioncode;
    public String opcode;
    public String register1;
    public String register2;
    public String register3;
    public String immediate;
    public String shamt;
    public String address;

    instructionObject() 
    {
        this.name = "";
        this.format = "";
        this.functioncode = "";
        this.opcode = "";
    }

    instructionObject(String name, String format, String functioncode, String opcode) 
    {
        this.name = name;
        this.format = format;
        this.functioncode = functioncode;
        this.opcode = opcode;
        this.register1 = "";
        this.register2 = "";
        this.register3 = "";
        this.immediate = "";
        this.shamt = "";
        this.address = "";
    }
    
}

//lab2 l = new lab2();
//lab2.instructionObject i = l.new instructionObject("1", "1", "1", "1");
