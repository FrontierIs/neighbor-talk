package Util;

public class Timer extends Thread{

	int time = 0;
	boolean running = true;
	boolean stop = false;

	Tools T = new Tools();

	public void run() {

		while(running) {

			T.wait(1000);
			if(!stop)
				time++;

		}
	}

	public int getTime(){

		return time;

	}

	public void endTime() {

		running = false;
	}

	public void resetTime() {

		time = 0;

	}

	public void stopTime() {

		stop = true;
	}

	public void restartTime() {

		stop = false;
	}


}
