

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Util.Log;
import Util.Timer;


public class MultiServerThread extends Thread{

	public ServerSocket serverSocket = null;
	public Socket socket = null;

	public ArrayList<String> gotMsgs = new ArrayList<String>();
	public ArrayList<String> sendMsgs = new ArrayList<String>();
	public ArrayList<Object> serverThreads = new ArrayList<Object>();

	public int portNum = 11000;

	private final Lock gotMsgLock = new ReentrantLock(true);
	static Timer threadCleaner = new Timer();

	public static void main(String arg[]) {

		MultiServerThread MST = new MultiServerThread(11000);
		MST.start();

		threadCleaner.start();

		while(true) {

			if(threadCleaner.getTime()>5)
			for(int i=0; i<MST.serverThreads.size(); i++) {

				coServerThread cST = (coServerThread) MST.serverThreads.get(i);

				if(!cST.connect) {
					MST.serverThreads.remove(i);
					//System.out.println("remove! "+ cST.name + " (" + cST.coSocket.getInetAddress() + "," + cST.getId() + ")");
				}


			}


			String msg = MST.getMsg();

			if(msg!=null) {

				//System.out.println(MST.serverThreads.size() + " found: " + msg);

				String msgs[] = msg.split(";");

				if(msgs.length>=2) {

					boolean findReceiver = false;

					for(int i=0; i<MST.serverThreads.size(); i++) {

						coServerThread cST = (coServerThread) MST.serverThreads.get(i);

						if(cST.name!=null && cST.name.equals(msgs[0])) {

							cST.sendMsg(msg.replaceFirst(msgs[0]+";", ""));
							//System.out.println("send msg: " + msgs[1] + " to " + cST.name + " (" + cST.coSocket.getInetAddress() + "," + cST.getId() + ")");
							new Log("send msg: [" + msg.replaceFirst(msgs[0]+";", "") + "] to '" + cST.name + "' (" + cST.coSocket.getInetAddress() + ", ID:" + cST.getId() + ")");

							findReceiver = true;
						}
					}

					if(!findReceiver) {
						new Log("Could not find name: '" + msgs[0] + "'");
					}

				}else {

					//System.out.println("msg error: " + msg);
					new Log("unknown msg: " + msg);
				}


			}


		}


	}


	public MultiServerThread(int _portNum) {

		try {
			InetAddress addr = InetAddress.getLocalHost();

			Enumeration testMIP = NetworkInterface.getNetworkInterfaces();

			if (null != testMIP) {
				System.out.println("ServerThread Start! Here is ");

				System.out.println("Port Number: " + _portNum);

				while (testMIP.hasMoreElements()) {
					NetworkInterface testNI = (NetworkInterface) testMIP.nextElement();
					//System.out.println("network interface: " + testNI.getDisplayName());

					Enumeration testInA = testNI.getInetAddresses();
					while (testInA.hasMoreElements()) {
						InetAddress testIP = (InetAddress)testInA.nextElement();
						if(testIP.getHostAddress().contains("."))
						System.out.println("[" + testNI.getDisplayName() + "] getHostAddress: " + testIP.getHostAddress());
					}

				}

			}

			portNum = _portNum;


		} catch (UnknownHostException | SocketException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}


	public void run() {

		while(true) {


			// サーバーの設定
			try {

				if(serverSocket==null)
					serverSocket = new ServerSocket(portNum);
				socket = serverSocket.accept();

				threadCleaner.resetTime();

				coServerThread cST = new coServerThread(socket);
				cST.start();

				serverThreads.add(cST);

				//System.out.println("MultiServerThread Connect with " + socket.getInetAddress() + ", ID: " + cST.getId());
				new Log("Connect with " + socket.getInetAddress() + ", ID: " + cST.getId());


			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
				//break;
			}

		}
	}


	public void sendMsgAll(String msg) {

		for(int i=0; i<serverThreads.size(); i++) {

			coServerThread cST = (coServerThread) serverThreads.get(i);

			if(cST!=null) {
				if(!cST.sendMsg(msg)) {
					serverThreads.remove(i);
					i--;
				}
			}else {
				serverThreads.remove(i);
				i--;

			}


		}


	}



	public class coServerThread extends Thread{

		public DataInputStream is = null;
		public DataOutputStream os = null;

		public Socket coSocket = null;

		public boolean connect = false;
		public String name = null;

		public coServerThread(Socket _socket) {

			coSocket = _socket;

			try {
				is = new DataInputStream(coSocket.getInputStream());
				os = new DataOutputStream(coSocket.getOutputStream());

				connect = true;
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}


		}


		public void run() {

			byte[] pre_b = new byte[1000];
			int pre_len = 0;

			//String msg="";

			while(connect) {

				try {

					//String msg = is.readUTF();
					//msgStack.add(msg);

					byte[] b = new byte[1000];

					int len = is.read(b);
					if(len==-1) {
						disconnect();
						break;
					}

					//String tempMsg = new String(copyNewByte(b,len), "UTF-8");



					String msg = new String(copyNewByte(b,len), "UTF-8");
					System.out.println("recieve: " + msg.replaceAll("\n", " "));

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

						if(remainByte.length>900)
							System.out.println("ERROR: not found [\n] " + new String(pre_b, "UTF-8"));

						//String temp = new String(pre_b, "UTF-8");
						//System.out.println("remain: " + temp);

					}else
						pre_len = 0;


					for(int i=0; i<enqCount; i++) {

						if(msgs[i].startsWith("name;")) {

							name = msgs[i].split(";")[1];
							//System.out.println("Register name as: " + name
							//		 + " (" + coSocket.getInetAddress() + "," + this.getId() + ")");

							new Log("Register name as: '" + name
									 + "' (" + coSocket.getInetAddress() + ", ID:" + this.getId() + ")");

						}else {
							//inputMsgThread imt = new inputMsgThread(msgs[i]);
							//imt.start();

							inputMsg(msgs[i]);

							//System.out.println("enqueue: " + msgs[i]);
						}

					}



				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					//e.printStackTrace();
					ifError(e);
					break;
				}

			}

		}

		public boolean sendMsg(String msg) {


			if(connect) {
				try {

					//os.writeUTF(msg);
					//new SystemOut("send: " + msg);

					msg += "\n";
					byte[] b = msg.getBytes("UTF-8");
					os.write(b);



				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					//e.printStackTrace();
					ifError(e);
					return false;
				}
			}else {
				//System.out.println("[MultiServerThread] no connection error, could not sendMsg");
				new Log("no connection error, could not sendMsg");

				return false;
			}
			return true;
		}

		public void ifError(IOException e) {

			System.out.println(e.toString());
			disconnect();

		}

		public void disconnect() {

			try {

				is.close();
				os.close();
				coSocket.close();

				connect = false;

				//System.out.println("disconnect wiht: " + name + " (" + coSocket.getInetAddress() + "," + this.getId() + ")");
				new Log("disconnect with: '" + name + "' (" + coSocket.getInetAddress() + "," + this.getId() + ")");

			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				//e.printStackTrace();
				System.out.println(e.toString());
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



	public String getMsg() {

		gotMsgLock.lock();

		String msg = manageMsgQueue("DEQ", null);

		gotMsgLock.unlock();

		return msg;

	}


	public void inputMsg(String addMsg) {

		gotMsgLock.lock();

		manageMsgQueue("ENQ", addMsg);

		gotMsgLock.unlock();


	}


	public class inputMsgThread extends Thread{

		String addMsg;

		inputMsgThread(String _addMsg){

			addMsg = _addMsg;

		}

		public void run() {

			inputMsg(addMsg);

		}

	}



	public String manageMsgQueue(String command, String addMsg) {


		switch(command) {

			case "DEQ":
				if(gotMsgs.size()!=0) {

					String getMsg = gotMsgs.get(0);
					if(getMsg != null) {
						gotMsgs.remove(0);
						return getMsg;
					}else
						return null;

				}else
					return null;

			case "ENQ":
				gotMsgs.add(addMsg);

				if(gotMsgs.size()>100) {

					//System.out.println("manageMsgQueue too much stack(>100): remove msg: " + gotMsgs.get(0));
					new Log("manageMsgQueue too much stack(>100): remove msg: " + gotMsgs.get(0));
					gotMsgs.remove(0);

				}

				return null;

			default:
				System.out.println("manageMsgQueue command error: " + command);
				return null;

		}




	}


}
