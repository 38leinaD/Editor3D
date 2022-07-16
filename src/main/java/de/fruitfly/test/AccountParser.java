package de.fruitfly.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Date;

public class AccountParser {

	public static void main(String[] args) {
        try {
			String content = new String(Files.readAllBytes(Paths.get("C:/Users/daniel.platz/Desktop/DUMP/20150801-1585694-umsatz.CSV")));
			String[] lines = content.split("\n");
			
			int i=0;
			LocalDate oldDate = null;
			float sum = 0;
			for (String line : lines) {
				if (i++==0) continue;
				String[] columns = line.split(";");
				String dateString = columns[2].substring(1, columns[2].length()-1);
		        
		        LocalDate d = LocalDate.parse(dateString,DateTimeFormatter.ofPattern("dd.MM.yy"));
		        
		        String amountString = columns[8].substring(1, columns[8].length()-1);
		        
		        DecimalFormat df = new DecimalFormat();
		        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		        symbols.setDecimalSeparator(',');
		        symbols.setGroupingSeparator(' ');
		        df.setDecimalFormatSymbols(symbols);		        
		        Number number = df.parse(amountString);
		        
		        float amount = number.floatValue();
		        
		        
		        if (oldDate != null) {
			        
			        if (oldDate.getMonthValue() != d.getMonthValue()) {
			       
						System.out.println(oldDate.getMonthValue() + "." + oldDate.getYear() + ": " + sum);
			        	
			        	sum = 0;
			        }
		        }
		        oldDate = d;
		        sum += amount;
		        
			}
        } catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}		
	}

}
