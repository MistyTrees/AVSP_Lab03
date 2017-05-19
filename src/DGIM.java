

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by Luka on 19/05/2017.
 */
public class DGIM {
    public static final int MAXBUCKETCOUNT = 2;
    public static int maxBucketSize = 1;
    public static int LAST;
    public static int TOTAL;
    public static int currentTime;
    public static int windowWidth;
    public static ArrayList<Bucket> buckets;

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        //BufferedReader reader = new BufferedReader(new FileReader("tests\\1.in"));
        windowWidth = Integer.parseInt(reader.readLine());
        String line;
        int k;

        buckets = new ArrayList<>();
        TOTAL = 0;
        LAST = 0;
        currentTime = 0;

        StringBuilder sb = new StringBuilder();
        while((line = reader.readLine())!= null){
            if(line.startsWith("q")){   //query for 1's in last k bits
                k = Integer.parseInt(line.substring(2));
                sb.append(query(k)+"\n");//"\r\n");
            }
            else{
                addNewBits(line);
            }
        }

        //String rez = new String(Files.readAllBytes(Paths.get("tests\\3.out")));
        //compareResults(sb.toString(), rez);
        System.out.print(sb.toString());
    }

    private static void addNewBits(String bits) {
        for (int i=0; i<bits.length(); i++) {
            char bit = bits.charAt(i);
            int expiryTime = ++currentTime - windowWidth;
            if (!buckets.isEmpty() && buckets.get(buckets.size()-1).timeStamp <= expiryTime ){
                Bucket removed = buckets.remove(buckets.size()-1);
                TOTAL -= removed.size;
                LAST = buckets.isEmpty() ? 0 : buckets.get(buckets.size()-1).size;
            }

            if(bit == '1'){
                TOTAL ++;
                Bucket newBucket = new Bucket(1);
                newBucket.timeStamp =  currentTime;
                updateBucketList( newBucket);
            }

        }
    }

    private static void updateBucketList(Bucket newBucket) {
        buckets.add(0, newBucket);
        for(int bucketSize=1; bucketSize<=maxBucketSize; bucketSize++){
            int lastBucketWithSize = 0;
            int count = 0;
            for(int pos=0; pos<buckets.size() && buckets.get(pos).size<=bucketSize; pos++){
                if(buckets.get(pos).size == bucketSize){
                    count++;
                    lastBucketWithSize = pos;
                }
            }

            if(count > MAXBUCKETCOUNT){     //wont work if MAXBUCKETCOUNT is 0 :/
                Bucket merged = buckets.get(lastBucketWithSize).merge(buckets.get(lastBucketWithSize-1));
                buckets.set(lastBucketWithSize-1, merged);
                buckets.remove(lastBucketWithSize);
            }
        }

        LAST = buckets.get(buckets.size()-1).size;
    }

    private static String query(int k) {
        int thresholdTime = currentTime-k;

        int z = -1;
        for(int pos=0; pos<buckets.size(); pos++){
            Bucket b = buckets.get(pos);
            if(b.timeStamp <= thresholdTime){
                break;
            }
            z = pos;

        }

        int sum = 0;
        for(int pos=0; pos<buckets.size(); pos++){
            Bucket b = buckets.get(pos);
            if(pos < z){
                sum += b.size;
            }
            else{
                sum += (b.size/2);
                break;
            }
        }
        return Integer.toString(sum);
    }

    private static void compareResults(String calc, String rez) {
        String[] calculatedLines = calc.split("\n"); //tests have different line ends linux vs win
        String[] recievedLines = rez.split("\n");

        //System.out.println("Calculated lines: "+calculatedLines.length+"\nRecieved lines: "+recievedLines.length);
        for(int i=0; i<calculatedLines.length; i++){
            //System.out.println(i+". calc: " +calculatedLines[i]+"     rec:"+recievedLines[i]);
            if(calculatedLines[i].compareTo(recievedLines[i]) != 0){
                System.out.println("Request no. "+i+"and diff is calc:"+calculatedLines[i]+"vs. rec:"+recievedLines[i]);
            }
        }

        System.out.print(calc.compareTo(rez)==0? "Same results" : "Errors were made!!");
    }

    private static class Bucket {
        public int timeStamp;
        //public int[] frameElements;
        public int size;

        public Bucket(){
            timeStamp = 0;
            //frameElements = null;
            size = 0;
        }

        public Bucket(int frameSize){
            timeStamp = 0;
            //frameElements = null;
            size = frameSize;
        }

        public Bucket merge(Bucket b2){
            if(this.size != b2.size){
                throw new IllegalArgumentException("Buckets are not of the same size: " +
                        Integer.toString(this.size) +" and "+ Integer.toString(b2.size));
            }
            Bucket merged = new Bucket(this.size*2);
            merged.timeStamp = Math.max(this.timeStamp, b2.timeStamp);

            if(merged.size > maxBucketSize){
                maxBucketSize = merged.size;
            }
            return merged;
        }

    }
}
