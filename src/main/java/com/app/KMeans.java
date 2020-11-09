package com.app;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

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
import de.lmu.ifi.dbs.elki.datasource.FileBasedDatabaseConnection;
import de.lmu.ifi.dbs.elki.distance.distancefunction.minkowski.EuclideanDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancefunction.minkowski.SquaredEuclideanDistanceFunction;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;
import de.lmu.ifi.dbs.elki.utilities.random.RandomFactory;

public class KMeans {
	
public static void run(String src, String dest, int k) throws IOException {
		
		// Adapter to load data from an existing array.
		ListParameterization params = new ListParameterization();
	    params.addParameter(FileBasedDatabaseConnection.Parameterizer.INPUT_ID, src);
	    Database db = ClassGenericsUtil.parameterizeOrAbort(StaticArrayDatabase.class, params);
	    
		db.initialize();
		
		// K-means should be used with squared Euclidean (least squares):
		SquaredEuclideanDistanceFunction dist = SquaredEuclideanDistanceFunction.STATIC;
		// Default initialization, using global random:
		
		// Setup textbook k-means clustering:
		KMeansSort<NumberVector> km = new KMeansSort<NumberVector>(
				/* distance= */ EuclideanDistanceFunction.STATIC,
				/* k= */ k, /* maxiter= */ 0,
				/* init= */ new KMeansPlusPlusInitialMeans<NumberVector>(new RandomFactory(/* seed= */ 0L))
				);
		
		// Run the algorithm:
		Clustering<KMeansModel> c = km.run(db);
		
		// Relation containing the number vectors:
		Relation<NumberVector> rel = db.getRelation(TypeUtil.NUMBER_VECTOR_FIELD);
		// We know that the ids must be a continuous range:
		DBIDRange ids = (DBIDRange) rel.getDBIDs();
		
		// read the src file to list of strings
		List<String> list = FileUtils.readLines(new File(src));

		int i = 0;
		for(Cluster<KMeansModel> clu : c.getAllClusters()) {
			
			String cluster = "";
			
			for(int j=0; j<clu.getModel().getPrototype().length; j++) {
				if(j < clu.getModel().getPrototype().length-1) {
					cluster += Math.round(clu.getModel().getPrototype()[j]) + "," ;
				} else {
					cluster += Math.round(clu.getModel().getPrototype()[j]) ;
				}				
			}
			
			System.out.println(clu.getNameAutomatic()+" centroid : "+ cluster);
		  
		  for(DBIDIter it = clu.getIDs().iter(); it.valid(); it.advance()) {
		    // To get the vector use:
		    NumberVector v = rel.get(it);

		    // Offset within our DBID range: "line number"
		    final int offset = ids.getOffset(it);
		    
		    if(offset < 14) {
		    	// header keep it
		    	//System.out.println("header keep it "+offset);
		    } else {
		    	list.set(offset, cluster);
		    }
		  }
		  ++i;
		}
		
		// 
		byte [] data = ConvertImage.toBytes(list);
		ConvertImage.toImage(data, dest);
	}

}
