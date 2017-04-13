import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 */

/**
 * @author Archana
 *
 */
public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// File operations start
		String fileName = "src/training_data";
		String line = null;
		int ax = 2, ay = 3, az = 3, gx = 2, gy = 3, gz = 3;
		// TreeMap<Integer, String> sortedNumbers = new TreeMap<>();//sorted
		// collection
		// Map<Integer, String> hashMap = new HashMap<Integer, String>();
		List<Pair<Integer, String>> charList = new ArrayList<Pair<Integer, String>>();
		int records = 0;
		int fall = 0;
		int notfall = 0;
		int min = 0;

		try {

			FileReader fileReader = new FileReader(fileName);

			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null) {

				String[] lineVal = line.split(",");
				// System.out.println( Arrays.toString(lineVal));
				int distance = (int) Math.pow((Integer.parseInt(lineVal[0]) - ax), 2)
						+ (int) Math.pow((Integer.parseInt(lineVal[1]) - ay), 2)
						+ (int) Math.pow((Integer.parseInt(lineVal[2]) - az), 2)
						+ (int) Math.pow((Integer.parseInt(lineVal[3]) - gx), 2)
						+ (int) Math.pow((Integer.parseInt(lineVal[4]) - gy), 2)
						+ (int) Math.pow((Integer.parseInt(lineVal[5]) - gz), 2);

				// hashMap.put(distance, lineVal[6]);
				charList.add(new Pair(distance, lineVal[6]));
				records++;
			}

			// find out minimun distance & it values
			System.out.print("PermuteBySortg: [");
			int threshold = (int) Math.pow(records, 0.5);
			System.out.println("Threashold = " + threshold);

			for (Pair<Integer, String> pair : charList) {

				System.out.println(pair.key + " -> " + pair.value);
				if (pair.key <= threshold) {
					if (pair.value == "+") {
						fall++;
					} else {
						notfall++;
					}
				}

			}
			System.out.println("fall = " + fall + "Notfall =" + notfall);
			if( fall !=0 || notfall !=0 ){
			if (fall >= notfall) {
				System.out.println("FALL");
			} else {
				System.out.println("NOT FALL");
			}}

			System.out.println("]");

			// Always close files.
			bufferedReader.close();
			// File operations end
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}

}
