package org.thinkit.decoder;

import com.google.protobuf.ByteString;
import org.thinkit.decoder.protocal.protobuf.FixAudioSearchProtocal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by thinkit on 2015/3/5.
 */
public class SpeechAnalysisTool {

    long id = 0; // 语音编号
    int language = 1; // 语音语种：1为普通话
    private byte[] sounddata = null; // 语音数据
    private byte[] indexdata = null; // 索引数据
    private byte[] keyworddata = null; // 关键词数据
    private String keywordresult = ""; // 关键词判断结果
    private String recognitionresult = ""; //识别结果
    private int recognitionIndexType=3;
    private int recognitionAudioType=1;

    private int resultHeaderLength =15;//返回结果头大小

    private String decoderServerIp = "127.0.0.1"; // decoder所在服务器ip地址
    private int decoderServerPort = 40010; // decoder所在服务器端口号

    public SpeechAnalysisTool() {
    }

    //做说话人识别for 第一次识别
    public void doSpeakerSearchForAudioByetes(){
        //发送语音头
        FixAudioSearchProtocal.AudioHead.Builder builder1 = FixAudioSearchProtocal.AudioHead.newBuilder();
        builder1.setIdx(0001);
        Timestamp d = new Timestamp(System.currentTimeMillis());
        builder1.setTimeStamp(d.getTime());
        builder1.setVersion(1);
        builder1.setDatacode(recognitionAudioType);
        //获取数据长度的大小
        builder1.setDataLen(getSounddata().length);

        FixAudioSearchProtocal.AudioHead ATSHeader = builder1.build();
        byte[] EncodingATSHeader = ATSHeader.toByteArray();
        //开始进行说话人识别
        Socket socket = null;
        DataOutputStream ops = null;
        DataInputStream ips = null;
        try {
            socket = new Socket(decoderServerIp, decoderServerPort);

            // 开启保持活动状态的套接字
            socket.setKeepAlive(true);
            ops = new DataOutputStream(socket.getOutputStream());

            //1.0开始发送识别头信息
            ops.write(EncodingATSHeader); // 开始识别
            ops.flush();

            System.out.println("开始发送语音数据");
            ops.write(getSounddata());
            System.out.println("结束发送语音数据");


            // 得到返回结果
            ips = new DataInputStream(socket.getInputStream());

            FixAudioSearchProtocal.ResultHead testResultHead = null;
            FixAudioSearchProtocal.TIT_Result titResult;

            if (null != ops && null != ips) {

                byte buffer[] = new byte[resultHeaderLength];
                // 读取输入流
                if ((ips.read(buffer, 0, resultHeaderLength) != -1)) {
                    // System.out.print(new String(buffer));
//						System.out.println("总次数为:" + (++i)	+ asr.parserArray(buffer).toString());
                    System.out.println(testResultHead = FixAudioSearchProtocal.ResultHead.parseFrom(buffer));
                }
                //若果识别成功 添加结果类型
                if (1 == testResultHead.getResultCode()) {
                    int resultLen = testResultHead.getResultLen();
                    byte buffer1[] = new byte[resultLen];
                    //读取结果信息
                    if ((ips.read(buffer1, 0, resultLen) != -1)) {
                        //打印结果信息
//                        System.out.println(testResult = FixAudioSearchProtocal.ATS_Result.parseFrom(buffer1));
                        System.out.println(titResult = FixAudioSearchProtocal.TIT_Result.parseFrom(buffer1));
                        //将结果信息 格式化输出
                        recognitionresult = parseTITResult(FixAudioSearchProtocal.TIT_Result.parseFrom(buffer1));
                        indexdata=FixAudioSearchProtocal.TIT_Result.parseFrom(buffer1).getIndex().toByteArray();
                    }
                }
                /**
                 * 错误信息处理
                 * */
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭连接
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ops != null) {
                try {
                    ops.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ips != null) {
                try {
                    ips.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //做说话人识别for 历史检索识别
    public void doSpeakerSearchForAudioIndexByetes(){
//发送语音头
        FixAudioSearchProtocal.AudioHead.Builder builder1 = FixAudioSearchProtocal.AudioHead.newBuilder();
        builder1.setIdx(0001);
        Timestamp d = new Timestamp(System.currentTimeMillis());
        builder1.setTimeStamp(d.getTime());
        builder1.setVersion(1);
        builder1.setDatacode(recognitionIndexType);
        //获取数据长度的大小
        builder1.setDataLen(getSounddata().length);

        FixAudioSearchProtocal.AudioHead ATSHeader = builder1.build();
        byte[] EncodingATSHeader = ATSHeader.toByteArray();
        //开始进行说话人识别
        Socket socket = null;
        DataOutputStream ops = null;
        DataInputStream ips = null;
        try {
            socket = new Socket(decoderServerIp, decoderServerPort);

            // 开启保持活动状态的套接字
            socket.setKeepAlive(true);
            ops = new DataOutputStream(socket.getOutputStream());

            //1.0开始发送识别头信息
            ops.write(EncodingATSHeader); // 开始识别
            ops.flush();

            System.out.println("开始发送语音数据");
            ops.write(getIndexdata());
            System.out.println("结束发送语音数据");


            // 得到返回结果
            ips = new DataInputStream(socket.getInputStream());

            FixAudioSearchProtocal.ResultHead testResultHead = null;
            FixAudioSearchProtocal.TIT_Result titResult;

            if (null != ops && null != ips) {

                byte buffer[] = new byte[resultHeaderLength];
                // 读取输入流
                if ((ips.read(buffer, 0, resultHeaderLength) != -1)) {
                    // System.out.print(new String(buffer));
//						System.out.println("总次数为:" + (++i)	+ asr.parserArray(buffer).toString());
                    System.out.println(testResultHead = FixAudioSearchProtocal.ResultHead.parseFrom(buffer));
                }
                //若果识别成功 添加结果类型
                if (1 == testResultHead.getResultCode()) {
                    int resultLen = testResultHead.getResultLen();
                    byte buffer1[] = new byte[resultLen];
                    //读取结果信息
                    if ((ips.read(buffer1, 0, resultLen) != -1)) {
                        //打印结果信息
//                        System.out.println(testResult = FixAudioSearchProtocal.ATS_Result.parseFrom(buffer1));
                        System.out.println(titResult = FixAudioSearchProtocal.TIT_Result.parseFrom(buffer1));
                        //将结果信息 格式化输出
                        recognitionresult = parseTITResult(FixAudioSearchProtocal.TIT_Result.parseFrom(buffer1));
                    }
                }else if(0 == testResultHead.getResultCode()){
                    recognitionresult="no data";
                    System.out.println(recognitionresult);
                }else if(-1 == testResultHead.getResultCode()){
                    recognitionresult="network error";
                    System.out.println(recognitionresult);
                }else if(-3 == testResultHead.getResultCode()){
                    recognitionresult="engineAnalysis error";
                    System.out.println(recognitionresult);
                }
                /**
                 * 错误信息处理
                 * */
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭连接
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ops != null) {
                try {
                    ops.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ips != null) {
                try {
                    ips.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 做固定音频检索的服务
    public void doFixAudioSearch() {
        //发送语音头
        FixAudioSearchProtocal.AudioHead.Builder builder1 = FixAudioSearchProtocal.AudioHead.newBuilder();
        builder1.setIdx(0001);
        Timestamp d = new Timestamp(System.currentTimeMillis());
        builder1.setTimeStamp(d.getTime());
        builder1.setVersion(1);
        builder1.setDatacode(1);
        //获取数据长度的大小
        builder1.setDataLen(getSounddata().length);

        FixAudioSearchProtocal.AudioHead ATSHeader = builder1.build();
        byte[] EncodingATSHeader = ATSHeader.toByteArray();
        //开始进行识别
        Socket socket = null;
        DataOutputStream ops = null;
        DataInputStream ips = null;
        try {
            socket = new Socket(decoderServerIp, decoderServerPort);

            // 开启保持活动状态的套接字
            socket.setKeepAlive(true);
            ops = new DataOutputStream(socket.getOutputStream());

            //1.0开始发送识别头信息
            ops.write(EncodingATSHeader); // 开始识别
            ops.flush();

            System.out.println("开始发送语音数据");
            ops.write(getSounddata());
            System.out.println("结束发送语音数据");


            // 得到返回结果
            ips = new DataInputStream(socket.getInputStream());

            FixAudioSearchProtocal.ResultHead testResultHead = null;
            FixAudioSearchProtocal.ATS_Result testResult;

            if (null != ops && null != ips) {

                byte buffer[] = new byte[resultHeaderLength];
                // 读取输入流
                if ((ips.read(buffer, 0, resultHeaderLength) != -1)) {
                    // System.out.print(new String(buffer));
//						System.out.println("总次数为:" + (++i)	+ asr.parserArray(buffer).toString());
                    System.out.println(testResultHead = FixAudioSearchProtocal.ResultHead.parseFrom(buffer));
                }
                //若果识别成功 添加结果类型
                if (1 == testResultHead.getResultCode()) {
                    int resultLen = testResultHead.getResultLen();
                    byte buffer1[] = new byte[resultLen];
                    //读取结果信息
                    if ((ips.read(buffer1, 0, resultLen) != -1)) {
                        //打印结果信息
                        System.out.println(testResult = FixAudioSearchProtocal.ATS_Result.parseFrom(buffer1));
                        //将结果信息 格式化输出
                        recognitionresult = parseATSResult(FixAudioSearchProtocal.ATS_Result.parseFrom(buffer1));
                    }
                }
                /**
                 * 错误信息处理
                 * */
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭连接
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ops != null) {
                try {
                    ops.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ips != null) {
                try {
                    ips.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String parseTITResult(FixAudioSearchProtocal.TIT_Result tit_result){
        String titResult = null;
        titResult =  tit_result.getSpeaker().toString();
        return titResult;
    }

    private String parseATSResult(FixAudioSearchProtocal.ATS_Result ats_result1) {
        String ats_result = null;
        int astResultNum = ats_result1.getResultNum();
        List<ByteString> atsArr = ats_result1.getResultListList();
        //默认选择第一个
        if (astResultNum > 0 && atsArr.size() > 0) {
            ats_result = atsArr.get(0).toString();
        }
        if (null != ats_result) {
            return ats_result;
        }
        ats_result = "";
        return ats_result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public byte[] getSounddata() {
        return sounddata;
    }

    public void setSounddata(byte[] sounddata) {
        this.sounddata = sounddata;
    }

    public byte[] getIndexdata() {
        return indexdata;
    }

    public void setIndexdata(byte[] indexdata) {
        this.indexdata = indexdata;
    }

    public byte[] getKeyworddata() {
        return keyworddata;
    }

    public void setKeyworddata(byte[] keyworddata) {
        this.keyworddata = keyworddata;
    }

    public String getKeywordresult() {
        return keywordresult;
    }

    public void setKeywordresult(String keywordresult) {
        this.keywordresult = keywordresult;
    }

    public String getRecognitionresult() {
        return recognitionresult;
    }

    public void setRecognitionresult(String recognitionresult) {
        this.recognitionresult = recognitionresult;
    }

    public String getDecoderServerIp() {
        return decoderServerIp;
    }

    public void setDecoderServerIp(String decoderServerIp) {
        this.decoderServerIp = decoderServerIp;
    }

    public int getDecoderServerPort() {
        return decoderServerPort;
    }

    public void setDecoderServerPort(int decoderServerPort) {
        this.decoderServerPort = decoderServerPort;
    }
}
