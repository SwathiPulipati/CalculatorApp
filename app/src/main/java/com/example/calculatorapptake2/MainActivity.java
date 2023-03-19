package com.example.calculatorapptake2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.calculatorapptake2.databinding.ActivityMainBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    TextView t1;
    ArrayList<String> chars = new ArrayList<String>();
    boolean neg = false;
    boolean error;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);

        t1 = binding.textView1;

    //setOnClickListener
        binding.button1.setOnClickListener(this);
        binding.button2.setOnClickListener(this);
        binding.button3.setOnClickListener(this);
        binding.button4.setOnClickListener(this);
        binding.button5.setOnClickListener(this);
        binding.button6.setOnClickListener(this);
        binding.button7.setOnClickListener(this);
        binding.button8.setOnClickListener(this);
        binding.button9.setOnClickListener(this);
        binding.button0.setOnClickListener(this);
        binding.buttonplus.setOnClickListener(this);
        binding.buttonminus.setOnClickListener(this);
        binding.buttonmultiply.setOnClickListener(this);
        binding.buttondivide.setOnClickListener(this);
        binding.buttonclear.setOnClickListener(this);
        binding.buttonequals.setOnClickListener(this);
        binding.buttonpoint.setOnClickListener(this);
        binding.buttonneg.setOnClickListener(this);
        binding.buttonopenparen.setOnClickListener(this);
        binding.buttoncloseparen.setOnClickListener(this);
        t1.setOnClickListener(this);

    }

    //onClick
    public void onClick(View view){
    //remove starting zero
        if(chars.isEmpty())
            t1.setText("");
        switch(view.getId()){
            case R.id.button1:
                numButtonFunct("1");
                break;
            case R.id.button2:
                numButtonFunct("2");
                break;
            case R.id.button3:
                numButtonFunct("3");
                break;
            case R.id.button4:
                numButtonFunct("4");
                break;
            case R.id.button5:
                numButtonFunct("5");
                break;
            case R.id.button6:
                numButtonFunct("6");
                break;
            case R.id.button7:
                numButtonFunct("7");
                break;
            case R.id.button8:
                numButtonFunct("8");
                break;
            case R.id.button9:
                numButtonFunct("9");
                break;
            case R.id.button0:
                numButtonFunct("0");
                break;
            case R.id.buttonplus:
                operButtonFunct("+");
                break;
            case R.id.buttonminus:
                operButtonFunct("-");
                break;
            case R.id.buttonmultiply:
                operButtonFunct("*");
                break;
            case R.id.buttondivide:
                operButtonFunct("/");
                break;
            case R.id.buttonclear:
            //reset ArrayList, answerbox, and negatives
                t1.setText("0");
                chars.clear();
                neg = false;
                break;
            case R.id.buttonpoint:
                try {
                    if (!chars.get(chars.size() - 1).contains(".")) //if there is already a point in the number
                        numButtonFunct(".");
                }
                catch(IndexOutOfBoundsException e){ //if there is less than one value in the ArrayList
                    numButtonFunct(".");
                }
                break;
            case R.id.buttonneg:
                neg = !neg; //set positive or negative
                if(neg){
                    if (chars.isEmpty()) {  //if first value
                        chars.add("-");
                        t1.setText("-");
                    } else {
                        switch (chars.get(chars.size() - 1)) {
                        //if after operator and start of new value
                            case "+":
                            case "-":
                            case "*":
                            case "/":
                            case "(":
                                chars.add("-");
                                t1.append("-");
                                break;
                        //if after closeParen, then do multiplication
                            case ")":
                                t1.append("*-");
                                chars.add("*");
                                chars.add("-");
                                break;
                        //if after any numerical value, set that to be negative
                            default:
                                if(chars.get(chars.size() - 1).contains("-")){      //number calculated is already negative
                                    neg = !neg;
                                    break;
                                }
                                chars.set(chars.size() - 1, "-"+chars.get(chars.size() - 1));
                                resetText();
                        }
                    }
                }
                if (!neg){
                    try{
                        chars.set(chars.size() - 1, chars.get(chars.size() - 1).substring(1));  //remove minus from previously negative number
                    }
                    catch(StringIndexOutOfBoundsException e){   //if there is only one value in the ArrayList and it's only a '-'
                        chars.remove(chars.size() - 1);
                    }
                    resetText();    //reset the displayed text to remove the negative
                }
                break;
            case R.id.buttonopenparen:
                neg = false;
                openParenFunct();
                break;
            case R.id.buttoncloseparen:
                neg = false;
                t1.append(")");
                chars.add(")");
                break;
            case R.id.buttonequals:
            //set .(alone) or -.(alone) to equal 0
                for(int i=0; i<chars.size(); i++){
                    if (chars.get(i).equals(".") || chars.get(i).equals("-."))
                        chars.set(i, "0");
                }
            //try-catch to check if starts with number
                error = false;
                error = operError(0,"(", chars);
                if(!error){
                    error = operError(chars.size() - 1, ")", chars);
                }
            //parentheses error
                if (!error) {
                    error = parenErrors();
                }
            //equalsFunct call
                try {
                    chars = equalsFunct(chars);
                }
                catch(ArithmeticException e){
                    error = true;
                }

            //starting operator error
                if (error){
                    if(!chars.isEmpty()) {
                        t1.setText("Error");
                        chars.clear();
                    }
                    else
                        t1.setText("0");
                }
                else{
            //make whole numbers integers
                    if(Math.floor(parseVal(chars.get(0))) == parseVal(chars.get(0)))
                      chars.set(0,String.valueOf((int)parseVal(chars.get(0))));

            //print answer
                    t1.setText(decimalFormat(chars.get(0)));
                }
                break;
        }
    //log.i
        for(int i=0; i<chars.size(); i++){
            Log.i("MYCHARARRAY", "At the index " +i+ " is the character " +chars.get(i));
        }
    }

    //button functionality for numbers and point
    public void numButtonFunct(String num){
        if(chars.isEmpty()) {   //start off with value or point
            chars.add(num);
            t1.append(num);
        }
        else if (chars.size() == 1 && chars.get(0).equals("-")){    //if negative sign to start, set first value to negative num
            chars.set(0, "-" +num);
            t1.append(num);
        }
        else {
            switch (chars.get(chars.size() - 1)) {
                case "-":   //if after negative sign
                    switch (chars.get(chars.size() - 2)){
                    //if sign before is an operator, treat it like a negative sign
                        case "+":
                        case "-":
                        case "*":
                        case "/":
                        case "(":
                            chars.set(chars.size() - 1, (chars.get(chars.size() - 1)) + num);
                            t1.append(num);
                            break;
                    //else treat it like an operator
                        default:
                            chars.add(num);
                            t1.append(num);
                    }
                    break;
                //if immediately after operator
                case "+":
                case "*":
                case "/":
                case "(":
                    chars.add(num);
                    t1.append(num);
                    break;
                //if after closeParen, then do multiplication
                case ")":
                    t1.append("*" +num);
                    chars.add("*");
                    chars.add(num);
                    break;
                //if after numerical value, then add to the string itself not the ArrayList
                default:
                    chars.set(chars.size() - 1, (chars.get(chars.size() - 1)) + num);
                    t1.append(num);
            }
        }

    }

    //button functionality for operators (+,-,*,/)
    public void operButtonFunct(String oper){
        try { //if not first value
            switch (chars.get(chars.size() - 1)) {
            //if after operator, replace operator with new one
                case "+":
                case "-":
                case "*":
                case "/":
                    chars.set(chars.size() - 1, oper);
                    resetText();
                    break;
                case "(": //this will give "error" but we want to step through
                    break;
                default:    //if after numerical value
                    chars.add(oper);
                    t1.append(oper);
            }
        }
        catch(IndexOutOfBoundsException e){ //if first value, do not add or print operator
            t1.setText("0");
            chars.clear();
        }
        neg = false;  //reset negative
    }

    //button functionality for opening parentheses
    public void openParenFunct(){
        if(chars.isEmpty()){        //if chars is empty
            t1.append("(");
            chars.add("(");
        }
        else if (chars.get(chars.size()-1).equals("-")){        //if after minus or negative sign
            if (chars.size() == 1){         //if negative is first value, then do -1*...
                t1.append("1*(");
                chars.set(0, "-1");
                chars.add("*");
                chars.add("(");
            }
            else{
                switch(chars.get(chars.size() - 2)){    //if negative after operator, then treat like -1*...
                    case "+":
                    case "-":
                    case "*":
                    case "/":
                    case "(":
                        t1.append("1*(");
                        chars.set(chars.size()-1, "-1");
                        chars.add("*");
                        chars.add("(");
                        break;
                    default:    //if minus after value, then treat like operator, just add
                        t1.append("(");
                        chars.add("(");
                }
            }
        }
        else{
            switch (chars.get(chars.size()-1)){
            //if after operator then add
                case "+":
                case "*":
                case "/":
                case "(":
                    t1.append("(");
                    chars.add("(");
                    break;
            //if after numerical value, multiply
                default:
                    t1.append("*(");
                    chars.add("*");
                    chars.add("(");
            }
        }

    }

    //calculations for equals button with order of operations
    public ArrayList<String> equalsFunct(ArrayList<String> vals){
        if (!error) {
            ArrayList<Integer> operators = new ArrayList<Integer>();
            if (vals.contains("("))
                vals = parenCalc(vals); //P part of PEMDAS
            for (int i = 0; i < vals.size(); i++) {     //puts all indexes of * and / in ArrayList
                if (vals.get(i).equals("*") || vals.get(i).equals("/"))
                    operators.add(i);
            }
            while (vals.contains("*") || vals.contains("/")) {
                for (Integer i : operators) {   //iterate through all indexes with * and /
                    i -= 2 * (operators.indexOf(i));       //when moving to next one, values are being removed so shift down two*n
                    if (i < vals.size()) {
                        if (vals.get(i).equals("*")) {      //multiplication
                            vals.set(i, String.valueOf(multiply(vals.get(i - 1), vals.get(i + 1))));
                            vals.remove(i + 1);
                            vals.remove(i - 1);
                        }
                    }
                    if (i < vals.size()) {
                        if (vals.get(i).equals("/")) {         //division
                            vals.set(i, String.valueOf(divide(vals.get(i - 1), vals.get(i + 1))));
                            //divide by zero error
                            if (vals.get(i + 1).equals("0"))
                                error = true;
                            vals.remove(i + 1);
                            vals.remove(i - 1);
                        }
                    }
                }
            }
            operators.clear();
            //same for addition/subtraction
            for (int i = 0; i < vals.size(); i++) {
                if (vals.get(i).equals("+") || vals.get(i).equals("-"))
                    operators.add(i);
            }
            while (vals.contains("+") || vals.contains("-")) {
                for (Integer i : operators) {
                    i -= 2 * (operators.indexOf(i));
                    if (i < vals.size()) {
                        if (vals.get(i).equals("+")) {
                            vals.set(i, String.valueOf(addition(vals.get(i - 1), vals.get(i + 1))));
                            vals.remove(i + 1);
                            vals.remove(i - 1);
                        }
                    }
                    if (i < vals.size()) {
                        if (vals.get(i).equals("-")) {
                            vals.set(i, String.valueOf(subtract(vals.get(i - 1), vals.get(i + 1))));
                            vals.remove(i + 1);
                            vals.remove(i - 1);
                        }
                    }
                }
            }
            operators.clear();
        }
        return vals;
    }

    //setting all errors associated with errors
    public boolean operError(int val, String paren, ArrayList<String> vals){
        try{
            Double.parseDouble(vals.get(val));  //test if numerical value
        }
        catch(NumberFormatException | IndexOutOfBoundsException e){ //could give either error
            try{
                if(vals.get(val).equals(paren))  //if its NumberFormatException, then check if specified character
                    return false;
            }
            catch(IndexOutOfBoundsException f){     //else error
                return true;
            }
            return true;    //else error
        }
        return false;      //if fine, no error
    }

    //setting all errors associated with parentheses
    public boolean parenErrors(){
        int openParen = 0;
        int closeParen = 0;
        for (String s: chars){
            if (s.equals("("))
                openParen++;
            if(s.equals(")"))
                closeParen++;
            if (closeParen > openParen)     //if there are ever more closing than opening then error
                return true;
        }
        if(openParen != closeParen)     //if uneven number of parentheses then error
            return true;
        if (chars.size() > 1) {
            for (int i = chars.size() - 1; i > 0; i--) {        //if nothing between openParen and closeParen then error
                if (chars.get(i).equals(")") && chars.get(i - 1).equals("("))
                    return true;
                if (chars.size() > 2){      //if only negative sign inside parens, the error
                    if (chars.get(i).equals(")") && chars.get(i-1).equals("-") && chars.get(i - 2).equals("("))
                        return true;
                }
            }
        }
        return false;   //if it gets to this point, then no error
    }

    //iterating through calculations involving parentheses
    public ArrayList<String> parenCalc(ArrayList<String> vals){
        ArrayList<String> inParens = new ArrayList<String>();
        int numOpen = 0;
        int numClose = 0;
        int valOpen = 0;
        int valClose = 0;
        boolean ifParenOpen = false;
        for(int i=0; i<vals.size()-2; i++){     //if only one numerical value inside parens, then remove parens
            if(vals.get(i).equals("(") && vals.get(i+2).equals(")")){
                vals.remove(i+2);
                vals.remove(i);
            }
        }
        if(!vals.contains("("))     //if no parens, then do math
            equalsFunct(vals);
        while(vals.contains("(")){
            for (int i=0; i<vals.size(); i++){
                if(vals.get(i).equals(")")){
                    numClose++;
                    if (numOpen == numClose){   //if the corrresponding close for the designated open, then stop taking values
                        ifParenOpen = false;
                        valClose = i-1;     //index of inParens ending val in vals
                    }
                }
                if(ifParenOpen)     //add values to inParens
                    inParens.add(vals.get(i));
                if(vals.get(i).equals("(")){        //start letting values in to inParens
                    ifParenOpen = true;
                    numOpen++;
                    if (numOpen == 1)
                        valOpen = i+1;      //index of inParens starting val in vals
                }
                if(!ifParenOpen && !inParens.isEmpty()) {       //if values have been added to inParens already
                    if(!error){
                        error = operError(inParens.size() - 1, ")", inParens);  //check if errors in inParens
                    }
                    vals.set(valOpen, equalsFunct(inParens).get(0));    //add correct answer to beginning of sublist in vals
                    vals.subList(valOpen+1, valClose+1).clear();        //remove rest of sublist in vals
                    return parenCalc(vals);     //recall parenCalc to continue operating
                }
            }
        }
        return vals;    //return final value to chars in OnClick
    }

    //turn String to Double
    public double parseVal(String num){     //method for a method cause it's easier to type
        return Double.parseDouble(num);
    }

    //resetting the text on the screen when changing things already there
    public void resetText(){    //reset text on screen
        String newText = "";
        for (int i=0; i<chars.size(); i++){
            String val;
            try{
                val = decimalFormat(chars.get(i));
            }
            catch(NumberFormatException e){
                val = chars.get(i);
            }
            newText = newText + val;
        }
        t1.setText(newText);
    }

    //decimal formatting
    public String decimalFormat(String val){        //decimal format for 5 decimal points
        DecimalFormat df = new DecimalFormat("#.#####");
        return df.format(parseVal(val));
    }

//calculations
    public double addition(String a, String b){
        return parseVal(a)+parseVal(b);
    }
    public double subtract(String a, String b){
        return parseVal(a)-parseVal(b);
    }
    public double multiply(String a, String b){
        return parseVal(a)*parseVal(b);
    }
    public double divide(String a, String b){
        return parseVal(a)/parseVal(b);
    }
}