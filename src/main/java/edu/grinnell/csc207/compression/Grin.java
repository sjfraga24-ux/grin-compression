package edu.grinnell.csc207.compression;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The driver for the Grin compression program.
 */
public class Grin {
    /**
     * Decodes the .grin file denoted by infile and writes the output to the
     * .grin file denoted by outfile.
     * @param infile the file to decode
     * @param outfile the file to ouptut to
     * @throws IOException 
     */
    public static void decode (String infile, String outfile) throws IOException {
        BitInputStream in = new BitInputStream(infile);
        BitOutputStream out = new BitOutputStream(outfile);
        HuffmanTree temp = new HuffmanTree(in);
        temp.decode(in, out);
    }

    /**
     * Creates a mapping from 8-bit sequences to number-of-occurrences of
     * those sequences in the given file. To do this, read the file using a
     * BitInputStream, consuming 8 bits at a time.
     * @param file the file to read
     * @return a freqency map for the given file
     * @throws IOException 
     */
    public static Map<Short, Integer> createFrequencyMap (String file) throws IOException {
        BitInputStream in = new BitInputStream(file);
        HashMap<Short,Integer> ret = new HashMap<>();
        while(in.hasBits()){
            short temp = (short)in.readBits(8);
            if(ret.containsKey(temp)){
                ret.put(temp, ret.get(temp)+1);
            }else{
                ret.put(temp, 1);
            }
        }
        return ret;
    }

    /**
     * Encodes the given file denoted by infile and writes the output to the
     * .grin file denoted by outfile.
     * @param infile the file to encode.
     * @param outfile the file to write the output to.
     * @throws IOException 
     */
    public static void encode(String infile, String outfile) throws IOException {
        BitInputStream in = new BitInputStream(infile);
        BitOutputStream out = new BitOutputStream(outfile);
        HuffmanTree temp = new HuffmanTree(createFrequencyMap(infile));
        temp.encode(in, out);
    }

    /**
     * The entry point to the program.
     * @param args the command-line arguments.
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        if(args.length != 3 || !args[0].equals("encode") || !args[0].equals("decode")){
            System.out.println("Usage: java Grin <encode|decode> <infile> <outfile>");
        } else if(args[0].equals("encode")){
            encode(args[1],args[2]);
        } else{
            decode(args[1],args[2]);
        }
        
    }
}
