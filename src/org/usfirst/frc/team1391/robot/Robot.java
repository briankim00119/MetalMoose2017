
package org.usfirst.frc.team1391.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team1391.robot.commands.GyroRight;
import org.usfirst.frc.team1391.robot.commands.GyroStop;
import org.usfirst.frc.team1391.robot.commands.GyroVision;
import org.usfirst.frc.team1391.robot.commands.MecanumDrive;
import org.usfirst.frc.team1391.robot.subsystems.DriveBase;
import org.usfirst.frc.team1391.robot.subsystems.Gear;
import org.usfirst.frc.team1391.robot.subsystems.Shooter;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	public static final DriveBase driveBase = new DriveBase();
	public static final Gear gear = new Gear();
	
	public static final Shooter shooter = new Shooter();
	public static final GyroRight gyroRight = new GyroRight();
	public static final GyroVision gyroVision = new GyroVision();
	public static final GyroStop gyroStop = new GyroStop();
	public static final MecanumDrive mecanumDrive = new MecanumDrive();
	public static OI oi;

	Command autonomousCommand;
	SendableChooser<Command> chooser = new SendableChooser<>();

	public static boolean visionFlag = true;
	public static boolean visionTarget = false; //false = gear; true = target;  
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		oi = new OI();
		// chooser.addObject("My Auto", new MyAutoCommand());
		SmartDashboard.putData("Auto mode", chooser);
		
		CameraServer.getInstance().addAxisCamera("10.13.91.3");
		CameraServer.getInstance().addServer("10.13.91.3");
		
		SmartDashboard.putBoolean("visionTarget", visionTarget);
		
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {
		autonomousCommand = chooser.getSelected();

		/*
		 * String autoSelected = SmartDashboard.getString("Auto Selector",
		 * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
		 * = new MyAutoCommand(); break; case "Default Auto": default:
		 * autonomousCommand = new ExampleCommand(); break; }
		 */

		// schedule the autonomous command (example)
		if (autonomousCommand != null)
			autonomousCommand.start();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {

		if (autonomousCommand != null)
			autonomousCommand.cancel();
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {

		if (OI.driverB.get() && visionFlag == false) {
			gyroVision.execute();
			System.out.println(202);
		}else if(OI.driverY.get()) {
			gyroStop.execute();
			System.out.println(101);
		}else if(!driveBase.getPIDController().isEnabled() && !Robot.gear.active){
			mecanumDrive.execute();
			System.out.println(303);
		}
		
		if(!OI.driverB.get() && visionFlag == true){
			visionFlag = false;
		}
		
		if(OI.driverJoyL.get()){
			driveBase.setToLowGear();
		}else if(OI.driverJoyR.get()){
			driveBase.setToHighGear();
		}
		
		driveBase.getAngle();
		
		if(OI.driverLB.get()){
			gear.open();
		}else if(OI.driverRB.get()){
			gear.close();
		}else{
			gear.stop();
		}
		
		if(OI.driverX.get()){
			Robot.gear.active = true;
		}else if(OI.driverA.get()){
			Robot.gear.sequenceEject();
		}
		
		Robot.gear.sequence();
		
		if(OI.driverLT.get()){ //gear
			visionTarget = false;
			SmartDashboard.putBoolean("visionTarget", visionTarget);
		}else if(OI.driverRT.get()){ //vision target
			visionTarget = true;
			SmartDashboard.putBoolean("visionTarget", visionTarget);
		}

	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		LiveWindow.run();
	}

}
