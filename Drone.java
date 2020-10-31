package dsa_assignment2;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import jdk.nashorn.api.tree.ForInLoopTree;

/**
 * A Drone class to simulate the decisions and information collected by a drone
 * on exploring an underground maze.
 * 
 */
public class Drone implements DroneInterface
{
	private static final Logger logger     = Logger.getLogger(Drone.class);
	
	public String getStudentID()
	{
		//change this return value to return your student id number
		return "2045143";
	}

	public String getStudentName()
	{
		//change this return value to return your name
		return "Philip Broadley";
	}

	/**
	 * The Maze that the Drone is in
	 */
	private Maze                maze;

	/**
	 * The stack containing the portals to backtrack through when all other
	 * doorways of the current chamber have been explored (see assignment
	 * handout). Note that in Java, the standard collection class for both
	 * Stacks and Queues are Deques
	 */
	private Deque<Portal>       visitStack = new ArrayDeque<>();

	/**
	 * The set of portals that have been explored so far.
	 */
	private Set<Portal>         visited    = new HashSet<>();

	/**
	 * The Queue that contains the sequence of portals that the Drone has
	 * followed from the start
	 */
	private Deque<Portal>       visitQueue = new ArrayDeque<>();

	/**
	 * This constructor should never be used. It is private to make it
	 * uncallable by any other class and has the assert(false) to ensure that if
	 * it is ever called it will throw an exception.
	 */
	@SuppressWarnings("unused")
	private Drone()
	{
		assert (false);
	}

	/**
	 * Create a new Drone object and place it in chamber 0 of the given Maze
	 * 
	 * @param maze
	 *            the maze to put the Drone in.
	 */
	public Drone(Maze maze)
	{
		this.maze = maze;
	}

	/* 
	 * @see dsa_assignment2.DroneInterface#searchStep()
	 */
	@Override
	public Portal searchStep(){

	Portal entry = portalChooser();
	Portal exit;
	if(entry != null) {
		variableUpdater(entry);
		exit = maze.traverse(entry.getDoor());
		visitStack.push(exit);
		
		return	variableUpdater(exit);
	}
	else {
		if (visitStack.isEmpty()){
			return null;
		}
	 	Portal lastVisited = visitStack.pop();
		variableUpdater(lastVisited);
		return variableUpdater(maze.traverse(lastVisited.getDoor()));

		}
	}

	public Portal variableUpdater(Portal portal) {
		visited.add(portal);
		visitQueue.add(portal);
		return portal;
	}
	
	public Portal portalChooser() {
		boolean checkPortal;
		for (int i = 0; i < maze.getNumDoors(); i++) {
			checkPortal = true;
			for (Portal portal : visited) {
				if (portal.getDoor()==i && portal.getChamber()==maze.getCurrentChamber()) {
					checkPortal = false;
				}
			}
			if (checkPortal) {
				return new Portal(maze.getCurrentChamber(),i);
			}
		}
		return null;
	}
	
	
	@Override
	public Portal[] getVisitOrder() {
		Portal[] visitOrder = new Portal[visitQueue.size()];
		for (int i = 0; i < visitOrder.length; i++) {
			visitOrder[i] = visitQueue.remove();
		}
		for (int i = 0; i < visitOrder.length; i++) {
			visitQueue.add(new Portal(visitOrder[i].getChamber(),visitOrder[i].getDoor()));
		}
		return visitOrder;
	}

	/*
	 * @see dsa_assignment2.DroneInterface#findPathBack()
	 */
	
	public Portal[] stackToArray (Deque<Portal> newStack) {
		Portal[] pathBack = new Portal[newStack.size()];
		for (int i = 0; i < pathBack.length; i++) {
			pathBack[i] = visitStack.pop();
		}
		for (int i = 1; i < pathBack.length+1; i++) {
			newStack.push(new Portal(pathBack[pathBack.length-i].getChamber(),pathBack[pathBack.length-i].getDoor()));
		}
		return pathBack;
	}
	
	@Override
	public Portal[] findPathBack(){
		Portal[] portalArray = stackToArray(visitStack);
		
		for (int i = 1; i < portalArray.length; i++) {
			if (portalArray[i].getChamber() == portalArray[0].getChamber()) {
				for (int j = 0; j < i; j++) {
					visitStack.remove(portalArray[j]);
				}
			}
		}
		
		return stackToArray(visitStack);
	}

}
