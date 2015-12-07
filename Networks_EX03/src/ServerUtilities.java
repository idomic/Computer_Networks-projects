import java.io.File;
import java.util.HashMap;
import java.util.Map;


class ServerUtilities {

	private Map<String, String> m_dictionary;
	private static boolean m_isFileValid;
	public ServerUtilities() {
		// TODO Auto-generated constructor stub
	}
	private void parseConfigFile(File i_configFile) {
		m_dictionary = new HashMap<String, String>();
		for
	
	}
	
	protected String getFileType(String i_type) {
		
		return null;
	}
	protected void checkFile(File i_fileToCheck) {
		m_isFileValid = i_fileToCheck.exists() && !i_fileToCheck.isDirectory() && i_fileToCheck.canRead();
	}

}
