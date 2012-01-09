package testplayer;

import battlecode.common.*;

import java.util.Random;

public class RobotPlayer implements Runnable {

	int failures;
	RobotController myRC;

	public RobotPlayer(RobotController rc) {
		myRC = rc;
	}

	public void _assert_equal(int a, int b) {
		if(a!=b) {
			StackTraceElement [] trace = new AssertionError().getStackTrace();
			System.out.format("Test failed at %s\n",trace[1].toString());
			System.out.format("Expected %d, got %d\n",b,a);
			failures++;
		}
	}
	
	public void _assert_equal(Object a, Object b) {
		if(a!=b) {
			StackTraceElement [] trace = new AssertionError().getStackTrace();
			System.out.format("Test failed at %s\n",trace[1].toString());
			System.out.format("Expected %d, got %d",b,a);
			failures++;
		}
	}

	public int hashCode() {
		return 2011;
	}

	public void run() {
		try {
			//if(myRC.senseNearbyAirRobots().length>1)
			//	myRC.suicide();
			Object o = new Object();
			Object p = new Object();
			Object q = new Object();
			// hash code
			_assert_equal(o.hashCode(),0);
			_assert_equal(System.identityHashCode(o),0);
			java.util.HashSet<Object> h = new java.util.HashSet<Object>();
			h.add(q);
			_assert_equal(p.hashCode(),2);
			_assert_equal(q.hashCode(),1);
			_assert_equal(o.hashCode(),0);
			_assert_equal(hashCode(),2011);
			_assert_equal(super.hashCode(),3);
			// random
			Random r = new Random();
			Random s = new Random();
			_assert_equal(r.nextInt(),s.nextInt());
			// class
			_assert_equal(RobotPlayer.class,getClass());
			myRC.yield();
			if(failures==0)
				System.out.println("Success!");
			else
				System.out.format("%d tests failed\n",failures);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			System.out.println("Test failed to finish due to an exception");
		}
		myRC.resign();
	}
}
