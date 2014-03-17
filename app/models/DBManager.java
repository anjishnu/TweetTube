package models;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.Logger;
import play.Play;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

public class DBManager {
	
	static String tableName = Play.configuration.getProperty("tableName");
	static String secretKey = Play.configuration.getProperty("aws.secret.key")	;
	static String accessKey = Play.configuration.getProperty("aws.access.key")	;
	static AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
	
    public static AmazonDynamoDBClient client= new AmazonDynamoDBClient(credentials);
    

    public static void createEntry (String name , String id, String parentId, String isParent, String comment, String user) {
    	
    	Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
    	
    	item.put("id", new AttributeValue().withS(id));
    	item.put("name", new AttributeValue().withS(name));
    	item.put("parentId", new AttributeValue().withS(parentId));
    	item.put("isParent", new AttributeValue().withS(isParent));
    	item.put("user", new AttributeValue().withS(user));
    	
    	if (comment==null || comment.length()==0)
    	{
    		comment = "*NONE*";
    	}
    	
    	item.put("comment", new AttributeValue().withS(comment));
    	
    	
    	PutItemRequest putItemRequest = new PutItemRequest()
    	  .withTableName(tableName)
    	  .withItem(item);
    	PutItemResult result = client.putItem(putItemRequest);
    	
    }
    public static Video getVideoById(String id) {
    	Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        key.put("id", new AttributeValue().withS(id));
        
        GetItemRequest getItemRequest = new GetItemRequest()
            .withTableName(tableName)
            .withKey(key); 
            //.withAttributesToGet(Arrays.asList("id","name", "parentId", "isParent"))
            
        	
        GetItemResult result = client.getItem(getItemRequest);

        // Check the response.
        System.out.println("Printing item after retrieving it....");
        return getName(result.getItem());            
    }

    public static List<Video> getVideoByParentId(String id) {
    	
    	Condition hashKeyCondition = new Condition()
        .withComparisonOperator(ComparisonOperator.EQ)
        .withAttributeValueList(new AttributeValue().withS(id));

    	HashMap<String, Condition> keyConditions = new HashMap<String, Condition>();
    	keyConditions.put("parentId", hashKeyCondition);
    		
    	
    	QueryRequest queryRequest = new QueryRequest()
        	.withTableName(tableName)
        	.withIndexName("parentId-index")
        	.withSelect("ALL_ATTRIBUTES")
        	.withScanIndexForward(true);
    	queryRequest.setKeyConditions(keyConditions);
    		

    QueryResult result = client.query(queryRequest);
    ArrayList<Video> names = new ArrayList<Video> ();
    for (Map<String, AttributeValue> item : result.getItems()) {
        names.add(getName(item));
    }
    return names ; 
    
   }

     public static List<Video> getVideoForHomePage() {
    	
    	Condition hashKeyCondition = new Condition()
        .withComparisonOperator(ComparisonOperator.EQ)
        .withAttributeValueList(new AttributeValue().withS("y"));

    	HashMap<String, Condition> keyConditions = new HashMap<String, Condition>();
    	keyConditions.put("isParent", hashKeyCondition);
    		
    	
    	QueryRequest queryRequest = new QueryRequest()
        	.withTableName(tableName)
        	.withIndexName("isParent-index")
        	.withSelect("ALL_ATTRIBUTES")
        	.withScanIndexForward(true);
    	queryRequest.setKeyConditions(keyConditions);
    		
    	
    QueryResult result = client.query(queryRequest);
    ArrayList<Video> names = new ArrayList<Video>() ;
    for (Map<String, AttributeValue> item : result.getItems()) {
        names.add(getName(item));
    }
    return names;
            
    }
    
    private static Video getName(Map<String, AttributeValue> attributeList) {
        String fileName = "" ;
        String fileId = "" ;
        String comment = "";
        String user = "";
    	for (Map.Entry<String, AttributeValue> item : attributeList.entrySet()) {
            String attributeName = item.getKey();
            AttributeValue value = item.getValue();
            if ( attributeName.equals("name")){
            	fileName = value.getS() ;
            }
            if ( attributeName.equals("id")){
            	fileId = value.getS() ;
            }
            if ( attributeName.equals("comment")){
            	comment = value.getS();
            }
            
            if ( attributeName.equals("user")){
            	user = value.getS();
            }
    	}
    	
    	Video vf = new Video(fileName, fileId, comment, user) ; 
		return vf;
    }
}
   