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
   	 * The path of the folder in the FS to store the resources.
   	 */
    public static final String resourceStoragePath = Config.getString("STORAGE_PATH"); //$NON-NLS-1$
    
    /*
   	 * The path of the folder in the FS to store the policies.
   	 */
    public static final String policiesStoragePath = Config.getString("POLICIES_PATH"); //$NON-NLS-1$
    
    public static final String resourceExternalPath = Config.getString("RESOURCE_EXTERNAL_PATH"); //$NON-NLS-1$

    
    
    public static final String baseURI = Config.getString("BASE_URI"); //$NON-NLS-1$
    public static final String contextBaseURI = Config.getString("CONTEXT_BASE_URI"); //$NON-NLS-1$
    public static final String contextsURI = Config.getString("CONTEXT_URI"); //$NON-NLS-1$
    
    /*
     * Unified URI to be put into the contexts and policies
     */
    public static final String shi3ldURI = Config.getString("SHI3LD_URI"); //$NON-NLS-1$
    
    public static final String s4acPrefixURI = "http://ns.inria.fr/s4ac/v3#"; //$NON-NLS-1$
    public static final String rdfPrefixURI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"; //$NON-NLS-1$

    
    /**************************************************************************/
}