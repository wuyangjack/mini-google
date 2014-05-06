/**********************************************************************************************
 * Copyright 2009 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file 
 * except in compliance with the License. A copy of the License is located at
 *
 *       http://aws.amazon.com/apache2.0/
 *
 * or in the "LICENSE.txt" file accompanying this file. This file is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under the License. 
 *
 * ********************************************************************************************
 *
 *  Amazon Product Advertising API
 *  Signed Requests Sample Code
 *
 *  API Version: 2009-03-31
 *
 */

package amazonAPI;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;



/*
 * This class shows how to make a simple authenticated ItemLookup call to the
 * Amazon Product Advertising API.
 * 
 * See the README.html that came with this sample for instructions on
 * configuring and running the sample.
 */
public class ItemSearchTool {
	
//	Access key ID: AKIAJCW7Y2IQW75EGO4A
//	secret access key: IOBYIt7T8vZZIqVyjpxDWiUJAneMBW/VI4/rwXFf
    /*
     * Your AWS Access Key ID, as taken from the AWS Your Account page.
     */
    private static final String AWS_ACCESS_KEY_ID = "AKIAJCW7Y2IQW75EGO4A";

    /*
     * Your AWS Secret Key corresponding to the above ID, as taken from the AWS
     * Your Account page.
     */
    private static final String AWS_SECRET_KEY = "IOBYIt7T8vZZIqVyjpxDWiUJAneMBW/VI4/rwXFf";

    /*
     * Use one of the following end-points, according to the region you are
     * interested in:
     * 
     *      US: ecs.amazonaws.com 
     *      CA: ecs.amazonaws.ca 
     *      UK: ecs.amazonaws.co.uk 
     *      DE: ecs.amazonaws.de 
     *      FR: ecs.amazonaws.fr 
     *      JP: ecs.amazonaws.jp
     * 
     */
    private static final String ENDPOINT = "ecs.amazonaws.com";

    private List<Item> item_list = new ArrayList<Item>();
//    private List<String> img_list = new ArrayList<String>();
//    private List<String> price_list = new ArrayList<String>();
//    private List<String> url_list = new ArrayList<String>();
    
    
    public ItemSearchTool(){
    	
    }
    
    public List<Item> getItems(){
    	return item_list;
    }

    public void fetch(String keyword) {
        /*
         * Set up the signed requests helper 
         */
        SignedRequestsHelper helper;
        try {
            helper = SignedRequestsHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        String requestUrl = null;
        String imgRequestUrl = null;
        String infoRequestUrl = null;

        ArrayList<String> ASIN = new ArrayList<String>();
        

        /* The helper can sign requests in two forms - map form and string form */
        
        /*
         * Here is an example in map form, where the request parameters are stored in a map.
         */
        System.out.println("Map form example:");
        Map<String, String> params = new HashMap<String, String>();
        params.put("Service", "AWSECommerceService");
        params.put("Version", "2011-08-01");
        params.put("Operation", "ItemSearch");
        params.put("ItemPage", "1");
        params.put("SearchIndex", "All");
        params.put("Keywords", keyword);
        params.put("ResponseGroup", "Small");
        params.put("AssociateTag","th0426-20");
        
        infoRequestUrl = helper.sign(params);
        System.out.println("Signed small Request is \"" + infoRequestUrl + "\"");
        
		try {
			DocumentBuilder info_builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document info_doc = info_builder.parse(infoRequestUrl);
			for(int i = 0; i < 3; i++){
				System.out.println(i);
				Node ASINNode = info_doc.getElementsByTagName("ASIN").item(i);
				if(ASINNode == null){
					System.out.println("no more results");
					break;
				}
				else{
					System.out.println(ASINNode.getTextContent());
					ASIN.add(ASINNode.getTextContent());
				}
	        }
			
		} catch (ParserConfigurationException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	catch (SAXException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}

		
//		System.out.println("******************Phase 2*****************");
        for(int i = 0; i < ASIN.size(); i++){
//        System.out.println("round" + i + " " + ASIN.get(i));
        
        	params.clear();
        	params.put("Service", "AWSECommerceService");
        	params.put("Version", "2011-08-01");
        	params.put("Operation", "ItemLookup");
        	params.put("ItemPage", "1");
        	params.put("IdType", "ISBN");
        	params.put("ItemId", ASIN.get(i));
        	params.put("SearchIndex", "All");
        	params.put("ResponseGroup", "Images");
        	params.put("AssociateTag","th0426-20");
        
        	imgRequestUrl = helper.sign(params);
        	System.out.println("Signed img Request is \"" + imgRequestUrl + "\"");
        
        	params.put("ResponseGroup", "Medium");
        	infoRequestUrl = helper.sign(params);
        	System.out.println("Signed Medium Request is \"" + infoRequestUrl + "\"");

        
        	try {

			
        		DocumentBuilder img_builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        		Document img_doc = img_builder.parse(imgRequestUrl);
        		DocumentBuilder info_builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        		Document info_doc = info_builder.parse(infoRequestUrl);
				Node titleNode = info_doc.getElementsByTagName("Title").item(0);
				if(titleNode == null){
					System.out.println("title empty");
					break;
				}
	            String title = titleNode.getTextContent();
	            Node priceNode = info_doc.getElementsByTagName("FormattedPrice").item(0);
	            if(priceNode == null){
	            	System.out.println("price empty");
					break;
	            }
	            String price = priceNode.getTextContent();
	            Node detailNode = info_doc.getElementsByTagName("DetailPageURL").item(0);
	            if(detailNode == null){
	            	System.out.println("detail empty");
					break;
	            }
	            String detail = detailNode.getTextContent();
	            Node imageNode = img_doc.getElementsByTagName("MediumImage").item(0).getFirstChild();
	            if(imageNode == null){
	            	System.out.println("img empty");
					break;
	            }
	            String imgUrl = imageNode.getTextContent();
//	            System.out.println("title: " + title + "\nimgurl: " + imgUrl + "\nprice: " + price + "\nurl: " + detail);
//			    title_list.add(title);
//			    img_list.add(imgUrl);
//			    price_list.add(price);
//			    url_list.add(detail);
	            Item new_item = new Item(title, imgUrl, price, detail);
//	            new_item.print();
	            item_list.add(new_item);
			
        	} catch (ParserConfigurationException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
        	catch (SAXException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
        	catch (IOException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
        }
    }

}
