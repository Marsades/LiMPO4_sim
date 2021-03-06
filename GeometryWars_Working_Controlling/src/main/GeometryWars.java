package main;
import processing.core.*;
import units.*;

import java.util.*;
import Geometry.Geometry;
import GraphHandling.*;
import shortestPathImplement.*;
import Math.Quaternion;
import java.util.Random;
//import java.awt.AWTException;
//import java.awt.MouseInfo;
//import java.awt.Robot;
//import java.awt.PointerInfo;

public class GeometryWars extends PApplet {
	
	//Structures and objects
	private BoxList boxList;
//	private BoxList boxList2;
//	private Geometry geom;
	private SateliteList satList;
//	Robot rob;
//	private ArrayList<Unit> UnitList;
	
	//Units
	public Soldier player;
	private HashMap<PVector, Marker> markerList;
	
	//User interface/controlling
	private BoxCursor boxCursor;
	private PosCursor posCursor;
	private Controller controller;
	
	// Temporary line object
	private PVector startPoint;
	private PVector endPoint;

	//Viewing
	private float phi;
	private float theta;
	private PVector camPos;
	private PVector camFocus;
	private CameraMode cameraMode = CameraMode.freeView;
	private float rotSpeed = 0.02f;
	private float cursorSpeed = 1f;
	private float zoom;
	private float zoomSpeed = 5;
	private float minZoom;
	private float maxZoom;
	
	//Buttons and keypresses
	private boolean UP_PRESSED = false;
	private boolean DOWN_PRESSED = false;
	private boolean LEFT_PRESSED = false;
	private boolean RIGHT_PRESSED = false;
	private boolean A_PRESSED = false;
	private boolean S_PRESSED = false;
	private boolean D_PRESSED = false;
	private boolean W_PRESSED = false;
	private boolean E_PRESSED = false;
	private boolean Q_PRESSED = false;
	private boolean J_PRESSED = false;
	private boolean K_PRESSED = false;
	private boolean L_PRESSED = false;
	private boolean I_PRESSED = false;
	private boolean U_PRESSED = false;
	private boolean O_PRESSED = false;
	private boolean ONE_PRESSED = false;
	private boolean TWO_PRESSED = false;
	private boolean SHIFT_PRESSED = false;
//	private boolean buildXPos = false;
//	private boolean buildYPos = false;
//	private boolean buildXNeg = false;
//	private boolean buildYNeg = false;
	
	//Utilities:
	private int s = 36;
	private PImage  p;
//	private Random rand;
	public float sideLength;
	private int dirCounter;
	private int counter;
	private int action;
	private float defect = 0;
	private float precision = 1;
	private float FoV;
	private float aspectRatio;
	
	//Second view:
//	private PWindow window;
//	PApplet[] windows;
  
	public static void main(String[] args) {
		PApplet.main("main.GeometryWars");
	}

	public void settings() {
		size(1400, 800, P3D);
//		fullScreen(P3D);
		
//		//Setup of second-view window
//		String[] args = { "--location=10,10", "PWindow" };
//		window = new PWindow();
//		PApplet.runSketch(args, window);

//		windows = new PApplet[2];
//		windows[0] = this;
//		windows[1] = window;
	}

	public void setup() {
		phi = 0;
		theta = 0;
		sideLength = 300;
		camPos = new PVector(0, 0, 0);
		camFocus = new PVector(0, 0, 0);
		
		satList = new SateliteList();
		minZoom = sideLength/2;
		maxZoom = 4 * sideLength;

		zoom = (minZoom + maxZoom) / 2;
		startPoint = new PVector(0,0,0);
		endPoint = new PVector(0,0,0);
		
//		rand = new Random(0);
		boxList = new BoxList(this, sideLength, "Box" + 1);
//		boxList2 = new BoxList(this, sideLength, new PVector(-sideLength, -sideLength, -2*sideLength), "Box"+2);
		
		satList.add(boxList);
//		satList.add(boxList2);
		
		int M = 15;
//		for (int i = 0; i < 1; i++) {
//			satList.add(new BoxList(this, sideLength, (new PVector(rand.nextInt(2*M)-M, rand.nextInt(2*M)-M, rand.nextInt(2*M)-M)).mult(sideLength), "Box" + i)); 
//		}
		
		boxCursor = new BoxCursor(this, boxList, sideLength);
		posCursor = new PosCursor(this, sideLength, boxList, 0);
		player = new Soldier(this, sideLength, boxList, 0);
//		geom = new Geometry(this);
//		try {
//			rob = new Robot();
//		} catch (AWTException e) {
//			e.printStackTrace();
//		}

		markerList = new HashMap<PVector, Marker>();
		controller = new Controller(player.getGlobalPos());
		dirCounter = 0;	
		p = loadImage("C:\\Users\\anders\\eclipse-workspace\\StartSkyImage.png");
		p.resize(800, 800);
		float cameraZ = (float) ((this.height/2.0f) / tan(PI/6f) );
		FoV = PI/3.0f;
		aspectRatio = (float) width / (float) height;
		perspective(FoV, aspectRatio, 1f, cameraZ*100.0f);
		printProjection();
		
	}
	
//	public void translate(float x, float y, float z) {
//		super.translate(x, y, z);
//		window.translate(x, y, z);
//	}
//
//	public void box(float size) {
//		super.box(size);
//		window.box(size);
//	}
//
//	public void background(float r, float g, float b) {
//		super.background(r, g, b);
//		window.background(r, g, b);
//	}

	public void draw() {
		//		camera(width / 2, height / 2, zoom, width / 2, height / 2, 0, 0, 1, 0);
		camera(0, 0, zoom, 0, 0, 0, 0, 1, 0);
//		window.camera(zoom, 0, 0, 0, 0, 0, 0, 1, 0);
		
//		translate(width / 2, height / 2, zoom);
//		translate(width / 2, height / 2, 0);
//		for (int i = 0; i < windows.length; i++) {
//			windows[i].rotateX(theta);
//			windows[i].rotateZ(phi);
//		}
		rotateX(theta);
		rotateZ(phi);
		
		
		if(cameraMode == CameraMode.playerView) {
			PVector X = player.getGlobalDirection();
			PVector Z = player.getNormal();
			PVector Y = Z.copy().cross(X);
			PMatrix3D M = new PMatrix3D(X.x, X.y, X.z, 0, Y.x, Y.y, Y.z, 0, Z.x, Z.y, Z.z, 0, 0, 0, 0, 1);
//			PMatrix3D M = new PMatrix3D(-X.x, -Y.x, -Z.x, 0, -X.y, -Y.y, -Z.y, 0, -X.z, -Y.z, -Z.z, 0, 0, 0, 0, 1);
			applyMatrix(M);
			PVector pos = player.getGlobalPos();
			translate(-pos.x, -pos.y, -pos.z);
//			System.out.print(X.toString());
//			System.out.print(Y.toString());
//			System.out.println(Z.toString());
		}
		
		if(cameraMode == CameraMode.lockedView) {
			Quaternion Q = boxList.getOrientation();
			Q = Q.conjugate();
			applyMatrix(Q.toRotationMatrix());
		}

		translate(camFocus.x, camFocus.y, camFocus.z);
		
		satList.draw();
		
//		drawSideConnections(boxList, boxList2);
		
		boxCursor.draw();
		posCursor.draw();
		player.draw();
		
		stroke(color(0, 0, 0));
		// noStroke();
		// fill(color(200,0,0));
		// for(int i=0;i<4;i++) {
		// pushMatrix();
		// rotateY((PI/2) * (float) (i));
		// translate(sideLength, 0, 0);
		// G.tetrahedronE(50, (float) (second())/60);
		// popMatrix();
		//
		// }
		// println(second());
		/*
		 * pushMatrix(); translate(2*sideLength, sideLength, 0); rotateX(-HALF_PI);
		 * G.tetrahedronE(sideLength/2, 1); popMatrix();
		 * 
		 * pushMatrix(); translate(2*sideLength, 0, sideLength); rotateX(HALF_PI);
		 * G.tetrahedronE(sideLength/2, 1); popMatrix();
		 * 
		 * pushMatrix(); translate(2*sideLength, sideLength, sideLength);
		 * G.tetrahedronE(sideLength/2, 1); popMatrix();
		 */
		for(PVector it : markerList.keySet() ) {
			Marker k = markerList.get(it);
			k.draw();
		}
		
		pushStyle();
		stroke(color(255, 255, 255));
		strokeWeight(5);
		startPoint = camPos.copy();
		line(startPoint.x, startPoint.y, startPoint.z, endPoint.x, endPoint.y, endPoint.z);
		popStyle();
		
		updateKeyPress();
		
//		AI Controlling
//		updateCommands();

//		Updates markers
//		updateMarkers();
////			println(boxList.List.size());
//		counter++;
		
		//Draw skybox/cubemap:
		drawCubemap();
	}

	private void drawCubemap() {
		pushMatrix();
		for(int i=0; i<4;i++) {
			rotateY(i*HALF_PI);
			drawBackground();
		}
		popMatrix();
		rotateZ(HALF_PI);
		drawBackground();
		rotateZ(PI);
		drawBackground();
	}

	private void drawBackground() {
		float S = 10000f;
		beginShape(PApplet.QUADS);
		texture(p);
		textureMode(PApplet.NORMAL);
		vertex(S, -S, -S, 0, 0);
		vertex(S, -S, S, 1, 0);
		vertex(S, S, S, 1, 1);
		vertex(S, S, -S, 0, 1 );
		endShape();
	}

	private void drawSideConnections(BoxList a, BoxList b) {
		PMatrix3D M1 = a.getOrientation().toRotationMatrix();
		PMatrix3D M2 = b.getOrientation().toRotationMatrix();
		
		PVector dX1 = new PVector(M1.m10, -M1.m11, M1.m12);
		PVector startX = a.getOrigin().add(dX1.mult(50));
		PVector pX2 = startX.copy().add(dX1.mult(10));
		
		PVector dX2 = new PVector(M2.m10, -M2.m11, M2.m12);
		PVector pX3 = b.getOrigin().add(dX2.mult(50));
		PVector endX = pX3.copy().add(dX2.mult(10));
		
//		System.out.println("The length of the vector is: " + p);
		this.bezier(startX.x, startX.y, startX.z, pX2.x, pX2.y, pX2.z, endX.x, endX.y, endX.z, pX3.x, pX3.y, pX3.z);
		
		PVector dZ1 = new PVector(M1.m20, -M1.m21, M1.m22);
		PVector startZ = a.getOrigin().add(dZ1.mult(50));
		PVector pZ2 = startZ.copy().add(dZ1.mult(10));
		
		PVector dZ2 = new PVector(M2.m20, -M2.m21, M2.m22);
		PVector pZ3 = b.getOrigin().add(dZ2.mult(50));
		PVector endZ = pZ3.copy().add(dZ2.mult(10));
		
		this.bezier(startZ.x, startZ.y, startZ.z, pZ2.x, pZ2.y, pZ2.z, endZ.x, endZ.y, endZ.z, pZ3.x, pZ3.y, pZ3.z);
		
	}

	private void updateCommands() {
		action = controller.getOrder(player);
		if (action == 1 || action == -1) {
			player.turn(action > 0);
		} else if (action == 2) {
			player.moveForwards();
		}
	}

	private void updateMarkers() {
		if (counter % s == s * 6 / 12) {
			PVector p = player.getGlobalPos();
			p = new PVector((int) p.x, (int) p.y, (int) p.z);
			if(!markerList.containsKey( p ) ){
				markerList.put(p, new Marker(this, player.getGlobalPos(), color(255, 0, 0)) );
			}
		}
//		if (counter % s == s * 7 / 12) {
//			if(!markerList.containsKey( player.getPos() ) ){
//				markerList.put(player.getPos(), new Marker(this, player.getPos(), color(255, 255, 0)) );
//			}
//		}
		if (counter % s == s * 8 / 12) {
			PVector p = player.getGlobalPos();
			p = new PVector((int) p.x, (int) p.y, (int) p.z);
			println(p);
			if(!markerList.containsKey( p ) ){
				markerList.put(p, new Marker(this, player.getGlobalPos(), color(0, 255, 0)) );
			}
		}
//		if (counter % s == s * 9 / 12) {
//		PVector p = player.getPos(); 
//		if(!markerList.containsKey( new PVector(p.x,p.y) ) ){
//				markerList.put(player.getPos(), new Marker(this, player.getPos(), color(0, 255, 255)) );
//			}
//		}
		if (counter % s == s * 10 / 12) {
			PVector p = player.getGlobalPos();
			p = new PVector((int) p.x, (int) p.y, (int) p.z);
			if(!markerList.containsKey( p ) ){
				markerList.put(p, new Marker(this, player.getGlobalPos(), color(0, 0, 255)) );
			}
		}
//		if (counter % s == s * 11 / 12) {
//			if(!markerList.containsKey( player.getPos() ) ){
//				markerList.put(player.getPos(), new Marker(this, player.getPos(), color(255, 0, 255)) );
//			}
//		}
		if ( (counter + 1) % s == 0) {
			PVector p = player.getGlobalPos();
			p = new PVector((int) p.x, (int) p.y, (int) p.z);
			if(!markerList.containsKey( p ) ){
				markerList.put(p, new Marker(this, player.getGlobalPos(), color(0, 0, 0)) );
			}
		}
		
		if (counter % s == 0) {
			// println();
			// print("Local pos: ");
			// player.printPos();
			// print("Global pos: ");
			// print(player.globalCoord.toString());

			// print(" Box list: ");
			// print(B.toString());
			// print(" Command Position: ");
			// print(Cont.moveTo.toString());
			// print(" Command: ");
			// print(action);
			// print(" Direction: ");
			// print(Player.dir);
			// print(" Side: ");
			// print(Player.side);
//			println();
			player.teleport(0, 0, sideLength/4, sideLength/4);
			dirCounter += 179;
			player.setDir( ((float) dirCounter)*PI/180);
			println(markerList.size());
		}
	}

	public PVector sphericalVector(float phi, float theta) {
		return new PVector(sin(phi)*sin(theta), cos(phi)*sin(theta), cos(theta));
	}

	// Updates all stuff using the keys that are pressed.
	public void updateKeyPress() {
		if (SHIFT_PRESSED) {
			precision = 0.02f;
		} else {
			precision = 1.0f;
		}
		if (A_PRESSED) {
			phi += -rotSpeed*precision;
		}
		if (D_PRESSED) {
			phi += rotSpeed*precision;
		}
		if (W_PRESSED && theta > 0) {
			theta -= rotSpeed*precision;
		}
		if (S_PRESSED && theta < PI) {
			theta += rotSpeed*precision;
		}
		if (Q_PRESSED && zoom < maxZoom) {
			zoom += zoomSpeed*precision;
		}
		if (E_PRESSED && zoom > minZoom) {
			zoom -= zoomSpeed*precision;
    }
    if (UP_PRESSED) {
			player.moveForwards();
			// camPos.y += 1;
		}
		if (DOWN_PRESSED) {
			// player.move(-playerMoveSpeed);
			// camPos.y -= 1;
		}
		if (LEFT_PRESSED) {
			player.turn(false);
			// camPos.x += 1;
		}
		if (RIGHT_PRESSED) {
			player.turn(true);
			// camPos.x -= 1;
		}
		if (posCursor.selected && L_PRESSED) {
			posCursor.moveX(-cursorSpeed);
		}
		if (posCursor.selected && J_PRESSED) {
			posCursor.moveX(cursorSpeed);
		}
		if (posCursor.selected && K_PRESSED) {
			posCursor.moveY(-cursorSpeed);
		}
		if (posCursor.selected && I_PRESSED) {
			posCursor.moveY(cursorSpeed);
		}
		if (ONE_PRESSED) {
			defect -= 5f*precision;
		}
		if (TWO_PRESSED) {
			defect += 5f*precision;
		}
	}

	public void mousePressed() {
		float mX = ((float) (mouseX*2 - width)) / ((float) width);
		float mY = ((float) (mouseY*2 - height)) / ((float) height);
		printProjection();
		printCamera();
		sphericalVector(phi, theta);
		getMatrix();
		PVector direction = (new PVector(tan(mX*FoV/2), -tan(mY*FoV/2 / aspectRatio), 1)).mult(100);
		endPoint = camPos.copy().add(direction);
		System.out.println("[" + mX + "," + mY + "]");
		System.out.println("Camera position: " + camPos.toString());
		System.out.println("From " + startPoint.toString() + " to " + endPoint.toString());
		
		PVector intersection = boxList.getLineIntersection(startPoint, direction);
		if (intersection != null) {
			PVector p = intersection.copy();
			markerList.put(p, new Marker(this, intersection, color(255,255,255)) );	
			System.out.println("A marker was put!");
		}
	}
  
	// Updates key presses
	public void keyPressed() {
		if (key == CODED) {
//			double mouseX = MouseInfo.getPointerInfo().getLocation().getX();
			switch (keyCode) {
			case LEFT:
				LEFT_PRESSED = true;
//				rob.mouseMove((int) mouseX - 10, (int) mouseY);
				break;
			case RIGHT:
				RIGHT_PRESSED = true;
//				rob.mouseMove((int) mouseX + 10, (int) mouseY);
				break;
			case DOWN:
				DOWN_PRESSED = true;
//				player.moveBackwards();
				break;
			case UP:
//				player.moveForwards();
				UP_PRESSED = true;
				break;
			case SHIFT:
				SHIFT_PRESSED = true;
				break;
			}
		} else {
			switch (key) {
			case 'a':
			case 'A':
				A_PRESSED = true;
				break;
			case 's':
			case 'S':
				S_PRESSED = true;
				break;
			case 'd':
			case 'D':
				D_PRESSED = true;
				break;
			case 'w':
			case 'W':
				W_PRESSED = true;
				break;
			case 'e':
			case 'E':
				E_PRESSED = true;
				break;
			case 'q':
			case 'Q':
				Q_PRESSED = true;
				break;
			case 'j':
			case 'J':
				if (boxCursor.selected) {
					boxCursor.move(-1, 0, 0);
				}
				J_PRESSED = true;
				break;
			case 'k':
			case 'K':
				if (boxCursor.selected) {
					boxCursor.move(0, 0, -1);
				}
				K_PRESSED = true;
				break;
			case 'l':
			case 'L':
				if (boxCursor.selected) {
					boxCursor.move(1, 0, 0);
				}
				L_PRESSED = true;
				break;
			case 'y':
			case 'Y':
				player.shoot();
				System.out.println("This is jump!");
				break;
			case 'i':
			case 'I':
				if (boxCursor.selected) {
					boxCursor.move(0, 0, 1);
				}
				I_PRESSED = true;
				break;
			case 'u':
			case 'U':
				if (boxCursor.selected) {
					boxCursor.move(0, -1, 0);
				}
				U_PRESSED = true;
				break;
			case 'o':
			case 'O':
				if (boxCursor.selected) {
					boxCursor.move(0, 1, 0);
				}
				O_PRESSED = true;
				break;
			case 'f':
			case 'F':
				boxList.add(boxCursor.x, boxCursor.y, boxCursor.z);
				break;
//			case 'h':
//			case 'H':
//				// controller.setMovePosition(posCursor.getPos());
//				if (player.getB() == boxList2) {
//					player.transport(boxList);
//				} else {
//					player.transport(boxList2);
//				}
//				break;
			case ' ':
				boxCursor.turnSelected();
				posCursor.turnSelected();
				break;
			case 'c':
			case 'C':
				cameraMode = cameraMode.next();
				break;
			//Accelerates rotation speed such that they have same angular speed.
//			case 'z':
//			case 'Z': {
//				PVector A = boxList.getAngSpeed();
//				PVector B = boxList2.getAngSpeed();
//				PVector target = PVector.add(A, B).mult(0.5f);
//				boxList.setTargetAngSpeed(target);
//				boxList2.setTargetAngSpeed(target);
//				// boxList.setAngSpeed(A.add(B).mult(0.5f));
//				// boxList2.setAngSpeed(A.add(B).mult(0.5f));
//				break;
//			}
			//Change angular speed so that the rotate along line through their origins
//			case 'x':
//			case 'X': {
//				PVector A = boxList.getOrigin();
//				PVector B = boxList2.getOrigin();
//				PVector midPoint = PVector.add(A, B).mult(0.5f);
//				PVector target = PVector.sub(midPoint, A).normalize().div(100f);
//				boxList.setTargetAngSpeed(target);
//				boxList2.setTargetAngSpeed(target);
//				// boxList.setAngSpeed(A.add(B).mult(0.5f));
//				// boxList2.setAngSpeed(A.add(B).mult(0.5f));
//				break;
//			}
			// Turns the cubes to align sides:
//			case 'v':
//			case 'V': {
//				PVector A = boxList.getOrigin();
//				PVector B = boxList2.getOrigin();
//				PVector X = A.sub(B);
//				PVector Y = X.cross(new PVector(1,0,0)).normalize();
//				boxList2.applyTorque(Y.div(100f));
////				Quaternion A = boxList.getOrientation();
////				boxList2.setTargetOrientation(A);
////				Quaternion B = boxList2.getOrientation();
////				Quaternion C = A.mult(B.conjugate());
//				// boxList.setAngSpeed(A.add(B).mult(0.5f));
//				// boxList2.setAngSpeed(A.add(B).mult(0.5f));
//				break;
//			}
//			case 'b':
//			case 'B':
//				PVector A = boxList.getOrigin();
//				PVector B = boxList2.getOrigin();
//				PVector difference = PVector.sub(A, B);
//				boxList2.applyForce(difference.div(1000f));
//				break;
			case 'n':
			case 'N':
				PVector xPos = player.getGlobalPos();
				int xFace = player.getSide();
				PVector yPos = posCursor.Pos;
				int yFace = posCursor.side;
				
				boxList.navCoord.updateStartEndPair(
						new SurfPoint(xPos, new int[] {xFace}), 
						new SurfPoint(yPos, new int[] {yFace}));
				break;
			case '1':
				ONE_PRESSED = true;
				System.out.println("One pressed, defect: " + defect + ", Zoom: " + zoom);
				break;
			case '2':
				TWO_PRESSED = true;
				System.out.println("Two pressed, defect: " + defect + ", Zoom: " + zoom);
				break;
			case '.':
				camPos = sphericalVector(phi + PI,  - theta).mult(zoom);
				System.out.println("Punktum pressed, defect: " + defect + ", Zoom: " + zoom);
				break;
			}
		}
	}

	// Updates key releases
	public void keyReleased() {
		if (key == CODED) {
			switch (keyCode) {
			case LEFT:
				LEFT_PRESSED = false;
				break;
			case DOWN:
				DOWN_PRESSED = false;
				break;
			case RIGHT:
				RIGHT_PRESSED = false;
				break;
			case UP:
				UP_PRESSED = false;
				break;
			case SHIFT:
				SHIFT_PRESSED = false;
				break;
			}
		} else {
			switch (key) {
			case 'a':
			case 'A':
				A_PRESSED = false;
				break;
			case 's':
			case 'S':
				S_PRESSED = false;
				break;
			case 'd':
			case 'D':
				D_PRESSED = false;
				break;
			case 'w':
			case 'W':
				W_PRESSED = false;
				break;
			case 'e':
			case 'E':
				E_PRESSED = false;
				break;
			case 'q':
			case 'Q':
				Q_PRESSED = false;
				break;
			case 'j':
			case 'J':
				J_PRESSED = false;
				break;
			case 'k':
			case 'K':
				K_PRESSED = false;
				break;
			case 'i':
			case 'I':
				I_PRESSED = false;
				break;
			case 'l':
			case 'L':
				L_PRESSED = false;
				break;
			case '1':
				ONE_PRESSED = false;
				break;
			case '2':
				TWO_PRESSED = false;
				break;
			}
		}
	}

}