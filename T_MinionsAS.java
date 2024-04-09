/*
Copyright 2021 FIRST Tech Challenge Team FTC

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * Remove a @Disabled the on the next line or two (if present) to add this opmode to the Driver Station OpMode list,
 * or add a @Disabled annotation to prevent this OpMode from being added to the Driver Station
 */
@TeleOp(name = "T_MinionsAS", group = "AAA")
//@Disabled
public class T_MinionsAS extends OpMode {
    /* Declare OpMode members. */
    
    private FreezeRay4BarV1AS freezeray = new FreezeRay4BarV1AS();
    private PiranhaDogV4AS piranhadog = new PiranhaDogV4AS();
    private AirshipChassisV4AS airship = new AirshipChassisV4AS();   // Use Omni-Directional drive system
    private RocketHangV1AS rocket = new RocketHangV1AS();
    private PiranhaTailAS piranhatail = new PiranhaTailAS();
    private LauncherV1AS launcher = new LauncherV1AS();

    @Override
    public void init() {
        freezeray.initialize(this);
        piranhadog.initialize(this);
        
        airship.initialize(this, airship.CHASSIS_SWERVE, airship.CHASSIS_LEFT_FWD,"navx",
                  //airship.USE_QC_LIB,
                  airship.USE_KL_LIB,
                  "mtrLeftFront","mtrLeftBack","mtrRightFront","mtrRightBack",
                  null, null,//odoHoriz,odoVert
                  "srvoLeftFront","srvoLeftBack","srvoRightFront","srvoRightBack",
                  //"dsLeft","dsRight","dsCenter",
                  airship.OPT_AUTOCORRECT_DIR_OFF, //options
                  8d,4d,3d,//fastmomentum adj, slowmomentum adj, tolerance deg
                  1d,.3d,.75d,.2d,.005, //fastpwr, slowprm,rotpwr scale, min rot pwer, rotP
                  2000,.3d,.2d,1000,.3,500, //slow down dist,min dirprr, dirP, brake dist,brake pwr, lBrakeTime
                  50,500);
        airship.resetTeleop(true);
        rocket.initialize(this);
        launcher.initialize(this);
        piranhatail.initialize(this,piranhatail.TAIL_INIT_TELEOP);
        telemetry.update();
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {

    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        freezeray.operate(this);
        piranhadog.operate(this);
        airship.operate(this,gamepad1);
        rocket.operate(this);
        launcher.operate(this);
        piranhatail.operate(this);
        telemetry.update();

    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        freezeray.shutdown(this);
        piranhadog.shutdown(this);
        airship.shutdown(this);
        rocket.shutdown(this);
        launcher.shutdown(this);
    }
}
