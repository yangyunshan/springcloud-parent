/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "OGC Service Framework".
 
 The Initial Developer of the Original Code is the VAST team at the University of Alabama in Huntsville (UAH). <http://vast.uah.edu> Portions created by the Initial Developer are Copyright (C) 2007 the Initial Developer. All Rights Reserved. Please Contact Alexandre Robin or
 Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
    Alexandre Robin
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.ows;

import org.vast.xml.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * <p>
 * Reader for exceptions generated by OGC Web Services
 * </p>
 *
 * @author Alex Robin
 * @since Feb 16, 2014
 */
public class OWSExceptionReader
{
	
	public static void checkException(DOMHelper dom, Element elt) throws OWSException
	{
		// SOAP envelope and fault cases
		if (elt.getLocalName().equals("Envelope"))
		{
		    elt = dom.getElement(elt, "Body/*");		
			if (elt.getLocalName().equals("Fault"))
			    elt = dom.getElement(elt, "faultstring");
		}
		
        OWSException e = readException(dom, elt);
        if (e != null)
            throw e;
	}
	
	
	protected static OWSException readException(DOMHelper dom, Element excElt)
	{
	    String eltName = excElt.getLocalName();
	    
	    if (eltName.equals("ExceptionReport"))
	    {
	        OWSExceptionReport report = new OWSExceptionReport();
	        NodeList excElts = dom.getElements(excElt, "Exception");
	        for (int i=0; i<excElts.getLength(); i++)
	            report.add(readException(dom, (Element)excElts.item(i)));
	        	        
	        if (report.getExceptionList().size() == 1)
	            return report.getExceptionList().get(0);
	        else
	            return report;
	    }
	    else if (eltName.equals("Exception"))
	    {	   
    	    String exceptionText = dom.getElementValue(excElt, "ExceptionText");
            String code = dom.getAttributeValue(excElt, "exceptionCode");
            String locator = dom.getAttributeValue(excElt, "locator");            
            return new OWSException(code, locator, exceptionText);
	    }
	    else if (eltName.equals("ServiceException"))
        {      
	        String exceptionText = dom.getElementValue(excElt);
            String code = dom.getAttributeValue(excElt, "code");
            String locator = dom.getAttributeValue(excElt, "Locator");            
            return new OWSException(code, locator, exceptionText);
        }
	    
	    return null;
	}
}
