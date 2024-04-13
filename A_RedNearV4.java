package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.drive.SampleSwerveDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;


@Autonomous(name="RedNearV4",group = "drive")
public class A_RedNearV4 extends LinearOpMode {

    private SampleSwerveDrive drive;
    private Goggles2V3AS goggles2 = new Goggles2V3AS();
    private PiranhaDogV4AS piranhadog = new PiranhaDogV4AS();
    private PiranhaTailAS piranhatail = new PiranhaTailAS();
    private FreezeRay4BarV1AS freezeray = new FreezeRay4BarV1AS();
    private String gstrClassName=this.getClass().getSimpleName();

    @Override
    public void runOpMode() {
        int nTagToFind=-1;
        int nPropPos=0;
        int chosenTraj;

        drive = new SampleSwerveDrive(hardwareMap);

        goggles2.initialize(this,goggles2.RED_CAM);//Red is 'Webcam 1

        piranhadog.initialize(this);
        freezeray.initialize(this);
        piranhatail.initialize(this,piranhatail.TAIL_INIT_AUTON);

        //establishes starting coordinates on the field
        Pose2d startPose = new Pose2d(11.25, -62.5, Math.toRadians(0));

        drive.setPoseEstimate(startPose);

        TrajectorySequence leftTraj1 = drive.trajectorySequenceBuilder(startPose)
                //go to prop
                .strafeTo(new Vector2d(18.75, -58.5))
                .lineToLinearHeading(new Pose2d(11, -40, Math.toRadians(175)))
                //.strafeTo(new Vector2d(8, 42))
                .build();
        TrajectorySequence leftTraj2 = drive.trajectorySequenceBuilder(leftTraj1.end())
                //backup
                .lineToLinearHeading(new Pose2d(14, -40, Math.toRadians(175)))
                //raise 4bar
                .addTemporalMarker(() -> {
                    freezeray.autonRaiseWeaponHeight(this,1500);
                })
                //go to backdrop
                .lineToLinearHeading(new Pose2d(51,-31, Math.toRadians(0)))
                .build();

        TrajectorySequence leftTraj3 = drive.trajectorySequenceBuilder(leftTraj2.end())
                //extend bipod
                .addTemporalMarker(() -> {
                    freezeray.autonAimWeapon(this,.470d,0.530d); //left .472 right 524
                })
                //release pixel
                .addTemporalMarker(.5, () -> { // Can call other parts of the robot
                    freezeray.autonShoot(this);
                })
                .waitSeconds(1.5)
                //back away
                .lineToLinearHeading(new Pose2d(46, -31, Math.toRadians(5)))
                .build();

        TrajectorySequence midTraj1 = drive.trajectorySequenceBuilder(startPose)
                //go to spikemark
                .lineToLinearHeading(new Pose2d(15, -34, Math.toRadians(90)))
                .build();
        TrajectorySequence midTraj2 = drive.trajectorySequenceBuilder(midTraj1.end())
                //back away
                .strafeTo(new Vector2d(18, -37))
                .build();
        TrajectorySequence midTraj3 = drive.trajectorySequenceBuilder(midTraj2.end())
                //go to backdrop
                .lineToLinearHeading(new Pose2d(51,-33, Math.toRadians(0)))
                .build();
        TrajectorySequence midTraj4 = drive.trajectorySequenceBuilder(midTraj3.end())
                //release pixel
                .addTemporalMarker(.5, () -> { // Can call other parts of the robot
                    freezeray.autonShoot(this);
                })
                .waitSeconds(2)
                //back away
                .lineToLinearHeading(new Pose2d(46, -33, Math.toRadians(0)))

                .build();

        //right
        TrajectorySequence rightTraj1 = drive.trajectorySequenceBuilder(startPose)
                //go to spike mark
                .lineToLinearHeading(new Pose2d(17,-43, Math.toRadians(60)))
                .build();

        TrajectorySequence rightTraj2 = drive.trajectorySequenceBuilder(rightTraj1.end())
                //back away from pixel
                .lineToLinearHeading(new Pose2d(17,-55, Math.toRadians(0)))
                .build();


        TrajectorySequence rightTraj3 = drive.trajectorySequenceBuilder(rightTraj2.end())
                //go to backdrop
                .splineToLinearHeading(new Pose2d(51, -42, 0), Math.toRadians(30))
                .build();

        TrajectorySequence rightTraj4 = drive.trajectorySequenceBuilder(rightTraj3.end())
                //release pixel
                .addTemporalMarker(.5, () -> { // Can call other parts of the robot
                    freezeray.autonShoot(this);
                })
                .waitSeconds(2)
                //back away
                .lineToLinearHeading(new Pose2d(46, -42, Math.toRadians(0)))

                .build();


        telemetry.addData(gstrClassName, "Initialized");
        telemetry.update();

        waitForStart();

        if(isStopRequested()) return;

        nPropPos=goggles2.findProp(this,5000);

        telemetry.addData(gstrClassName, "Prop position:%d",nPropPos);
        telemetry.update();

        if (nPropPos == goggles2.PROP_NONE)
            nPropPos = goggles2.PROP_MID;

        if (nPropPos == goggles2.PROP_LEFT) {
            drive.followTrajectorySequence(leftTraj1);
            drive.followTrajectory(buildCorrectionTrajectory(leftTraj1.end(), 5, 5));
            piranhatail.autonFlickPixel(this,2200,100);
            drive.followTrajectorySequence(leftTraj2);
            drive.followTrajectory(buildCorrectionTrajectory(leftTraj2.end(), 10, 10));
            //push into wall
            drive.followTrajectorySequence(leftTraj3);
            freezeray.autonMakeWeaponSafe(this);
        }
        else if (nPropPos == goggles2.PROP_MID) {
            drive.followTrajectorySequence(midTraj1);
            drive.followTrajectory(buildCorrectionTrajectory(midTraj1.end(), 10, 10));
            piranhatail.autonFlickPixel(this,2200,100);
            drive.followTrajectorySequence(midTraj2);
            //raise 4bar
            freezeray.autonRaiseWeaponHeight(this,1500);
            //go to backdrop
            drive.followTrajectorySequence(midTraj3);
            drive.followTrajectory(buildCorrectionTrajectory(midTraj3.end(), 10, 10));
            //extend bipod
            freezeray.autonAimWeapon(this,.470d,0.530d); //left .472 right 524
            //push into wall
            drive.followTrajectorySequence(midTraj4);
            freezeray.autonMakeWeaponSafe(this);
        }
        else {
            drive.followTrajectorySequence(rightTraj1);
            drive.followTrajectory(buildCorrectionTrajectory(rightTraj1.end(), 10, 10));
            piranhatail.autonFlickPixel(this,2200,100);
            drive.followTrajectorySequence(rightTraj2);
            //raise 4bar
            freezeray.autonRaiseWeaponHeight(this,1500);
            //go to backdrop
            drive.followTrajectorySequence(rightTraj3);
            drive.followTrajectory(buildCorrectionTrajectory(rightTraj3.end(), 10, 10));
            //extend bipod
            freezeray.autonAimWeapon(this,.470d,0.530d); //left .472 right 524
            //push into wall
            drive.followTrajectorySequence(rightTraj4);
            freezeray.autonMakeWeaponSafe(this);
        }

//        Trajectory moveToPark = drive.trajectoryBuilder(chosenTraj.end())
//             .strafeTo(new Vector2d(48, -60))
//                .build(); // traj instead of trajSeq for simplicity as this is building during autonomous

//        drive.followTrajectory(moveToPark);
        //TODO: COMMENT OUT BELOW WHEN DONE!!
        TrajectorySequence returnBack = drive.trajectorySequenceBuilder(drive.getPoseEstimate())
                //go to front of truss
                //.lineToLinearHeading(new Pose2d(10.0, 58.5, Math.toRadians(0))) //x:18-48(two tiles)-8 (other side of prop)
                //go to back of truss

                //.lineToLinearHeading(new Pose2d(-42.0, 58.5, Math.toRadians(0))) //x:18-48(two tiles)-8 (other side of prop)
                //go past truss
                .lineToLinearHeading(startPose)
                .build();
        drive.followTrajectorySequence(returnBack);
    }

    private Trajectory buildCorrectionTrajectory(Pose2d pose) {
        Trajectory correction = drive.trajectoryBuilder(drive.getPoseEstimate())
                .lineToLinearHeading(pose)
                .build();
        return correction;
    }
    private Trajectory buildCorrectionTrajectory(Pose2d pose, double maxVel, double maxAccel) {
        Trajectory correction = drive.trajectoryBuilder(drive.getPoseEstimate())
                .lineToLinearHeading(pose,
                        SampleSwerveDrive.getVelocityConstraint(maxVel, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleSwerveDrive.getAccelerationConstraint(maxAccel))
                .build();
        return correction;
    }
}
