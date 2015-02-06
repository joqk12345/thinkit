package org.thinkit.decoder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

import org.thinkit.data.util.Random;
import org.thinkit.decoder.ASRHeader;
import org.thinkit.decoder.AnalysisXML;
import org.thinkit.decoder.StreamTool;

public class Sound {

	long id = 0; // 语音编号
	int language = 1; // 语音语种：1为普通话
	private byte[] sounddata = null; // 语音数据
	private byte[] indexdata = null; // 索引数据
	private byte[] keyworddata = null; // 关键词数据
	private String keywordresult = ""; // 关键词判断结果
	private String recognitionresult = ""; //识别结果

	private String serverip = "127.0.0.1"; // decoder所在服务器ip地址
	private int serverport = 8888; // decoder所在服务器端口号

	public Sound() {}

	public Sound(Sound a) {
		id = a.getID();
		sounddata = a.getSoundData();
		keyworddata = a.getKeywordData();
		keywordresult = a.getKeywordResult();
		indexdata = a.getIndexData();
	}

	// 初始化Sound类，包含声音id，声音数据，关键词数据
	public Sound(long ID, byte[] sound, byte[] keyword) {
		id = ID;
		sounddata = sound;
		keyworddata = keyword;
	}

	// 获取声音的ID
	public long getID() {
		return id;
	}

	// 获取声音数据
	public byte[] getSoundData() {
		return sounddata;
	}
	
	// 获取索引结果
	public byte[] getIndexData() {
		return indexdata;
	}
	
	// 获取关键词数据
	public byte[] getKeywordData() {
		return keyworddata;
	}

	// 获取关键词结果
	public String getKeywordResult() {
		return keywordresult;
	}

	// 获取识别结果
	public String getRecognitionResult() {
		return recognitionresult;
	}

	// 设置ID
	public void setID(long ID) {
		id = ID;
	}

	// 设置语种
	public void setLanguage(int lan) {
		language = lan;
	}
	
	// 设置语音数据
	public void setSoundData(byte[] sound) {
		sounddata = sound;
	}

	// 设置关键词数据
	public void setKeywordData(byte[] keyword) {
		keyworddata = keyword;
	}

	// 设置索引数据
	public void setIndexData(byte[] index) {
		indexdata = index;
	}

	// 设置服务器ip地址端口号
	public void setServer(String ipaddress, int port) {
		serverip = ipaddress;
		serverport = port;
	}

	// 只设置服务器ip地址
	public void setServerIP(String ipaddress) {
		serverip = ipaddress;
	}

	public static byte[] readRecogntionData(String filepath) throws Exception {
		File file = new File(filepath);
		FileInputStream fin = new FileInputStream(file);
		byte[] filebt = StreamTool.readStream(fin);
		return filebt;
	}

	// 用语音数据做关键词返回结果和索引
	private void doKeywordReturnIndex() {
		try {

			int time = sounddata.length / 15000;
			Socket socket = new Socket(serverip, serverport);
			// 开启保持活动状态的套接字
			socket.setKeepAlive(true);
			// 设置读取超时时间
			if (time > 30) {
				socket.setSoTimeout(time * 1000);
			} else {
				socket.setSoTimeout(30 * 1000);
			}

			ASRHeader asrask = new ASRHeader();
			asrask.setCmd((short) 1);
			asrask.setRes_num(1);
			asrask.setReserved((short) 1);

			byte[] worddata = keyworddata;
			String word = new String(worddata, "GBK");
			System.out.println("关键词是：" + word);
			String keyword = word.replaceAll("[\\\n\\\r]+", "\n");
			byte[] keyworddata = keyword.getBytes("GBK");

			ASRHeader asrkeyword = new ASRHeader();
			asrkeyword.setCmd((short) 8);
			asrkeyword.setBody_len(keyworddata.length);
			short wordidx = 1;
			asrkeyword.setIdx(wordidx);

			DataOutputStream ops = new DataOutputStream(
					socket.getOutputStream());

			// ---------------------发送过程-------------------------------
			System.out.println("询问能否做关键字请求：" + asrask.toString());
			ops.write(asrask.toByteArray()); // 开始识别
			ops.flush();

			// 得到返回结果
			DataInputStream ips = new DataInputStream(socket.getInputStream());

			if (null != ops && null != ips) {
				System.out.println("准备取到返回结果：");

				int n = 44;
				int i = 0;
				byte bufferask[] = new byte[n];
				if ((ips.read(bufferask, 0, n) != -1)) {
					// System.out.print(new String(buffer));
					System.out.println("总次数为:" + (++i)	+ asrask.parserArray(bufferask).toString());
				}
				System.out.println("读取结果完毕");

				int reask = asrask.parserArray(bufferask).getCmd();
				if (reask == 0) {// 第一次应答体的返回值中 err_no字段的值为 0
					System.out.println("询问能否做关键字报文交互 解析结果返回值:" + reask			+ asrask.parserArray(bufferask).toString());

					// 开始发送关键词

					System.out.println("开始发送关键词" + asrkeyword.toString());
					ops.write(asrkeyword.toByteArray());
					ops.write(keyworddata);

					byte bufferkeyword[] = new byte[n];

					// 读取输入流
					if ((ips.read(bufferkeyword, 0, n) != -1)) {
						// System.out.print(new String(buffer));
						System.out.println("关键词返回信息头:" + asrkeyword.parserArray(bufferkeyword).toString());
					}
					int rekeyword = asrkeyword.parserArray(bufferkeyword).getCmd();
					if (rekeyword == 0) {
						System.out.println("关键词报文交互时候取得的信息："+ rekeyword + asrkeyword.parserArray(bufferkeyword).toString());

						// 测试用
						byte[] orisounddata = sounddata;
						byte[] soundtype = new byte[4];
						// soundtype = new Getformat().Getfrequency(soundfile);
						soundtype[0] = 1;
						soundtype[1] = 0;
						soundtype[2] = 0;
						soundtype[3] = 0;
						byte[] sound = new byte[orisounddata.length	+ soundtype.length];
						System.arraycopy(soundtype, 0, sound, 0,soundtype.length);
						System.arraycopy(orisounddata, 0, sound,soundtype.length, orisounddata.length);

						ASRHeader asrsound = new ASRHeader();
						asrsound.setCmd((short) 4);
						asrsound.setBody_len(sound.length);
						short soundidx = 1;
						asrsound.setIdx(soundidx);

						ASRHeader asrsoundfin = new ASRHeader();
						asrsoundfin.setCmd((short) 3);
						asrsoundfin.setVersion((short) 3);// version
						asrsoundfin.setIdx((short) 2);
						ASRHeader asrgetindex = new ASRHeader();

						// 开始发送语音数据cmd4
						System.out.println("开始发送语音数据" + asrsound.toString());
						ops.write(asrsound.toByteArray());
						// 发送语音
						ops.write(sound);

						// 结束发送
						System.out.println("结束发送语音数据" + asrsoundfin.toString());
						ops.write(asrsoundfin.toByteArray());
						// 获取结果

						// 获取结果头信息
						asrsound.clear();
						// System.out.println("准备拿到识别体的识别头信息："+asr.toString());
						// ips.read(resp); //读取返回信息头

						byte soundbuffer[] = new byte[n];
						// 读取输入流
						if ((ips.read(soundbuffer, 0, n) != -1)) {
							// System.out.print(new String(buffer));
							System.out.println("返回信息头:"
									+ asrsound.parserArray(soundbuffer)
											.toString());
						}
						int resound = asrsound.parserArray(soundbuffer)
								.getCmd();
						if (resound == 0) {
							System.out.println("发送语音交互时候取得的信息："
									+ resound
									+ asrsound.parserArray(soundbuffer)
											.toString());
							// 获取发送语音交互的报文体信息

							int bodylen = asrgetindex.parserArray(soundbuffer)
									.getBody_len();
							int len = 0;
							byte[] body = new byte[bodylen];
							while (len < bodylen) {
								len = len + ips.read(body, len, bodylen - len);
							}

							String result = new String(body, "GBK");
							keywordresult = result;
							System.out.println("获取返回体信息:" + result);
							// new AnalysisXML().xmlElements(result);

						} else {
							System.out.println("语音数据报文交互之后失败了："
									+ resound
									+ asrsound.parserArray(soundbuffer)
											.toString());
						}

						byte indexbuffer[] = new byte[n];
						// 读取输入流
						if ((ips.read(indexbuffer, 0, n) != -1)) {
							// System.out.print(new String(buffer));
							System.out.println("索引 返回信息头:"
									+ asrgetindex.parserArray(indexbuffer)
											.toString());
						}
						int reindex = asrgetindex.parserArray(indexbuffer)
								.getCmd();
						if (reindex == 0) {
							System.out.println("获取索引交互时候取得的信息："
									+ reindex
									+ asrgetindex.parserArray(indexbuffer)
											.toString());
							// 获取索引交互的报文体信息
							int indexlen = asrgetindex.parserArray(indexbuffer)
									.getBody_len();
							int len = 0;
							byte[] index = new byte[indexlen];
							while (len < indexlen) {
								len = len
										+ ips.read(index, len, indexlen - len);
							}

							System.out.println("获取索引:" + index.length + index);
							indexdata = index;

						} else {
							System.out.println("获取索引报文交互之后失败了："
									+ reindex
									+ asrgetindex.parserArray(indexbuffer)
											.toString());
						}

					} else {
						System.out.println("关键词列表报文交互之后失败了：" + rekeyword
								+ asrkeyword.toString());
					}

				} else {
					System.out.println("不能做关键词，报文交互 解析结果返回值:"
							+ asrask.toString());
				}
			}

			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 用语音数据做关键词返回结果不返回索引
	private void doKeywordWithoutIndex() {
		try {

			int time = sounddata.length / 15000;
			Socket socket = new Socket(serverip, serverport);
			// 开启保持活动状态的套接字
			socket.setKeepAlive(true);
			// 设置读取超时时间
			if (time > 30) {
				socket.setSoTimeout(time * 1000);
			} else {
				socket.setSoTimeout(30 * 1000);
			}

			ASRHeader asrask = new ASRHeader();
			asrask.setCmd((short) 1);
			asrask.setRes_num(1);
			asrask.setReserved((short) 1);

			byte[] worddata = keyworddata;
			String word = new String(worddata, "GBK");
			System.out.println("关键词是：" + word);
			String keyword = word.replaceAll("[\\\n\\\r]+", "\n");
			byte[] keyworddata = keyword.getBytes("GBK");

			ASRHeader asrkeyword = new ASRHeader();
			asrkeyword.setCmd((short) 8);
			asrkeyword.setBody_len(keyworddata.length);
			short wordidx = 1;
			asrkeyword.setIdx(wordidx);

			DataOutputStream ops = new DataOutputStream(socket.getOutputStream());

			// ---------------------发送过程-------------------------------
			System.out.println("询问能否做关键字请求：" + asrask.toString());
			ops.write(asrask.toByteArray()); // 开始识别
			ops.flush();

			// 得到返回结果
			DataInputStream ips = new DataInputStream(socket.getInputStream());

			if (null != ops && null != ips) {
				System.out.println("准备取到返回结果：");

				int n = 44;
				int i = 0;
				byte bufferask[] = new byte[n];
				if ((ips.read(bufferask, 0, n) != -1)) {
					// System.out.print(new String(buffer));
					System.out.println("总次数为:" + (++i)
							+ asrask.parserArray(bufferask).toString());
				}
				System.out.println("读取结果完毕");

				int reask = asrask.parserArray(bufferask).getCmd();
				if (reask == 0) {// 第一次应答体的返回值中 err_no字段的值为 0
					System.out.println("询问能否做关键字报文交互 解析结果返回值:" + reask
							+ asrask.parserArray(bufferask).toString());

					// 开始发送关键词

					System.out.println("开始发送关键词" + asrkeyword.toString());
					ops.write(asrkeyword.toByteArray());
					ops.write(keyworddata);

					byte bufferkeyword[] = new byte[n];

					// 读取输入流
					if ((ips.read(bufferkeyword, 0, n) != -1)) {
						// System.out.print(new String(buffer));
						System.out.println("关键词返回信息头:"
								+ asrkeyword.parserArray(bufferkeyword)
										.toString());
					}
					int rekeyword = asrkeyword.parserArray(bufferkeyword)
							.getCmd();
					if (rekeyword == 0) {
						System.out.println("关键词报文交互时候取得的信息："
								+ rekeyword
								+ asrkeyword.parserArray(bufferkeyword)
										.toString());

						// 测试用
						byte[] orisounddata = sounddata;
						byte[] soundtype = new byte[4];
						// soundtype = new Getformat().Getfrequency(soundfile);
						soundtype[0] = 1;
						soundtype[1] = 0;
						soundtype[2] = 0;
						soundtype[3] = 0;
						byte[] sound = new byte[orisounddata.length	+ soundtype.length];
						System.arraycopy(soundtype, 0, sound, 0,soundtype.length);
						System.arraycopy(orisounddata, 0, sound,soundtype.length, orisounddata.length);

						ASRHeader asrsound = new ASRHeader();
						asrsound.setCmd((short) 4);
						asrsound.setBody_len(sound.length);
						short soundidx = 1;
						asrsound.setIdx(soundidx);

						ASRHeader asrsoundfin = new ASRHeader();
						asrsoundfin.setCmd((short) 3);
						asrsoundfin.setVersion((short) 3);// version
						ASRHeader asrgetindex = new ASRHeader();

						// 开始发送语音数据cmd4
						System.out.println("开始发送语音数据" + asrsound.toString());
						ops.write(asrsound.toByteArray());
						// 发送语音
						ops.write(sound);

						// 结束发送
						System.out.println("结束发送语音数据" + asrsoundfin.toString());
						ops.write(asrsoundfin.toByteArray());
						// 获取结果

						// 获取结果头信息
						asrsound.clear();
						// System.out.println("准备拿到识别体的识别头信息："+asr.toString());
						// ips.read(resp); //读取返回信息头

						byte soundbuffer[] = new byte[n];
						// 读取输入流
						if ((ips.read(soundbuffer, 0, n) != -1)) {
							// System.out.print(new String(buffer));
							System.out.println("返回信息头:"
									+ asrsound.parserArray(soundbuffer)
											.toString());
						}
						int resound = asrsound.parserArray(soundbuffer)
								.getCmd();
						if (resound == 0) {
							System.out.println("发送语音交互时候取得的信息："	+ resound+ asrsound.parserArray(soundbuffer).toString());
							// 获取发送语音交互的报文体信息

							int bodylen = asrgetindex.parserArray(soundbuffer).getBody_len();
							int len = 0;
							byte[] body = new byte[bodylen];
							while (len < bodylen) {
								len = len + ips.read(body, len, bodylen - len);
							}

							String result = new String(body, "GBK");
							keywordresult = result;
							System.out.println("获取返回体信息:" + result);
							// new AnalysisXML().xmlElements(result);

						} else {
							System.out.println("语音数据报文交互之后失败了："
									+ resound
									+ asrsound.parserArray(soundbuffer)
											.toString());
						}

					} else {
						System.out.println("关键词列表报文交互之后失败了：" + rekeyword
								+ asrkeyword.toString());
					}

				} else {
					System.out.println("不能做关键词，报文交互 解析结果返回值:"
							+ asrask.toString());
				}
			}

			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//用语音数据做关键词返回索引不返回结果
	private void doKeywordonlyIndex() {
		try {

			int time = sounddata.length / 15000;
			Socket socket = new Socket(serverip, serverport);
			// 开启保持活动状态的套接字
			socket.setKeepAlive(true);
			// 设置读取超时时间
			if (time > 30) {
				socket.setSoTimeout(time * 1000);
			} else {
				socket.setSoTimeout(30 * 1000);
			}

			ASRHeader asrask = new ASRHeader();
			asrask.setCmd((short) 1);
			asrask.setRes_num(1);
			asrask.setReserved((short) 1);

			byte[] worddata = keyworddata;
			String word = new String(worddata, "GBK");
			System.out.println("关键词是：" + word);
			String keyword = word.replaceAll("[\\\n\\\r]+", "\n");
			byte[] keyworddata = keyword.getBytes("GBK");

			ASRHeader asrkeyword = new ASRHeader();
			asrkeyword.setCmd((short) 8);
			asrkeyword.setBody_len(keyworddata.length);
			short wordidx = 1;
			asrkeyword.setIdx(wordidx);

			DataOutputStream ops = new DataOutputStream(
					socket.getOutputStream());

			// ---------------------发送过程-------------------------------
			System.out.println("询问能否做关键字请求：" + asrask.toString());
			ops.write(asrask.toByteArray()); // 开始识别
			ops.flush();

			// 得到返回结果
			DataInputStream ips = new DataInputStream(socket.getInputStream());

			if (null != ops && null != ips) {
				System.out.println("准备取到返回结果：");

				int n = 44;
				int i = 0;
				byte bufferask[] = new byte[n];
				if ((ips.read(bufferask, 0, n) != -1)) {
					// System.out.print(new String(buffer));
					System.out.println("总次数为:" + (++i)
							+ asrask.parserArray(bufferask).toString());
				}
				System.out.println("读取结果完毕");

				int reask = asrask.parserArray(bufferask).getCmd();
				if (reask == 0) {// 第一次应答体的返回值中 err_no字段的值为 0
					System.out.println("询问能否做关键字报文交互 解析结果返回值:" + reask
							+ asrask.parserArray(bufferask).toString());

					// 开始发送关键词

					System.out.println("开始发送关键词" + asrkeyword.toString());
					ops.write(asrkeyword.toByteArray());
					ops.write(keyworddata);

					byte bufferkeyword[] = new byte[n];

					// 读取输入流
					if ((ips.read(bufferkeyword, 0, n) != -1)) {
						// System.out.print(new String(buffer));
						System.out.println("关键词返回信息头:"
								+ asrkeyword.parserArray(bufferkeyword)
										.toString());
					}
					int rekeyword = asrkeyword.parserArray(bufferkeyword)
							.getCmd();
					if (rekeyword == 0) {
						System.out.println("关键词报文交互时候取得的信息："
								+ rekeyword
								+ asrkeyword.parserArray(bufferkeyword)
										.toString());

						// 测试用
						byte[] orisounddata = sounddata;
						byte[] soundtype = new byte[4];
						// soundtype = new Getformat().Getfrequency(soundfile);
						soundtype[0] = 1;
						soundtype[1] = 0;
						soundtype[2] = 0;
						soundtype[3] = 0;
						byte[] sound = new byte[orisounddata.length
								+ soundtype.length];
						System.arraycopy(soundtype, 0, sound, 0,
								soundtype.length);
						System.arraycopy(orisounddata, 0, sound,
								soundtype.length, orisounddata.length);

						ASRHeader asrsound = new ASRHeader();
						asrsound.setCmd((short) 4);
						asrsound.setBody_len(sound.length);
						short soundidx = 1;
						asrsound.setIdx(soundidx);

						ASRHeader asrsoundfin = new ASRHeader();
						asrsoundfin.setCmd((short) 3);
						asrsoundfin.setVersion((short) 3);// version
						asrsoundfin.setIdx((short) 1);
						ASRHeader asrgetindex = new ASRHeader();

						// 开始发送语音数据cmd4
						System.out.println("开始发送语音数据" + asrsound.toString());
						ops.write(asrsound.toByteArray());
						// 发送语音
						ops.write(sound);

						// 结束发送
						System.out.println("结束发送语音数据" + asrsoundfin.toString());
						ops.write(asrsoundfin.toByteArray());
						// 获取结果

						// 获取结果头信息
						asrsound.clear();
						// System.out.println("准备拿到识别体的识别头信息："+asr.toString());
						// ips.read(resp); //读取返回信息头

						byte indexbuffer[] = new byte[n];
						// 读取输入流
						if ((ips.read(indexbuffer, 0, n) != -1)) {
							// System.out.print(new String(buffer));
							System.out.println("索引 返回信息头:"
									+ asrgetindex.parserArray(indexbuffer)
											.toString());
						}
						int reindex = asrgetindex.parserArray(indexbuffer)
								.getCmd();
						if (reindex == 0) {
							System.out.println("获取索引交互时候取得的信息："
									+ reindex
									+ asrgetindex.parserArray(indexbuffer)
											.toString());
							// 获取索引交互的报文体信息
							int indexlen = asrgetindex.parserArray(indexbuffer)
									.getBody_len();
							int len = 0;
							byte[] index = new byte[indexlen];
							while (len < indexlen) {
								len = len
										+ ips.read(index, len, indexlen - len);
							}

							System.out.println("获取索引:" + index.length + index);
							indexdata = index;

						} else {
							System.out.println("获取索引报文交互之后失败了："
									+ reindex
									+ asrgetindex.parserArray(indexbuffer)
											.toString());
						}

					} else {
						System.out.println("关键词列表报文交互之后失败了：" + rekeyword
								+ asrkeyword.toString());
					}

				} else {
					System.out.println("不能做关键词，报文交互 解析结果返回值:"
							+ asrask.toString());
				}
			}

			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//用索引数据做关键词返回结果
	public void doIndexKeyword() {
		try {

			Socket socket = new Socket(serverip, serverport);
			// 开启保持活动状态的套接字
			socket.setKeepAlive(true);
			// 设置读取超时时间
			socket.setSoTimeout(30 * 1000);

			ASRHeader asrask = new ASRHeader();
			asrask.setCmd((short) 14);

			asrask.setRes_num(1);
			asrask.setReserved((short) 1);

			byte[] worddata = keyworddata;
			String word = new String(worddata, "GBK");
			System.out.println("关键词是：" + word);
			String keyword = word.replaceAll("[\\\n\\\r]+", "\n");
			byte[] keyworddata = keyword.getBytes("GBK");

			ASRHeader asrkeyword = new ASRHeader();
			asrkeyword.setCmd((short) 8);
			asrkeyword.setBody_len(keyworddata.length);
			short wordidx = 1;
			asrkeyword.setIdx(wordidx);

			DataOutputStream ops = new DataOutputStream(
					socket.getOutputStream());

			// ---------------------发送过程-------------------------------
			System.out.println("询问能否做关键字请求：" + asrask.toString());
			ops.write(asrask.toByteArray()); // 开始识别
			ops.flush();

			// 得到返回结果
			DataInputStream ips = new DataInputStream(socket.getInputStream());

			if (null != ops && null != ips) {
				System.out.println("准备取到返回结果：");
				int n = 44;
				int i = 0;
				byte bufferask[] = new byte[n];
				if ((ips.read(bufferask, 0, n) != -1)) {
					System.out.println("总次数为:" + (++i)
							+ asrask.parserArray(bufferask).toString());
				}
				System.out.println("读取结果完毕");

				int reask = asrask.parserArray(bufferask).getCmd();
				if (reask == 0) {// 第一次应答体的返回值中 err_no字段的值为 0
					System.out.println("询问能否做关键字报文交互 解析结果返回值:" + reask
							+ asrask.parserArray(bufferask).toString());

					System.out.println("开始发送关键词" + asrkeyword.toString());
					ops.write(asrkeyword.toByteArray());
					ops.write(keyworddata);

					byte bufferkeyword[] = new byte[n];

					// 读取输入流
					if ((ips.read(bufferkeyword, 0, n) != -1)) {
						// System.out.print(new String(buffer));
						System.out.println("关键词返回信息头:"
								+ asrkeyword.parserArray(bufferkeyword)
										.toString());
					}
					int rekeyword = asrkeyword.parserArray(bufferkeyword)
							.getCmd();
					if (rekeyword == 0) {
						System.out.println("关键词报文交互时候取得的信息："
								+ rekeyword
								+ asrkeyword.parserArray(bufferkeyword)
										.toString());

						ASRHeader asrindex = new ASRHeader();
						asrindex.setCmd((short) 10);
						asrindex.setBody_len(indexdata.length);

						ASRHeader asrindexfin = new ASRHeader();
						asrindexfin.setCmd((short) 3);
						asrindexfin.setVersion((short) 3);// version

						// 开始发送索引数据cmd10
						System.out.println("开始发送索引数据" + asrindex.toString());
						ops.write(asrindex.toByteArray());
						// 发送索引
						ops.write(indexdata);

						// 结束发送
						System.out.println("发送结束");
						ops.write(asrindexfin.toByteArray());
						// 获取结果

						// System.out.println("准备拿到识别体的识别头信息："+asr.toString());
						// ips.read(resp); //读取返回信息头

						byte indexbuffer[] = new byte[n];
						// 读取输入流
						if ((ips.read(indexbuffer, 0, n) != -1)) {
							// System.out.print(new String(buffer));
							System.out.println("返回信息头:"
									+ asrindex.parserArray(indexbuffer)
											.toString());
						}
						int reindex = asrindex.parserArray(indexbuffer)
								.getCmd();
						if (reindex == 0) {
							System.out.println("发送索引交互时候取得的信息："
									+ reindex
									+ asrindex.parserArray(indexbuffer)
											.toString());
							// 获取发送语音交互的报文体信息

							int bodylen = asrindex.parserArray(indexbuffer)
									.getBody_len();
							int len = 0;
							byte[] body = new byte[bodylen];
							while (len < bodylen) {
								len = len + ips.read(body, len, bodylen - len);
							}

							String result = new String(body, "GBK");
							keywordresult = result;

						} else {
							System.out.println("索引报文交互之后失败了："
									+ reindex
									+ asrindex.parserArray(indexbuffer)
											.toString());
						}

					} else {
						System.out.println("关键词列表报文交互之后失败了：" + rekeyword
								+ asrkeyword.toString());
					}

				} else {
					System.out.println("不能做关键词，报文交互 解析结果返回值:"
							+ asrask.toString());
				}
			}

			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 用语音数据做识别返回结果
	public void doRecognize(){
		
		try {

			int time = sounddata.length / 15000;
			Socket socket = new Socket(serverip, serverport);
			// 开启保持活动状态的套接字
			socket.setKeepAlive(true);
			// 设置读取超时时间
			if (time > 30) {
				socket.setSoTimeout(time * 1000);
			} else {
				socket.setSoTimeout(30 * 1000);
			}
			ASRHeader asr = new ASRHeader();
			asr.setCmd((short) 1);

			
			byte[] redata = sounddata;
			
			//int 
			
			
			int log_id =Random.getRandomInt();
			ASRHeader asr1 = new ASRHeader();
			asr1.setCmd((short) 4);
			asr1.setBody_len(redata.length);
			asr1.setLog_id(log_id);
			
			
			ASRHeader asr2 = new ASRHeader();
			asr2.setCmd((short) 3);
			asr2.setVersion((short) 2);
			asr2.setLog_id(log_id);
			DataOutputStream ops = new DataOutputStream(socket.getOutputStream());

			// ---------------------发送过程-------------------------------
			System.out.println("第一次请求：" + asr.toString());
			ops.write(asr.toByteArray()); // 开始识别
			ops.flush();

			// 得到返回结果
			 DataInputStream ips = new DataInputStream(socket.getInputStream());

//			InputStream ips = socket.getInputStream();

			if (null != ops && null != ips) {
					System.out.println("准备取到返回结果：");
					// int i=0;
					// while(ips.read(res)!=-1){
					// System.out.println("读到的结果:" + ips.toString()+ "总计数:"+
					// (i++));
					// }

					int n = 44;
					int i = 0;
					byte buffer[] = new byte[n];
					// 读取输入流
					if ((ips.read(buffer, 0, n) != -1)) {
						// System.out.print(new String(buffer));
						System.out.println("总次数为:" + (++i)	+ asr.parserArray(buffer).toString());
					}

					System.out.println("读取结果完毕");
					int a = asr.parserArray(buffer).getCmd();
					if (a == 0) {// 第一次应答体的返回值中 err_no字段的值为 0
						System.out.println("第一次报文交互 解析结果返回值:" + a + asr.parserArray(buffer).toString());
						// 开始发送语音数据cmd4
						System.out.println("开始发送语音数据" + asr1.toString());
						ops.write(asr1.toByteArray());
						// 发送语音
						ops.write(redata);
						
						
						// 结束发送
						System.out.println("结束发送语音数据" + asr2.toString());
						ops.write(asr2.toByteArray());
						// 获取结果

						// 获取结果头信息
						asr.clear();
						// System.out.println("准备拿到识别体的识别头信息："+asr.toString());
						// ips.read(resp); //读取返回信息头
						byte buffer1[] = new byte[n];
						// 读取输入流
						if ((ips.read(buffer1, 0, n) != -1)) {
							// System.out.print(new String(buffer));
							System.out.println("返回信息头:" + asr.parserArray(buffer1).toString());
						}
						int b = asr.parserArray(buffer1).getCmd();
						if (0 == b) {
							System.out.println("第二次报文交互时候取得的信息：" + b + asr.parserArray(buffer1).toString());
							// 获取第二次交互的报文体信息
							int bodylen = asr.parserArray(buffer1).getBody_len();
							int len = 0;
							byte[] body1 = new byte[bodylen];
							while (len<bodylen)
							{
								len = len+ips.read(body1,len,bodylen-len);		
							}
							String result = new String (body1,"GBK");
							System.out.println("获取返回体信息:" +result);
							recognitionresult = result;
							
						}else if(-4 == b){
							System.out.println("无结果："  );
							recognitionresult="无结果";
						}else if(-3 == b){
							System.out.println("数据过长"  );
							recognitionresult="数据过长";
						}
						else {
							System.out.println("第二次报文交互之后失败了：" + b + asr.toString());
						}

						// 获取结果体信息
					} else {
						System.out.println("第一次报文交互 解析结果返回值:" + asr.toString());
					}
				}
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		//关键词的测试
		String wordfile = "F://工作//分布式语音平台//decoder通信协议//keyword.txt";
		String soundfile = "F://工作//统计工具//粤语片段标注//粤语片段标注//1//1//1_000.wav";
		String indexfile = "F://工作//分布式语音平台//decoder通信协议//index.txt";
		//固定音频的测试
		String soundFm = "C://Users//lenovo//Desktop//固定音频检索//15s//000001.01.wav";

		long id = 10003;
		Sound a = new Sound(id, Sound.readRecogntionData(soundfile),Sound.readRecogntionData(wordfile));
//		
//		Sound a = new Sound();
//		a.setID(id);
//		a.setIndexData(Sound.readRecogntionData(indexfile));
//		a.setKeywordData(Sound.readRecogntionData(wordfile));
		
		a.setServerIP("192.168.11.40");
		a.doRecognize();
//		System.out.println(a.getIndexData().length);
		new AnalysisXML().xmlElements(a.getRecognitionResult());
	}

}
