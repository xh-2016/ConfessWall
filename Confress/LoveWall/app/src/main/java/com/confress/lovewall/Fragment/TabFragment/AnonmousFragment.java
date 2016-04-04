package com.confress.lovewall.Fragment.TabFragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.confress.lovewall.Activity.HomeActivity;
import com.confress.lovewall.Adapter.AnonmousAdapter;
import com.confress.lovewall.Adapter.NewsAndHotAdapter;
import com.confress.lovewall.R;
import com.confress.lovewall.Utils.T;
import com.confress.lovewall.model.MessageWall;
import com.confress.lovewall.model.User;
import com.confress.lovewall.presenter.FragmentPresenter.AnonmousFragmentPresenter;
import com.confress.lovewall.presenter.FragmentPresenter.HotFragmentPresenter;
import com.confress.lovewall.view.AtyView.IAnnomousView;
import com.confress.lovewall.view.FragmentView.IAnonmousFragmentView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;

/**
 * Created by admin on 2016/3/13.
 */
public class AnonmousFragment extends Fragment implements IAnonmousFragmentView{
    View root;
    @Bind(R.id.mListView)
    ListView mListView;
    @Bind(R.id.mRefresh)
    MaterialRefreshLayout mRefresh;
    @Bind(R.id.tabs_progress)
    ProgressBar tabsProgress;
    private AnonmousFragmentPresenter anonmousFragmentPresenter = new AnonmousFragmentPresenter(this, getActivity());
    private AnonmousAdapter adapter;
    private List<MessageWall> messageWalls;
    private int currentpage = 1;
    private int mFirstY;
    private int mCurrentY;
//    private Handler mhandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == 0) {
//                Failure();
//            } else if(msg.what==1){
//                messageWalls.addAll((List<MessageWall>) msg.obj);
//                if (messageWalls.size() > 0) {
//                    adapter.notifyDataSetChanged();
//                } else {
//                    T.showShort(getActivity(), "没有数据！");
//                }
//            }else  if (msg.what==2){
//                if (messageWalls.size()>0){
//                    messageWalls.addAll((List<MessageWall>) msg.obj);
//                    adapter.notifyDataSetChanged();
//                    currentpage++;
//                }
//                mRefresh.finishRefreshLoadMore();
//            }else  if (msg.what==3){
//                T.showShort(getActivity(), "没有更多数据了！");
//            }
//            mRefresh.finishRefresh();
//            mRefresh.finishRefreshLoadMore();
//        }
//    };


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_tab1, container, false);
        ButterKnife.bind(this, root);
        messageWalls = new ArrayList<MessageWall>();
        adapter = new AnonmousAdapter(messageWalls, getActivity());
        mListView.setAdapter(adapter);
        //初始化加载一次数据并且显示
        new Thread(new Runnable() {
            @Override
            public void run() {
                anonmousFragmentPresenter.FirstLoadingData( getActivity());
                currentpage=1;
            }
        }).start();
        //刷新加载数据
        mRefresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                //下拉刷新...与第一次加载数据一样
                currentpage=1;
                messageWalls.clear();
                anonmousFragmentPresenter.FirstLoadingData(getActivity());
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                //上拉刷新...
                anonmousFragmentPresenter.PullDownRefreshqueryData(currentpage, getActivity());
            }
        });

        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mFirstY = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:
                        mCurrentY = (int) event.getY();
                        if (mCurrentY - mFirstY > 50) {
                            Log.e("Show", "Show");
                            HomeActivity.showMainBottom();

                        } else if (mFirstY - mCurrentY > 20) {
                            Log.e("Hide", "Hide");
                            HomeActivity.hideMainBottom();
                        }
                        break;
                }
                return false;
            }
        });

        return root;
    }



    @Override
    public void LoadOver() {
        if (mRefresh!=null) {
            mRefresh.finishRefresh();
            mRefresh.finishRefreshLoadMore();
        }
    }

    @Override
    public User getCurrentUser() {
        return BmobUser.getCurrentUser(getActivity(), User.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        anonmousFragmentPresenter=null;
    }

    @Override
    public void CollectionSuccess() {
        T.showShort(getActivity(), "收藏成功！");
    }

    @Override
    public void CollectionFailure() {
        T.showShort(getActivity(), "收藏失败！");
    }

    @Override
    public void DelCollectionSuccess() {
        T.showShort(getActivity(), "取消收藏成功！");
    }

    @Override
    public void DelCollectionFailure() {
        T.showShort(getActivity(), "取消收藏失败！");
    }

    @Override
    public void UpdateAdapter(int size, List<MessageWall> ImessageWalls) {
        if (size==0){
        }else if (size==1){
            messageWalls.addAll(ImessageWalls);
            if (messageWalls.size()>0){
                adapter.notifyDataSetChanged();
            }else {
                T.showShort(getActivity(), "没有数据！");
            }
        }else  if (size==2){
            if (messageWalls.size() > 0) {
                messageWalls.addAll(ImessageWalls);
                adapter.notifyDataSetChanged();
                currentpage++;
            }
        }else  if (size==3){
            T.showShort(getActivity(), "没有更多数据了！");
        }
        LoadOver();
    }

}
