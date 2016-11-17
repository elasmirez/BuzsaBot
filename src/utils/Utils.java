package utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Utils {

    private static final String URL_AUZSA = "http://www.urbanosdezaragoza.es/frm_esquemaparadatime.php?poste=";
	private static final String URL_AYTO = "http://www.urbanosdezaragoza.es/frm_esquemaparadatime.php?poste=";
    private static final String DATA_IDS_TXT = "data/ids.txt";

    private Utils(){}

	/**
	 * @param id a bus stop number
	 * @return URL for getting all data from Autobuses Urbanos de Zaragoza's website
	 */
	public static URL getUrlAuzsa(String id){
		URL url = null;
		try {
			url = new URL(URL_AUZSA + id);
		} catch (MalformedURLException e) {
			Logger.getLogger(Utils.class.getName()).log(
					java.util.logging.Level.SEVERE,
					URL_AUZSA + id
			);
		}
		return url;
	}

	/**
	 * @param id a bus stop number
	 * @return URL for getting all data from Ayuntamiento de Zaragoza's API
	 */
	public static URL getUrlAyto(Long id){
		URL url = null;
		try {
			url = new URL(URL_AYTO + id);
		} catch (MalformedURLException e) {
			Logger.getLogger(Utils.class.getName()).log(
					java.util.logging.Level.SEVERE,
					URL_AUZSA + id
			);
		}
		return url;
	}
	
	/**
	 * @param url - URL ready be used
	 * @return JSON string
	 */
	public static String call4json(URL url) {
		String result = null; 
		try {
			result = IOUtils.toString(url);
		} catch (IOException e) {
			System.out.println("FAIL: IOException when getting JSON data" + e.getMessage());
		}
		return result;
	}

	/**
	 * @param url a String containing a URL
	 * @return URL instance for the given <code>url</code>
	 */
	public static URL getUrlInstance(String url) {
		URL builtUrl = null;
	
		try {
			builtUrl = new URL(url);
		} catch (MalformedURLException e) {
			System.out.println("FAIL: Malformed URL");
			e.printStackTrace();
		}
	
		return builtUrl;
	}
	
	/**
	 * @return A File object that contains all processed Ids
	 */
	public static File getProcessedIdsFile(){
		File f = new File(DATA_IDS_TXT);
        if (Files.notExists(f.toPath())) {
			try {
                if (!Files.isDirectory(f.toPath().getParent())){
                    Files.createDirectory(f.toPath().getParent());
                }
				Files.createFile(f.toPath());
			} catch (IOException e) {
				Logger.getLogger(Utils.class.getName()).log(
						java.util.logging.Level.SEVERE,
						e.getMessage()
				);
			}
		}
		return new File(f.toString());
	}
	
	/**
	 * @param file a File containing all processed ids.
	 * @return a Set containing all updateIds read from a file. 
	 */
	public static Set<Long> getUpdateIds(File file) {
		Set<Long> updateIds = new HashSet<>();
		try (Stream<String> stream = Files.lines(file.toPath())){
			stream.filter(NumberUtils::isParsable)
				.forEach(id -> updateIds.add(new Long(id)));
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return updateIds; 
	}

	public static void waitAsecond(long tStart) {
		long tEnd = System.currentTimeMillis();
		if (tEnd - tStart < 1000) try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
			Thread.currentThread().interrupt();
		}
	}
	
}

