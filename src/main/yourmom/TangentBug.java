package yourmom;

public class TangentBug {
	final static int[][] d = new int[][] {{-1,-1}, {0,-1}, {1,-1}, {1,0}, 
		{1,1}, {0,1}, {-1,1}, {-1,0}};
	
	/** Scan this many times every turn to see if there is a wall in our way. */
	final static int MLINE_SCAN_STEPS_PER_TURN_TRACING = 1, MLINE_SCAN_STEPS_PER_TURN_NOT_TRACING = 1;
	/** Trace down the obstructing wall this many times in one turn. */
	final static int WALL_SCAN_STEPS_PER_TURN = 1;
	/** Look this many steps in each way to find the tangent in one turn. */
	final static int FIND_TANGENT_STEPS_PER_TURN = 1;
	
	/** Default number of turns we have to prepare before we have a reasonable notion of where to go. */
	final static int DEFAULT_MIN_PREP_TURNS = 10;
	/** We approximate that it will take an average of this*d squares to navigate a distance of d. */
	public final static double MAP_UGLINESS_WEIGHT = 1.5;
	
	final static int BUFFER_START = 4096;
	final static int BUFFER_LENGTH = BUFFER_START*2;
	
	final boolean[][] map;
	final int xmax;
	final int ymax;
	
	int tx = -1;
	int ty = -1;
	
	// Map edge variables - these need to be updated from the outside
	public int edgeXMin, edgeXMax, edgeYMin, edgeYMax;
	
	// Target variables - cleared every time we get a new target
	int minPrepRounds = 3; // The amount of times we have to prepare before we can compute
	double chanceGoLongWay = 0; // The chance that we pick the longer way to trace around walls
	
	// Wall variables - cleared every time we start tracing a new wall
	final int[][] buffer = new int[BUFFER_LENGTH][2];
	final int[][] wallCache; // fields are curWallCacheID*BUFFER_LENGTH for walls, and curWallCacheID*BUFFER_LENGTH+bufferPos for squares on the trace path
	int leftWallDir = -1;
	int rightWallDir = -1; 
	int curWallCacheID = 1;
	int bufferLeft = BUFFER_START; //buffer side for clockwise tracing
	int bufferRight = BUFFER_START; //for counterclockwise tracing
	public boolean tracing = false;
	int traceDirLastTurn = -1;
	boolean doneTracingClockwise = false;
	boolean doneTracingCounterclockwise = false;
	boolean traceDirLocked = false;
	boolean[] hitMapEdge = new boolean[2];
	int tangentPosLastTurn = -1;
	boolean directionalBugging = false;
	int directionalBugDirection = -1;
	int directionalBugWallDir = -1;
	int directionalBugStartDotProduct = -1;
	
	// Preparatory variables - cleared every time robot moves
	boolean donePreparing = false;
	int turnsPreparedBeforeMoving = 0;
	int preparingsx = -1;
	int preparingsy = -1;
	boolean startedTracingDuringCurrentPrepCycle = false;
	boolean hitWallCache = false;
	int hitWallX = -1;
	int hitWallY = -1;
	int hitWallPos = -1;
	int scanx = -1;
	int scany = -1;
	int[] bpos = new int[2];
	int[] bdir = new int[2];
	double[] heuristicValue = new double[2];
	boolean[] crossedMLine = new boolean[2];
	int[] lastdDir = new int[2];
	int[] bestdDir = new int[2];
	int findTangentProgress = 0;
	
	public TangentBug(boolean[][] map) {
		this.map = map;
		xmax = map.length;
		ymax = map[0].length;
		edgeXMin = -1;
		edgeXMax = Integer.MAX_VALUE;
		edgeYMin = -1;
		edgeYMax = Integer.MAX_VALUE;
		wallCache = new int[xmax][ymax];
		reset();
	}
	/** Completely clears the tangent bug of any state. */
	public void reset() {
		resetWallTrace(DEFAULT_MIN_PREP_TURNS, 0);
	}
	/** Resets the tangent bug of any state tracing a wall, but keeps the state that helps it
	 * get around difficult obstacles.
	 */
	public void resetWallTrace() {
		resetWallTrace(minPrepRounds, chanceGoLongWay);
	}
	/** Resets the tangent bug of any state tracing a wall, and sets a state that helps it
	 * get around difficult obstacles.
	 * @param minPrepRounds the minimum rounds we have to prepare before computing
	 * @param chanceGoLongWay the probability that when we heuristically pick which direction
	 * 	to trace the wall (CW or CCW), we go the opposite way.
	 */
	public void resetWallTrace(int minPrepRounds, double chanceGoLongWay) {
		tracing = false;
		curWallCacheID++;
		clearPreparatoryVariables();
		this.minPrepRounds = minPrepRounds;
		this.chanceGoLongWay = chanceGoLongWay;
	}
	public void setTarget(int tx, int ty) {
		if(this.tx==tx && this.ty==ty) return;
		
		reset();
		this.tx = tx; 
		this.ty = ty;
	}
	public void clearPreparatoryVariables() {
		donePreparing = false;
		preparingsx = -1;
		preparingsy = -1;
		turnsPreparedBeforeMoving = 0;
		startedTracingDuringCurrentPrepCycle = false;
		hitWallCache = false;
		hitWallX = -1;
		hitWallY = -1;
		hitWallPos = -1;
		scanx = -1;
		scany = -1;
		for(int traceDir=0; traceDir<=1; traceDir++) {
			crossedMLine[traceDir] = false;
			lastdDir[traceDir] = 1000;
			bestdDir[traceDir] = -1;
			bpos[traceDir] = BUFFER_START;
			bdir[traceDir] = -1;
			heuristicValue[traceDir] = (traceDirLocked && traceDir!=traceDirLastTurn) ? 1 : 0;
		}
		findTangentProgress = 0;
	}
	public int getTurnsPrepared() {
		return turnsPreparedBeforeMoving;
	}
	public void prepare(int sx, int sy) {
		if(!(preparingsx==sx && preparingsy==sy)) {
			if(preparingsx != -1)
				clearPreparatoryVariables();
			preparingsx = sx;
			preparingsy = sy;
		}
		turnsPreparedBeforeMoving++; 
		if(donePreparing) 
			return;
		if(tracing) {
			//trace clockwise and cache 
			for(int n=0; n<WALL_SCAN_STEPS_PER_TURN && !doneTracingClockwise; n++) {
				traceClockwiseHelper();
			}
			
			//trace counterclockwise and cache 
			for(int n=0; n<WALL_SCAN_STEPS_PER_TURN && !doneTracingCounterclockwise; n++) {
				traceCounterclockwiseHelper();
			}
			
			if(!hitWallCache && !startedTracingDuringCurrentPrepCycle) {
				if(scanx==-1) {
					scanx = sx;
					scany = sy;
				}
				for(int step=0; step<MLINE_SCAN_STEPS_PER_TURN_TRACING; step++) {
					if(scanx==tx && scany==ty) break;
					
					// go towards dest until we hit a wall
					int dirTowards = getDirTowards(tx-scanx, ty-scany);
					scanx += d[dirTowards][0];
					scany += d[dirTowards][1];
					if(tracing && !hitWallCache && wallCache[scanx][scany]==curWallCacheID*BUFFER_LENGTH) {
						// we've hit our wall cache! we're still in trace mode
						hitWallCache = true;
						hitWallX = scanx;
						hitWallY = scany;
						computeHitWallPos(dirTowards);
					}
				}
			}
			
			
			if(hitWallCache || startedTracingDuringCurrentPrepCycle) {
				int i;
				for(i=findTangentProgress; i<findTangentProgress+FIND_TANGENT_STEPS_PER_TURN; i++) {
					boolean flag = false;
					for(int traceDir = 0; traceDir<=1; traceDir++) {
						if(traceDirLocked && traceDir!=traceDirLastTurn) continue;
						int pos = (traceDirLocked?tangentPosLastTurn:
							startedTracingDuringCurrentPrepCycle?BUFFER_START:
								hitWallPos) + ((traceDir==0) ? -i : i);
						if(pos<=bufferLeft || pos>=bufferRight) continue;
						flag = true;
						
						findTangentsHelper(pos, traceDir, sx, sy);
					}
					if(!flag)
						break;
				}
				findTangentProgress = i;
			}
		} else {
			if(scanx==-1) {
				scanx = sx;
				scany = sy;
			}
			for(int step=0; step<MLINE_SCAN_STEPS_PER_TURN_NOT_TRACING; step++) {
				if(scanx==tx && scany==ty) {
					donePreparing = true;
					return;
				}
				
				// go towards dest until we hit a wall
				int dirTowards = getDirTowards(tx-scanx, ty-scany);
				scanx += d[dirTowards][0];
				scany += d[dirTowards][1];
				
				if(map[scanx][scany]) {
					// we've hit a wall! start tracing
					startTraceHelper(scanx - d[dirTowards][0], scany - d[dirTowards][1], 
							dirTowards);
					turnsPreparedBeforeMoving = 0;
					startedTracingDuringCurrentPrepCycle = true;
					break;
				}
			}
		}
	}
	
	/** Returns a (dx, dy) indicating which way to move. 
	 * <br/>
	 * <br/>May return null for various reasons:
	 * <br/> -have not prepared enough rounds
	 * <br/> -already at destination
	 * <br/> -in between switching into or out of trace mode
	 * <br/> -no directions to move
	 * <br/> -other random shit?
	 */
	public int[] computeMove(int sx, int sy) {
		if(turnsPreparedBeforeMoving < minPrepRounds) 
			return null;
		if(sx==tx && sy==ty) 
			return null;
		if(Math.abs(sx-tx)<=1 && Math.abs(sy-ty)<=1) {
			clearPreparatoryVariables();
			return new int[] {tx-sx, ty-sy};
		}
//		 if(tracing) {StringBuilder sb = new StringBuilder(); for(int y=0; y<ymax; y++) { for(int x=0; x<xmax; x++) sb.append((wallCache[x][y]==curWallCacheID*BUFFER_LENGTH)?'#':(wallCache[x][y]>curWallCacheID*BUFFER_LENGTH)?'o':'.'); sb.append("\n"); } System.out.print(sb);}
//		 if(tracing) for(int i=BUFFER_START-30; i<=BUFFER_START+30; i++) System.out.println("  "+i+" "+buffer[i][0]+","+buffer[i][1]);
		
		if(!tracing) {
			int[] ret = d[getDirTowards(tx-sx, ty-sy)];
			clearPreparatoryVariables();
			return map[sx+ret[0]][sy+ret[1]] ? null : ret;
		} else if(!hitWallCache && !startedTracingDuringCurrentPrepCycle) {
			resetWallTrace();
			int[] ret = d[getDirTowards(tx-sx, ty-sy)];
			return map[sx+ret[0]][sy+ret[1]] ? null : ret;
		}
		
//		System.out.println("tracedirlocked: "+traceDirLocked);
//		System.out.println("hitWallPos: "+hitWallPos);
		
		//find better direction by taking smaller heuristic value
		int bestTraceDir =  heuristicValue[0]<heuristicValue[1]?0:1;
		
		if(hitMapEdge[0] && !hitMapEdge[1]) 
			bestTraceDir = 1;
		else if(!hitMapEdge[0] && hitMapEdge[1]) 
			bestTraceDir = 0;
		else if(!traceDirLocked && bdir[bestTraceDir]==-1 && bdir[1-bestTraceDir]!=-1) 
			bestTraceDir = 1 - bestTraceDir;
		
		if(!traceDirLocked && chanceGoLongWay>0 && Util.randDouble()<chanceGoLongWay) 
			bestTraceDir = 1 - bestTraceDir;
		traceDirLastTurn = bestTraceDir;
		tangentPosLastTurn = bpos[bestTraceDir];
		int finalDir = bdir[bestTraceDir];
		
//		System.out.println(" currently at: "+sx+","+sy);
//		System.out.println(" startedTracingDuringCurrentPrepCycle: "+startedTracingDuringCurrentPrepCycle);
//		System.out.println(" last trace dir: "+traceDirLastTurn);
//		System.out.println(" finalDir: "+finalDir);
//		System.out.println(" tangent point: "+buffer[bpos[bestTraceDir]][0]+","+buffer[bpos[bestTraceDir]][1]);
		if(finalDir==-1) {
			// this happens when there were no valid tangent points found
			resetWallTrace();
			return null;
		}
		int x = sx+d[finalDir][0];
		int y = sy+d[finalDir][1];
		if(map[x][y] && wallCache[x][y]>curWallCacheID*BUFFER_LENGTH) {
			// our wall cache is out of date due to newly sensed walls
			resetWallTrace();
			return null;
		}
			
		if(!traceDirLocked){
			while(map[x][y]) {
				finalDir = (finalDir+(traceDirLastTurn==0?1:-1)+8)%8;
				x = sx+d[finalDir][0];
				y = sy+d[finalDir][1];
				
			}
		} else {
			if(directionalBugging) {
				int dFinalDir = ((traceDirLastTurn==0 ? (finalDir - directionalBugDirection) : 
					(directionalBugDirection - finalDir)) + 7) % 8;
				if(dFinalDir<4) {
					directionalBugging = false;
				}
			}
			if(!directionalBugging) {
				if(map[x][y]) {
					directionalBugging = true;
					directionalBugDirection = finalDir;
					directionalBugStartDotProduct = d[finalDir][0]*sx + d[finalDir][1]*sy;
					directionalBugWallDir = finalDir;
				}
			}
			if(directionalBugging) {
				int dot = d[directionalBugDirection][0]*sx + d[directionalBugDirection][1]*sy;
				x = sx+d[directionalBugDirection][0];
				y = sy+d[directionalBugDirection][1];
				if(!map[x][y] && dot > directionalBugStartDotProduct) {
					finalDir = directionalBugDirection;
					directionalBugging = false;
				} else {
					for(int wx=-1, wy=-1, ti=0; ti<d.length; ti++) {
						int i = ((traceDirLastTurn==0?1:-1)*ti + directionalBugWallDir + 8) % 8;
//						System.out.println(" round of directional bugging, i="+i);
						x = sx+d[i][0];
						y = sy+d[i][1];
						if(map[x][y]) {
							wx = x; 
							wy = y;
						} else {
							finalDir = i;
							for(int j=0; j<d.length; j++) {
								if(x+d[j][0]==wx && y+d[j][1]==wy) {
									directionalBugWallDir = j;
									break;
								}	
							}
							break;
						}
					}
				}
			}
		}
//		System.out.println(" directional bugging: "+directionalBugging);
		
		if(doneTracingClockwise && doneTracingCounterclockwise || hitMapEdge[0] || hitMapEdge[1])
			traceDirLocked = true;
		clearPreparatoryVariables();
		return d[finalDir];
		
	}
	
	private void startTraceHelper(int x, int y, int dir) {
		tracing = true;
		curWallCacheID++;
		buffer[BUFFER_START][0] = x;
		buffer[BUFFER_START][1] = y;
		wallCache[x][y] = curWallCacheID*BUFFER_LENGTH+BUFFER_START;
		bufferLeft = BUFFER_START-1;
		bufferRight = BUFFER_START+1;
		leftWallDir = dir;
		rightWallDir = dir;
		traceDirLastTurn = -1;
		doneTracingClockwise = false;
		doneTracingCounterclockwise = false;
		traceDirLocked = false;
		tangentPosLastTurn = -1;
		directionalBugging = false;
		directionalBugDirection = -1;
		directionalBugWallDir = -1;
		directionalBugStartDotProduct = -1;
		hitMapEdge[0] = false;
		hitMapEdge[1] = false;
	}
	
	private void traceClockwiseHelper() {
		if(hitMapEdge[0]) return;
		if(bufferLeft+1!=BUFFER_START && 
				buffer[bufferLeft+1][0]==buffer[BUFFER_START][0] && 
				buffer[bufferLeft+1][1]==buffer[BUFFER_START][1]) {
			doneTracingClockwise = true;
			return;
		}
		if(bufferLeft<BUFFER_START-2) {
			int Ax = buffer[BUFFER_START][0];
			int Ay = buffer[BUFFER_START][1];
			int Bx = tx;
			int By = ty;
			int Cx = buffer[bufferLeft+1][0];
			int Cy = buffer[bufferLeft+1][1];
			int Dx = buffer[bufferLeft+2][0];
			int Dy = buffer[bufferLeft+2][1];
			int distSquaredAtoB = (Ax-Bx)*(Ax-Bx)+(Ay-By)*(Ay-By);
			int distSquaredBtoC = (Bx-Cx)*(Bx-Cx)+(By-Cy)*(By-Cy);
			int ABcrossAC = (Ax-Bx)*(Ay-Cy)-(Ax-Cx)*(Ay-By);
			int ABcrossAD = (Ax-Bx)*(Ay-Dy)-(Ax-Dx)*(Ay-By);
			if(distSquaredBtoC < distSquaredAtoB && ABcrossAC*ABcrossAD<=0) {
				doneTracingClockwise = true;
				return;
			}
		}
		for(int wx=-1, wy=-1, ti=0; ti<d.length; ti++) {
			int i = (-1*ti + leftWallDir + d.length) % d.length;
			int x = buffer[bufferLeft+1][0]+d[i][0];
			int y = buffer[bufferLeft+1][1]+d[i][1];
			if(map[x][y]) {
				wx = x; 
				wy = y;
				if(wx<=edgeXMin || wx>=edgeXMax || wy<=edgeYMin || wy>=edgeYMax)
					hitMapEdge[0] = true;
				wallCache[wx][wy] = curWallCacheID*BUFFER_LENGTH;
			} else {
				buffer[bufferLeft][0] = x;
				buffer[bufferLeft][1] = y;
				wallCache[x][y] = curWallCacheID*BUFFER_LENGTH+bufferLeft;
				bufferLeft--;
				for(int j=0; j<d.length; j++) {
					if(x+d[j][0]==wx && y+d[j][1]==wy) {
						leftWallDir = j;
						break;
					}	
				}
				break;
			}
		}
	}
	
	private void traceCounterclockwiseHelper() {
		if(hitMapEdge[1]) return;
		if(bufferRight-1!=BUFFER_START && 
				buffer[bufferRight-1][0]==buffer[BUFFER_START][0] && 
				buffer[bufferRight-1][1]==buffer[BUFFER_START][1]) {
			doneTracingCounterclockwise = true;
			return;
		}
		if(bufferRight>BUFFER_START+2) {
			int Ax = buffer[BUFFER_START][0];
			int Ay = buffer[BUFFER_START][1];
			int Bx = tx;
			int By = ty;
			int Cx = buffer[bufferRight-1][0];
			int Cy = buffer[bufferRight-1][1];
			int Dx = buffer[bufferRight-2][0];
			int Dy = buffer[bufferRight-2][1];
			int distSquaredAtoB = (Ax-Bx)*(Ax-Bx)+(Ay-By)*(Ay-By);
			int distSquaredBtoC = (Bx-Cx)*(Bx-Cx)+(By-Cy)*(By-Cy);
			int ABcrossAC = (Ax-Bx)*(Ay-Cy)-(Ax-Cx)*(Ay-By);
			int ABcrossAD = (Ax-Bx)*(Ay-Dy)-(Ax-Dx)*(Ay-By);
			if(distSquaredBtoC < distSquaredAtoB && ABcrossAC*ABcrossAD<=0) {
				doneTracingCounterclockwise = true;
				return;
			}
		}
		for(int wx=-1, wy=-1, ti=0; ti<d.length; ti++) {
			int i = (1*ti + rightWallDir + d.length) % d.length;
			int x = buffer[bufferRight-1][0]+d[i][0];
			int y = buffer[bufferRight-1][1]+d[i][1];
			if(map[x][y]) {
				wx = x; 
				wy = y;
				if(wx<=edgeXMin || wx>=edgeXMax || wy<=edgeYMin || wy>=edgeYMax)
					hitMapEdge[1] = true;
				wallCache[wx][wy] = curWallCacheID*BUFFER_LENGTH;
			} else {
				buffer[bufferRight][0] = x;
				buffer[bufferRight][1] = y;
				wallCache[x][y] = curWallCacheID*BUFFER_LENGTH+bufferRight;
				bufferRight++;
				for(int j=0; j<d.length; j++) {
					if(x+d[j][0]==wx && y+d[j][1]==wy) {
						rightWallDir = j;
						break;
					}	
				}
				break;
			}
		}
		
	}
	private void computeHitWallPos(int dirHitWallFrom) {
		int ddirorder[] = new int[] {0,-1,1,-2,2,-3,3,4};
		for(int ddir: ddirorder) {
			int dir = (dirHitWallFrom + 4 + ddir) % 8;
			int x = hitWallX + d[dir][0];
			int y = hitWallY + d[dir][1];
			if(wallCache[x][y]>curWallCacheID*BUFFER_LENGTH) {
				hitWallPos = wallCache[x][y] % BUFFER_LENGTH;
				return;
			}
		}
	}
	private void findTangentsHelper(int pos, int traceDir, int sx, int sy) {
		int cx = buffer[pos][0];
		int cy = buffer[pos][1];
		if(sx==cx && sy==cy) {
			return;
		}
		int dirStoT = (traceDir==1) ? getDirCounterclockwiseOf(tx-sx, ty-sy) : 
			getDirClockwiseOf(tx-sx, ty-sy);
		int dirStoC = (traceDir==1) ? getDirClockwiseOf(cx-sx, cy-sy) : 
			getDirCounterclockwiseOf(cx-sx, cy-sy);
		int dDir = (((traceDir==0)?(dirStoT-dirStoC):(dirStoC-dirStoT))+9) % 8;
		if(lastdDir[traceDir]>8) {
			crossedMLine[traceDir] = false; //dDir>5 && !traceDirLocked;
		} else {
			if(dDir-lastdDir[traceDir]>4) crossedMLine[traceDir] = true;
			if(lastdDir[traceDir]-dDir>4) crossedMLine[traceDir] = false;
		}
//		System.out.println("  checking tangent: "+pos+" "+buffer[pos][0]+","+buffer[pos][1]+" "+dirStoC+" "+crossedMLine[traceDir]);
		if(!crossedMLine[traceDir] && dDir>bestdDir[traceDir]) {
			bestdDir[traceDir] = dDir;
			bpos[traceDir] = pos;
			bdir[traceDir] = dirStoC;
		}
		if(!traceDirLocked && !crossedMLine[traceDir]) {
			heuristicValue[traceDir] = Math.max(heuristicValue[traceDir],
					Math.sqrt((sx-cx)*(sx-cx)+(sy-cy)*(sy-cy))+
					Math.sqrt((tx-cx)*(tx-cx)+(ty-cy)*(ty-cy))*MAP_UGLINESS_WEIGHT);
		}
		lastdDir[traceDir] = dDir;
	}
	
	/** Returns the direction that is equivalent to the given dx, dy value, 
	 * or clockwise of it by as little as possible.
	 */
	private static int getDirClockwiseOf(int dx, int dy) {
		if(dx==0) {
			if(dy>0) return 5;
			else return 1;
		}
		double slope = ((double)dy)/dx;
		if(dx>0) {
			if(slope>=1) return 4;
			else if(slope>=0) return 3;
			else if(slope>=-1) return 2;
			else return 1;
		} else {
			if(slope>=1) return 0;
			else if(slope>=0) return 7;
			else if(slope>=-1) return 6;
			else return 5;
		}
	}
	/** Returns the direction that is equivalent to the given dx, dy value, 
	 * or counterclockwise of it by as little as possible.
	 */
	private static int getDirCounterclockwiseOf(int dx, int dy) {
		if(dx==0) {
			if(dy>0) return 5;
			else return 1;
		}
		double slope = ((double)dy)/dx;
		if(dx>0) {
			if(slope>1) return 5;
			else if(slope>0) return 4;
			else if(slope>-1) return 3;
			else return 2;
		} else {
			if(slope>1) return 1;
			else if(slope>0) return 0;
			else if(slope>-1) return 7;
			else return 6; 
		}
	}
	/** Returns the direction that is equivalent to the given dx, dy value, 
	 * or as close to it as possible.
	 */
	private static int getDirTowards(int dx, int dy) {
		if(dx==0) {
			if(dy>0) return 5;
			else return 1;
		}
		double slope = ((double)dy)/dx;
		if(dx>0) {
			if(slope>2.414) return 5;
			else if(slope>0.414) return 4;
			else if(slope>-0.414) return 3;
			else if(slope>-2.414) return 2;
			else return 1;
		} else {
			if(slope>2.414) return 1;
			else if(slope>0.414) return 0;
			else if(slope>-0.414) return 7;
			else if(slope>-2.414) return 6;
			else return 5;
		}
	}
}
