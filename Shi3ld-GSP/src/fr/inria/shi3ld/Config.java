package fr.inria.shi3ld;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * System configuration vars
 *
 * @author oscar
 */
public final class Config {
	
	
	private static final String BUNDLE_NAME = "fr.inria.shi3ld.config"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Config() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	

	/**************************************************************************/
	/*
	 * The base URL of the fuseki server to be used as triple stor. i.e.: "http://localhost:3030"
	 */
    public static final String fusekiServerURL = Config.getString("FUSEKI_SERVER_URL"); //$NON-NLS-1$
    
    /*
	 * The name of the dataset to be used to store the graphs. i.e.: "ds"
	 */
    public static final String fusekiServerDatasetName = Config.getString("FUSEKI_DATASET_NAME"); //$NON-NLS-1$
    
    /*
   	 * The path of the folder in the FS to store the policies.
   	 */
    public static final String policiesStoragePath = Config.getString("POLICIES_STORAGE_PATH"); //$NON-NLS-1$
    
    public static final String baseURI = Config.getString("BASE_URI"); //$NON-NLS-1$
    public static final String contextBaseURI = Config.getString("CONTEXT_BASE_URI"); //$NON-NLS-1$
    
    public static final String s4acPrefixURI = "http://ns.inria.fr/s4ac/v2#"; //$NON-NLS-1$
    public static final String rdfPrefixURI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"; //$NON-NLS-1$
    
    /**************************************************************************/
    
    
    
    
    
    
    /*
     * Do not change the vars below unless you know exactly what you're doing!
     */
    public static final String fusekiQueryURL = fusekiServerURL + "/" + fusekiServerDatasetName + "/query"; //$NON-NLS-1$ //$NON-NLS-2$
    public static final String fusekiDataURL = fusekiServerURL + "/" + fusekiServerDatasetName + "/data"; //$NON-NLS-1$ //$NON-NLS-2$
}