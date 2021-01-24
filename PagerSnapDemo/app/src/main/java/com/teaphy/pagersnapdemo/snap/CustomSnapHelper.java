package com.teaphy.pagersnapdemo.snap;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

// SnapHelper这个辅助类，用于辅助RecyclerView在滚动结束时将Item对齐到某个位置。
// 特别是列表横向滑动时，很多时候不会让列表滑到任意位置，而是会有一定的规则限制，
// 这时候就可以通过SnapHelper来定义对齐规则了。
public abstract class CustomSnapHelper extends RecyclerView.OnFlingListener {

    static final float MILLISECONDS_PER_INCH = 100f;
    // SnapHelper附着的RecyclerView
    protected RecyclerView mRecyclerView;
    // 用来通过给定的X/Y轴上的速度来计算每个方向上的估计滚动距离。
    protected Scroller mGravityScroller;

    // 当RecyclerView滚动时，处理Snap
    private final RecyclerView.OnScrollListener mScrollListener =
            new RecyclerView.OnScrollListener() {
                boolean mScrolled = false;

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    // mScrolled为true表示之前进行过滚动.
                    // newState为SCROLL_STATE_IDLE状态表示滚动结束停下来
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && mScrolled) {
                        mScrolled = false;
                        snapToTargetExistingView();
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dx != 0 || dy != 0) {
                        // mScrolled为true表示之前进行过滚动.
                        mScrolled = true;
                    }
                }
            };

    // 处理Fling
    @Override
    public boolean onFling(int velocityX, int velocityY) {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        // 如果没有提供layoutManager，不做任何处理
        if (layoutManager == null) {
            return false;
        }
        // 如果没有设置Adapter，不做任何处理
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter == null) {
            return false;
        }

        //获取RecyclerView要进行fling操作需要的最小速率，
        //只有超过该速率，ItemView才会有足够的动力在手指离开屏幕时继续滚动下去
        int minFlingVelocity = mRecyclerView.getMinFlingVelocity();
        // 调用snapFromFling()方法，通过该方法实现平滑滚动并使得在滚动停止时itemView对齐到目的坐标位置
        return (Math.abs(velocityY) > minFlingVelocity || Math.abs(velocityX) > minFlingVelocity)
                && snapFromFling(layoutManager, velocityX, velocityY);
    }

    // 通过调用`RecyclerView.setOnFlingListener(RecyclerView.OnFlingListener)`将SnapHelper
    // 附着到提供的RecyclerView，从而实现辅助RecyclerView滚动对齐操作。也可以在调用此方法传null，以
    //  将其与当前RecyclerView分离。
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView)
            throws IllegalStateException {
        //如果SnapHelper之前已经附着到此RecyclerView上，不做任何操作
        if (mRecyclerView == recyclerView) {
            return;
        }
        // 如果SnapHelper之前附着的RecyclerView和现在的不一致，用以清除掉之前RecyclerView的监听回调
        if (mRecyclerView != null) {
            destroyCallbacks();
        }

        // 更新RecyclerView对象引用
        mRecyclerView = recyclerView;
        if (mRecyclerView != null) {
            // 设置当前RecyclerView对象的回调
            setupCallbacks();
            // 创建一个Scroller对象，用于辅助计算fling的总距离
            mGravityScroller = new Scroller(mRecyclerView.getContext(),
                    new DecelerateInterpolator());
            // 调用snapToTargetExistingView()方法以实现对SnapView的对齐滚动处理
            snapToTargetExistingView();
        }
    }

    // 当附着到RecyclerView时调用，以添加RecyclerView的onScroll和onFling监听
    private void setupCallbacks() throws IllegalStateException {
        if (mRecyclerView.getOnFlingListener() != null) {
            throw new IllegalStateException("An instance of OnFlingListener already set.");
        }
        mRecyclerView.addOnScrollListener(mScrollListener);
        mRecyclerView.setOnFlingListener(this);
    }

    // 当与RecyclerView的实例分离时调用，用以清除掉之前RecyclerView的onScroll和onFling监听
    private void destroyCallbacks() {
        mRecyclerView.removeOnScrollListener(mScrollListener);
        mRecyclerView.setOnFlingListener(null);
    }

    // 通过给定的X/Y轴上的速度来计算每个方向上的估计滚动距离。
    public int[] calculateScrollDistance(int velocityX, int velocityY) {
        int[] outDist = new int[2];
        mGravityScroller.fling(0, 0, velocityX, velocityY,
                Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
        outDist[0] = mGravityScroller.getFinalX();
        outDist[1] = mGravityScroller.getFinalY();
        return outDist;
    }

    // 实现平滑滚动并使得在滚动停止时itemView对齐到目的坐标位置
    // 如果处理了fling返回true，否则返回false
    private boolean snapFromFling(@NonNull RecyclerView.LayoutManager layoutManager, int velocityX,
                                  int velocityY) {
        // layoutManager必须实现ScrollVectorProvider接口
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return false;
        }

        // 创建平滑滚动器SmoothScroller对象，用于对ItemView进行平滑滚动操作
        RecyclerView.SmoothScroller smoothScroller = createScroller(layoutManager);
        if (smoothScroller == null) {
            return false;
        }

        // 通过findTargetSnapPosition()方法，以layoutManager和速率作为参数，找到targetSnapPosition
        int targetPosition = findTargetSnapPosition(layoutManager, velocityX, velocityY);
        // 如果 targetPosition为RecyclerView.NO_POSITION，表明找不到`targetSnapPosition`
        // 此时不做任何操作
        if (targetPosition == RecyclerView.NO_POSITION) {
            return false;
        }

        // 通过setTargetPosition()方法设置滚动器的滚动目标位置
        smoothScroller.setTargetPosition(targetPosition);
        // 利用layoutManager启动平滑滚动器，开始滚动到目标位置
        layoutManager.startSmoothScroll(smoothScroller);
        return true;
    }

    // 实现对SnapView的对齐滚动处理
    // 调用时机：
    //     1. 第一次附着到RecyclerView时 - 在attachToRecyclerView()方法中调用。
    //     2. 当ReycyclerView停止滚动时 - 在RecyclerView.OnScrollListener的onScrollStateChanged方法中调用
    void snapToTargetExistingView() {
        // 如果SnapHelper没有附着到RecyclerView不做任何处理
        if (mRecyclerView == null) {
            return;
        }
        // 如果附着的RecyclerView没有设置LayoutManager，不做任何处理
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager == null) {
            return;
        }

        // 通过findSnapView()方法获取当前layoutManager上最接近对齐位置的View
        // 也就是targetSnapView
        View snapView = findSnapView(layoutManager);

        // 如果找不到targetSnapView，不做任何处理
        if (snapView == null) {
            return;
        }

        // 通过calculateDistanceToFinalSnap()方法获取targetSnapView当前的坐标与需要对齐的坐标之间的距离。
        int[] snapDistance = calculateDistanceToFinalSnap(layoutManager, snapView);
        if (snapDistance[0] != 0 || snapDistance[1] != 0) {
            // 将RecyclerView平滑滚动
            mRecyclerView.smoothScrollBy(snapDistance[0], snapDistance[1]);
        }
    }

    // 创建要在Snap实现中使用的SmoothScroller。
    @Nullable
    protected RecyclerView.SmoothScroller createScroller(RecyclerView.LayoutManager layoutManager) {
        return createSnapScroller(layoutManager);
    }

    // 创建要在Snap实现中使用的SmoothScroller。
    @Nullable
    @Deprecated
    protected LinearSmoothScroller createSnapScroller(RecyclerView.LayoutManager layoutManager) {
        // 判断layoutManager是否实现了ScrollVectorProvider这个接口，
        // 如果没有实现该接口就不创建SmoothScroller
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return null;
        }
        // 创建一个LinearSmoothScroller对象，
        // 也就是说，最终创建出来的平滑滚动器就是LinearSmoothScroller
        return new LinearSmoothScroller(mRecyclerView.getContext()) {

            //在targetSnapView被layout出来的时候调用。
            //
            // 参数：
            //     targetView：就是findSnapView获取的targetSnapView
            //     RecyclerView.State：
            //     Action: 它是SmoothScroller的一个静态内部类,
            //             保存着SmoothScroller在平滑滚动过程中一些信息，比如滚动时间，滚动距离，插值器等
            @Override
            protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
                if (mRecyclerView == null) {
                    // The associated RecyclerView has been removed so there is no action to take.
                    return;
                }
                // 获取targetSnapView当前坐标到目的坐标之间的距离
                int[] snapDistances = calculateDistanceToFinalSnap(mRecyclerView.getLayoutManager(),
                        targetView);
                final int dx = snapDistances[0];
                final int dy = snapDistances[1];
                // 获取减速滚动所需的时间
                final int time = calculateTimeForDeceleration(Math.max(Math.abs(dx), Math.abs(dy)));
                if (time > 0) {
                    // 调用Action的update()方法，更新SmoothScroller的滚动速率，使其减速滚动到停止
                    // 这里的这样做的效果是，SmoothScroller用time这么长的时间以mDecelerateInterpolator这个
                    // 插值器的滚动变化率滚动dx或者dy这么长的距离
                    action.update(dx, dy, time, mDecelerateInterpolator);
                }
            }

            // 计算滚动速率的，返回值代表滚动速率
            // 该值会影响calculateTimeForDeceleration()的方法的返回返回值，
            // MILLISECONDS_PER_INCH的值是100，也就是说该方法的返回值代表着每dpi的距离要滚动100毫秒
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
            }
        };
    }

    // 重写此方法，对应的ItemView(即targetSnapView)当前的坐标与需要对齐的坐标之间的距离。
    //
    // 当SnapHelper处理了fling，并且需要知道滚动到targetSnapView所需的确切距离时调用此方法。
    @SuppressWarnings("WeakerAccess")
    @Nullable
    public abstract int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,
                                                       @NonNull View targetView);


    // 重写此方法，当前layoutManager上最接近对齐位置的那个view，该view称为SanpView，对应
    // 的position称为SnapPosition。如果返回null，就表示没有需要对齐的View，也就不会做滚动对齐调整。
    @SuppressWarnings("WeakerAccess")
    @Nullable
    public abstract View findSnapView(RecyclerView.LayoutManager layoutManager);

    // 重写此方法，根据触发Fling操作的速率（参数velocityX和参数velocityY）来找到RecyclerView需要滚
    // 动到哪个位置，该位置对应的ItemView就是那个需要进行对齐的列表项。我们把这个位置称为targetSnapPosition，
    // 对应的View称为targetSnapView。如果找不到targetSnapPosition，就返回RecyclerView.NO_POSITION
    public abstract int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX,
                                               int velocityY);
}
