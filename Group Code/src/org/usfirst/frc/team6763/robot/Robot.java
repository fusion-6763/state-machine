/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6763.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.SerialPort;

import java.util.ArrayList;
import java.util.List;

import org.usfirst.frc.team6763.robot.Instruction;
import org.usfirst.frc.team6763.robot.Instruction.State;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot {
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	SendableChooser<String> stationChooser = new SendableChooser<>();
	
	DifferentialDrive myRobot = new DifferentialDrive(new Spark(0), new Spark(2));
	Joystick stick = new Joystick(0);
	Joystick DriveStick = new Joystick(1);
	
	Spark elevator1 = new Spark(3);
	Spark elevator2 = new Spark(4);
	
	Spark intakeL = new Spark(7);
	Spark intakeR = new Spark(8);
	
	JoystickButton X = new JoystickButton(stick, 3);
	JoystickButton Y = new JoystickButton(stick, 4);
	JoystickButton A = new JoystickButton(stick, 1);
	
	JoystickButton bumperR = new JoystickButton(DriveStick, 6);
	
	Encoder leftEncoder = new Encoder(0, 1);
	Encoder rightEncoder = new Encoder(2, 3);
	
	Timer timer = new Timer();
	
	double o;
	double op;
	final double d = .006;
	final double DecelerationRate = .005;
	final double ScaleOutput = 0.8;
	final double SwitchOutput = 0.5;
	final double DistanceScale = 0.8;
	
	//Spark climber = new Spark(7);
	
	final static int tolerance = 2;
	AHRS navx = new AHRS(SerialPort.Port.kUSB);
	
	String data;
	
	final float ticksPerInch = 53;
	double defaultSpeed;
		
	float lastEncoderValue;
	double lastTimerValue;
	
	State state = State.STOP;
	List<Instruction> autoMode = new ArrayList<Instruction>();
	int instructionIndex = 0;
	

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_chooser.addDefault("Scale", "scale");
		m_chooser.addObject("Switch", "switch");
		SmartDashboard.putData("Auto choices", m_chooser);
		
		stationChooser.addDefault("Station 1", "station1");
		stationChooser.addObject("Station 2", "station2");
		stationChooser.addObject("Station 3", "station3");
		
		rightEncoder.setReverseDirection(true);
		leftEncoder.setReverseDirection(true);
		
		SmartDashboard.putNumber("Default Speed", 0.8);
		
		elevator1.setInverted(true);
		intakeL.setInverted(true);
		
		leftEncoder.reset();
		rightEncoder.reset();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void robotPeriodic() {
		SmartDashboard.putNumber("Gyro", navx.getYaw());
		
		//System.out.println(leftEncoder.get());
		//System.out.println("Left Encoder: " + leftEncoder.get());
		//System.out.println("Right Encoder: " + rightEncoder.get());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void autonomousInit()
	{
		System.out.println("Auto selected: " + m_chooser.getSelected());
		
		o = 0.4;
		instructionIndex = 0;
		
		navx.reset();
		leftEncoder.reset();
		rightEncoder.reset();
		
		data = DriverStation.getInstance().getGameSpecificMessage();
		
		defaultSpeed = SmartDashboard.getNumber("Default Speed", 0.8);
		
		timer.stop();
		timer.reset();
						
		/*
		 * Set the Autonomous Mode based on field information and supplied driver information.
		 */
		
		// Determine if the driver selected scale or switch.
		if (m_chooser.getSelected().equals("scale")) {
			
			// Determine if the scale is on the right or left.
			if (data.charAt(1) == 'R') {
				
				// Determine which alliance station the robot is in.
				switch (stationChooser.getSelected()) {
					case "station1":
						autoMode = AutonomousMode.scaleRightPositionLeft;
						break;
					case "station2":
						autoMode = AutonomousMode.scaleRightPositionCenter;
						break;
					default:
						autoMode = AutonomousMode.scaleRightPositionRight;
						break;
				}
			} else {
				
				// Determine which alliance station the robot is in.
				switch (stationChooser.getSelected()) {
					case "station1":
						autoMode = AutonomousMode.scaleLeftPositionLeft;
						System.out.println("Picked Auto Mode");
						break;
					case "station2":
						autoMode = AutonomousMode.scaleLeftPositionCenter;
						break;
					default:
						autoMode = AutonomousMode.scaleLeftPositionRight;
						break;
				}
			} 
		} else if (m_chooser.getSelected().equals("switch")) {
			
			// Determine if the switch is on the left or right.
			if (data.charAt(0) == 'R') {
				
				// Determine which alliance station the robot is in.
				switch (stationChooser.getSelected()) {
					case "station1":
						autoMode = AutonomousMode.switchRightPositionLeft;
						break;
					case "station2":
						autoMode = AutonomousMode.switchRightPositionCenter;
						break;
					default:
						autoMode = AutonomousMode.switchRightPositionRight;
						break;
				}
			}
			else {
				switch (stationChooser.getSelected()) {
					case "station1":
						autoMode = AutonomousMode.switchLeftPositionLeft;
						break;
					case "station3":
						autoMode = AutonomousMode.switchLeftPositionCenter;
						break;
					default:
						autoMode = AutonomousMode.switchLeftPositionRight;
						break;
				}
			}
		} else {
			// Set to default mode (STOP)
			autoMode = AutonomousMode.stop;
		}
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		Instruction instruction = new Instruction(State.STOP, 0, 0);
		if(instructionIndex < autoMode.size()) {
			instruction = autoMode.get(instructionIndex);
		}
		
		final float currentAngle = navx.getYaw();
		
		switch (instruction.getState()) {
		
			case DRIVE_FORWARD:
				System.out.println("Robot Driving Forward: " + getDistanceTraveled());
				if (getDistanceTraveled() < instruction.getLimit()) {
					if(o < defaultSpeed && leftEncoder.get() / ticksPerInch < DistanceScale * instruction.getLimit()) {
						o+=d;
					} else if(leftEncoder.get() / ticksPerInch > DistanceScale * instruction.getLimit() && o > 0.6){
						o-=d;
					}
					accurateDrive(navx.getYaw(), o, instruction.getTargetAngle(), tolerance);
				}
				else {
					o = 0.4;
					leftEncoder.reset();
					rightEncoder.reset();
					instructionIndex++;
				}
				break;
				
			case DRIVE_BACKWARD:
				if (getDistanceTraveled() < instruction.getLimit()) {
					if(o < defaultSpeed && leftEncoder.get() / ticksPerInch < DistanceScale * instruction.getLimit()) {
						o+=d;
					}
					else if(leftEncoder.get() / ticksPerInch > DistanceScale * instruction.getLimit() && o > 0.6) {
						o-=d;
					}
					accurateDrive(navx.getYaw(), -o, instruction.getTargetAngle(), tolerance);
				}
				else {
					o = 0.4;
					leftEncoder.reset();
					rightEncoder.reset();
					instructionIndex++;
				}
				break;
				
			case TURN_RIGHT:
				System.out.println("Robot Turning Right");
				if(o <= 0.4) {
					o = defaultSpeed;
				}
				if (currentAngle < instruction.getTargetAngle() - tolerance ||
				    currentAngle > instruction.getTargetAngle() + tolerance) {
					if(o > 0.55) {
						o -= DecelerationRate;
					}
					myRobot.tankDrive(o, -o);
				} else {
					leftEncoder.reset();
					rightEncoder.reset();
					instructionIndex++;
				}
				break;
				
			case TURN_LEFT:
				if(o <= 0.4) {
					o = defaultSpeed;
				}
				if (currentAngle < instruction.getTargetAngle() - tolerance ||
				    currentAngle > instruction.getTargetAngle() + tolerance) {
					if(o > 0.55) {
						o -= DecelerationRate;
					}
					myRobot.tankDrive(-o, o);
				} else {
					leftEncoder.reset();
					rightEncoder.reset();
					instructionIndex++;
				}
				break;
				
			case RAISE_LIFT:
				if (Double.compare(timer.get(), 0) == 0) {
					timer.start();
				}
				System.out.println("Time: "+timer.get());
				if(timer.get() < instruction.getLimit()) {
					elevator1.set(1.0);
					elevator2.set(1.0);
					myRobot.tankDrive(0.0, 0.0);
				}
				else {
					timer.stop();
					timer.reset();
					elevator1.set(0.0);
					elevator2.set(0.0);
					instructionIndex++;
				}
				break;
			case EJECT_CUBE:
				if (Double.compare(timer.get(), 0) == 0) {
					timer.start();
				}
				if(timer.get() < instruction.getLimit()) {
					intakeL.set((m_chooser.getSelected().equals("switch")) ? SwitchOutput : ScaleOutput);
					intakeR.set((m_chooser.getSelected().equals("switch")) ? SwitchOutput : ScaleOutput);
				}
				else {
					timer.stop();
					timer.reset();
					intakeL.set(-0.2);
					intakeR.set(-0.2);
					instructionIndex++;
				}
				break;
			case WAIT:
				if (Double.compare(timer.get(), 0) == 0) {
					timer.start();
				}
				if (timer.get() < instruction.getLimit()) {
					try {
						timer.wait(10);
					} catch (InterruptedException | IllegalMonitorStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					timer.stop();
					timer.reset();
					instructionIndex++;
				}
				break;
			case STOP: // Intentional fall-through
			default:
				myRobot.tankDrive(0, 0);
				elevator1.set(0.0);
				elevator2.set(0.0);
				break;
		}
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
			if(bumperR.get()) {
				myRobot.arcadeDrive(-(DriveStick.getY()*0.6), DriveStick.getX()*0.6);
			}
			else {
				myRobot.arcadeDrive(-DriveStick.getY(), DriveStick.getX()*0.8);
			}
			
			//myRobot.tankDrive(-stick.getY(), -stick.getRawAxis(5));
			
			//climber.set(-stick.getRawAxis(3));
			
			elevator1.set(-stick.getRawAxis(5) / 1.5);
			elevator2.set(-stick.getRawAxis(5) / 1.5);
			
			//System.out.println("Elevator Encoder: "+elevatorEncoder.get());

			if(X.get()) {
				intakeL.set(-0.8);
				intakeR.set(-0.8);
			}
			else if(Y.get()) {
				intakeL.set(0.8);
				intakeR.set(0.8);
			}
			else if(A.get()) {
				intakeL.set(-0.8);
				intakeR.set(0.8);
			}
			else {
				intakeL.set(-0.2);
				intakeR.set(-0.2);
			}
		}
	

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
	
	/**
	 * Drives the robot to the target angle with the given tolerance.
	 * If the robot is driving straight, it keeps the robot 
	 * 
	 * @param gyroValue
	 * @param speed
	 * @param targetAngle
	 * @param tolerence
	 */
	private void accurateDrive(final float gyroValue, final double speed, final double targetAngle, final int tolerence) {
		System.out.println("Speed: "+speed);
		if(gyroValue < targetAngle - tolerence) {
			System.out.println("Too far left");
			myRobot.tankDrive(speed, speed / 4);
		}
		else if(gyroValue > targetAngle + tolerence) {
			System.out.println("Too far right");
			myRobot.tankDrive(speed / 4, speed);
		}
		else {
			System.out.println("Good");
			myRobot.tankDrive(speed, speed);
		}
	}
	
	/**
	 * Averages the encoder values to more accurately measure distance.
	 * 
	 * @return The distance traveled in inches.
	 */
	private float getDistanceTraveled() {
		final float leftDistance = leftEncoder.get() / ticksPerInch;
		final float rightDistance = rightEncoder.get() / ticksPerInch;
		//System.out.println("Left Encoder: "+leftEncoder.get());
		//System.out.println("Right Encoder: "+rightEncoder.get());
		return (leftDistance + rightDistance) / 2f;
	}
}
