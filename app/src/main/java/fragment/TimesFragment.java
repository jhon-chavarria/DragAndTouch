package fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import jhon.dragandtouch.R;


/**
 * Created by jhon on 23/06/14.
 */
public class TimesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String[] mDays;
    private String[] mGridNumbers;
    private ViewGroup mNumbersGridContainer;
    private LayoutInflater mInflater;
    private ViewGroup mDaysContainerLayout;
    private View mView;
    private RelativeLayout mScheduleBarView;
    private GestureDetector mGestureDetector;
    private View mCurrenView;
    private int mCurY;
    private int mDayContainerY;
    private ScrollView mScrollView;
    private int mbarId = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_time, container, false);

        mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDaysContainerLayout = (ViewGroup) mView.findViewById(R.id.days_container);
        mScrollView = (ScrollView) mView.findViewById(R.id.scrollview);

        mDaysContainerLayout.removeAllViews();
        mDays = getResources().getStringArray(R.array.days);

        for (int i = 0; i < mDays.length; i++) {

            final View mViewBtn = mInflater.inflate(R.layout.times_day_layout, mDaysContainerLayout, false);
            mViewBtn.setId(60 + i);
            mViewBtn.setOnDragListener(new ScheduleDragListener());
            final Button daysButton = (Button) mViewBtn.findViewById(R.id.btn);
            daysButton.setText(mDays[i]);

            final int position = i;

            mViewBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View arg0, MotionEvent ev) {
                    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                        mDayContainerY = (int) ev.getY();
                    }
                    return false;
                }
            });

            mViewBtn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // TODO Auto-generated method stub
                    addScheduleBar(mViewBtn, mViewBtn.getId());
                    daysButton.setBackground(getResources().getDrawable(R.drawable.btn_day_selected));
                    daysButton.setTextColor(getResources().getColor(android.R.color.black));
                    daysButton.setSelected(true);
                    return true;
                }
            });

            daysButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View button) {

                    RelativeLayout barContainer = (RelativeLayout) mView.findViewById(mViewBtn.getId());
                    if (barContainer.getChildCount() == 1)
                        return;

                    button.setSelected(!button.isSelected());
                    if (button.isSelected()) {
                        daysButton.setBackground(getResources().getDrawable(R.drawable.btn_day_selected));
                        daysButton.setTextColor(getResources().getColor(android.R.color.black));

                        setDayActive(daysButton);

                        RelativeLayout layout = (RelativeLayout) mView.findViewById(mViewBtn.getId());
                        if (layout.getChildCount() >= 2) {
                            int j;
                            for (j = 0; j < layout.getChildCount(); j++) {
                                if (j != 0) {
                                    LinearLayout scheduleBar = (LinearLayout) layout.getChildAt(j).findViewById(R.id.bar);
                                    scheduleBar.setBackground(getResources().getDrawable(R.drawable.btn_day_selected));
                                }
                            }
                        }

                    } else {

                        if (barContainer.getChildCount() >= 2) {
                            RelativeLayout layout = (RelativeLayout) mView.findViewById(mViewBtn.getId());
                            int j;
                            for (j = 0; j < layout.getChildCount(); j++) {
                                if (j != 0) {
                                    LinearLayout scheduleBar = (LinearLayout) layout.getChildAt(j).findViewById(R.id.bar);
                                    scheduleBar.setBackground(getResources().getDrawable(R.drawable.btn_day_unselected));
                                }
                            }
                            setDayInactive(daysButton);
                        } else
                            setDayInactive(daysButton);

                    }
                }
            });
            mDaysContainerLayout.addView(mViewBtn);
        }

        mDaysContainerLayout.bringToFront();
        mGridNumbers = getResources().getStringArray(R.array.grid_values);
        mNumbersGridContainer = (ViewGroup) mView.findViewById(R.id.grid_container);
        mNumbersGridContainer.removeAllViews();

        for (int i = 0; i < mGridNumbers.length; i++) {
            View mViewGrid = mInflater.inflate(R.layout.grid_numbers_layout, mNumbersGridContainer, false);
            ((TextView) mViewGrid.findViewById(R.id.number)).setText(mGridNumbers[i]);
            mNumbersGridContainer.addView(mViewGrid);
        }
        // Gesture detection
        mGestureDetector = new GestureDetector(getActivity(), new MyGestureDetector());

        final ViewTreeObserver observer= mNumbersGridContainer.getViewTreeObserver();

        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mNumbersGridContainer.getHeight());
                lp.setMargins(55, 0, 0, 0);
                mDaysContainerLayout.setLayoutParams(lp);
            }
        });


        return mView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(mCurrenView);
            mCurrenView.startDrag(data, shadowBuilder, mCurrenView, 0);
            mCurrenView.setVisibility(View.INVISIBLE);
        }


        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                float distanceY) {


            final LinearLayout ll = (LinearLayout) mCurrenView.findViewById(R.id.bar);
            float diffY = e2.getY() - e1.getY();
            if (diffY > 0) {
                ll.getLayoutParams().height = (ll.getHeight() + 15);
                ll.requestLayout();

            } else {
                ll.getLayoutParams().height = (ll.getHeight() - 15);
                ll.requestLayout();
            }
            return false;
        }

    }

    public void addScheduleBar(final View parent, int mViewBtnId) {
        mScheduleBarView = (RelativeLayout) parent.findViewById(mViewBtnId);
        final View barView = mInflater.inflate(R.layout.schedule_bar, null, false);
        barView.setId(mbarId);
        mbarId = mbarId + 1;
        LinearLayout bar = (LinearLayout) mView.findViewById(R.id.bar_temp);
        barView.setY((float) (mDayContainerY - (bar.getHeight() * 0.7)));
        mScheduleBarView.addView(barView);
        barView.setOnTouchListener(new ScheduleTouchListener());
    }

    private class ScheduleDragListener implements View.OnDragListener {


        @Override
        public boolean onDrag(View v, DragEvent event) {

            switch (event.getAction()) {

                case DragEvent.ACTION_DRAG_LOCATION:
                    mCurY = (int) event.getY();
                    break;
                case DragEvent.ACTION_DROP:

                    // Dropped, reassign View to ViewGroup
                    View view = (View) event.getLocalState();
                    ViewGroup owner = (ViewGroup) view.getParent();
                    owner.removeView(view);
                    RelativeLayout container = (RelativeLayout) v;

                    Button btnSelected = (Button) v.findViewById(R.id.btn);
                    setDayActive(btnSelected);

                    if(owner.getId()!= container.getId()){
                        Button btnUnselected = (Button) owner.findViewById(R.id.btn);
                        setDayInactive(btnUnselected);
                    }

                    container.addView(view);
                    LinearLayout scheduleBar = (LinearLayout) view.findViewById(R.id.bar);
                    scheduleBar.setBackground(getResources().getDrawable(R.drawable.btn_day_selected));

                    mCurrenView = view;
                    view.setVisibility(View.VISIBLE);
                    mCurrenView.setY(mCurY - (view.getHeight() / 2));

                    break;
                default:
                    break;
            }
            return true;
        }
    }

    private final class ScheduleTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mCurrenView = view;
            mGestureDetector.onTouchEvent(motionEvent);

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mScrollView.requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                    mScrollView.requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return true;
        }
    }

    public void setDayActive(Button selectedBtn) {
        selectedBtn.setTextColor(getResources().getColor(android.R.color.black));
        selectedBtn.setBackground(getResources().getDrawable(R.drawable.btn_day_selected));
        selectedBtn.setSelected(true);
    }

    public void setDayInactive(Button unselectedBtn) {
        unselectedBtn.setTextColor(getResources().getColor(android.R.color.white));
        unselectedBtn.setBackground(getResources().getDrawable(R.drawable.btn_day_unselected));
    }
}

