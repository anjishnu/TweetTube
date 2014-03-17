package models;
import java.io.File;
import java.io.IOException;

import play.Play;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;


public class S3BucketManager {
	
	
	static String tableName = Play.configuration.getProperty("tableName");
	static String secretKey = Play.configuration.getProperty("aws.secret.key")	;
	static String accessKey = Play.configuration.getProperty("aws.access.key")	;
	static AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
	public static AmazonS3Client s3 = new AmazonS3Client(credentials);
	
	public static String bucket_name = Play.configuration.getProperty("s3.bucket");
	
			//public String bucket_name = "elasticbeanstalk-us-west-2-786410740436";
	
	
	
	public static void putObject(String key, File file)
	{		
		try {
			//put object - bucket, key, value(file)
			System.out.println("Putting object on S3");
			s3.putObject(new PutObjectRequest(bucket_name, key, file).withCannedAcl(CannedAccessControlList.PublicRead));
			System.out.println("Done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
