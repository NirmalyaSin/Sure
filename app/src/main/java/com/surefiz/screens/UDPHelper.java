package com.surefiz.screens;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.net.SocketException;

import cn.onecoder.scalewifi.net.ReceiveDataThread;
import cn.onecoder.scalewifi.net.SendDataThread;
import cn.onecoder.scalewifi.net.impl.OnDataListener;
import cn.onecoder.scalewifi.net.socket.utils.NetUtils;
import cn.onecoder.scalewifi.util.HexUtil;

public class UDPHelper implements SendDataThread.SendDataListener, ReceiveDataThread.ReceiveDataListener {
    public static final long TIMEOUT_IN_MILLIS_MIN = 1000L;
    private Long timeoutInMillis = 1000L;
    private static final String TAG = UDPHelper.class.getSimpleName();
    private static final int BUFFER_LENGTH = 1024;
    private boolean debug = false;
    private boolean singleTaskMode = true;
    private long sendTaskId = 0L;
    private long receiveTaskId = 0L;
    private long retryPeriodInMillis = 100L;
    private long samplingPeriodInMillis = 100L;
    private SendDataThread sendDataThread;
    private ReceiveDataThread receiveDataThread;
    private int port;
    private DatagramSocket datagramSocket;
    private DatagramPacket sendDatagramPacket;
    private DatagramPacket receiveDatagramPacket;
    private InetAddress sendToInetAddress;
    private InetAddress receiveFromInetAddress;
    private SocketAddress receiveSocketAddress;
    private boolean reply = false;
    private boolean init = false;
    private OnDataListener onDataListener;

    public UDPHelper(int port) throws Exception {
        if (port < 0 && port > 65535) {
            throw new Exception("Param port:" + port + " is invalid.");
        } else {
            this.sendToInetAddress = InetAddress.getByName(NetUtils.getLocalBroadCast());
            this.port = port;
            this.sendDatagramPacket = new DatagramPacket(new byte[1024], 1024);
            this.sendDatagramPacket.setPort(port);
            this.receiveDatagramPacket = new DatagramPacket(new byte[1024], 1024);
            this.receiveDatagramPacket.setPort(port);
        }
    }

    public boolean isDebug() {
        return this.debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isSingleTaskMode() {
        return this.singleTaskMode;
    }

    public void setSingleTaskMode(boolean singleTaskMode) {
        this.singleTaskMode = singleTaskMode;
    }

    public void setOnDataListener(OnDataListener onDataListener) {
        this.onDataListener = onDataListener;
    }

    public void setTimeoutInMillis(Long timeoutInMillis) {
        if (timeoutInMillis != null && timeoutInMillis < 1000L) {
            timeoutInMillis = 1000L;
        }

        this.timeoutInMillis = timeoutInMillis;
    }

    public void setRetryPeriodInMillis(long retryPeriodInMillis) {
        if (retryPeriodInMillis < 100L) {
            retryPeriodInMillis = 100L;
        }

        this.retryPeriodInMillis = retryPeriodInMillis;
    }

    public void setSamplingPeriodInMillis(long samplingPeriodInMillis) {
        if (samplingPeriodInMillis < 100L) {
            samplingPeriodInMillis = 100L;
        }

        this.samplingPeriodInMillis = samplingPeriodInMillis;
    }

    public boolean init() {
        this.close();
        boolean ret = false;

        try {
            this.datagramSocket = new MulticastSocket(this.port);
//            this.datagramSocket.joinGroup(InetAddress.getByName("192.168.0.5"));
            ret = true;
        } catch (SocketException var3) {
            var3.printStackTrace();
            if (this.onDataListener != null) {
                this.onDataListener.onConnectError(var3);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.init = ret;
        if (this.onDataListener != null) {
            this.onDataListener.onConnectResult(ret);
        }

        if (ret) {
            this.receiveDataThread = new ReceiveDataThread(this);
            this.receiveDataThread.setDebug(this.debug);
            this.receiveDataThread.setSamplingPeriodInMillis(this.samplingPeriodInMillis);
            this.receiveDataThread.setTaskId(this.receiveTaskId);
            this.receiveDataThread.start();
            if (this.debug) {
                System.out.println(TAG + " init ret:" + ret + " receiveTaskId:" + this.receiveTaskId);
            }

            ++this.receiveTaskId;
        }

        return ret;
    }

    public boolean isInit() {
        return this.init;
    }

    public InetAddress getReceiveFromInetAddress() {
        return this.receiveFromInetAddress;
    }

    public SocketAddress getReceiveSocketAddress() {
        return this.receiveSocketAddress;
    }

    public void setSendToInetAddress(InetAddress sendToInetAddress) {
        this.sendToInetAddress = sendToInetAddress;
    }

    public boolean isReply() {
        return this.reply;
    }

    public void setReply(boolean reply) {
        this.reply = reply;
    }

    public boolean sendDataAsync(byte[] data) {
        boolean var10000;
        label48:
        {
            if (this.isInit() && data != null && data.length > 0) {
                label44:
                {
                    if (this.reply) {
                        if (this.receiveSocketAddress == null) {
                            break label44;
                        }
                    } else if (this.sendToInetAddress == null && this.receiveFromInetAddress == null) {
                        break label44;
                    }

                    if (this.datagramSocket != null) {
                        var10000 = true;
                        break label48;
                    }
                }
            }

            var10000 = false;
        }

        boolean ret = var10000;
        if (!ret) {
            return ret;
        } else {
            if (this.singleTaskMode && this.sendDataThread != null && this.sendDataThread.isRunning()) {
                this.sendDataThread.stopThread();
            }

            this.sendDataThread = new SendDataThread(data, this);
            this.sendDataThread.setDebug(this.debug);
            this.sendDataThread.setTimeoutInMillis(this.timeoutInMillis);
            this.sendDataThread.setRetryPeriodInMillis(this.retryPeriodInMillis);
            this.sendDataThread.setTaskId(this.sendTaskId);
            this.sendDataThread.start();
            if (this.debug) {
                System.out.println(TAG + " sendDataAsync end. sendTaskId:" + this.sendTaskId + " data:" + (data != null ? HexUtil.encodeHexStr(data) : data));
            }

            ++this.sendTaskId;
            return true;
        }
    }

    public Long getSendTaskId() {
        long taskId = this.sendTaskId - 1L;
        return taskId < 0L ? null : taskId;
    }

    public Long getReceiveTaskId() {
        long taskId = this.receiveTaskId - 1L;
        return taskId < 0L ? null : taskId;
    }

    public boolean isTargetSendTaskId(Long taskId) {
        boolean compareRet = taskId != null && taskId == this.sendTaskId - 1L;
        compareRet |= !this.singleTaskMode;
        if (this.debug) {
            System.out.println(TAG + " isTargetSendTaskId taskId:" + taskId + " sendTaskId:" + this.sendTaskId + " compareRet:" + compareRet);
        }

        return compareRet;
    }

    public boolean isTargetReceiveTaskId(Long taskId) {
        boolean compareRet = taskId != null && taskId == this.receiveTaskId - 1L;
        compareRet |= !this.singleTaskMode;
        if (this.debug) {
            System.out.println(TAG + " isTargetReceiveTaskId taskId:" + taskId + " receiveTaskId:" + this.receiveTaskId + " compareRet:" + compareRet);
        }

        return compareRet;
    }

    public void close() {
        if (this.singleTaskMode && this.sendDataThread != null && this.sendDataThread.isRunning()) {
            this.sendDataThread.stopThread();
            this.sendDataThread = null;
        }

        if (this.singleTaskMode && this.receiveDataThread != null && this.receiveDataThread.isRunning()) {
            this.receiveDataThread.stopThread();
            this.receiveDataThread = null;
        }

        if (this.datagramSocket != null) {
            this.datagramSocket.close();
            this.datagramSocket = null;
        }

        if (this.debug) {
            System.out.println(TAG + " closed");
        }

        this.init = false;
    }

    public boolean onDoSendDataTask(Long taskId, byte[] data) throws Exception {
        if (this.debug) {
            System.out.println(TAG + " onDoSendDataTask start. taskId:" + taskId + " data:" + (data != null ? HexUtil.encodeHexStr(data) : data) + " dataStr:" + (data != null ? new String(data) : null));
        }

        boolean var10000;
        label61:
        {
            label60:
            {
                if (this.isInit() && this.isTargetSendTaskId(taskId) && data != null && data.length > 0 && this.datagramSocket != null) {
                    if (this.reply) {
                        if (this.receiveSocketAddress != null) {
                            break label60;
                        }
                    } else if (this.sendToInetAddress != null || this.receiveFromInetAddress != null) {
                        break label60;
                    }
                }

                var10000 = false;
                break label61;
            }

            var10000 = true;
        }

        boolean ret = var10000;
        if (!ret) {
            return ret;
        } else {
            this.sendDatagramPacket.setData(data);
            if (this.reply) {
                this.sendDatagramPacket.setSocketAddress(this.receiveSocketAddress);
            } else {
                this.sendDatagramPacket.setAddress(this.sendToInetAddress != null ? this.sendToInetAddress : this.receiveFromInetAddress);
            }

            if (this.debug) {
                System.out.println(TAG + " onDoSendDataTask will send to " + this.sendDatagramPacket.getSocketAddress());
            }

            this.datagramSocket.send(this.sendDatagramPacket);
            return ret;
        }
    }

    public void onSendDataTimeout(Long taskId, byte[] data) {
        if (this.debug) {
            System.out.println(TAG + " onSendDataTimeout taskId:" + taskId);
        }

        if (this.isInit() && this.isTargetSendTaskId(taskId)) {
            if (this.onDataListener != null) {
                this.onDataListener.onTimeout(true, taskId, data);
            }

        }
    }

    public void onSendDataError(Long taskId, Exception e) {
        if (this.debug) {
            System.out.println(TAG + " onSendDataError taskId:" + taskId + " exception:" + e);
        }

        if (this.isInit() && this.isTargetSendTaskId(taskId)) {
            if (this.onDataListener != null) {
                this.onDataListener.onError(true, taskId, e);
            }

        }
    }

    public void onSendDataResult(Long taskId, boolean success) {
        if (this.debug) {
            System.out.println(TAG + " onSendDataResult taskId:" + taskId + " success:" + success);
        }

        if (this.isInit() && this.isTargetSendTaskId(taskId)) {
            if (this.onDataListener != null) {
                this.onDataListener.onResult(true, taskId, success);
            }
        }
    }

    public byte[] onDoReceiveDataTask(Long taskId) throws Exception {
        if (this.debug) {
            System.out.println(TAG + " onDoReceiveDataTask taskId:" + taskId + " isInit:" + this.isInit());
        }

        if (this.isInit() && this.isTargetReceiveTaskId(taskId)) {
            byte[] data = null;
            if (this.debug) {
                System.out.println(TAG + " onDoReceiveDataTask taskId:" + taskId + " to receive data.");
            }

            this.datagramSocket.receive(this.receiveDatagramPacket);
            int dataLength = this.receiveDatagramPacket.getLength();
            if (this.debug) {
                System.out.println(TAG + " onDoReceiveDataTask taskId:" + taskId + " dataLength:" + dataLength);
            }

            if (dataLength > 0) {
                InetAddress inetAddress = this.receiveDatagramPacket.getAddress();
                this.receiveSocketAddress = this.receiveDatagramPacket.getSocketAddress();
                this.receiveFromInetAddress = inetAddress;
                data = this.receiveDatagramPacket.getData();
                if (data != null) {
                    byte[] targetData = new byte[dataLength];
                    System.arraycopy(data, 0, targetData, 0, dataLength);
                    String dataStr = new String(targetData);
                    if (this.debug) {
                        System.out.println(TAG + " onDoReceiveDataTask received data:\n" + (targetData != null ? HexUtil.encodeHexStr(targetData) : targetData) + "\n dataStr:" + dataStr + "\n dataLength:" + dataLength + " inetAddress:" + inetAddress + " socketAddress:" + this.receiveSocketAddress);
                    }

                    if (this.onDataListener != null) {
                        this.onDataListener.onReceiveData(taskId, targetData);
                    }
                }
            }

            return data;
        } else {
            return null;
        }
    }

    public void onReceiveDataTimeout(Long taskId) {
        if (this.debug) {
            System.out.println(TAG + " onReceiveDataTimeout taskId:" + taskId);
        }

        if (this.isInit() && this.isTargetReceiveTaskId(taskId)) {
            if (this.onDataListener != null) {
                this.onDataListener.onTimeout(false, taskId, (byte[]) null);
            }

        }
    }

    public void onReceiveDataError(Long taskId, Exception e) {
        if (this.debug) {
            System.out.println(TAG + " onReceiveDataError taskId:" + taskId + " exception:" + e);
        }

        if (this.isInit() && this.isTargetReceiveTaskId(taskId)) {
            if (this.onDataListener != null) {
                this.onDataListener.onError(false, taskId, e);
            }

        }
    }

    public void onReceiveDataResult(Long taskId, boolean success) {
        if (this.debug) {
            System.out.println(TAG + " onReceiveDataResult taskId:" + taskId + " success:" + success);
        }

        if (this.isInit() && this.isTargetReceiveTaskId(taskId)) {
            if (this.onDataListener != null) {
                this.onDataListener.onResult(false, taskId, success);
            }

        }
    }

}
