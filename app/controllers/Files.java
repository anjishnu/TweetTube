package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;

import models.Document;
import models.DBManager;
import models.S3BucketManager;
import models.SNSManager;
import models.Video;
import play.Play;
import play.libs.MimeTypes;
import play.mvc.Controller;

public class Files extends Controller
{

  public static void uploadForm()
  {
    render();
  }
  
  public static void subForm()
  {
	  render();
  }
  
  public static void SNSubscribe(String email) throws Exception
  {
	  SNSManager.doSubscribe(email);
	  listUploads();
  }
  
  public static void doUpload(File file, String comment, String ParentId, String user ) throws Exception
  {
	 System.out.println(user);
	if (user==null || user.length()==0)
	{	user = "Anonymous";
	}
	java.util.Date date= new java.util.Date();
	Timestamp t1 = new Timestamp(date.getTime());
	String FileId = 	t1.toString() + file.getName();  
	FileId = FileId.replaceAll("\\s+","");
	String fileName = file.getName() ; 
	String isParent = "n" ; 
		
	S3BucketManager.putObject(FileId, file);
	
	DBManager.createEntry( fileName, FileId,ParentId, isParent, comment, user);
    String message = "New reply on a thread in TweetTube- URL: "
    				+ "https://twittube-env.elasticbeanstalk.com/chat/" 
    				+ ParentId + " !";
    
	SNSManager.publishMessage(message);
    listUploadsVideos(ParentId);
  }
  public static void doUploadNewChat(File file, String comment, String user) throws Exception
  {
	System.out.println(user);
	if (user==null || user.length()==0)
	{
			user = "Anonymous";
	}
	java.util.Date date= new java.util.Date();
	Timestamp t1 = new Timestamp(date.getTime());
	String FileId = 	t1.toString() + file.getName();  
	
	FileId = FileId.replaceAll("\\s+","");
	String ParentId = "none" ; 
	String isParent = "y" ;
	String fileName = file.getName() ; 
	
	S3BucketManager.putObject(FileId, file);	
	DBManager dbInstance = new DBManager() ;
	dbInstance.createEntry( fileName, FileId,ParentId, isParent, comment, user);
    String message = "New thread created with filename "+ fileName + " !";
	SNSManager.publishMessage(message);
    listUploads();
  }

  public static void listUploadsVideos(String ParentId) throws Exception
  {
	DBManager dbInstance = new DBManager() ;
	List<Video> replyThreads = dbInstance.getVideoByParentId(ParentId) ;	
	
	System.out.println("***************************"  + ParentId) ;
	Video mainThread = dbInstance.getVideoById(ParentId) ; 
	render(ParentId, replyThreads, mainThread);
    
  }
  public static void listUploads() throws Exception
  {
	  System.out.println("Listing uploads");
		List<Video> names = DBManager.getVideoForHomePage() ;	
	    render(names);
  }
  public static void downloadFile(long id)
  {
    final Document doc = Document.findById(id);
    notFoundIfNull(doc);
    
  }
	
	

}
