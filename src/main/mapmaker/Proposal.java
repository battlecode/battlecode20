package mapmaker;

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
			if(d.isDiagonal()){
				proposalList.add(new Proposal(locus.add(d),d,distToLocus+incrementalDist*14));
			}else{
				proposalList.add(new Proposal(locus.add(d),d,distToLocus+incrementalDist*10));
			}
		}
	}
	
}