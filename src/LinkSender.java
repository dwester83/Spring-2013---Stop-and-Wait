import java.io.*;
import java.util.*;

// LinkSender sends a message to LinkReceiver and receives a reply.
// LinkReceiver needs to be started before LinkSender.

public class LinkSender {

	static int senderPort = 3200;   // port number used by sender
	static int receiverPort = 3300; // port number used by receiver

	public static void main (String args[]) throws Exception {

		byte[] sendingBuffer = new byte[36];
		byte[] receivingBuffer = new byte[1];
		byte seq = 0;
		byte last = 0;
		byte len = 0;
		byte[] payload = new byte[32];
		CRC8 crc = new CRC8();
		FileInputStream inputFile = null;
		Random error = new Random();

		boolean trace = false;
		//int frameReceived = 0;
		int frameTransmitted = 0;
		int totalDamageTransmitted = 0;

		int transmittedFrame = 0;
		int transmittedFrameTotal = 0;
		int transmittedFrameMax = 0;
		int transmittedFrameMaxTotal = 0;

		// Set up a link with source and destination ports
		Link myLink = new SimpleLink(senderPort, receiverPort);

		try {
			// Prompt user on what file they want.
			Scanner scan = new Scanner(System.in);
			System.out.print("Text file you want to use: ");
			String input = scan.nextLine();
			File file = new File("bin/" + input);
			inputFile = new FileInputStream(file);

			// Prompt the user on error rate.
			System.out.print("Error rate you'd like (0 - 100): ");
			input = scan.nextLine();
			double randomNumber = (Double.parseDouble(input) / 100);

			// Prompt the user if they want a tracer on
			System.out.print("Turn on trace?: ");
			input = scan.nextLine();
			if ((input.toLowerCase()).charAt(0) == 'y')
				trace = true;

			// Starts loop of sending the file.
			do {
				//Checking the length of the file being written
				if(inputFile.available() > payload.length){
					last = 0;
					len = (byte) payload.length;
				} else {
					last = 1;
					len = (byte) inputFile.available();
				}

				//Building the sendingBuffer
				sendingBuffer[0] = seq++;
				sendingBuffer[1] = last;
				sendingBuffer[2] = len;

				//Built the pay load
				inputFile.read(payload, 0, len);

				// Transmitted frame reset
				transmittedFrameTotal = 0;
				transmittedFrame = seq;

				//Starts the loop in case of errors
				do {

					for (int i = 0; i < len; i++)
						sendingBuffer[i+3] = payload[i];

					//Adding the CRC check.
					sendingBuffer[35] = 0;
					sendingBuffer[35] = crc.checksum(sendingBuffer);

					//Introduce Error here
					if ((error.nextDouble() - randomNumber) <= 0) {
						sendingBuffer[10] = 10;
						// Trace will state if error was sent or not
						if (trace) {
							totalDamageTransmitted++;
							System.out.println("Frame " + transmittedFrame + " transmitted, Error");
						}
					} else {
						if (trace)
							System.out.println("Frame " + transmittedFrame + " transmitted, OK");
					}

					// Frame being sent to check what sent the most
					transmittedFrameTotal++;
					if (transmittedFrameTotal > transmittedFrameMaxTotal) {
						transmittedFrameMax = transmittedFrame;
						transmittedFrameMaxTotal = transmittedFrameTotal;
					}

					// Send the message
					myLink.sendFrame (sendingBuffer, sendingBuffer.length);
					frameTransmitted++;

					// Receive a message
					myLink.receiveFrame(receivingBuffer);
					//frameReceived++;

					// Received message is good break loop, bad re-do loop.
					if (receivingBuffer[0] == 0)
						break;

				} while (true);
			} while ((last == 0));

			// Final output for the user
			System.out.println("Total number of frames 'packets': " + transmittedFrame);
			System.out.println("Total number of frames transmitted: " + frameTransmitted);
			System.out.println("Theoretical total number of frames transmitted: " + (transmittedFrame / (1 - randomNumber)));
			System.out.println("Total number of frames damaged: " + totalDamageTransmitted);
			System.out.println("Maximum number of retransmission for a single frame: " + 
					transmittedFrameMax + "   transmitted: " + transmittedFrameMaxTotal + " times.");

		} catch (FileNotFoundException e) {
			System.out.println("Couldn't find a file. Make sure it's in the bin folder and includes .txt");
		} catch (NumberFormatException e) {
			System.out.println("Please enter a number, Strings aren't numbers.");
		} finally {
			// Close the connection	
			inputFile.close();
			myLink.disconnect();
		}
	}
}
