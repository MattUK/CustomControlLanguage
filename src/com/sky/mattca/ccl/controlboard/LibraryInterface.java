package com.sky.mattca.ccl.controlboard;

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Library to interface with the Interface Board Library, using JNA.
 * <p/>
 * User: 06mcarter
 * Date: 13/11/12
 * Time: 13:14
 */
public interface LibraryInterface extends StdCallLibrary {

    // General Functions
    public int OpenDevice(int CardAddress);

    public void CloseDevice();

    public int SearchDevices();

    public int SetCurrentDevice(int lngCardAddress);

    public int Version();

    // Analog to Digital converter functions
    public int ReadAnalogChannel(int Channel);

    public void ReadAllAnalog(IntByReference Data1, IntByReference Data2);

    // Digital to Analog conversion functions
    public void OutputAnalogChannel(int Channel, int Data);

    public void OutputAllAnalog(int Data1, int Data2);

    public void ClearAnalogChannel(int Channel);

    public void ClearAllAnalog();

    public void SetAnalogChannel(int Channel);

    public void SetAllAnalog();

    // Digital Output Functions
    public void WriteAllDigital(int Data);

    public void ClearDigitalChannel(int Channel);

    public void ClearAllDigital();

    public void SetDigitalChannel(int Channel);

    public void SetAllDigital();

    // Digital Input functions
    public boolean ReadDigitalChannel(int Channel);

    public int ReadAllDigital();

    // Counter functions
    public void ResetCounter(int CounterNr);

    public int ReadCounter(int CounterNr);

    public void SetCounterDebounceTime(int CounterNr, int DebounceTime);

}
