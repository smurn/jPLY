package org.smurn.jply;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class PlyWriter {

    public byte[] convertFloatToByteArray(float f) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(f).array();
    }

    public void writePly(File file, ArrayList<Element> elements) {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(new
                    FileOutputStream(file));

            out.writeBytes("ply\n");
            out.writeBytes("format binary_little_endian 1.0\n");
            out.writeBytes("element vertex " + elements.size() + "\n");
            out.writeBytes("property float x\n");
            out.writeBytes("property float y\n");
            out.writeBytes("property float z\n");
            out.writeBytes("end_header\n");

            for (ELement p : elements) { //only for vertices for now
                out.write(convertFloatToByteArray((float) p.getDouble("x")));
                out.write(convertFloatToByteArray((float) p.getDouble("y")));
                out.write(convertFloatToByteArray((float) p.getDouble("z")));

            }

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}