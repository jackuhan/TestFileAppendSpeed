package com.test;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
/**
 * Test result
 *
 *
 * RandomAccessFile with ByteBuffer:153600 test2.tmp spent time=206ms
 * RandomAccessFile with ByteBuffer:1048576 test3.tmp spent time=145ms
 * RandomAccessFile + with MappedByteBuffer and FileMap:104857600 test4.tmp spent time=141ms
 *  DataOutputStream with BufferedOutputStream :153600 test5.tmp spent time=1170 ms
 * PrintWriter/FileWriter with BufferedWriter:153600 test7.tmp spent time=2790 ms
 * PrintWriter/FileWriter without BufferedWriter:153600 test8.tmp spent time=3554 ms
 * PrintWriter/OutputStreamWriter with BufferedWriter:153600 test9.tmp spent time=856 ms
 * PrintWriter/OutputStreamWriter without BufferedWriter:153600 test10.tmp spent time=2058 ms
 *
 */
public class TestFileAppendSpeed {
    public static void main(String[] args) throws Exception {
        // Raw RandomAccessFile
        // it is too slow to comment it
        // test1("test1.tmp",100000);
        // nio ByteBuffer RandomAccessFile
        test2("test2.tmp", 1000000, 1024 * 150);
        // nio ByteBuffer RandomAccessFile, with big buffer
        test2("test3.tmp", 1000000, 1024 * 1024);
        // using map file with MappedByteBuffer
        test3("test4.tmp", 1000000, 1024 * 1024 * 100);
        // old io, BufferedOutputStream with BufferedOutputStream
        test4("test5.tmp", 1000000, 1024 * 150);
        // old io, BufferedOutputStream without BufferedOutputStream
        // it is too slow to comment it
        // test5("test6.tmp", 1000000, 1024 * 150);
        // old io, PrintWriter/FileWriter with BufferedWriter
        test6("test7.tmp", 1000000, 1024 * 150);
        // old io, PrintWriter/FileWriter without BufferedWriter
        test7("test8.tmp", 1000000, 1024 * 150);
        // old io, PrintWriter/OutputStreamWriter without BufferedWriter
        test8("test9.tmp", 1000000, 1024 * 150);
        // old io, PrintWriter/FileWriter without BufferedWriter
        test9("test10.tmp", 1000000, 1024 * 150);
    }
    public static void test9(String file, int loop, int mapsize)
            throws Exception {
        new File(file).delete();
        PrintWriter raf = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(file, true)));
        byte[] b1 = new byte[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
        byte[] utfstr = "this is a test".getBytes("UTF-8");
        long s = System.nanoTime();
        for (int i = 0; i < loop; i++) {
            raf.print(b1);
            raf.print(i);
            raf.print(i + 1);
            raf.print(utfstr);
            raf.print((byte) '\n');
        }
        raf.close();
        long d = System.nanoTime() - s;
        System.out
                .println("PrintWriter/OutputStreamWriter without BufferedWriter:"
                        + mapsize
                        + " "
                        + file
                        + " spent time="
                        + (d / 1000000)
                        + " ms");
    }
    public static void test8(String file, int loop, int mapsize)
            throws Exception {
        new File(file).delete();
        PrintWriter raf = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, true)),
                mapsize));
        byte[] b1 = new byte[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
        byte[] utfstr = "this is a test".getBytes("UTF-8");
        long s = System.nanoTime();
        for (int i = 0; i < loop; i++) {
            raf.print(b1);
            raf.print(i);
            raf.print(i + 1);
            raf.print(utfstr);
            raf.print((byte) '\n');
        }
        raf.close();
        long d = System.nanoTime() - s;
        System.out
                .println("PrintWriter/OutputStreamWriter with BufferedWriter:"
                        + mapsize + " " + file + " spent time=" + (d / 1000000)
                        + " ms");
    }
    public static void test7(String file, int loop, int mapsize)
            throws Exception {
        new File(file).delete();
        PrintWriter raf = new PrintWriter(new FileWriter(file));
        // output = new PrintWriter(new OutputStreamWriter(new
        // FileOutputStream(file, true)));
        byte[] b1 = new byte[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
        byte[] utfstr = "this is a test".getBytes("UTF-8");
        long s = System.nanoTime();
        for (int i = 0; i < loop; i++) {
            raf.print(b1);
            raf.print(i);
            raf.print(i + 1);
            raf.print(utfstr);
            raf.print((byte) '\n');
        }
        raf.close();
        long d = System.nanoTime() - s;
        System.out
                .println("PrintWriter/FileWriter without BufferedWriter:"
                        + mapsize + " " + file + " spent time=" + (d / 1000000)
                        + " ms");
    }
    public static void test6(String file, int loop, int mapsize)
            throws Exception {
        new File(file).delete();
        PrintWriter raf = new PrintWriter(new BufferedWriter(new FileWriter(
                file), mapsize));
        // output = new PrintWriter(new OutputStreamWriter(new
        // FileOutputStream(file, true)));
        byte[] b1 = new byte[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
        byte[] utfstr = "this is a test".getBytes("UTF-8");
        long s = System.nanoTime();
        for (int i = 0; i < loop; i++) {
            raf.print(b1);
            raf.print(i);
            raf.print(i + 1);
            raf.print(utfstr);
            raf.print((byte) '\n');
        }
        raf.close();
        long d = System.nanoTime() - s;
        System.out
                .println("PrintWriter/FileWriter with BufferedWriter:"
                        + mapsize + " " + file + " spent time=" + (d / 1000000)
                        + " ms");
    }
    public static void test5(String file, int loop, int mapsize)
            throws Exception {
        new File(file).delete();
        DataOutputStream raf = new DataOutputStream(new FileOutputStream(
                new File(file), true));
        byte[] b1 = new byte[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
        byte[] utfstr = "this is a test".getBytes("UTF-8");
        long s = System.nanoTime();
        for (int i = 0; i < loop; i++) {
            raf.write(b1);
            raf.writeInt(i);
            raf.writeInt(i + 1);
            raf.write(utfstr);
            raf.write((byte) '\n');
        }
        raf.close();
        long d = System.nanoTime() - s;
        System.out
                .println(" DataOutputStream without BufferedOutputStream :"
                        + mapsize + " " + file + " spent time=" + (d / 1000000)
                        + " ms");
    }
    public static void test4(String file, int loop, int mapsize)
            throws Exception {
        new File(file).delete();
        DataOutputStream raf = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(new File(file), true), mapsize));
        byte[] b1 = new byte[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
        byte[] utfstr = "this is a test".getBytes("UTF-8");
        long s = System.nanoTime();
        for (int i = 0; i < loop; i++) {
            raf.write(b1);
            raf.writeInt(i);
            raf.writeInt(i + 1);
            raf.write(utfstr);
            raf.write((byte) '\n');
        }
        raf.close();
        long d = System.nanoTime() - s;
        System.out
                .println(" DataOutputStream with BufferedOutputStream :"
                        + mapsize + " " + file + " spent time=" + (d / 1000000)
                        + " ms");
    }
    public static void test3(String file, int loop, int mapsize)
            throws Exception {
        new File(file).delete();
        RandomAccessFile raf1 = new RandomAccessFile(file, "rw");
        FileChannel fc = raf1.getChannel();
        MappedByteBuffer raf = fc.map(MapMode.READ_WRITE, 0, mapsize);
        raf.clear();
        // ByteBuffer raf = ByteBuffer.allocateDirect(mapsize);
        byte[] b1 = new byte[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
        byte[] utfstr = "this is a test".getBytes("UTF-8");
        long s = System.nanoTime();
        long count = 0;
        for (int i = 0; i < loop; i++) {
            if (raf.remaining() < 140) {
                System.out.println("remap");
                count += raf.position();
                raf = fc.map(MapMode.READ_WRITE, count, mapsize);
                // raf = fc.map(MapMode.READ_WRITE, raf.position(),
                // raf.position()+mapsize);
            }
            raf.put(b1);
            raf.putInt(i);
            raf.putInt(i + 1);
            raf.put(utfstr);
            raf.put((byte) '\n');
        }
        fc.close();
        raf1.close();
        long d = System.nanoTime() - s;
        System.out
                .println("RandomAccessFile + with MappedByteBuffer and FileMap:"
                        + mapsize
                        + " "
                        + file
                        + " spent time="
                        + (d / 1000000)
                        + "ms");
    }
    public static void test2(String file, int loop, int mapsize)
            throws Exception {
        new File(file).delete();
        RandomAccessFile raf1 = new RandomAccessFile(file, "rw");
        FileChannel fc = raf1.getChannel();
        ByteBuffer raf = ByteBuffer.allocate(mapsize);
        raf.clear();
        // ByteBuffer raf = ByteBuffer.allocateDirect(mapsize);
        byte[] b1 = new byte[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
        byte[] utfstr = "this is a test".getBytes("UTF-8");
        long s = System.nanoTime();
        for (int i = 0; i < loop; i++) {
            raf.put(b1);
            raf.putInt(i);
            raf.putInt(i + 1);
            raf.put(utfstr);
            raf.put((byte) '\n');
            if (raf.remaining() < 140) {
                raf.flip();
                fc.write(raf);
                raf.compact();
            }
        }
        raf.flip();
        fc.write(raf);
        fc.close();
        raf1.close();
        long d = System.nanoTime() - s;
        System.out.println("RandomAccessFile with ByteBuffer:" + mapsize + " "
                + file + " spent time=" + (d / 1000000) + "ms");
    }
    public static void test1(String file, int loop) throws Exception {
        new File(file).delete();
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        byte[] b1 = new byte[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
        byte[] utfstr = "this is a test".getBytes("UTF-8");
        long s = System.nanoTime();
        for (int i = 0; i < loop; i++) {
            raf.write(b1);
            raf.writeInt(i);
            raf.writeInt(i + 1);
            raf.write(utfstr);
            raf.write('\n');
        }
        raf.close();
        long d = System.nanoTime() - s;
        System.out.println("RandomAccessFile without any buffer " + file
                + "  spent time=" + (d / 1000000) + "ms");
    }
}