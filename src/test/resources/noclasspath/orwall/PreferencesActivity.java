package org.ethack.orwall;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import org.ethack.orwall.iptables.InitializeIptables;

import java.util.Arrays;
import java.util.List;


public class PreferencesActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preferences_header, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        String prepend = "org.ethack.orwall.PreferencesActivity$";
        String[] fragments = {
                prepend + "ScriptPrefs",
                prepend + "SpecialApps",
                prepend + "NetworkPrefs",
                prepend + "ProxyPorts",
        };

        return Arrays.asList(fragments).contains(fragmentName);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static class ScriptPrefs extends PreferenceFragment {

        private SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                InitializeIptables iptables = new InitializeIptables(getActivity());
                if (sharedPreferences.getBoolean(s, true) && s.equals("enforce_init_script")) {
                    iptables.installInitScript(getActivity());
                }
                if (sharedPreferences.getBoolean(s, true) && s.equals("deactivate_init_script") && !sharedPreferences.getBoolean("enforce_init_script", true)) {
                    iptables.removeIniScript();
                }
            }
        };

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            PreferenceManager.setDefaultValues(getActivity(), R.xml.network_preference, true);
            addPreferencesFromResource(R.xml.fragment_init_pref);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
        }
    }

    public static class SpecialApps extends PreferenceFragment {
        private SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                return;
            }
        };

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //PreferenceManager.setDefaultValues(getActivity(), R.xml.fragment_apps_prefs, true);
            addPreferencesFromResource(R.xml.fragment_apps_prefs);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
        }
    }

    public static class NetworkPrefs extends PreferenceFragment {
        private SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                InitializeIptables iptables = new InitializeIptables(getActivity());
                if (s.equals("enable_lan")) {
                    iptables.LANPolicy(sharedPreferences.getBoolean(s, false));
                }

                if (s.equals("enable_adb")) {
                    iptables.enableADB(sharedPreferences.getBoolean(s, false));
                }

                if (s.equals("enable_captive_portal")) {
                    Context context = getActivity();
                    Intent bgpProcess = new Intent(context, BackgroundProcess.class);
                    bgpProcess.putExtra(BackgroundProcess.PARAM_ACTIVATE, sharedPreferences.getBoolean(s, false));
                    bgpProcess.putExtra(BackgroundProcess.ACTION, BackgroundProcess.ACTION_PORTAL);
                    context.startService(bgpProcess);
                }
            }
        };

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            PreferenceManager.setDefaultValues(getActivity(), R.xml.other_preferences, true);
            addPreferencesFromResource(R.xml.fragment_network_prefs);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
        }
    }

    public static class ProxyPorts extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.fragment_proxy_ports);
        }
    }

}
