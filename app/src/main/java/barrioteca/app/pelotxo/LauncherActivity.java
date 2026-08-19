package barrioteca.app.pelotxo;

import android.os.Bundle;

/**
 * Actividad principal de la Trusted Web Activity.
 * Abre la PWA Barrioteca Acalenca alojada en el NAS.
 */
public class LauncherActivity extends com.google.androidbrowserhelper.trusted.LauncherActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
