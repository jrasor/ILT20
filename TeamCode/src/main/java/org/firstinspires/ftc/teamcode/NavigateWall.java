/* Copyright (c) 2017 FIRST. All rights reserved.
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

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Autonomous opmode for any Alliance, any starting position in 2019-2020FTC "SkyStone" game.
 * Start robot with rear wheels touching Wall, and front wheels on the seam between the tile
 * next to the Alliance specific Bridge, and the next tile away from the bridge. Be careful
 * not to allow the front wheels to drop in a slot between the Field tiles and the Wall.
 *
 * Those front wheels will move the 24" to the bridge, then another 8" to get the robot fully
 * under the Bridge. 5 points. This also leaves space for the Allied robot to Navigate near
 * the Neutral Bridge.
 *
 * It runs on a TrainerbotV2.
 */

@Autonomous(name="Navigate along Wall", group="ILT Spring 20")
//@Disabled
public class NavigateWall extends LinearOpMode {

    TrainerbotV2 robot   = new TrainerbotV2(this);
    private static final double TURN_SPEED  = 0.20; // Slow: less wheel slippage
    private static final double DRIVE_SPEED  = 0.30; // Slow: less wheel slippage

    @Override
    public void runOpMode() {
        robot.initHardware(hardwareMap);
        robot.setDriveRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.setDriveRunMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Wait for the game to start (driver presses PLAY).
        waitForStart();
        robot.driveStraight(DRIVE_SPEED, 32.0);
    }
}
