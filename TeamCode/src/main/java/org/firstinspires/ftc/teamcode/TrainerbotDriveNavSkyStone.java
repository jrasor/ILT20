/* Copyright (c) 2018 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.ArrayList;
import java.util.List;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;

/**
 *
 * <p>
 * This 2019-2020 OpMode illustrates the basics of using the Vuforia localizer to determine
 * positioning and orientation of robot on the SKYSTONE FTC field. The code is structured as a
 * LinearOpMode. No hardware is assumed; it can be run on a nullbot.
 * <p>
 * Eight perimeter targets are distributed evenly around the four perimeter walls. Four Bridge
 * targets are located on the bridge uprights. Refer to the Field Setup manual for more specific
 * location details.
 * <p>
 * When images are located, Vuforia is able to determine the position and orientation of the
 * image relative to the camera.  This sample code then combines that information with a
 * knowledge of where the target images are on the field, to determine the location of the camera.
 * A final calculation then uses the location of the camera on the robot to determine the
 * robot's location and orientation on the Field. *
 * <p>
 * Drive a TrainerbotV2 around, looking at SkyStone target images with the Robot Controller camera.
 * Those images allow calculation of the robot's location on the Field.
 *
 * <p>
 * When images are located, Vuforia is able to determine the position and orientation of the
 * image relative to the camera.  This opmode combines that information with target image
 * locations are on the Field, to determine the location of the camera. <p>
 * This opmode assumes an FTC square Field configuration where the Red and Blue Alliance
 * stations are on opposite walls of each other. In future seasons, Alliance stations may be near
 * each other, with only a corner separating them.
 * <p>
 * From the Audience perspective, the Red Alliance station is on the right and the Blue Alliance
 * Station is on the left.
 *
 *

 *
 * @see VuforiaLocalizer
 * @see VuforiaTrackableDefaultListener
 * see  ftc_app/doc/tutorial/FTC_FieldCoordinateSystemDefinition.pdf
 * <p>
 * IMPORTANT: In order to use this OpMode, you need to obtain your own Vuforia license key as
 * explained in the TrainerbotV2 class.
 *
 * Version history
 * 1.0		Summer 2019 JMR: developed for CSEE331 Robotics course on their Trainerbots.
 * 1.1		9/10/19 JMR Added support for Lookeebot and Tablebot. Untested.
 * 1.2		9/11/19 JMR Switched Vuforia and Tensorflow assets over to SkyStone versions.
 */

@TeleOp(name = "TrainerbotV2 Drive Navigate Paddle SkyStone", group = "TrainerbotV2")
//@Disabled
public class TrainerbotDriveNavSkyStone extends LinearOpMode {
	TrainerbotV2 robot = new TrainerbotV2(this);

	// Get Field dimensions.
	final float mmPerInch = TrainerbotV2.mmPerInch;
	final float mmFTCFieldWidth = TrainerbotV2.mmFTCFieldWidth;

	// FRONT is the camera on the phone's screen side.
	private static final VuforiaLocalizer.CameraDirection CAMERA_CHOICE = BACK;
	private static final boolean PHONE_IS_PORTRAIT = false  ;

	// Since ImageTarget trackables use mm to specifiy their dimensions, we must use mm for all the physical dimension.
	// We will define some constants and conversions here
	private static final float mmTargetHeight   = (6) * TrainerbotV2.mmPerInch;          // the height of the center of the target image above the floor

	// Constant for Stone Target
	private static final float stoneZ = 2.00f * TrainerbotV2.mmPerInch;

	// Constants for the center support targets
	private static final float bridgeZ = 6.42f * TrainerbotV2.mmPerInch;
	private static final float bridgeY = 23 * TrainerbotV2.mmPerInch;
	private static final float bridgeX = 5.18f * TrainerbotV2.mmPerInch;
	private static final float bridgeRotY = 59;                                 // Units are degrees
	private static final float bridgeRotZ = 180;

	// Constants for perimeter targets
	private static final float halfField = 72 * TrainerbotV2.mmPerInch;
	private static final float quadField  = 36 * TrainerbotV2.mmPerInch;

	// Class Members
	private OpenGLMatrix lastLocation = null;
	private VuforiaLocalizer vuforia = null;
	private boolean targetVisible = false;
	private float phoneXRotate    = 0;
	private float phoneYRotate    = 0;
	private float phoneZRotate    = 0;
	@Override
	public void runOpMode() {
		robot.initHardware(hardwareMap);
		telemetry.addData("Hardware", " mapped");
		telemetry.update();
		int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
				"cameraMonitorViewId",
				"id", hardwareMap.appContext.getPackageName());

		/*
		 * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine. We
		 * can pass Vuforia the handle to a camera preview resource (on the RC phone). If no camera
		 * monitor is desired, use the parameterless constructor instead (commented out below).
		 */
		VuforiaLocalizer vuforia;
		VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
		// VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
		parameters.vuforiaLicenseKey = GenericFTCRobot.VUFORIA_KEY;
		parameters.cameraDirection = CAMERA_CHOICE;
		// Prevent spurious reporting on loss of tracking.
		parameters.useExtendedTracking = false;
		vuforia = ClassFactory.getInstance().createVuforia(parameters);

		// Load the data sets that for the trackable objects. These particular data
		// sets are stored in the 'assets' part of our application.
		VuforiaTrackables targetsSkyStone =
				vuforia.loadTrackablesFromAsset("Skystone");
		VuforiaTrackable stoneTarget = targetsSkyStone.get(0);
		stoneTarget.setName("Stone Target");
		VuforiaTrackable blueRearBridge = targetsSkyStone.get(1);
		blueRearBridge.setName("Blue Rear Bridge");
		VuforiaTrackable redRearBridge = targetsSkyStone.get(2);
		redRearBridge.setName("Red Rear Bridge");
		VuforiaTrackable redFrontBridge = targetsSkyStone.get(3);
		redFrontBridge.setName("Red Front Bridge");
		VuforiaTrackable blueFrontBridge = targetsSkyStone.get(4);
		blueFrontBridge.setName("Blue Front Bridge");
		VuforiaTrackable red1 = targetsSkyStone.get(5);
		red1.setName("Red Perimeter 1");
		VuforiaTrackable red2 = targetsSkyStone.get(6);
		red2.setName("Red Perimeter 2");
		VuforiaTrackable front1 = targetsSkyStone.get(7);
		front1.setName("Front Perimeter 1");
		VuforiaTrackable front2 = targetsSkyStone.get(8);
		front2.setName("Front Perimeter 2");
		VuforiaTrackable blue1 = targetsSkyStone.get(9);
		blue1.setName("Blue Perimeter 1");
		VuforiaTrackable blue2 = targetsSkyStone.get(10);
		blue2.setName("Blue Perimeter 2");
		VuforiaTrackable rear1 = targetsSkyStone.get(11);
		rear1.setName("Rear Perimeter 1");
		VuforiaTrackable rear2 = targetsSkyStone.get(12);
		rear2.setName("Rear Perimeter 2");

		// Gather all the trackable objects into a conveniently iterable collection.
		List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
		allTrackables.addAll(targetsSkyStone);

		/**
		 * In order for localization to work, we need to tell the system where each target is on the field, and
		 * where the phone resides on the robot.  These specifications are in the form of <em>transformation matrices.</em>
		 * Transformation matrices are a central, important concept in the math here involved in localization.
		 * See <a href="https://en.wikipedia.org/wiki/Transformation_matrix">Transformation Matrix</a>
		 * for detailed information. Commonly, you'll encounter transformation matrices as instances
		 * of the {@link OpenGLMatrix} class.
		 *
		 * If you are standing in the Red Alliance Station looking towards the center of the field,
		 *     - The X axis runs from your left to the right. (positive from the center to the right)
		 *     - The Y axis runs from the Red Alliance Station towards the other side of the Field
		 *       where the Blue Alliance Station is. (Positive is from the center, towards the BlueAlliance station)
		 *     - The Z axis runs from the floor, upwards towards the ceiling.  (Positive is above the floor)
		 *
		 * Before being transformed, each target image is conceptually located at the origin of the field's
		 *  coordinate system (the center of the field), facing up.
		 */

		// Set the position of the Stone Target.  Since it's not fixed in position, assume it's at the field origin.
		// Rotated it to to face forward, and raised it to sit on the ground correctly.
		// This can be used for generic target-centric approach algorithms
		stoneTarget.setLocation(OpenGLMatrix
				.translation(0, 0, stoneZ)
				.multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90)));

		//Set the position of the bridge support targets with relation to origin (center of field)
		blueFrontBridge.setLocation(OpenGLMatrix
				.translation(-bridgeX, bridgeY, bridgeZ)
				.multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0, bridgeRotY, bridgeRotZ)));

		blueRearBridge.setLocation(OpenGLMatrix
				.translation(-bridgeX, bridgeY, bridgeZ)
				.multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0, -bridgeRotY, bridgeRotZ)));

		redFrontBridge.setLocation(OpenGLMatrix
				.translation(-bridgeX, -bridgeY, bridgeZ)
				.multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0, -bridgeRotY, 0)));

		redRearBridge.setLocation(OpenGLMatrix
				.translation(bridgeX, -bridgeY, bridgeZ)
				.multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0, bridgeRotY, 0)));

		//Set the position of the perimeter targets with relation to origin (center of field)
		red1.setLocation(OpenGLMatrix
				.translation(quadField, -halfField, mmTargetHeight)
				.multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180)));

		red2.setLocation(OpenGLMatrix
				.translation(-quadField, -halfField, mmTargetHeight)
				.multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180)));

		front1.setLocation(OpenGLMatrix
				.translation(-halfField, -quadField, mmTargetHeight)
				.multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , 90)));

		front2.setLocation(OpenGLMatrix
				.translation(-halfField, quadField, mmTargetHeight)
				.multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 90)));

		blue1.setLocation(OpenGLMatrix
				.translation(-quadField, halfField, mmTargetHeight)
				.multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0)));

		blue2.setLocation(OpenGLMatrix
				.translation(quadField, halfField, mmTargetHeight)
				.multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0)));

		rear1.setLocation(OpenGLMatrix
				.translation(halfField, quadField, mmTargetHeight)
				.multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , -90)));

		rear2.setLocation(OpenGLMatrix
				.translation(halfField, -quadField, mmTargetHeight)
				.multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90)));


		//
		// Create a transformation matrix describing where the phone is on the robot.
		//
		// NOTE !!!!  It's very important that you turn OFF your phone's Auto-Screen-Rotation option.
		// Lock it into Portrait for these numbers to work.
		//
		// Info:  The coordinate frame for the robot looks the same as the field.
		// The robot's "forward" direction is facing out along X axis, with the LEFT side facing out along the Y axis.
		// Z is UP on the robot.  This equates to a bearing angle of Zero degrees.
		//
		// The phone starts out lying flat, with the screen facing Up and with the physical top of the phone
		// pointing to the LEFT side of the Robot.
		// The two examples below assume that the camera is facing forward out the front of the robot.

		// We need to rotate the camera around its long axis to bring the correct camera forward.
		if (CAMERA_CHOICE == BACK) {
			phoneYRotate = -90;
		} else {
			phoneYRotate = 90;
		}

		// Rotate the phone vertical about the X axis if it's in portrait mode
		if (PHONE_IS_PORTRAIT) {
			phoneXRotate = 90 ;
		}

		// Next, translate the camera lens to where it is on the robot.
		// In this example, it is centered (left to right), but forward of the middle of the robot, and above ground level.
		final float CAMERA_FORWARD_DISPLACEMENT  = 4.0f * mmPerInch;   // eg: Camera is 4 Inches in front of robot center
		final float CAMERA_VERTICAL_DISPLACEMENT = 8.0f * mmPerInch;   // eg: Camera is 8 Inches above ground
		final float CAMERA_LEFT_DISPLACEMENT     = 0;     // eg: Camera is ON the robot's center line

		OpenGLMatrix robotFromCamera = OpenGLMatrix
				.translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
				.multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES, phoneYRotate, phoneZRotate, phoneYRotate));

		/**  Let all the trackable listeners know where the phone is.  */
		for (VuforiaTrackable trackable : allTrackables) {
			((VuforiaTrackableDefaultListener) trackable.getListener()).setPhoneInformation(robotFromCamera, parameters.cameraDirection);
		}

		// WARNING:
		// In this sample, we do not wait for PLAY to be pressed.  Target Tracking is started immediately when INIT is pressed.
		// This sequence is used to enable the new remote DS Camera Preview feature to be used with this sample.
		// CONSEQUENTLY do not put any driving commands in this loop.
		// To restore the normal opmode structure, just un-comment the following line:

		// waitForStart();

		// Note: To use the remote camera preview:
		// AFTER you hit Init on the Driver Station, use the "options menu" to select "Camera Stream"
		// Tap the preview window to receive a fresh image.

		targetsSkyStone.activate();
		while (!isStopRequested()) {
			double left;
			double right;

			robot.justDrive();


			// check all the trackable target to see which one (if any) is visible.
			targetVisible = false;
			for (VuforiaTrackable trackable : allTrackables) {
				if (((VuforiaTrackableDefaultListener) trackable.getListener()).isVisible()) {
					telemetry.addData("Visible Target", trackable.getName());
					targetVisible = true;

					// getUpdatedRobotLocation() will return null if no new information is available since
					// the last time that call was made, or if the trackable is not currently visible.
					OpenGLMatrix robotLocationTransform =
							((VuforiaTrackableDefaultListener) trackable.getListener()).getUpdatedRobotLocation();
					if (robotLocationTransform != null) {
						lastLocation = robotLocationTransform;
					}
					break;
				}
			}
			reportLocation();
		}
	}

	private void reportLocation () {
		// Provide feedback as to where the robot is located (if we know).
		if (targetVisible) {
			// express position (translation) of robot in inches.
			VectorF translation = lastLocation.getTranslation();
			telemetry.addData("Pos (in)",
					"      X % 6.1f           Y % 6.1f             Z % 6.1f",
					translation.get(0) / mmPerInch, translation.get(1) / mmPerInch,
					translation.get(2) / mmPerInch);

			// express the rotation of the robot in degrees.
			Orientation rotation = Orientation.getOrientation(
					lastLocation, EXTRINSIC, XYZ, DEGREES);
			telemetry.addData("Rotation",
					" Roll %6.0f°,   Pitch %6.0f°,   Heading %6.0f°",
					rotation.firstAngle, rotation.secondAngle, rotation.thirdAngle);
		} else {
			telemetry.addData("I see target", "nothing.");
		}
		telemetry.update();
	}
}
