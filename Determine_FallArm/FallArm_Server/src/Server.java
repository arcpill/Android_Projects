
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.io.*;

public class Server extends Thread {
	public final static int defaultPort = 8888;
	ServerSocket theServer;
	static int num_threads = 1;

	public static void main(String[] args) {
		int port = defaultPort;
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception e) {
		}
		if (port <= 0 || port >= 65536)
			port = defaultPort;
		try {
			ServerSocket ss = new ServerSocket(port);
			System.out.println("Server Socket Start!!");
			for (int i = 0; i < num_threads; i++) {
				System.out.println("Create num_threads " + i + " Port:" + port);
				Server pes = new Server(ss);
				pes.start();
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public Server(ServerSocket ss) {
		theServer = ss;
	}

	public void run() {
		while (true) {
			try {

				DataOutputStream output;
				DataInputStream input;
				Socket connection = theServer.accept();

				input = new DataInputStream(connection.getInputStream());
				output = new DataOutputStream(connection.getOutputStream());
				System.out.println("Client Connected and Start get I/O!!");
				while (true) {
					// System.out.println("==> Input from Client: " +
					// input.readUTF());

					// Split the string & get each coordinate value
					// (x,y,z,x,y,z)
					String inputFromAndroid = input.readUTF();

					// System.out.println(inputFromAndroid);
					String[] inputArr = inputFromAndroid.split(",");

					// convert to integer values
					float[] coordinates = new float[inputArr.length];
					for (int i = 0; i < coordinates.length; i++) {
						coordinates[i] = Float.parseFloat(inputArr[i]);
						
					}
					
					System.out.format(
							"Accelerometer    X: %f \n %15s Y: %f \n %15s Z: %f \n"
									+ "Orientation      X: %f \n %15s Y: %f \n %15s Z: %f \nFall Down (YES/NO):",
							coordinates[0], "", coordinates[1], "", coordinates[2], coordinates[3], "", coordinates[4],
							"", coordinates[5]);

					// Logic to read file, calculate distance & decide FALL or
					// Not FALL
					String fileName = "src/training_data";
					String line = null;
					List<Pair<Integer, String>> charList = new ArrayList<Pair<Integer, String>>();
					List<Integer> distances = new ArrayList<Integer>();
					int records = 0;
					int fall = 0;
					int notfall = 0;

					try {

						// Read training data
						FileReader fileReader = new FileReader(fileName);
						// Always wrap FileReader in BufferedReader.
						BufferedReader bufferedReader = new BufferedReader(fileReader);

						// Read one record at a time & calculate distance
						while ((line = bufferedReader.readLine()) != null) {

							String[] lineVal = line.split(",");
							// System.out.println( Arrays.toString(lineVal));
							int distance = (int) Math.pow((Float.parseFloat(lineVal[0]) - coordinates[0]), 2)
									+ (int) Math.pow((Float.parseFloat(lineVal[1]) - coordinates[1]), 2)
									+ (int) Math.pow((Float.parseFloat(lineVal[2]) - coordinates[2]), 2)
									+ (int) Math.pow((Float.parseFloat(lineVal[3]) - coordinates[3]), 2)
									+ (int) Math.pow((Float.parseFloat(lineVal[4]) - coordinates[4]), 2)
									+ (int) Math.pow((Float.parseFloat(lineVal[5]) - coordinates[5]), 2);

							charList.add(new Pair<Integer, String>(distance, lineVal[6])); // calculatesDistance,+/-
							distances.add(distance); // calculatedDistances
							//System.out.println( distance );
							records++;
						}

						// Calculate threshold( here it is 2 always coz 8
						// records , square root of 8 = approx 2 )
						int threshold = (int) Math.pow(records, 0.5);

						// sort distances
						Collections.sort(distances);

						// Check & decide the minimun distances
						for (Pair<Integer, String> pair : charList) {

							// System.out.println(pair.key + " -> " +
							// pair.value);

							for (int j = 0; j < threshold; j++) {
								if (pair.key.compareTo(distances.get(j)) == 0) {
									if (pair.value.contains("+")) {
										fall++;
									} else {
										notfall++;
									}
								}
							}

						}
						// System.out.println("fall = " + fall + "Notfall =" +
						// notfall);
						if (fall != 0 || notfall != 0) {
							if (fall >= notfall) {
								System.out.println("YES");
								// output.writeUTF("FALL");
							} else {
								System.out.println("NO");
								// output.writeUTF("NO FALL DETECTED");
							}
						} else {
							output.writeUTF("NOT SURE");
						}

						// Always close files.
						bufferedReader.close();
						// File operations end
					} catch (IOException e) {
						//e.printStackTrace();
					}

					// System.out.println("Output to Client ==> \"Connection
					// successful\"");
					output.writeUTF("Connection successful");
					output.flush();
				} // end while
			} // end try
			catch (IOException e) {

				//e.printStackTrace();
			}

		}
	}

}
