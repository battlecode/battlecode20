package hubertTheFraternal;

import java.util.ArrayList;

import battlecode.common.*;

public class Proposal {
	
	public MapLocation loc;//the proposed outermost location
	public Direction dir;//the direction to the outermost location
	public int dist;//the distance via the route implied
	
	public Proposal(MapLocation toMapLoc,Direction fromDirection, int fromDistance){
		loc=toMapLoc;
		dir=fromDirection;
		dist=fromDistance;
	}
	
	public static void generateProposals(MapLocation locus, int distToLocus,int incrementalDist,ArrayList<Proposal> proposalList, Direction[] consideredDirs){
		for(Direction d:consideredDirs){
			Proposal p;
			if(d.isDiagonal()){
				p = new Proposal(locus.add(d),d,distToLocus+incrementalDist*14);
			}else{
				p = new Proposal(locus.add(d),d,distToLocus+incrementalDist*10);
			}
			int val = BreadthFirst.getMapData(p.loc);
			if(val>0){//not off-map or entirely void-filled
				p.dist+=Math.pow((val-10000),2)*10;//TODO evaluate fudge factor of 10 for importance of void spaces
				proposalList.add(p);
			}
		}
	}
	
}