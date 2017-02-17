import java.io.*;
import java.util.stream.IntStream;

/**
 * The code array stores the gray code in big-endian form [ numBits, numBits-1, ..., 1, 0 ]
 * For the 0th column, each of the numbers 0 to radix will appear radix^0 times, sequentially
 * For the 1st column, each of the numbers 0 to radix will appear radix^1 times, sequentially
 * For the ith column, each of the numbers 0 to radix will appear radix^i times, sequentially
 * After the sequence has finished for the ith column, the sequence is reversed until the end of the column.
 * This is how the generalized gray code is generated.
 */
public class GrayCode {
    public static void main(String[] args) {
        int[] arguments = getArgs(args);
        int numBits = arguments[0], radix = arguments[1];

        int rows = (int)Math.pow(radix,numBits);
        int[][] code = new int[rows][numBits];

        // Sequence of numbers for base-radix. Ex. Base-3 would mean vals = 0,1,2
        int[] vals = IntStream.range(0,radix).toArray();

        long t1 = System.nanoTime();
        for(int col = numBits - 1; col >= 0; col--) {
            // limit is how many times the number in the sequence will repeat before moving to the next number
            int limit = (int)Math.pow(radix, numBits - col - 1), direction = 1;
            for(int row = 0, selector = 0; row < rows; row++) {
                // Makes sure that each number in the sequence is added radix^(ith column) times
                if(row != 0 && row % limit == 0)
                    selector += direction;

                // Mirror vals after reaching adding the last numbers in the sequence
                if(selector == radix) {
                    direction = -1;
                    selector = radix - 1;
                }
                else if(selector == -1) {
                    direction = 1;
                    selector = 0;
                }

                code[row][col] = vals[selector];
            }
        }
        long t2 = System.nanoTime();
        writeToFile(code,"gray.txt");
        long t3 = System.nanoTime();

        double computeTime = ((double)(t2-t1)) / 1000000000;
        double writeTime = ((double)(t3-t2)) / 1000000000;
        System.out.printf("It took %.3f seconds to compute the gray code and %.3f seconds to write the results%n",computeTime,writeTime);
    }

    /**
     * Writes array answer to a file
     * @param arr the arr containing the gray code
     * @param filename the filename to save the file as
     */
    public static void writeToFile(int[][] arr, String filename) {
        int rows = arr.length;
        int cols = arr[0].length;
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter(filename);
            bw = new BufferedWriter(fw);
            for(int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++)
                    bw.write("" + arr[row][col]);
                bw.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) bw.close();
                if (fw != null) fw.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Gets arguments passed in from pipe or args and tries to get the expected input.
     * Input should be two numbers, n: number of numBits, & k: radix
     * @param args
     * @return int array containing n & k
     */
    public static int[] getArgs(String[] args) {
        int[] arguments = new int[2];

        // If args is empty, arguments may have been passed in by System.in
        if(args.length == 0) {
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            String x = null;
            try {
                // Try and read the first line of input, it should be in the form "<number>,<number>"
                if((x = input.readLine()) != null)
                    args = x.split(",");
            } catch(IOException e) {
                System.out.println(e);
                System.exit(-1);
            } finally {
                try {
                    input.close();
                } catch(IOException e) {
                    System.out.println(e);
                    System.exit(-1);
                }
            }
        }
        else if(args.length == 1) {
            args = args[0].split(",");
        }

        // If input wasn't in the form "<number>,<number>", exit the program
        if(args.length != 2) {
            System.out.printf("Unexpected number of arguments! Expected 2 but got %d", args.length);
            System.exit(0);
        }

        // Transform String arguments to their int values
        for(int i = 0; i < arguments.length; i++)
            arguments[i] = Integer.parseInt(args[i]);

        return arguments;
    }
}