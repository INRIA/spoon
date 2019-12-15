package test.resources.com.leafactor.cli.rules.WakeLockRefactoringRule.Legacy;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class Input {
    public class C1 extends Activity {
        private PowerManager.WakeLock wl;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
            WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "WakeLockSample");
            wl.acquire();
        }

        @Override
        protected void onPause() {
            super.onPause();
        }

        @Override
        public void onDestroy(){
            wl.release();
            super.onDestroy();
        }
    }

    public class C2 extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
            WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "WakeLockSample");
            wl.acquire();
        }

        @Override
        public void onDestroy(){
            wl.release();
            super.onDestroy();
        }
    }

    public class C3 extends Activity {
        private WakeLock wl;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "WakeLockSample");
            wl.acquire();
        }

        @Override() protected void onPause(){
            super.onPause();
            wl.release();
        }
    }

    public class C4 extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
            WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "WakeLockSample");
            wl.acquire();
        }
    }
}
