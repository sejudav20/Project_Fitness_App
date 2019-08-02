package com.example.accelerationtest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;

public class MovementService extends Service {

    /**
    *  Movement Service is a bound service that will return acclertion and phone rotation
     *  ------------------------------------------------------------------
    *       How to use MovementService
     *    1. set up the following global methods these will be useful for checking connection later
     *       [      private boolean bound = true;
     *               private MovementService movementService;        ]
     *    2. set up a service connection as demonstrated below
     *     [ private ServiceConnection connection = new ServiceConnection() {
     *         @Override
     *         public void onServiceConnected(ComponentName name, IBinder service) {
     *             MovementService.MovementBinder movementBinder = (MovementService.MovementBinder) service;
     *             movementService = movementBinder.getMovementService();
     *
     *             bound = true;
     *              //this is a good place to add stuff that should happen
     *
     *
     *         }
     *
     *         @Override
     *         public void onServiceDisconnected(ComponentName name) {
     *             bound = false;
     *             //what to do after service closes
     *
     *         }
     *     };     ]
     *
     *     3.  bind the service wherever you need it using the following code
     *                Intent intent = new Intent(this, MovementService.class);
     *                 bindService(intent, connection, Context.BIND_AUTO_CREATE);
     *     4. call any method from MovementService using the global variable we created in step 1
     *        be careful if bound==false you will get a null exception error put a loading screen
     *        if necessary

     * ----------------------------------------------------------------------
     * Methods:
     * setOnMovementListener(): this method gives access to the services linear motion through
     * a movement listener interface. The abstract methods of this interface are as follows
     *      onMovementDone: this method is called everytime there is some acceleration and then a stop
     *      onFall: when the acceleration exceeds 10 m/s usually denoting the phone fell or was thrown
     *      onShake: Where phone is moved in one direction and then the opposite direction maintaining
     *          the initial angle
     *      onUnidirectional motion: a motion in a certain direction with a constant angle
     * isCalibrated(): Movement service will check if acceleration is close to zero
     * ----------------------------------------------------------------------------
     * The MovementService class uses another class Movement in the listener.
     * note: the sensor provides acceleration on a x y z axis as if the phone screen is facing you
     * The class provides detailed information on movement by the user through these methods
     *
     *  getAccelerationX(): returns an ArrayList of acceleration is the X direction through
     *  equal time intervals
     *  getAccelerationY(): returns an ArrayList of acceleration is the y direction through
     *      *  equal time intervals
     *  getAccelerationZ(): returns an ArrayList of acceleration is the Z direction through
     *      *  equal time intervals
     *  getDuration(): get how long the motion took to complete in miliseconds
     *  getTimeSinceLastMotion(): get the time before the last movement occured
     *  getAcAngles(): get acceleration angle what angle the phone is moving not orientation
     *   note: please do not use the add_ method
     *
     *
    * */



    private final IBinder binder = new MovementBinder();
    private MovementListener listener;
    private SensorManager sensorManager;
    private Sensor gyro;
    private Sensor accelerometer;
    private int notMovingCount = 0;
    private int swipeCounter = 0;
    private boolean calibrated=false;
    Movement m;
    private SensorEventListener accelerometerListener;
    //accelerometer values
    private long timeAtLastMovement=0;
    private long timeSinceLastMovement = 0;
    private double tempAngle = 0;

    public MovementService() {

    }

    public boolean isCalibrated() {
        return calibrated;

    }

    public void setOnMovementListener(final MovementListener listener) {
        this.listener = listener;


        int sampleRate = 50000;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);



        accelerometerListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(calibrated) {

                    if (m == null || notMovingCount == 20) {

                        if (notMovingCount == 20) {
                            m.timeSinceLastMotion = SystemClock.elapsedRealtime() - timeAtLastMovement - m.getDuration();
                            if (swipeCounter == 2) {
                                Log.d("test","A shake occured");
                                listener.onShake(m);
                            }

                            if (swipeCounter == 1) {
                                Log.d("test","A unidirectional movement");
                                listener.unidirectionalMotion(m);
                            }
                            int counter = 0;
                            for (double e : m.getAccelerationZ()) {


                                if (Math.abs(e) > 20) {
                                    if (counter > 3) {
                                        Log.d("test","A fall  occured");
                                        listener.onFall(m);
                                    }
                                    counter++;
                                } else {
                                    counter = 0;
                                }

                            }

                            if (m != null) {
                                listener.onMovementDone(m);
                                timeAtLastMovement = SystemClock.elapsedRealtime();
                                Log.d("test"," Shake counter "+swipeCounter+" temp angle "+tempAngle);
                            }

                        }
                        if (Math.abs(event.values[0]) > 1 || Math.abs(event.values[1]) > 1 || Math.abs(event.values[2]) > 1) {
                            m = new Movement(timeSinceLastMovement);
                        } else {
                            m = null;
                        }

                        notMovingCount = 0;
                        swipeCounter = 0;
                        tempAngle = 0;

                    }
                    if(m!=null){
                        double ax = 0;
                        if (event.values[0] > 1 || event.values[0] < -1) {
                            ax = event.values[0];
                        }
                        double ay = 0;
                        if (event.values[1] > 1 || event.values[1] < -1) {
                            ay = event.values[1];
                        }
                        double az = 0;
                        if (event.values[2] > 1 || event.values[2] < -1) {
                            az = event.values[2];
                        }
                        if (ax == 0 && ay == 0 && az == 0) {
                            notMovingCount++;

                        } else {
                            notMovingCount = 0;
                        }
                        double accelerationAngle, c = 1, d = 0, e = 0, f = ax, g = ay, h = az;
                        accelerationAngle = (Math.acos(((c * f) + (d * g) + (e * h)) / (Math.sqrt((c * c) + (d * d) + (e * e)) * Math.sqrt((g * g) + (f * f) + (h * h)))) * 360) / (Math.PI * 2);
                        m.add_(ax, ay, az, accelerationAngle);


                        tempAngle = m.initialState()[3];

                        if (tempAngle - 10 < accelerationAngle && tempAngle + 10 > accelerationAngle) {
                            if(swipeCounter==1){
                                Log.d("test","a 1 has been noticed in angle "+tempAngle);
                            }if(swipeCounter==2){
                                Log.d("test","a 2 has been noticed "+tempAngle);
                            }
                            if (swipeCounter == 0) {
                                if (m.initialState()[0] <= 0) {
                                    if (ax > 0) {
                                        swipeCounter=1;

                                    } else if(ax!=0){
                                        swipeCounter = 0;
                                    }

                                } else {
                                    if (ax < 0) {

                                        swipeCounter = 0;
                                    } else {
                                        swipeCounter++;
                                    }


                                }
                                if (m.initialState()[1] <= 0) {
                                    if (ay > 0) {
                                        swipeCounter=1;

                                    } else if(ay!=0){
                                        swipeCounter = 0;
                                    }

                                } else {
                                    if (ay < 0) {

                                        swipeCounter = 0;
                                    } else {
                                        swipeCounter=1;
                                    }


                                }
                                if (m.initialState()[2] <= 0) {
                                    if (az > 0) {
                                        swipeCounter=1;

                                    } else if(az!=0){
                                        swipeCounter = 0;
                                    }

                                } else {
                                    if (az < 0) {

                                        swipeCounter = 0;
                                    } else {
                                        swipeCounter=1;
                                    }


                                }
                            }


                            if (swipeCounter == 1) {
                                if (m.initialState()[0] <= 0) {
                                    if (ax > 0) {


                                    } else if(ax!=0){
                                        swipeCounter=2;
                                    }

                                } else {
                                    if (ax < 0) {
                                        swipeCounter=2;

                                    } else {
                                        swipeCounter = 1;
                                    }


                                }
                                if (m.initialState()[1] <= 0) {
                                    if (ay > 0) {
                                        swipeCounter = 1;


                                    } else if(ay!=0){
                                        swipeCounter=2;
                                    }

                                } else {
                                    if (ay < 0) {
                                        swipeCounter=2;

                                    } else {
                                        swipeCounter = 1;
                                    }


                                }
                                if (m.initialState()[2] <= 0) {
                                    if (az > 0) {
                                        swipeCounter = 1;

                                    } else if(az!=0){
                                        swipeCounter=2;
                                    }

                                } else {
                                    if (az < 0) {
                                        swipeCounter=2;

                                    } else {
                                        swipeCounter = 1;
                                    }


                                }
                            }


                        }

                    }}else{

                    if(event.values[0]<1&&event.values[1]<1&&event.values[2]<1){


                        calibrated=true;

                    }               }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        sensorManager.registerListener(accelerometerListener, accelerometer, sampleRate);


    }


    @Override
    public void onCreate() {
        super.onCreate();

    }


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(accelerometerListener);
        super.onDestroy();

    }


    public class MovementBinder extends Binder {
        MovementService getMovementService() {

            return MovementService.this;

        }


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    interface MovementListener {
        void onMovementDone(Movement movement);

        void onShake(Movement movement);


        //V add your gyroscope code here
        //public void onRotate(Movement movement);

        //public void onMinimalMovement(Movement movement);

        void onFall(Movement movement);

        void unidirectionalMotion(Movement movement);


    }

    public class Movement {
        private long timeSinceLastMotion;
        private long timeOnStart;
        private long duration;

        public long getDuration() {
            return duration;
        }

        private ArrayList<double[]> acceleration = new ArrayList();

        public double[] initialState() {

            return (double[]) acceleration.get(0);

        }

        public ArrayList<Double> getAccelerationX() {

            ArrayList<Double> ax = new ArrayList();
            for (double[] x : acceleration) {
                ax.add(x[0]);
            }
            return ax;
        }

        public ArrayList<Double> getAccelerationY() {
            ArrayList<Double> ay = new ArrayList();
            for (double[] y : acceleration) {
                ay.add(y[1]);
            }
            return ay;
        }

        public ArrayList<Double> getAccelerationZ() {
            ArrayList<Double> az = new ArrayList();
            for (double[] z : acceleration) {
                az.add(z[2]);
            }
            return az;
        }

        public ArrayList<Double> getAcAngles() {
            ArrayList<Double> angle = new ArrayList();
            for (double[] an : acceleration) {
                angle.add(an[3]);
            }
            return angle;
        }

        public long getTimeSinceLastMotion() {
            return timeSinceLastMotion;
        }


        public void add_(double ax, double ay, double az, double acangle) {
            double[] stuff = {ax, ay, az, acangle};
            acceleration.add(stuff);
            duration = SystemClock.elapsedRealtime() - timeOnStart;

        }

        ///V add your parameters here
        public Movement(long timeSinceLastMotion) {
            this.timeSinceLastMotion = timeSinceLastMotion;
            timeOnStart = SystemClock.elapsedRealtime();

        }


    }


}
