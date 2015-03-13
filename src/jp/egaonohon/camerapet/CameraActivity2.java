package jp.egaonohon.camerapet;

import java.io.ByteArrayOutputStream;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class CameraActivity2 extends Activity {

    private SurfaceView mySurfaceView;
    private Camera myCamera; //hardware


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity2);

        //SurfaceView
        mySurfaceView = (SurfaceView)findViewById(R.id.cameraView);
        //リスナ追加
        mySurfaceView.setOnClickListener(onSurfaceClickListener);

        //SurfaceHolder(SVの制御に使うInterface）
        SurfaceHolder holder = mySurfaceView.getHolder();
        //コールバックを設定
        holder.addCallback(callback);

    }

    //コールバック
    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {

            //CameraOpen
            myCamera = Camera.open();

            //Portrait対応
            myCamera.setDisplayOrientation(90);

            //出力をSurfaceViewに設定
            try{
                myCamera.setPreviewDisplay(surfaceHolder);
            }catch(Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int w, int h) {

            //最適サイズを取得 << STEP4
            Camera.Parameters params = myCamera.getParameters();
            List<Camera.Size> sizes = params.getSupportedPreviewSizes();
            Camera.Size optimalSize = getOptimalPreviewSize(sizes,w,h);
            params.setPreviewSize(optimalSize.width,optimalSize.height);
            myCamera.setParameters(params);

            //プレビュースタート（Changedは最初にも1度は呼ばれる）
            myCamera.startPreview();

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            //片付け
            myCamera.release();
            myCamera = null;
        }
    };

    //Surfaceをクリックした時
    private View.OnClickListener onSurfaceClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            if(myCamera != null){
                //AutoFocusを実行
                myCamera.autoFocus(autoFocusCallback);
            }
        }

    };

    //AutoFocusした時
    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback(){
        @Override
        public void onAutoFocus(boolean b, Camera camera) {

            //ただ写真を撮るだけならここにtakePicture()を入れても良い
            //しかし、Pixelを弄りたいのでもうワンクッション（画像処理やバーコード処理をしたいので）
            //Previewを1枚切り取る（そしてイベント発生）
            camera.setOneShotPreviewCallback(previewCallback);
        }
    };

    //切り取った時（ここで撮影、各種画像処理を行う）
    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback(){

        //OnShotPreview時のbyte[]が渡ってくる
        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {

            //プレビューのフォーマットはYUVなので、YUVをBmpに変換する必要がある（ややこしい）
            int w = camera.getParameters().getPreviewSize().width;
            int h = camera.getParameters().getPreviewSize().height;

            //切り取った画像を保存
            //Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length,null);
            Bitmap bmp = getBitmapImageFromYUV(bytes, w, h);

            //回転
            Matrix m = new Matrix();
            m.setRotate(90);

            //保存用 bitmap生成
            Bitmap rotated_bmp = Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),m,true);

            //保存
            String res = MediaStore.Images.Media.insertImage(getContentResolver(),rotated_bmp,"hoge.jpg",null);

        }
    };

    //YUVをBitmapに変換する関数（参照：http://tech.thecoolblogs.com/2013/02/get-bitmap-image-from-yuv-in-android.html）
    public static Bitmap getBitmapImageFromYUV(byte[] data, int width, int height) {
        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, width, height), 80, baos);
        byte[] jdata = baos.toByteArray();
        BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
        bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length, bitmapFatoryOptions);
        return bmp;
    }

    //ApiDemoでよく使うgetOptimalPreviewSize << STEP4
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

}