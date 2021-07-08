package com.xy.viewlib.label;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.xy.viewlib.R;

import java.util.ArrayList;
import java.util.List;

public abstract class ImageMoreView<T> extends LinearLayout implements ViewTreeObserver.OnGlobalLayoutListener{
    private List<T> data = new ArrayList<>();
    private int viewWidth = -1;
    private int cow = 3;//横向数量
    private int spaceHorizontal = 0;//间距
    private int spaceVertical = 0;//间距
    private int radius;
    private int itemHeight = -1;
    public ImageMoreView(Context context) {
        super(context);
        initView(null);
    }

    public ImageMoreView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public ImageMoreView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }
    private void initView(AttributeSet attrs){
        setOrientation(VERTICAL);
        if (attrs != null){
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.ImageMoreView);
            spaceHorizontal = array.getDimensionPixelSize(R.styleable.ImageMoreView_image_more_space_horizontal,0);
            spaceVertical = array.getDimensionPixelSize(R.styleable.ImageMoreView_image_more_space_vertical,0);
            radius = array.getDimensionPixelSize(R.styleable.ImageMoreView_image_more_radius,0);
            cow = array.getInteger(R.styleable.ImageMoreView_image_more_cow,3);
            itemHeight = array.getDimensionPixelOffset(R.styleable.ImageMoreView_image_more_item_height,-1);
            int space = array.getDimensionPixelSize(R.styleable.ImageMoreView_image_more_space,-1);
            if (space>=0){
                spaceHorizontal = space;
                spaceVertical = space;
            }
        }
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }


    /**
     * 设置数据源
     * @param data
     */
    public void setData(List<T> data){
        if (data == null)return;
        this.data = data;
        removeAllViews();
        onGlobalLayout();
    }
    /**
     * 获取需要添加的父布局  layout
     * @return
     */
    private LinearLayout getLayout(){
        LinearLayout linearLayout = null;
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        if (getChildCount()<=0) {
            linearLayout = new LinearLayout(getContext());
            linearLayout.setLayoutParams(params);
            this.addView(linearLayout);
        }else {
            linearLayout = (LinearLayout) getChildAt(getChildCount()-1);
            if (linearLayout.getChildCount()>=cow){
                params.topMargin =spaceVertical;
                linearLayout = new LinearLayout(getContext());
                linearLayout.setLayoutParams(params);
                this.addView(linearLayout);
            }
        }

        return linearLayout;
    }

    /**
     * 获取到位置的时候的回调
     */
    @Override
    public void onGlobalLayout() {
        int viewWidth = getWidth();
        if (viewWidth <=0)return;
        if (getChildCount()>0 )return;
        this.viewWidth = viewWidth;
        Log.e("==``",viewWidth+"----");
        if (data == null)return;
        for (int index = 0 ;index < data.size();index++){
            T value = data.get(index);
            addItemView(getLayout(),value,index);
        }
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }


    /**
     * 添加子数据
     * @param linearLayout
     * @param value
     */
    private void addItemView(LinearLayout linearLayout ,Object value,int index){
        int itemSize = (viewWidth-getPaddingLeft()-getPaddingRight()-(cow-1)*spaceHorizontal)/cow;
        ImageView imageView = new ImageView(getContext());
        LayoutParams params = new LayoutParams(itemSize,itemHeight>0?itemHeight:itemSize);
        params.leftMargin = linearLayout.getChildCount()>0? spaceHorizontal:0;
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String httpValue = null;
        if (value instanceof OnValueListener)
            httpValue = ((OnValueListener) value).getValue();
        else
            httpValue = value.toString();
        showItemView(radius,httpValue,imageView);
        imageView.setOnClickListener(new ImageViewClicked(index));
        linearLayout.addView(imageView);
    }


    public abstract void showItemView(int radius,String httpValue,ImageView imageView);

    /**
     * 点击事件
     */
    private class ImageViewClicked implements OnClickListener{
        private int position;

        public ImageViewClicked(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            ArrayList<String> data = new ArrayList<>();
            for (T item:ImageMoreView.this.data){
                if (item instanceof OnValueListener)
                    data.add(((OnValueListener) item).getValue());
                else
                    data.add(item.toString());
            }
//            PhotosAct.openAct(getContext(),data,position);
        }
    }
}
