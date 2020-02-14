

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import util.Log;



public class ClientThreadUTF8 extends Thread{

	public DataInputStream is = null;
	public DataOutputStream os = null;
	//public ClientSocket serverSocket = null;
	public Socket socket = null;

	public ArrayList<String> msgStack = new ArrayList<String>();

	public String ipAdress = "localhost";
	public int portNum = 7000;
	public boolean connect = false;

	private final Lock sendLock = new ReentrantLock(true);
	//Tools T = new Tools();




	public ClientThreadUTF8() {


	}



	public ClientThreadUTF8(String _ipAdress, int _portNum) {

		try {

			new Log("try to connect with " + _ipAdress + " " + _portNum);
			socket = new Socket();
			InetSocketAddress endpoint = new InetSocketAddress(_ipAdress, _portNum);
			socket.connect(endpoint, 1000);

			os = new DataOutputStream(socket.getOutputStream());
			is = new DataInputStream(socket.getInputStream());

			new Log("connected with " + _ipAdress + " " + _portNum);
			connect = true;

			ipAdress = _ipAdress;
			portNum = _portNum;


		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			//e.printStackTrace();
			//CRobotUtil.Log(TAG, e.toString());
		}



	}


	public void run() {


		byte[] pre_b = new byte[200];
		int pre_len = 0;

		//new SystemOut("recieve: 1");

		while(connect) {

			//new SystemOut("recieve: 2");

			try {

				//new SystemOut("recieve: 3");

				byte[] b = new byte[200];

				int len = is.read(b);
				if(len==-1) {
					disconnect();
					break;
				}

				//new SystemOut("recieve: 4");

				String msg = new String(copyNewByte(b,len), "UTF-8");
				//new SystemOut("recieve: " + msg);

				String msg2 = new String(combineByte(copyNewByte(pre_b,pre_len),copyNewByte(b,len)), "UTF-8");
				//System.out.println("combine recieve: " + msg2);

				String msgs[] = msg2.split("\n");
				int enqCount = msgs.length;

				if(!msg2.endsWith("\n")) {

					enqCount--;

					int read_len = 0;
					for(int i=0; i<enqCount; i++)
						read_len += msgs[i].getBytes().length;
					read_len += "\n".getBytes().length*enqCount;

					//System.out.println("read_len: " + read_len + " " + len+pre_len);

					byte[] remainByte = copyNewByte(b,read_len-pre_len,len);
					copyByte(pre_b,remainByte);
					pre_len = pre_len+len-read_len;

					String temp = new String(pre_b, "UTF-8");
					//System.out.println("remain: " + temp);

				}else
					pre_len = 0;


				for(int i=0; i<enqCount; i++) {
					manageMsgQueue("ENQ",msgs[i]);
					//System.out.println("enqueue: " + msgs[i]);
				}


				//sendMsg("recieve: " + msg);


			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				//e.printStackTrace();
				ifError(e);
				break;
			}


		}

		//CRobotUtil.Log(TAG, "ClientThread End");

	}


	public void sendMsg(String msg) {

		sendLock.lock();

		if(connect) {
			try {

				//os.writeUTF(msg);
				//CRobotUtil.Log(TAG, "send: " + msg);


				msg += "\n";
				byte[] b = msg.getBytes("UTF-8");
				os.write(b);



			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				//e.printStackTrace();
				ifError(e);
			}
		}else {

			//CRobotUtil.Log(TAG, "no connection error");
		}

		sendLock.unlock();
	}


	public String getMsg() {

		return manageMsgQueue("DEQ", null);

	}

	public String returnMsg(String msg) {

		return manageMsgQueue("RETURN", msg);
	}



	public synchronized String manageMsgQueue(String command, String addMsg) {

		switch(command) {

			case "DEQ":
				if(msgStack.size()!=0) {

					String getMsg = msgStack.get(0);
					if(getMsg != null) {
						msgStack.remove(0);
						return getMsg;
					}else
						return null;

				}else
					return null;

			case "ENQ":
				msgStack.add(addMsg);

				if(msgStack.size()>10) {

					//CRobotUtil.Log(TAG, "manageMsgQueue too much stack(>10): remove msg: " + msgStack.get(0));
					msgStack.remove(0);

				}
				return null;

			case "RETURN":
				msgStack.add(0, addMsg);

				return null;

			default:
				//CRobotUtil.Log(TAG, "manageMsgQueue command error: " + command);
				return null;

		}


	}


	public void ifError(IOException e) {

		//CRobotUtil.Log(TAG, e.toString());
		disconnect();

	}

	public void disconnect() {

		if(is!=null && os!=null && socket!=null)
		try {

			is.close();
			os.close();
			socket.close();

			connect = false;

			//CRobotUtil.Log(TAG, "Client Socket Close");

		} catch (IOException e ) {
			// TODO 自動生成された catch ブロック
			//e.printStackTrace();
			//CRobotUtil.Log(TAG, e.toString());
		}

	}





	public byte[] combineByte(byte[] a, byte[] b) {

		byte[] combinedByte = new byte[a.length + b.length];

		for(int i=0; i<a.length; i++)
			combinedByte[i] = a[i];

		for(int i=a.length; i<a.length+b.length; i++)
			combinedByte[i] = b[i-a.length];

		return combinedByte;

	}


	public byte[] copyNewByte(byte[] a, int len){

		byte[] newByte = new byte[len];

		for(int i=0; i<len; i++)
			newByte[i] = a[i];


		return newByte;
	}

	public byte[] copyNewByte(byte[] a, int off, int len){

		byte[] newByte = new byte[len-off];

		for(int i=off; i<len; i++)
			newByte[i-off] = a[i];

		return newByte;
	}


	public boolean copyByte(byte[] a, byte[] b) {

		if(a.length<b.length) {
			return false;
		}

		for(int i=0; i<b.length; i++)
			a[i] = b[i];

		return true;
	}

}
