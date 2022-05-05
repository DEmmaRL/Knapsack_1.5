package com.example.knapsack;

import android.app.Application;
import android.content.Context;
import android.renderscript.Sampler;

import com.facebook.stetho.DumperPluginsProvider;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.dumpapp.DumperPlugin;

import java.util.ArrayList;

public class App extends Application {
    private Context context;
    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                .enableDumpapp((DumperPluginsProvider) new SampleDumperPluginsProvider(context))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                .build()
        );
    }
    public static class SampleDumperPluginsProvider implements DumperPluginsProvider {
        private Context context;
        public SampleDumperPluginsProvider(Context context) { this.context = context; }

        @Override
        public  Iterable<DumperPlugin> get() {

            /*
            ArrayList<DumperPlugin> plugins = new ArrayList<>();
            for(DumperPlugin dp : Stetho.defaultDumperPluginsProvider(context).get()){
                plugins.add(dp);
            }
            */
            return Stetho.defaultDumperPluginsProvider(context).get();
        }
    }
}



























