package models;

public class Video {
	
	public String fileName ;
	public String fileId ;
	public String comment;
	public String user;
	
	public Video (String fileName, String fileId, String comment, String user)
	{
		this.fileName = fileName; 
		this.fileId = fileId ;
		this.comment = comment;
		this.user = user;
	}

}
