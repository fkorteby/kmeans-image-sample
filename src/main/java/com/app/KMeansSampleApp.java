package com.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;

/**
 * Hello world!
 */
public class KMeansSampleApp
{

    public KMeansSampleApp() {}

    public static void main(String[] args) throws IOException {
    	
    	try {
			if(args.length > 1) {
				
				String sourceImage = args[0];
				String k = args[1];
				
				String path = FilenameUtils.getFullPath(sourceImage);				
				String fileName = FilenameUtils.removeExtension(Paths.get(sourceImage).getFileName().toString()); 
				
				// Convert bmp image to byte array
				byte[] data = ConvertImage.toBytes(sourceImage);
				
				// Create csv file from byte array
				ConvertImage.toFileCsv(data, path + fileName + ".csv");
				
				// Convert back the image from csv file
				// for testing only
				//data = ConvertImage.fromCsv(path + fileName + ".csv");
				//ConvertImage.toImage(data, path + fileName + "dest.bmp");
				
				// run k-means algorithm
				KMeans.run(path + fileName + ".csv", path + fileName + "-" + k +"-kmeans.bmp", Integer.parseInt(k));
				
				System.out.println("File saved successfully in : " + path + fileName + "-" + k +"-kmeans.bmp");    		
			} else {
				
				System.out.println("You should specify the folowing inputs :");
				System.out.println(" 1- BMP format image ");
				System.out.println(" 2- K means number ");
			}
			
		} catch (FileNotFoundException e) {			
			System.out.println("File not found :" + args[0]);
			
		} catch (IOException e) {
			e.printStackTrace();
		}    	
    	
    }

}
