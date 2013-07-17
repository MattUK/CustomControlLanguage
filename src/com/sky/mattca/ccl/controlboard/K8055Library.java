package com.sky.mattca.ccl.controlboard;

import com.sun.jna.Native;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * User: 06mcarter
 * Date: 13/11/12
 * Time: 13:25
 */
public class K8055Library {

    private static LibraryInterface k8055;

    public static void loadLibraryInterface() {
        try {
            k8055 = (LibraryInterface) Native.loadLibrary("K8055D", LibraryInterface.class);
            System.out.println("Loaded K8055 library.");
        } catch (Exception e) {
            System.out.println("Error loading K8055 Interface Library.");
            e.printStackTrace();
        }
    }

    public static LibraryInterface getLibraryInterface() {
        return k8055;
    }

    public static boolean enableBoard() {
        k8055.CloseDevice();
        if (k8055.OpenDevice(0) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public static void disableBoard() {
        k8055.ClearAllDigital();
        k8055.ClearAllAnalog();
        k8055.CloseDevice();
    }

    // Specialised functions
    public static void enableDigitalOutputs(int... ids) {
        k8055.WriteAllDigital(setBits(ids));
    }

    public static void clearDigitalOutput(int... ids) {
        for (int i = 0; i < ids.length; i++) {
            k8055.ClearDigitalChannel(ids[i] + 1);
        }
    }

    public static void flashAndPause(int light, int pauseTime) {
        enableDigitalOutputs(light);

        try {
            Thread.sleep(pauseTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        clearDigitalOutput(light);
    }

    public static byte setBits(int... bits) {
        BitSet set = new BitSet();

        for (int i = 0; i < bits.length; i++) {
            set.set(bits[i], true);
        }

        return set.toByteArray()[0];
    }

    public static Integer[] getBits(byte data) {
        BitSet set = new BitSet(data);

        List<Integer> outList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            if (set.get(i) == true) {
                outList.add(i);
            }
        }

        return (Integer[]) outList.toArray();
    }

}
