package com.app;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import de.lmu.ifi.dbs.elki.algorithm.clustering.kmeans.KMeansLloyd;
import de.lmu.ifi.dbs.elki.algorithm.clustering.kmeans.KMeansSort;
import de.lmu.ifi.dbs.elki.algorithm.clustering.kmeans.initialization.KMeansPlusPlusInitialMeans;
import de.lmu.ifi.dbs.elki.data.Cluster;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.model.KMeansModel;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.StaticArrayDatabase;
import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.database.ids.DBIDRange;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.datasource.ArrayAdapterDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.DatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.FileBasedDatabaseConnection;
import de.lmu.ifi.dbs.elki.distance.distancefunction.NumberVectorDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancefunction.minkowski.EuclideanDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancefunction.minkowski.SquaredEuclideanDistanceFunction;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;
import de.lmu.ifi.dbs.elki.utilities.random.RandomFactory;
 
public class ConvertImage {
	
	public static byte[] toBytes(String src) throws FileNotFoundException {
		
		/*
    	 * 1. How to convert an image file to  byte array?
    	 */
 
        File file = new File(src);
 
        FileInputStream fis = new FileInputStream(file);
        //create FileInputStream which obtains input bytes from a file in a file system
        //FileInputStream is meant for reading streams of raw bytes such as image data. For reading streams of characters, consider using FileReader.
 
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        try {
            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                //Writes to this byte array output stream
                bos.write(buf, 0, readNum); 
            }
        } catch (IOException ex) {
            Logger.getLogger(ConvertImage.class.getName()).log(Level.SEVERE, null, ex);
        }
 
        return bos.toByteArray();
		
	}
	
	public static void toImage(byte[] bytes, String dest) throws IOException {
		/*
         * 2. How to convert byte array back to an image file?
         */
 
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        Iterator<?> readers = ImageIO.getImageReadersByFormatName("bmp");
 
        //ImageIO is a class containing static methods for locating ImageReaders
        //and ImageWriters, and performing simple encoding and decoding. 
 
        ImageReader reader = (ImageReader) readers.next();
        Object source = bis; 
        ImageInputStream iis = ImageIO.createImageInputStream(source); 
        reader.setInput(iis, true);
        ImageReadParam param = reader.getDefaultReadParam();
 
        Image image = reader.read(0, param);
        //got an image file
 
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        //bufferedImage is the RenderedImage to be written
 
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image, null, null);
 
        ImageIO.write(bufferedImage, "bmp", new File(dest));
	}
	
	public static void toFileCsv(byte[] bytes, String dest) throws IOException {
		
		ArrayList<String> lines = new ArrayList<String>();
		for(int i=0; i<bytes.length; i+=3) {
			String line = Integer.toString((int)bytes[i] ) +","+ Integer.toString((int)bytes[i +1]) +","+ Integer.toString((int)bytes[i+2]);
			lines.add(line);	
		}
		
		FileUtils.writeLines(new File(dest), lines);
	}
	
	public static byte[] fromCsv(String src) throws IOException {

		List<String> list = FileUtils.readLines(new File(src));
		byte[] data = new byte[list.size()*3];
		int i=0;
		
		for(String line : list) {
			String[] array = line.split(",");
			for(String elet : array) {
				data[i] = Byte.parseByte(elet);
				i++;
			}
		}
		
	    return data;
	}
	
	public static byte[] toBytes(List<String> list) throws IOException {
		
		byte[] data = new byte[list.size()*3];
		int i=0;
		
		for(String line : list) {
			String[] array = line.split(",");
			for(String elet : array) {
				data[i] = Byte.parseByte(elet);
				i++;
			}
		}
		
	    return data;
	}
	
	
	
    public static void main(String[] args) throws FileNotFoundException, IOException {
    	
    	//byte[] data = toBytes("D:\\Data Mining\\ELKI\\image.bmp");
    	//toFileCsv(data, "D:\\Data Mining\\ELKI\\image.csv");
    	
    	
    	//byte[] data =fromCsv("D:\\Data Mining\\ELKI\\image.csv");
    	//toImage(data, "D:\\Data Mining\\ELKI\\image-dest.bmp");
    	
    	//knn("D:\\Data Mining\\ELKI\\image.csv");
    	
    	/*for(byte b : data) {
    		int result = (int)b - 128; 
    		System.out.println(result);
    	}*/
    }
}