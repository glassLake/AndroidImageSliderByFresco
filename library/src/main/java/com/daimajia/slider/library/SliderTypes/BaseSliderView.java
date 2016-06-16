package com.daimajia.slider.library.SliderTypes;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.daimajia.slider.library.R;
import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;


import java.io.File;

/**
 * When you want to make your own slider view, you must extends from this class.
 * BaseSliderView provides some useful methods.
 * I provide two example: {@link com.daimajia.slider.library.SliderTypes.DefaultSliderView} and
 * {@link com.daimajia.slider.library.SliderTypes.TextSliderView}
 * if you want to show progressbar, you just need to set a progressbar id as @+id/loading_bar.
 */
public abstract class BaseSliderView {

    protected Context mContext;

    private Bundle mBundle;

    /**
     * Error place holder image.
     */
    private int mErrorPlaceHolderRes;

    /**
     * Empty imageView placeholder.
     */
    private int mEmptyPlaceHolderRes;

    private String mUrl;
    private File mFile;
    private int mRes;

    protected OnSliderClickListener mOnSliderClickListener;

    private boolean mErrorDisappear;

    private ImageLoadListener mLoadListener;

    private String mDescription;

    //private Picasso mPicasso;

    /**
     * Scale type of the image.
     */
    private ScaleType mScaleType = ScaleType.Fit;

    public enum ScaleType{
        CenterCrop, CenterInside, Fit, FitCenterCrop
    }

    protected BaseSliderView(Context context) {
        mContext = context;
    }

    /**
     * the placeholder image when loading image from url or file.
     * @param resId Image resource id
     * @return
     */
    public BaseSliderView empty(int resId){
        mEmptyPlaceHolderRes = resId;
        return this;
    }

    /**
     * determine whether remove the image which failed to download or load from file
     * @param disappear
     * @return
     */
    public BaseSliderView errorDisappear(boolean disappear){
        mErrorDisappear = disappear;
        return this;
    }

    /**
     * if you set errorDisappear false, this will set a error placeholder image.
     * @param resId image resource id
     * @return
     */
    public BaseSliderView error(int resId){
        mErrorPlaceHolderRes = resId;
        return this;
    }

    /**
     * the description of a slider image.
     * @param description
     * @return
     */
    public BaseSliderView description(String description){
        mDescription = description;
        return this;
    }

    /**
     * set a url as a image that preparing to load
     * @param url
     * @return
     */
    public BaseSliderView image(String url){
        if(mFile != null || mRes != 0){
            throw new IllegalStateException("Call multi image function," +
                    "you only have permission to call it once");
        }
        mUrl = url;
        return this;
    }

    /**
     * set a file as a image that will to load
     * @param file
     * @return
     */
    public BaseSliderView image(File file){
        if(mUrl != null || mRes != 0){
            throw new IllegalStateException("Call multi image function," +
                    "you only have permission to call it once");
        }
        mFile = file;
        return this;
    }

    public BaseSliderView image(int res){
        if(mUrl != null || mFile != null){
            throw new IllegalStateException("Call multi image function," +
                    "you only have permission to call it once");
        }
        mRes = res;
        return this;
    }

    /**
     * lets users add a bundle of additional information
     * @param bundle
     * @return
     */
    public BaseSliderView bundle(Bundle bundle){
        mBundle = bundle;
        return this;
    }

    public String getUrl(){
        return mUrl;
    }

    public boolean isErrorDisappear(){
        return mErrorDisappear;
    }

    public int getEmpty(){
        return mEmptyPlaceHolderRes;
    }

    public int getError(){
        return mErrorPlaceHolderRes;
    }

    public String getDescription(){
        return mDescription;
    }

    public Context getContext(){
        return mContext;
    }

    /**
     * set a slider image click listener
     * @param l
     * @return
     */
    public BaseSliderView setOnSliderClickListener(OnSliderClickListener l){
        mOnSliderClickListener = l;
        return this;
    }

    /**
     * When you want to implement your own slider view, please call this method in the end in `getView()` method
     * @param v the whole view
     * @param targetImageView where to place image
     */
    protected void bindEventAndShow(final View v, SimpleDraweeView targetImageView){
        final BaseSliderView me = this;

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(mOnSliderClickListener != null){
                mOnSliderClickListener.onSliderClick(me);
            }
            }
        });

        if (targetImageView == null)
            return;

        if (mLoadListener != null) {
            mLoadListener.onStart(me);
        }

        Uri uri = null;
        if(mUrl!=null){
            uri = Uri.parse(mUrl);
        }else if(mFile != null){
           uri = Uri.fromFile(mFile);
        }else if(mRes != 0){
            //Uri.Builder builder = new Uri.Builder();
            uri = Uri.parse("res://"+"com.dd.ee"+"/" + mRes);
        }else{
            return;
        }
        if (uri == null){
            return;
        }

        initImageView(v,targetImageView,uri);

       /* targetImageView.getHierarchy().setProgressBarImage(new ProgressBarDrawable());
        targetImageView.getHierarchy().setFailureImage();*/


       // targetImageView.setImageURI(uri);

        //targetImageView.setImageURI(Uri.);

        /*Picasso p = (mPicasso != null) ? mPicasso : Picasso.with(mContext);
        RequestCreator rq = null;
        if(mUrl!=null){
            rq = p.load(mUrl);
        }else if(mFile != null){
            rq = p.load(mFile);
        }else if(mRes != 0){
            rq = p.load(mRes);
        }else{
            return;
        }

        if(rq == null){
            return;
        }

        if(getEmpty() != 0){
            rq.placeholder(getEmpty());
        }

        if(getError() != 0){
            rq.error(getError());
        }*/



       /* rq.into(targetImageView,new Callback() {
            @Override
            public void onSuccess() {
                if(v.findViewById(R.id.loading_bar) != null){
                    v.findViewById(R.id.loading_bar).setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onError() {
                if(mLoadListener != null){
                    mLoadListener.onEnd(false,me);
                }
                if(v.findViewById(R.id.loading_bar) != null){
                    v.findViewById(R.id.loading_bar).setVisibility(View.INVISIBLE);
                }
            }
        });*/
   }

    private void initImageView(final View v, SimpleDraweeView targetImageView, Uri uri) {

        switch (mScaleType){
            case Fit:
                targetImageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
                // rq.fit();
                break;
            case CenterCrop:
                targetImageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
                // rq.fit().centerCrop();
                break;
            case CenterInside:
                targetImageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE);
                //  rq.fit().centerInside();
                break;
        }




        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(
                    String id,
                    @Nullable ImageInfo imageInfo,
                    @Nullable Animatable anim) {
                if (imageInfo == null) {
                    return;
                }

                if(v.findViewById(R.id.loading_bar) != null){
                    v.findViewById(R.id.loading_bar).setVisibility(View.INVISIBLE);
                }

                QualityInfo qualityInfo = imageInfo.getQualityInfo();
                FLog.d("Final image received! " +
                                "Size %d x %d",
                        "Quality level %d, good enough: %s, full quality: %s",
                        imageInfo.getWidth(),
                        imageInfo.getHeight(),
                        qualityInfo.getQuality(),
                        qualityInfo.isOfGoodEnoughQuality(),
                        qualityInfo.isOfFullQuality());
            }

            @Override
            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
               // FLog.d("Intermediate image received");
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                FLog.e(getClass(), throwable, "Error loading %s", id);
                if(mLoadListener != null){
                    mLoadListener.onEnd(false,BaseSliderView.this);
                }
                if(v.findViewById(R.id.loading_bar) != null){
                    v.findViewById(R.id.loading_bar).setVisibility(View.INVISIBLE);
                }
            }
        };



        AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setControllerListener(controllerListener)
                .build();

        targetImageView.setController(controller);
    }


    public BaseSliderView setScaleType(ScaleType type){
        mScaleType = type;
        return this;
    }

    public ScaleType getScaleType(){
        return mScaleType;
    }

    /**
     * the extended class have to implement getView(), which is called by the adapter,
     * every extended class response to render their own view.
     * @return
     */
    public abstract View getView();

    /**
     * set a listener to get a message , if load error.
     * @param l
     */
    public void setOnImageLoadListener(ImageLoadListener l){
        mLoadListener = l;
    }

    public interface OnSliderClickListener {
        public void onSliderClick(BaseSliderView slider);
    }

    /**
     * when you have some extra information, please put it in this bundle.
     * @return
     */
    public Bundle getBundle(){
        return mBundle;
    }

    public interface ImageLoadListener{
        public void onStart(BaseSliderView target);
        public void onEnd(boolean result,BaseSliderView target);
    }


}
