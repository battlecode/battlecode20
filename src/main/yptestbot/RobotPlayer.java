package yptestbot;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Upgrade;

/** This bot does absolutely nothing.
 *
 */
public class RobotPlayer {
	
	public final int w;
	public final int h;
	public final RobotController rc;
	
	public static void run(RobotController rc) {
		try {

			
			while (!rc.hasUpgrade(Upgrade.VISION))
			{
				rc.researchUpgrade(Upgrade.VISION);
				rc.yield();
			}
			while (true) rc.yield();
		} catch (Exception e) {
			
		}
//		new RobotPlayer(rc);
		
	}
	
	public RobotPlayer(RobotController rc) {
		w = rc.getMapHeight();
		h = rc.getMapWidth();
		this.rc = rc;

		MapLocation ml = rc.getLocation();
		System.out.println(Clock.getBytecodeNum());
		System.out.println(Clock.getBytecodeNum());
//		int w = this.w;
//		int h = this.h;
//		int x = ml.x;
//		int y = ml.y;
//		boolean offmap = ml.x<0||ml.x>w||ml.y<0||ml.y>h;
//		getoffmap(ml);
		int[] i = new int[4000];
		try {

			PipedInputStream pis = new PipedInputStream();
			PipedOutputStream pos = new PipedOutputStream(pis);
			ObjectOutputStream oos = new ObjectOutputStream(pos);
			oos.flush();
			ObjectInputStream ois = new ObjectInputStream(pis);
			
			oos.writeObject(i);
			oos.flush();
			int[] k = (int[])ois.readObject();
		} catch (Exception e) {e.printStackTrace();}
		System.out.println(Clock.getBytecodeNum());
	}
	
	public boolean getoffmap(MapLocation ml)
	{
		return !(ml.x>=0&&ml.x<=w&&ml.y>=0&&ml.y<=h);
//		return ml.x<0||ml.x>w||ml.y<0||ml.y>h;
	}
}
