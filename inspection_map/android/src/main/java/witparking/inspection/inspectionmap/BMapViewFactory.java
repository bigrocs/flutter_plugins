package witparking.inspection.inspectionmap;

import android.content.Context;
import android.view.View;


import io.flutter.plugin.common.MessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class BMapViewFactory extends PlatformViewFactory {

    private SIMapView mapView;

    public BMapViewFactory(MessageCodec<Object> createArgsCodec, SIMapView mapView) {
        super(createArgsCodec);
        this.mapView = mapView;
    }

    @Override
    public PlatformView create(Context context, int i, Object o) {
        return new PlatformView() {
            @Override
            public View getView() {
                return mapView;
            }

            @Override
            public void dispose() {

            }
        };
    }
}
