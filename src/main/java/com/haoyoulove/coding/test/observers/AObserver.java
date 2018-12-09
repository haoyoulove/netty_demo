package com.haoyoulove.coding.test.observers;

import java.util.Observable;
import java.util.Observer;

/**
 * @author yangjing
 * 观察者
 */
public class AObserver  implements Observer{

	public AObserver() {
		super();
	}


	@Override
	public void update(Observable o, Object arg) {
		System.out.println("start->AObserver receive:Data has changed to "+((ServerManager) arg).getData());
		try {
			Thread.sleep(200);
		}catch (Exception e){
			e.printStackTrace();
		}
		System.out.println("end-> AObserver receive:Data has changed to "+((ServerManager) arg).getData());
	}
}
