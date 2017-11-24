package com.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ACLUtil {

	public static void main(String[] args) throws IOException {
		String path = args[0];
		File file = new File(path);
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		String line = null;
		
		while((line = br.readLine()) != null ) {
			
			int firstNode = line.indexOf(",");
			
			String errorCode = line.substring(0, firstNode);
			String descCn = line.substring(firstNode, line.indexOf("。"));
			System.out.println(String.format("errorCode={%s}, descCn={%s}", errorCode, descCn));
		}
	}

}
