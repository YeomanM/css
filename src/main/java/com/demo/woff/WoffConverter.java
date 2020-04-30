package com.demo.woff;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class WoffConverter {
    private static final LinkedHashMap<String, Integer> woffHeaderFormat = new LinkedHashMap<String, Integer>() {
        {
            this.put("signature", 4);
            this.put("flavor", 4);
            this.put("length", 4);
            this.put("numTables", 2);
            this.put("reserved", 2);
            this.put("totalSfntSize", 4);
            this.put("majorVersion", 2);
            this.put("minorVersion", 2);
            this.put("metaOffset", 4);
            this.put("metaLength", 4);
            this.put("metaOrigLength", 4);
            this.put("privOffset", 4);
            this.put("privOrigLength", 4);
        }
    };
    private static final LinkedHashMap<String, Integer> tableRecordEntryFormat = new LinkedHashMap<String, Integer>() {
        {
            this.put("tag", 4);
            this.put("offset", 4);
            this.put("compLength", 4);
            this.put("origLength", 4);
            this.put("origChecksum", 4);
        }
    };
    private HashMap<String, Number> woffHeaders = new HashMap();
    private ArrayList<HashMap<String, Number>> tableRecordEntries = new ArrayList();
    private int offset = 0;
    private int readOffset = 0;

    public WoffConverter() {
    }

    public byte[] convertToTTFByteArray(InputStream var1) throws InvalidWoffException, IOException, DataFormatException {
        ByteArrayOutputStream var2 = this.convertToTTFOutputStream(var1);
        byte[] var3 = var2.toByteArray();
        return var3;
    }

    public ByteArrayOutputStream convertToTTFOutputStream(InputStream var1) throws InvalidWoffException, IOException, DataFormatException {
        this.getHeaders(new DataInputStream(var1));
        if ((Integer)this.woffHeaders.get("signature") != 2001684038) {
            throw new InvalidWoffException("Invalid woff file");
        } else {
            ByteArrayOutputStream var2 = new ByteArrayOutputStream();
            this.writeOffsetTable(var2);
            this.getTableRecordEntries(new DataInputStream(var1));
            this.writeTableRecordEntries(var2);
            this.writeFontData(var1, var2);
            return var2;
        }
    }

    private void getHeaders(DataInputStream var1) throws IOException {
        this.readTableData(var1, woffHeaderFormat, this.woffHeaders);
    }

    private void writeOffsetTable(ByteArrayOutputStream var1) throws IOException {
        var1.write(this.getBytes((Integer)this.woffHeaders.get("flavor")));
        int var2 = (Integer)this.woffHeaders.get("numTables");
        var1.write(this.getBytes((short)var2));
        int var3 = var2;
        int var4 = 16;

        short var5;
        for(var5 = 0; var3 > 1; var4 <<= 1) {
            var3 >>= 1;
            ++var5;
        }

        short var6 = (short)(var2 * 16 - var4);
        var1.write(this.getBytes((short)var4));
        var1.write(this.getBytes(var5));
        var1.write(this.getBytes(var6));
        this.offset += 12;
    }

    private void getTableRecordEntries(DataInputStream var1) throws IOException {
        int var2 = (Integer)this.woffHeaders.get("numTables");

        for(int var3 = 0; var3 < var2; ++var3) {
            HashMap var4 = new HashMap();
            this.readTableData(var1, tableRecordEntryFormat, var4);
            this.offset += 16;
            this.tableRecordEntries.add(var4);
        }

    }

    private void writeTableRecordEntries(ByteArrayOutputStream var1) throws IOException {
        Iterator var2 = this.tableRecordEntries.iterator();

        while(var2.hasNext()) {
            HashMap var3 = (HashMap)var2.next();
            var1.write(this.getBytes((Integer)var3.get("tag")));
            var1.write(this.getBytes((Integer)var3.get("origChecksum")));
            var1.write(this.getBytes(this.offset));
            var1.write(this.getBytes((Integer)var3.get("origLength")));
            var3.put("outOffset", this.offset);
            this.offset += (Integer)var3.get("origLength");
            if (this.offset % 4 != 0) {
                this.offset += 4 - this.offset % 4;
            }
        }

    }

    private void writeFontData(InputStream var1, ByteArrayOutputStream var2) throws IOException, DataFormatException {
        int var12;
        for(Iterator var3 = this.tableRecordEntries.iterator(); var3.hasNext(); var2.write(this.getBytes((int)0), 0, var12)) {
            HashMap var4 = (HashMap)var3.next();
            int var5 = (Integer)var4.get("offset");
            int var6 = var5 - this.readOffset;
            if (var6 > 0) {
                var1.skip((long)var6);
            }

            this.readOffset += var6;
            int var7 = (Integer)var4.get("compLength");
            int var8 = (Integer)var4.get("origLength");
            byte[] var9 = new byte[var7];
            byte[] var10 = new byte[var8];

            for(int var11 = 0; var11 < var7; var11 += var1.read(var9, var11, var7 - var11)) {
            }

            this.readOffset += var7;
            var10 = this.inflateFontData(var7, var8, var9, var10);
            var2.write(var10);
            this.offset = (Integer)var4.get("outOffset") + (Integer)var4.get("origLength");
            var12 = 0;
            if (this.offset % 4 != 0) {
                var12 = 4 - this.offset % 4;
            }
        }

    }

    private byte[] inflateFontData(int var1, int var2, byte[] var3, byte[] var4) {
        if (var1 != var2) {
            Inflater var5 = new Inflater();
            var5.setInput(var3, 0, var1);

            try {
                var5.inflate(var4, 0, var2);
            } catch (DataFormatException var7) {
                throw new InvalidWoffException("Malformed woff file");
            }
        } else {
            var4 = var3;
        }

        return var4;
    }

    private byte[] getBytes(int var1) {
        return ByteBuffer.allocate(4).putInt(var1).array();
    }

    private byte[] getBytes(short var1) {
        return ByteBuffer.allocate(2).putShort(var1).array();
    }

    private void readTableData(DataInputStream var1, LinkedHashMap<String, Integer> var2, HashMap<String, Number> var3) throws IOException {
        int var6;
        for(Iterator var4 = var2.keySet().iterator(); var4.hasNext(); this.readOffset += var6) {
            String var5 = (String)var4.next();
            var6 = (Integer)var2.get(var5);
            if (var6 == 2) {
                var3.put(var5, var1.readUnsignedShort());
            } else if (var6 == 4) {
                var3.put(var5, var1.readInt());
            }
        }

    }
}
