package Inventory;

import java.io.*;
import java.net.*;
import java.util.*;


public class Inventory {
    Map<String,Integer> inventoryMap;

    public Inventory(String filePath) throws FileNotFoundException, IOException
    {
        System.out.println("Inventory Constructor Hit");
     // parse the inventory file
     // Geeks for Geeks : https://www.geeksforgeeks.org/bufferedreader-readline-method-in-java-with-examples/
        FileReader fileReader = new FileReader(filePath);
        BufferedReader buffReader = new BufferedReader(fileReader);
        inventoryMap = new HashMap<String,Integer>();
        while (buffReader.ready())
        {
            String readLine = buffReader.readLine();
            String[] dataItem = readLine.split(" ");
            if(dataItem.length > 1)
            {
                inventoryMap.put(dataItem[0], Integer.parseInt(dataItem[1]));
            }
        }
        for(Map.Entry<String,Integer> entry : inventoryMap.entrySet())
        {
            System.out.println(entry.getKey() + " " + Integer.toString(entry.getValue()));
        }
    }

    public boolean buyItem(String item, Integer quantity)
    {
        Integer currentProductCount = 0;
        if (inventoryMap.containsKey(item))
        {
            currentProductCount = inventoryMap.get(item);
            currentProductCount = currentProductCount - quantity;
            if (currentProductCount >= 0)
            {
                inventoryMap.put(item, currentProductCount);
                return true;
            }
        }
        return false;
    }

    public void printMap()
    {
        for(Map.Entry<String,Integer> entry : inventoryMap.entrySet())
        {
            System.out.println(entry.getKey() + " " + Integer.toString(entry.getValue()));
        }
    }
}
