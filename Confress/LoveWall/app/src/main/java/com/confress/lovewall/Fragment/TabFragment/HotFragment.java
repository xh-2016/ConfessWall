package com.confress.lovewall.Fragment.TabFragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.confress.lovewall.Activity.CommentActivity;
import com.confress.lovewall.Activity.HomeActivity;
import com.confress.lovewall.Activity.UserWallActivity;
import com.confress.lovewall.Adapter.NewsAndHotAdapter;
import com.confress.lovewall.R;
import com.confress.lovewall.Utils.T;
import com.confress.lovewall.model.MessageWall;
import com.confress.lovewall.model.User;
import com.confress.lovewall.presenter.FragmentPresenter.HotFragmentPresenter;
import com.confress.lovewall.presenter.FragmentPresenter.NewsFragmentPresenter;
import com.confress.lovewall.view.FragmentView.IHotFragmentView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;

/**
 * Created by admin on 2016/3/13.
 */
public class HotFragment extends Fragment implements IHotFragmentView {
    View root;
    @Bind(R.id.mListView)
    ListView mListView;
    @Bind(R.id.mRefresh)
    MaterialRefreshLayout mRefresh;
    @Bind(R.id.tabs_progress)
    ProgressBar tabsProgress;
    private HotFragmentPresenter hotFragmentPresenter = new HotFragmentPresenter(this, getActivity());
    private NewsAndHotAdapter adapter;
    private List<MessageWall> messageWalls;
    private int currentpage = 1;
    private int mFirstY;
    private int mCurrentY;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_tab1, container, false);
        ButterKnife.bind(this, root);
        messageWalls = new ArrayList<MessageWall>();
        adapter = new NewsAndHotAdapter(messageWalls, getActivity());
        mListView.setAdapter(adapter);
        //初始化加载一次数据并且显示
        new Thread(new Runnable() {
            @Override
            public void run() {
                hotFragmentPresenter.FirstLoadingData(getActivity());
                currentpage = 1;
            }
        }).start();
        //刷新加载数据
        mRefresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                //下拉刷新...与第一次加载数据一样
                currentpage = 1;
                messageWalls.clear();
                hotFragmentPresenter.FirstLoadingData(getActivity());
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                //上拉刷新...
                hotFragmentPresenter.PullDownRefreshqueryData(currentpage, getActivity());
                // 结束上拉刷新...
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CommentActivity.class);
                intent.putExtra("messageWall", messageWalls.get(position));
                startActivity(intent);
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
        hotFragmentPresenter=null;
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
