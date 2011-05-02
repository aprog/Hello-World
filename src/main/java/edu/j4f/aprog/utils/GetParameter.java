package edu.j4f.aprog.utils;

import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetParameter {
	private static final Logger logger = LoggerFactory.getLogger(GetParameter.class);

	public static String get(String key) {
		logger.debug("Get parameter with key: '" + key + "' from ExternalContext");
		String parameter = "";
		if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey(key)) {
			parameter = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(key).toString();
		}
		/*
		try {
			//parameter = parameter == null ? "" : new String(parameter.toString().getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("Failed to convert parameter from existing encoding to UTF-8", e);
		}
		*/
		return parameter;
	}
	
	public static int getIntegerValue(String key) {
		logger.debug("Get integer parameter with key: '" + key + "' from ExternalContext");
		int parameter = -1;
		try {
			if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey(key) &&					
					!FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(key).isEmpty()) {
				parameter = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(key));
			}
		} catch (NumberFormatException e) {
			logger.error("Failed to convert parameter to integer value", e);
		}
		return parameter;		
	}

}
