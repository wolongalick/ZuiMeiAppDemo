package com.alick.zuimeiapp;

import java.util.ArrayList;
import java.util.List;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CustomLayout extends FrameLayout{
	private static final String TAG = CustomLayout.class.getSimpleName();
	private LinearLayout ll_bottom;
	private View rootView;
	private List<ImageView> imageViews=new ArrayList<ImageView>();
	private Context context;
	/**单张图片宽度*/
	private int singleImageWidth;
	/**单张图片的高度*/
	private int singleImageHeight;
	/**横幅可见高度*/
	private int bannerHeight;
	/**横幅可见宽度*/
	private int bannerWidth;
	/**屏幕宽度*/
	private int screenWidth;
	/**图标总个数*/
	private int count;
	/**上一次触摸的图标索引*/
	private int lastIndex=-1;
	/**每个图标的高度差*/
	private int grads;
	/**每个图片的marginRight距离*/
	private int marginRight;
	/**横幅可同时显示图标的个数*/
	private int visibleCount;
	/**最小高度*/
	private int minHeight=10;
	private long duration=200;
	private TimeInterpolator interpolator;
	/**界面显示的第一个图标的索引*/
	private int firstItemIndex;
	/**界面显示的最后一个图标的索引*/
	private int lastItemIndex;


	private final long scrollTimeInterval=200;


	public CustomLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		interpolator=new BounceInterpolator();
		if (attrs != null) {
			TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomLayout);
			marginRight = (int) typedArray.getDimension(R.styleable.CustomLayout_marginRight, 0);
		}
		DisplayMetrics dm=context.getResources().getDisplayMetrics();
		screenWidth=dm.widthPixels;
		rootView=LayoutInflater.from(context).inflate(R.layout.custom_layout, this);

		ll_bottom=(LinearLayout) rootView.findViewById(R.id.ll_bottom);
		fillImages();
		count=imageViews.size();
		showImages();
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,int bottom) {
		Log.i(TAG, "left=" + left + ",top=" + top + ",right=" + right + ",bottom=" + bottom + "--->onLayout()");
		bannerHeight=getHeight();
		bannerWidth=getWidth();
		Log.i(TAG, "底部横幅宽度:"+bannerWidth);
		Log.i(TAG, "底部横幅高度:"+bannerHeight);
		singleImageWidth=imageViews.get(0).getWidth();
		singleImageHeight=imageViews.get(0).getHeight();
		Log.i(TAG, "一张图片的宽度:" + singleImageWidth);
		Log.i(TAG, "一张图片的高度:"+singleImageHeight);
		if(singleImageWidth!=0){
			visibleCount=screenWidth/singleImageWidth;
			Log.i(TAG, "visibleCount="+visibleCount);
			grads=singleImageHeight/visibleCount;
		}
		super.onLayout(changed, left, top, right, bottom);
	}
	
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		Log.i(TAG,"event:"+event);
		return super.onKeyLongPress(keyCode, event);
	}
	

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i(TAG, "event="+event.getAction());
		int action=event.getAction();
		float x=event.getX();
		int touchIndex=(int)(x/singleImageWidth);
		if(!checkIndex(touchIndex)){
			if(action==MotionEvent.ACTION_UP){
				moveDown(lastIndex);
			}
			return true;
		}
		//获得手势事件
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				moveUp(firstItemIndex + touchIndex);
				if((firstItemIndex+touchIndex)<count-1){
					moveToLeftRight(touchIndex);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (lastIndex != touchIndex) {
					moveUp(firstItemIndex+touchIndex);
					if((firstItemIndex+touchIndex)<count-1){
						moveToLeftRight(touchIndex);
					}
				}else if(lastIndex==touchIndex){
					moveToLeftRight(touchIndex);
				}

				break;
			case MotionEvent.ACTION_UP:
				moveDown(firstItemIndex + touchIndex);
				break;
		}
		lastIndex = touchIndex;
		return true;
	}

	private long lastScrollTime;

	private void moveToLeftRight(int index){
		long currentScrollTime=System.currentTimeMillis();
		if(currentScrollTime-lastScrollTime<=scrollTimeInterval){
			return;
		}
		lastScrollTime=currentScrollTime;

		if(index>=visibleCount ){
			if(firstItemIndex+index+1>=count){
				return;
			}
			moveUp(firstItemIndex+index+1);
			//如果触摸位置横幅最右侧,就往左移动
			firstItemIndex++;
			for (int i = 0; i < count; i++) {
				ImageView imageView=imageViews.get(i);
				leftRightAnim(imageView,(i-firstItemIndex)*singleImageWidth);
			}
		}else if(index==0 && firstItemIndex>0){
			moveUp(firstItemIndex+index-1);
			//如果触摸位置再横幅最左侧,就往右移动
			firstItemIndex--;
			for (int i = 0; i < count; i++) {
				ImageView imageView=imageViews.get(i);
				leftRightAnim(imageView,(i-firstItemIndex)*singleImageWidth);
			}
		}
		lastScrollTime =System.currentTimeMillis();
	}

	/**
	 * 上升
	 * @param index		 索引
	 * @since 2015-5-12下午7:25:42
	 * @author cuixingwang
	 */
	private void moveUp(int index){
		final int selectImageTop=bannerHeight-singleImageHeight;
		ImageView selectImageView=imageViews.get(index);
		upAnim(selectImageView,selectImageTop);

		for (int i = index-1; i >=0; i--) {
			ImageView imageView=imageViews.get(i);
			int top=selectImageTop+(index-i)*grads;
			if(top>bannerHeight-minHeight){
				top=bannerHeight-minHeight;
			}
			upAnim(imageView, top);
		}

		for (int i = index+1; i < count; i++) {
			ImageView imageView=imageViews.get(i);
			int top=selectImageTop+(i-index)*grads;
			if(top>bannerHeight-minHeight){
				top=bannerHeight-minHeight;
			}
			upAnim(imageView, top);
		}
	}

	/**
	 * 下降
	 * @param index	索引
	 * @since 2015-5-12下午7:25:49
	 * @author cuixingwang
	 */
	private void moveDown(int index) {
		for (int i = 0; i < count; i++) {
			ImageView imageView=imageViews.get(i);
			if(i==index){
				downAnim(imageView, bannerHeight-singleImageHeight);
			}else{
				int top=bannerHeight-minHeight;
				downAnim(imageView,top);
			}
		}
	}

	/**
	 * 动画
	 * @param view	视图
	 *@param marginTop  @since 2015-5-12下午7:25:56
	 * @author cuixingwang
	 */
	@SuppressLint("NewApi")
	private void upAnim(View view, int marginTop){
		ObjectAnimator objectAnimator=ObjectAnimator.ofFloat(view, "y", marginTop)
				.setDuration(duration);
		objectAnimator.setInterpolator(interpolator);
		objectAnimator.start();
	}


	@SuppressLint("NewApi")
	private void downAnim(View view, int marginTop){
		ObjectAnimator objectAnimator=ObjectAnimator.ofFloat(view, "y", marginTop)
				.setDuration(duration);
		objectAnimator.setInterpolator(interpolator);
		objectAnimator.start();
	}

	@SuppressLint("NewApi")
	private void leftRightAnim(View view, float toPosition){
		ObjectAnimator.ofFloat(view,"x",toPosition)
				.setDuration(duration)
				.start();
	}

	/**
	 * 检查是否越界
	 * @param index			索引
	 * @return				若没越界则返回true
	 * @since 2015-5-11下午5:04:19
	 * @author cuixingwang
	 */
	private boolean checkIndex(int index){
		return index<count;
	}
	
	
	private void showImages() {
		for (int i = 0; i < count; i++) {
			ll_bottom.addView(imageViews.get(i));
		}
	}
	
	private void addImages(int imageResId) {
		ImageView imageView=new ImageView(context);
		imageView.setImageResource(imageResId);
		imageViews.add(imageView);
	}
	

	private void fillImages() {
		addImages(R.mipmap.b1);
		addImages(R.mipmap.b2);
		addImages(R.mipmap.b3);
		addImages(R.mipmap.b4);
		addImages(R.mipmap.b5);
		addImages(R.mipmap.b6);
		addImages(R.mipmap.b7);
		addImages(R.mipmap.b8);
		addImages(R.mipmap.b9);
		addImages(R.mipmap.b10);
		addImages(R.mipmap.b11);
		addImages(R.mipmap.b12);
		addImages(R.mipmap.b13);
		addImages(R.mipmap.b14);
	}

}
