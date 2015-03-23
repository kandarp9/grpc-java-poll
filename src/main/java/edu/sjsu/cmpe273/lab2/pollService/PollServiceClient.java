package edu.sjsu.cmpe273.lab2;

import io.grpc.ChannelImpl;
import io.grpc.transport.netty.NegotiationType;
import io.grpc.transport.netty.NettyChannelBuilder;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple client that creates a PollId from the {@link PollServiceServer}.
 */
public class PollServiceClient {
  private static final Logger logger = Logger.getLogger(PollServiceClient.class.getName());

  private final ChannelImpl channel;
  private final PollServiceGrpc.PollServiceBlockingStub blockingStub;



  public PollServiceClient(String host, int port) {
    channel =
        NettyChannelBuilder.forAddress(host, port).negotiationType(NegotiationType.PLAINTEXT)
            .build();
    blockingStub = PollServiceGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTerminated(5, TimeUnit.SECONDS);
  }

  public void createPoll(String strModeratorId, String strQuestion, String started_at, String expired_at, String[] choices) {
    
	if( null == choices || choices.length < 2){
		logger.info("Choices should be two.");
		new RuntimeException("Choices should be two.");
	}
	try{
    	logger.info("Creating a poll for moderator : " + strModeratorId);


      PollRequest request = PollRequest.newBuilder().setModeratorId(strModeratorId).setQuestion(strQuestion).setStartedAt(started_at).setExpiredAt(expired_at).addChoice(choices[0]).addChoice(choices[1]).build();
      PollResponse response = blockingStub.createPoll(request);
      logger.info("Created poll with PollId: " + response.getId());
    
    } catch (RuntimeException e) {
      logger.log(Level.WARNING, "RPC failed", e);
      return;
    }
  }

  public static void main(String[] args) throws Exception {
    PollServiceClient client = new PollServiceClient("localhost", 50051);
    try {

    String strModeratorId = "1";
	String strQuestion = "Which type of smartphone do you have?";
	String started_at = "2015-02-23T13:00:00.000Z";
	String expired_at = "2015-02-24T13:00:00.000Z";
	String[] arrChoices = new String[] {"Android", "iPhone"};
      if (args.length > 0) {
        strModeratorId  = args[0]; /* Use the arg as the name to greet if provided */
	strQuestion = args[1];
	started_at = args[2];
	expired_at = args[3];
     }
      client.createPoll(strModeratorId, strQuestion, started_at, expired_at, arrChoices);
    } finally {
      client.shutdown();
    }
  }
}
