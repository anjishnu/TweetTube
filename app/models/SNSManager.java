package models;

import java.util.List;

import play.Play;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.ListSubscriptionsByTopicRequest;
import com.amazonaws.services.sns.model.ListSubscriptionsByTopicResult;
import com.amazonaws.services.sns.model.ListSubscriptionsRequest;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.Subscription;
import com.amazonaws.services.sns.model.UnsubscribeRequest;
import com.amazonaws.services.sqs.AmazonSQSClient;

public class SNSManager {
	//Combined class managing both the SNS and SQS services of AWS
	
	static String secretKey = Play.configuration.getProperty("aws.secret.key")	;
	static String accessKey = Play.configuration.getProperty("aws.access.key")	;
	static AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
	static AmazonSNSClient sns = new AmazonSNSClient(credentials);
	static AmazonSQSClient sqs = new AmazonSQSClient(credentials);
	//static string topicARN = Play.configuration.getProperty("topicARN"); // Implement later
	static String topicArn = "arn:aws:sns:us-east-1:786410740436:TweetTube";
	
	public static void doSubscribe(String email)
	{
		sns.subscribe(topicArn,"email", email);
		System.out.println("Subscribe request sent for email: "+email);
	}
	
	public static void unSubscribe(String email)
	{
		ListSubscriptionsByTopicResult lsbtr = sns.listSubscriptionsByTopic(topicArn);
		String targetArn = "";
		
		List<Subscription> sublist= lsbtr.getSubscriptions();
		for (Subscription sub : sublist)
		{
			if (sub.getEndpoint().equalsIgnoreCase(email))
			targetArn=sub.getSubscriptionArn();
		}
		
		if (!targetArn.equals(""))
		{
		UnsubscribeRequest unsub = new UnsubscribeRequest();
		unsub.setSubscriptionArn(targetArn);  unsub.setRequestCredentials(credentials);
		sns.unsubscribe(unsub);
		System.out.println("Unsubscribe request sent for email: "+ email);
		}
	}
	
	public static void publishMessage(String message)
	{
		
		message = "Hi! Just to inform you that there has been some activity on TweetTube!\n" + message + "\nDo visit http://twittube-env.elasticbeanstalk.com/ to see the new messages";
		PublishRequest pubRequest = new PublishRequest(topicArn, message );
		sns.publish(pubRequest);	
		System.out.println("Publish request sent out- Message: "+message);
	}
}
