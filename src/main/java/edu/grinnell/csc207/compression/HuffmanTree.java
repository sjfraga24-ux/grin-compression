package edu.grinnell.csc207.compression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * A HuffmanTree derives a space-efficient coding of a collection of byte
 * values.
 *
 * The huffman tree encodes values in the range 0--255 which would normally
 * take 8 bits.  However, we also need to encode a special EOF character to
 * denote the end of a .grin file.  Thus, we need 9 bits to store each
 * byte value.  This is fine for file writing (modulo the need to write in
 * byte chunks to the file), but Java does not have a 9-bit data type.
 * Instead, we use the next larger primitive integral type, short, to store
 * our byte values.
 */
public class HuffmanTree {

    BitInputStream input;
    BitOutputStream output;
    PriorityQueue<Node> freq = new PriorityQueue();

    public class Pair{
        short key;
        int val;
        public Pair(short key,int val){
            this.key = key;
            this.val = val;
        }
    }
    
    public class Node{
        Node leafPairR;
        Node leafPairL;
        Integer leafIntR;
        Integer leafIntL;
        Integer val;
        Pair value;
        public Node(Node leafR, Node leafL){
            this.leafPairL = leafL;
            this.leafPairR = leafR;
            val = leafL.val + leafR.val;
        }
        
        public Node(int leafR, Node leafL){
            this.leafPairL = leafL;
            this.leafIntR = leafR;
            val = leafL.val + leafR;
        }

        public Node(Node leafR,int leafL){
            this.leafIntL = leafL;
            this.leafPairR = leafR;
            val = leafL + leafR.val;
        }

        public Node(int leafR, int leafL){
            this.leafIntL = leafL;
            this.leafIntR = leafR;
            val = leafL + leafR;
        }

        public Node(Pair leaf){
            value = leaf;
        }
        public Node(int leaf){
            val= leaf;
        }
    }

    public List<Short> getKeys(Map<Short,Integer> data){
        List <Short> l = new ArrayList <Short> ();
        Set<Short> s = data.keySet();
        l.addAll (s);
        return l;
    }

    public int getValMax(Map<Short, Integer> freqs){
        List<Short> keyList = getKeys(freqs);
        int max = 0;
        for(int i = 0; i < freqs.size(); i++){
            if(freqs.get(keyList.get(i)) > max){
                max = freqs.get(keyList.get(i));
            }
        }
        return max;
    }
    /**
     * Constructs a new HuffmanTree from a frequency map.
     * @param freqs a map from 9-bit values to frequencies.
     */
    public HuffmanTree (Map<Short, Integer> freqs) {
        List<Short> keyList = getKeys(freqs);

        for(int j = 0; j < getValMax(freqs); j++){
            for(int i = 0; i < freqs.size(); i++){
                if(freqs.get(keyList.get(i)) == j){
                    freq.add(new Node(new Pair(keyList.get(i), j)));
                }

            }
        }

        while(freq.size()>1){
            Node temp = new Node(freq.poll(), freq.poll());
            freq.add(temp);
        }


    }

    /**
     * Constructs a new HuffmanTree from the given file.
     * @param in the input file (as a BitInputStream)
     */
    public HuffmanTree (BitInputStream in) {

        while(in.hasBits()){
            freq.add(new Node(in.readBits(9)));
        }

        while(freq.size()>1){
            Node temp = new Node(freq.poll(), freq.poll());
            freq.add(temp);
        }
    }

    /**
     * Writes this HuffmanTree to the given file as a stream of bits in a
     * serialized format.
     * @param out the output file as a BitOutputStream
     */
    public void serialize (BitOutputStream out) {
        HuffmanTree temp = new HuffmanTree(input);
        while(temp != null){
            Integer tempVal = freq.poll().val;
            if( tempVal instanceof Integer){
                 out.writeBit(1);
            }else{
                out.writeBit(0);
                out.writeBits(input.readBits(8),8)
            }
           
        }
        
    }
   
    /**
     * Encodes the file given as a stream of bits into a compressed format
     * using this Huffman tree. The encoded values are written, bit-by-bit
     * to the given BitOuputStream.
     * @param in the file to compress.
     * @param out the file to write the compressed output to.
     */
    public void encode (BitInputStream in, BitOutputStream out) {
        while(in.hasBits()){
           int temp = in.readBits(8);
           out.writeBits(temp, 8);
        }
    }

    /**
     * Decodes a stream of huffman codes from a file given as a stream of
     * bits into their uncompressed form, saving the results to the given
     * output stream. Note that the EOF character is not written to out
     * because it is not a valid 8-bit chunk (it is 9 bits).
     * @param in the file to decompress.
     * @param out the file to write the decompressed output to.
     */
    public void decode (BitInputStream in, BitOutputStream out) {
        in.readBits(32);
        HuffmanTree temp = new HuffmanTree(in);
        while(in.hasBits()){
            out.writeBits(in.readBits(8),8);
        }
    }
}
