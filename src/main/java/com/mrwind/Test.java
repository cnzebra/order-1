package com.mrwind;

import java.util.ArrayList;
import java.util.Iterator;

public class Test {

	   public Test(){  
		   ArrayList<String> list=new ArrayList<String>();  
		      list.add("AAAAA");  
		      list.add("BBBBB");  
		      list.add("CCCCC");  
		      System.out.println("size1=:"+list.size());  
		      
		      Iterator<String> iterator = list.iterator();
		      while(iterator.hasNext()){
		    	  String next = iterator.next();
		    	  list.remove(next);
		    	  System.out.println("dddddd");
		      }
		      System.out.println("size1=:"+list.size());  
		   }  
		  
		   public int increseParam(int i){  
		      i++;  
		      return i;  
		   }  
		    
		   public int increseObj(Integer i){  
		      i++;  
		      return i;  
		   }  
		  
		   public String anaString(String str){  
		       str=str+"_APPEND";  
		       return str;  
		   }  
		  
		   public void clearList(ArrayList<String> list){  
		       list.clear();  
		       System.out.println("size2=:"+list.size());  
		   }  
		  
		   public void newList(ArrayList<String> list){  
			   list.add("dfdsfsd");
		       list=new ArrayList<String>();  
		       list.add("DDDDD");
		  
		       System.out.println("sizeB=:"+list.size());  
		   }  
		  
		   public static void main(String[] args){  
		      new Test();  
		   }  

}
