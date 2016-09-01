package be.tradecom.pony;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

/**
 * Created by jonathan on 30/08/2016.
 */
public enum GPSSocket {
    INSTANCE;

    private boolean mConnected = false;

    private Socket mSocket;

    public boolean connect(String ip, int port) {
        try {
            SocketAddress socketInformation = new InetSocketAddress(ip, port);
            mSocket = new Socket();

            mSocket.connect(socketInformation, 20000);
            mConnected = true;
            return true;
        } catch (Exception e) {
            mConnected = false;
            return false;
        }
    }

    public char[] read() throws SocketTimeoutException {
        char[] incomingBuffer = new char[1024];

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            in.read(incomingBuffer);
        } catch (SocketTimeoutException e) {
            throw new SocketTimeoutException();
        } catch (Exception e) {
            return null;
        }

        return incomingBuffer;
    }

    public boolean send(String line) {
        try {
            line += '\r';

            if (mConnected) {
                send(line.getBytes());
            }
        } catch (Exception e) {
            mConnected = false;
            return false;
        }

        return mConnected;
    }

    public boolean send(byte[] toSend) {
        try {
            if (mConnected) {
                mSocket.setSendBufferSize(toSend.length);
                DataOutputStream oStream = new DataOutputStream(mSocket.getOutputStream());
                oStream.flush();
                oStream.write(toSend, 0, toSend.length);
                oStream.flush();
            }
        } catch (Exception e) {
            mConnected = false;
            return false;
        }

        return mConnected;
    }
}
