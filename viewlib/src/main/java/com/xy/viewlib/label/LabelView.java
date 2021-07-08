package com.xy.viewlib.label;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.xy.viewlib.R;

import java.util.ArrayList;
import java.util.List;

public class LabelView<T> extends LinearLayout implements ViewTreeObserver.OnGlobalLayoutListener{
    private List<T> data = new ArrayList<>();
    private int viewWidth = -1;
    private int minWidth = 0;//最小间距
    private int textSize = 0;//文字大小
    private int maxLine = Integer.MAX_VALUE;//最大行数
    private int textListColor = R.color.gray_2222;
    private int textColor = 0x000000;
    private int background = R.drawable.bg_transparent;
    private int paddingLeft = 0;
    private int paddingRight = 0;
    private int paddingBottom = 0;
    private int paddingTop = 0;
    private int spaceVertical = 0;
    private int spaceHorizontal = 0;
    private LabelLoadSucListener labelLoadSucListener;
    public LabelView(Context context) {
        super(context);
        initView(null);
    }

    public LabelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public LabelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    /**
     * 初始化
     * @param attrs
     */
    private void initView(AttributeSet attrs){
        setOrientation(VERTICAL);
        if (attrs != null){
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.LabelView);
            textSize = array.getDimensionPixelSize(R.styleable.LabelView_lab_text_size,15);
            minWidth = array.getDimensionPixelSize(R.styleable.LabelView_lab_mine_size,-1);
            paddingLeft = array.getDimensionPixelSize(R.styleable.LabelView_lab_padding_left,0);
            paddingRight = array.getDimensionPixelSize(R.styleable.LabelView_lab_padding_right,0);
            paddingTop = array.getDimensionPixelSize(R.styleable.LabelView_lab_padding_top,0);
            paddingBottom = array.getDimensionPixelSize(R.styleable.LabelView_lab_padding_bottom,0);
            spaceVertical = array.getDimensionPixelSize(R.styleable.LabelView_lab_space_vertical,0);
            spaceHorizontal = array.getDimensionPixelSize(R.styleable.LabelView_lab_space_horizontal,0);
            textListColor = array.getResourceId(R.styleable.LabelView_lab_text_list_color,0);
            textColor = array.getResourceId(R.styleable.LabelView_lab_text_common_color,getResources().getColor(R.color.gray_3333));
            background = array.getResourceId(R.styleable.LabelView_lab_background,R.drawable.bg_transparent);
            int paddingHorizontal = array.getDimensionPixelSize(R.styleable.LabelView_lab_padding_horizontal,-1);
            int paddingVertical = array.getDimensionPixelSize(R.styleable.LabelView_lab_padding_vertical,-1);

            int padding = array.getDimensionPixelSize(R.styleable.LabelView_lab_padding,-1);
            if (paddingHorizontal>=0){
                paddingLeft = paddingHorizontal;
                paddingRight = paddingHorizontal;
            }
            if (paddingVertical>=0){
                paddingTop = paddingVertical;
                paddingBottom = paddingVertical;
            }
            if (padding >=0){
                paddingLeft = padding;
                paddingRight = padding;
                paddingTop = padding;
                paddingBottom = padding;
            }
        }
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    /**
     * 设置数据源
     * @param data
     */
    public void setData(List<T> data){
        this.data = data;
        if (viewWidth <= 0)return;
        removeAllViews();
        onGlobalLayout();
    }

    /**
     * 设置监听加载完成
     * @param labelLoadSucListener
     */
    public void setLabelLoadSucListener(LabelLoadSucListener labelLoadSucListener) {
        this.labelLoadSucListener = labelLoadSucListener;
    }

    /**
     * 设置最大显示行数 不然隐藏
     * @param maxLine
     */
    public void setMaxLine(int maxLine) {
        this.maxLine = maxLine;
    }

    /**
     * 测量宽度
     */
    protected int measureView(View view) {
        int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        return view.getMeasuredWidth();
    }

    /**
     * 获取到位置的时候的回调
     */
    @Override
    public void onGlobalLayout() {
        int viewWidth = getWidth();
        if (viewWidth <= 0)return;
        if (getChildCount()>0)return;
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
        this.viewWidth = viewWidth-getPaddingLeft()-getPaddingRight();
        if (data == null)return;
        for (T value:data){
            addItemView(getItemView(value));
        }
        if (labelLoadSucListener != null)
            labelLoadSucListener.onLabelLoadSucCallBack(this);
    }

    /**
     * 添加进去
     * @param textView
     */
    public void addItemView(TextView textView){
        int wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT;
        int matchParent = ViewGroup.LayoutParams.MATCH_PARENT;
        LayoutParams params = new LayoutParams(matchParent,wrapContent);
        LayoutParams textParams = new LayoutParams(wrapContent,wrapContent);
        for (int index = 0 ;index < getChildCount();index++){
            LinearLayout layout = (LinearLayout) getChildAt(index);
            int useWidth = 0 ;
            for (int layoutIndex = 0 ;layoutIndex < layout.getChildCount();layoutIndex++){
                View view = layout.getChildAt(layoutIndex);
                useWidth = useWidth+measureView(view) + (layoutIndex>0?spaceHorizontal:0);
            }
            useWidth = useWidth+measureView(textView)
                    +(layout.getChildCount()>0?spaceHorizontal:0);
            if (useWidth<= viewWidth) {
                textParams.leftMargin = layout.getChildCount()>0?spaceHorizontal:0;
                textView.setLayoutParams(textParams);
                layout.addView(textView);
                return;
            }
        }
        if (getChildCount() < maxLine) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            params.topMargin = getChildCount() > 0 ? spaceVertical : 0;
            linearLayout.setLayoutParams(params);
            linearLayout.addView(textView);
            this.addView(linearLayout);
        }
    }

    /**
     * 添加子数据
     * @param value
     */
    protected TextView getItemView(T value){
        TextView textView = new TextView(getContext());
        textView.setMinWidth(minWidth);
        textView.setPadding(paddingLeft,paddingTop,paddingRight,paddingBottom);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        if (textListColor != 0)
            textView.setTextColor(getResources().getColorStateList(textListColor));
        else textView.setTextColor(getResources().getColor(textColor));
        textView.setBackgroundResource(background);
        if (value instanceof OnValueListener) {
            textView.setText(((OnValueListener) value).getValue());
            selectView(textView,value,((OnValueListener) value).status());
            textView.setSelected(((OnValueListener) value).status());
        }else
            textView.setText(value.toString());
        actionView(textView,value);
        return textView;
    }


    /**
     * 扩展处理
     */
    public void actionView(TextView textView,T value){ }
    public void selectView(View textView,T value,boolean status){ }
}
