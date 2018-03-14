/**
 * Interface to hold autonomous mode instructions.
 */

package org.usfirst.frc.team6763.robot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.usfirst.frc.team6763.robot.Instruction;
import org.usfirst.frc.team6763.robot.Instruction.State;

public interface AutonomousMode {
	// state, distance, angle
	List<Instruction> switchLeftPositionLeft = new ArrayList<Instruction>(Arrays.asList(
			new Instruction(State.DRIVE_FORWARD, 36, 0),
			new Instruction(State.TURN_RIGHT, 0, 90)));
	
	List<Instruction> switchLeftPositionCenter = new ArrayList<Instruction>(Arrays.asList(
			new Instruction(State.DRIVE_FORWARD, 12, 45)));
	
	List<Instruction> switchLeftPositionRight = new ArrayList<Instruction>(Arrays.asList(
			new Instruction(State.DRIVE_FORWARD, 36, 0),
			new Instruction(State.TURN_LEFT, 0, -90)));
	
	List<Instruction> switchRightPositionLeft = new ArrayList<Instruction>(Arrays.asList(
			new Instruction(State.DRIVE_FORWARD, 36, 0)));
	
	List<Instruction> switchRightPositionCenter = new ArrayList<Instruction>(Arrays.asList(
			new Instruction(State.DRIVE_FORWARD, 36, 0)));
	
	List<Instruction> switchRightPositionRight = new ArrayList<Instruction>(Arrays.asList(
			new Instruction(State.DRIVE_FORWARD, 36, 0)));
	
	
	
	List<Instruction> scaleLeftPositionLeft = new ArrayList<Instruction>(Arrays.asList(
			new Instruction(State.DRIVE_FORWARD, 235, 0),
			new Instruction(State.TURN_RIGHT, 0, 15)));
	
	List<Instruction> scaleLeftPositionCenter = new ArrayList<Instruction>(Arrays.asList(
			new Instruction(State.DRIVE_FORWARD, 36, 0)));
	
	
	
	
	
}
