package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import models.Document;
import play.Play;
import play.libs.MimeTypes;
import play.modules.s3blobs.S3Blob;
import play.mvc.Controller;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.PutItemRequest;
import com.amazonaws.services.dynamodb.model.PutItemResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.securitytoken.model.Credentials;

public class Files extends Controller
{
	static String s3Bucket  = Play.configuration.getProperty("s3.bucket")		; 
    static String secretKey = Play.configuration.getProperty("aws.secret.key")	;
	static String accessKey = Play.configuration.getProperty("aws.access.key")	;
	static AWSCredentials myCredentials = new BasicAWSCredentials(accessKey, secretKey);
	static AmazonS3 s3client = new AmazonS3Client(myCredentials);
	static AmazonDynamoDBClient dynamoClient = new AmazonDynamoDBClient(myCredentials);
	static String cloudFront = Play.configuration.getProperty("cloudfront");
  public static void uploadForm()
  {
    render();
  }

  public static void doUpload(File file, String comment) throws IOException
  {
	  
    String filename = file.getName();
	//Storing file in the cloud
	//Now store about the image in the DynamoDB
	
	Map item = new HashMap();
	String cloudFrontURL = cloudFront + '/' + filename;
	item.put("Thread Number", new AttributeValue().withN("1"));
	item.put("Name", new AttributeValue().withS(filename));
	item.put("Position", new AttributeValue().withN("1"));
	item.put("Dunno what this is for", new AttributeValue().withS("random"));
	item.put("CloudfrontID", new AttributeValue().withS(cloudFrontURL));
	item.put("Comment", new AttributeValue().withS(comment));
	PutItemRequest putItemRequest = new PutItemRequest().withTableName("Videos").withItem(item);
	PutItemResult putItemResult = dynamoClient.putItem(putItemRequest);
	
	System.out.println("I'm tired "+putItemResult.getConsumedCapacityUnits());
    s3client.putObject(s3Bucket, filename, file);
	String message = filename + "Put into the Cloud: Transaction Complete";
	
    System.out.println(message);
    listUploads(message);
 
  }
  

  public static void listUploads(String comment)
  {  
	  //List<Document> docs = Document.findAll();
	  //docs
	  render(comment);
  }

  public static void downloadFile(long id)
  {
	  System.out.println("downloadFile");
//    final Document doc = Document.findById(id);
//    notFoundIfNull(doc);
//    response.setContentTypeIfNotSet(doc.file.type());
//    renderBinary(doc.file.get(), doc.fileName);
  }

}