package witparking.inspection.inspectionmap;

import android.content.Context;
import android.view.View;


import io.flutter.plugin.common.MessageCodec;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class BMapViewFactory extends PlatformViewFactory {

    public SIMapView mapView;
    private PluginRegistry.Registrar registrar;

    public BMapViewFactory(MessageCodec<Object> createArgsCodec, PluginRegistry.Registrar registrar) {
        super(createArgsCodec);
        this.registrar = registrar;
        mapView = new SIMapView(registrar.activity());
        mapView.mMapView.onPause();
    }

    @Override
    public PlatformView create(Context context, int i, Object o) {
        return new PlatformView() {
            @Override
            public View getView() {
                if (mapView == null) {
                    mapView = new SIMapView(registrar.activity());
                }
                mapView.mMapView.onResume();
                mapView.startLocationUser();
                return mapView;
            }

            @Override
            public void dispose() {
                mapView.mMapView.onPause();
                //mapView = null;
            }
        };
    }
}
