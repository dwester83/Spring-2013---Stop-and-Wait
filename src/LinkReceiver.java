import java.io.*;
import java.util.*;

// LinkReceiver receives a message from LinkSender and replies.
// LinkReceiver needs to be started before LinkSender.

public class LinkReceiver {

	static int senderPort = 3200;   // port number used by sender
	static int receiverPort = 3300; // port number used by receiver

	public static void main (String args[]) throws Exception {

		//String messageReceived;
		byte ack[] = new byte[1]; // value 0 for pos, 1 for neg
		byte[] receivingBuffer = new byte[36];
		byte last = 0;
		CRC8 crc = new CRC8();
		BufferedWriter bw = null;
		boolean trace = false;
		int frameSequence = 1;

		//int frameReceived = 0;
		//int frameTransmitted = 0;
		//int totalDamageTransmitted = 0;

		// Set up a link with source and destination ports
		// Any 4-digit number greater than 3000 should be fine. 
		Link myLink = new SimpleLink(receiverPort, senderPort);

		try {
			// Prompt user on what file name they want to save to.
			Scanner scan = new Scanner(System.in);
			System.out.print("Text file name you want to save: ");
			String input = scan.nextLine();
			File file = new File(input);
			bw = new BufferedWriter(new FileWriter(file));

			// Prompt the user if they want a tracer on
			System.out.print("Turn on trace?: ");
			input = scan.nextLine();
			if ((input.toLowerCase()).charAt(0) == 'y')
				trace = true;


			do {

				// Receive a message
				myLink.receiveFrame(receivingBuffer);
				//frameReceived++;

				//CRC Check
				byte b = receivingBuffer[35];
				receivingBuffer[35] = 0;
				byte a = crc.checksum(receivingBuffer);
				if (!(a == b)) {

					// Report the trace received incorrectly
					if (trace)
						System.out.println("Frame " + frameSequence + " received, Error");

					// Acknowledge the message was received incorrectly.
					ack[0] = 1;

					// Send the message
					//totalDamageTransmitted++;
					//frameTransmitted++;
					myLink.sendFrame(ack, ack.length);

				} else {

					// Report the trace received correctly
					if (trace)
						System.out.println("Frame " + frameSequence + " received, OK");

					// Write the message to the file.
					bw.write(new String(receivingBuffer, 3, receivingBuffer[2]));

					// Acknowledge the message was received correctly.
					ack[0] = 0;

					// Send the message
					//frameTransmitted++;
					myLink.sendFrame(ack, ack.length);

					// Next frame sequence number (Sequence number that should be next to receive)
					frameSequence++;

					// Checking of this is the last frame.
					last = receivingBuffer[1];
				}
			} while (last == 0);

			// Final output for the user
			//System.out.println("Total number of frames 'packets': " + frameSequence);
			//System.out.println("Total number of frames transmitted: " + frameTransmitted);
			//System.out.println("Theoretical total number of frames transmitted: " + (transmittedFrame / (1 - randomNumber)));
			//System.out.println("Total number of frames damaged: " + totalDamageTransmitted);
			//System.out.println("Maximum number of retransmission for a single frame: " + 
			//		transmittedFrameMax + "   transmitted: " + transmittedFrameMaxTotal + " times.");

			// It's all being reported on the Sender end at the moment, do I need to include it on the 
			// receiver end?

		} catch (Exception e) {

		} finally {
			// Close the connection
			bw.close();
			myLink.disconnect();
		}
	}
}
