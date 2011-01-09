package samuraiPlayer.Robot;

import java.util.Random;

import battlecode.common.*;

public class BasicRobot {
	protected final RobotController myRC;
	protected double myFlux;
	protected MapLocation myLocation;
	protected Direction myDirection;
	protected SensorController senseController;
	protected Team myTeam;
	protected int myCurrentRound = 0;//Time since birth.
	
	Random rand = new Random();
	
	
	public BasicRobot(RobotController rc){
		myRC = rc;
	}
	
	public void run(){
		
		while (true){
			try{
				//Variables that we use throughout the method
				//Some of these could technically just be set at the very beginning (such as location)
				//Because in the case of buildings, they don't move.
				myLocation = myRC.getLocation();
				myDirection = myRC.getDirection();
				myFlux = myRC.getTeamResources();
				myTeam = myRC.getTeam();
				myCurrentRound++;
				
				//Main run loop
				myRun();
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
	}
	
	protected Direction getRandomDirection(){
		Direction randDirection = Direction.NORTH;
		int rInt = rand.nextInt()%8;
		for(int i = 0; i < rInt; i++){
			randDirection = randDirection.rotateLeft();
		}
		return randDirection;
		
	}
	
	protected void myRun() throws GameActionException{
		
		
	}
}
